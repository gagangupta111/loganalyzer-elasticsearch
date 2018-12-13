package com.loganalyzer.elasticsearch.config;

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
import java.util.List;

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

        System.out.println("printing!");

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


}

