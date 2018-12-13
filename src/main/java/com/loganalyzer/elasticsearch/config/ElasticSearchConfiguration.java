package com.loganalyzer.elasticsearch.config;

import com.loganalyzer.elasticsearch.dao.LogDao;
import com.loganalyzer.elasticsearch.receiver.LogEventsGenerator;
import com.loganalyzer.elasticsearch.util.Utility;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Configuration
public class ElasticSearchConfiguration extends AbstractFactoryBean<RestHighLevelClient> {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConfiguration.class);
    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String clusterNodes;
    @Value("${spring.data.elasticsearch.cluster-name}")
    private String clusterName;
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    ApplicationArguments appArgs;

    @Autowired
    LogDao logDao;

    private String logsPath;

    @Value("${formatPattern}")
    private String formatPattern;

    @Value("${formatPatternNoLocation}")
    private String formatPatternNoLocation;

    @Value("${timestamp}")
    private String timestamp;

    @Value("#{'${filesWithFormatPattern}'.split(',')}")
    private List<String> filesWithFormatPattern;

    @Value("#{'${filesWithFormatPatternNoLocation}'.split(',')}")
    private List<String> filesWithFormatPatternNoLocation;

    @PostConstruct
    public void initialize(){

            logsPath = appArgs.getNonOptionArgs().get(0);

            List<File> list = new ArrayList<File>();
            String[] compressedList = new String[] {"zip", "gz"};
            Collection<File> compressedFiles = FileUtils.listFiles(new File(logsPath), compressedList, true);
            for (File file1 : compressedFiles){
                String ext = FilenameUtils.getExtension(file1.getPath());
                if (compressedList[0].equals(ext)){
                    String inputFile = file1.getAbsolutePath();
                    String outputFolder = inputFile.substring(0, inputFile.lastIndexOf("\\"));
                    unZipIt(inputFile, outputFolder);
                }else if (compressedList[1].equals(ext)){
                    String inputFile = file1.getAbsolutePath();
                    String outputFile = inputFile.substring(0, inputFile.lastIndexOf("."));
                    gunzipIt(inputFile, outputFile);
                }
            }

            String[] extensions = new String[] { "log" };
            Collection<File> array = FileUtils.listFiles(new File(logsPath), extensions, true);

            String format;
            for (File file : array){

                String fileName = Utility.getFileName(file.getPath());
                boolean found = filesWithFormatPatternNoLocation
                        .stream()
                        .filter((string) -> fileName.contains(string)).findFirst().isPresent();
                if (found){
                    generator(formatPatternNoLocation, file);
                }else {
                    found = filesWithFormatPattern
                            .stream()
                            .filter((string) -> fileName.contains(string)).findFirst().isPresent();
                    if (found) {
                        generator(formatPattern, file);
                    }
                }
            }
    }

    @Override
    public void destroy() {
        try {
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (final Exception e) {
            logger.error("Error closing ElasticSearch client: ", e);
        }
    }

    @Override
    public Class<RestHighLevelClient> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public RestHighLevelClient createInstance() {
        return buildClient();
    }

    private RestHighLevelClient buildClient() {
        try {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("aabrd0310.aaspl-brd.com", 9200, "http"),
                            new HttpHost("aabrd0310.aaspl-brd.com", 9201, "http")));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return restHighLevelClient;
    }

    private void generator(String format, File file){
        LogEventsGenerator receiver = new LogEventsGenerator(logDao);
        receiver.setTimestampFormat(timestamp);
        receiver.setLogFormat(format);
        receiver.setFileURL("file:///" + file.getAbsolutePath());
        receiver.setTailing(false);
        receiver.activateOptions();
    }


    public void gunzipIt(String inputFile, String outputFile){

        byte[] buffer = new byte[1024];

        try{

            GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(inputFile));
            FileOutputStream out = new FileOutputStream(outputFile);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();

            System.out.println("Done");

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void unZipIt(String zipFile, String outputFolder){

        byte[] buffer = new byte[1024];

        try{

            //create output directory is not exists
            File folder = new File(outputFolder);
            if(!folder.exists()){
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                System.out.println("file unzip : "+ newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            System.out.println("Done");

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

}

