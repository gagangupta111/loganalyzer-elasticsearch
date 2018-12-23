package com.loganalyzer.elasticsearch.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loganalyzer.elasticsearch.bean.Log;
import com.loganalyzer.elasticsearch.bean.SearchCriteria;
import com.loganalyzer.elasticsearch.util.JsonDateDeSerializer;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class LogDao {

    private final String INDEX = "logdata";
    private final String TYPE = "logs";

    @Value("${timestamp}")
    private String timestamp;

    private RestHighLevelClient restHighLevelClient;

    private ObjectMapper objectMapper;

    public LogDao( ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
        this.objectMapper = objectMapper;
        this.restHighLevelClient = restHighLevelClient;
    }

    public Log insertLog(Log log){
        log.setId(UUID.randomUUID().toString());
        System.out.println(log);
        Map<String, Object> dataMap = log.map();
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, log.getId())
                .source(dataMap);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest);
        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex){
            ex.getLocalizedMessage();
        }
        return log;
    }

    private String getMultiScriptAppend(String fieldHolder, String valueHolder){
        return  "{" +
                "\"query_string\":" +
                "{" +
                "\"default_field\" : \"{{" + fieldHolder + "}}\"," +
                "\"query\" : \"{{" + valueHolder + "}}\"," +
                "\"minimum_should_match\" : \"100%\"" +
                "}" +
                "}" +
                ",";
    }

    public List<Log> getAllTypes(SearchCriteria criteria) throws IOException {

        List<Log> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String multiScript = "{" +
                "\"sort\":[" +
                "{ \"logTimeStamp\" : {\"order\" : \"asc\"}}" +
                "],";

        if (criteria.getStart() != null){
            multiScript += "\"from\":" + criteria.getStart() + ",";
        }

        if (criteria.getSize() != null){
            multiScript += "\"size\":" + criteria.getSize() + ",";
        }

        multiScript +=
                "\"query\":" +
                "{" +
                "\"bool\":" +
                "{";

        Map<String, Object> scriptParams = new HashMap<>();
        String fieldHolder;
        String valueHolder;

        if (criteria.getStarting() != null && criteria.getEnding() != null) {

            multiScript +=
                    "\"filter\":" +
                    "{" +
                    "\"range\":" +
                    "{" +
                    "\"logTimeStamp\":" +
                    "{" +
                    "\"gte\":\"" + criteria.getStarting() + "\"," +
                    "\"lte\":\"" + criteria.getEnding() + "\"" +
                    "}" +
                    "}" +
                    "}" +
                    ",";

        }

        multiScript +=
                "\"must\":" +
                "[";

        if (criteria.getClassFile() != null) {

            fieldHolder = "classFileField";
            valueHolder = "classFileValue";

            multiScript += getMultiScriptAppend(fieldHolder, valueHolder);

            scriptParams.put(fieldHolder, "classFile");
            scriptParams.put(valueHolder, criteria.getClassFile());

        }

        if (criteria.getLevel() != null){

            fieldHolder = "levelField";
            valueHolder = "levelValue";

            multiScript += getMultiScriptAppend(fieldHolder, valueHolder);

            scriptParams.put(fieldHolder, "level");
            scriptParams.put(valueHolder, criteria.getLevel());

        }

        if (criteria.getLine() != null){

            fieldHolder = "lineField";
            valueHolder = "lineValue";

            multiScript += getMultiScriptAppend(fieldHolder, valueHolder);

            scriptParams.put(fieldHolder, "line");
            scriptParams.put(valueHolder, criteria.getLine());

        }

        if (criteria.getLogFile() != null){

            fieldHolder = "logFileField";
            valueHolder = "logFileValue";

            multiScript += getMultiScriptAppend(fieldHolder, valueHolder);

            scriptParams.put(fieldHolder, "logFile");
            scriptParams.put(valueHolder, criteria.getLogFile());

        }

        if (criteria.getMethodName() != null){

            fieldHolder = "methodNameField";
            valueHolder = "methodNameValue";

            multiScript += getMultiScriptAppend(fieldHolder, valueHolder);

            scriptParams.put(fieldHolder, "methodName");
            scriptParams.put(valueHolder, criteria.getMethodName());

        }

        if (criteria.getClassName() != null){

            fieldHolder = "classNameField";
            valueHolder = "classNameValue";

            multiScript += getMultiScriptAppend(fieldHolder, valueHolder);

            scriptParams.put(fieldHolder, "className");
            scriptParams.put(valueHolder, criteria.getClassName());

        }

        if (criteria.getMessage() != null){

            fieldHolder = "messageField";
            valueHolder = "messageValue";

            multiScript += getMultiScriptAppend(fieldHolder, valueHolder);

            scriptParams.put(fieldHolder, "message");
            scriptParams.put(valueHolder, criteria.getMessage());

        }

        multiScript = ',' == (multiScript.charAt(multiScript.length() - 1)) ? multiScript.substring(0, multiScript.length() - 1) : multiScript;

        multiScript +=
                "]" +
                "}" +
                "}" +
                "}";

        searchRequest.source(searchSourceBuilder);

        SearchTemplateRequest searchTemplateRequest = new SearchTemplateRequest();
        searchTemplateRequest.setRequest(searchRequest);
        searchTemplateRequest.setScriptType(ScriptType.INLINE);
        searchTemplateRequest.setScript(multiScript);
        searchTemplateRequest.setScriptParams(scriptParams);

        SearchTemplateResponse searchTemplateResponse = null;

        try {
            searchTemplateResponse = restHighLevelClient.searchTemplate(searchTemplateRequest, RequestOptions.DEFAULT);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }

        for (SearchHit hit : searchTemplateResponse.getResponse().getHits().getHits()){
            list.add(getLogById(hit.getId()));
        }

        return list;
    }

    public Log getLogById(String id){
        GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse = null;
        try {
            getResponse = restHighLevelClient.get(getRequest);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        return Log.mapToLog(sourceAsMap);
    }

    public Map<String, Object> updateLogById(String id, Log log){
        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id)
                .fetchSource(true);    // Fetch Object after its update
        Map<String, Object> error = new HashMap<>();
        error.put("Error", "Unable to update book");
        try {
            String bookJson = objectMapper.writeValueAsString(log);
            updateRequest.doc(bookJson, XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
            Map<String, Object> sourceAsMap = updateResponse.getGetResult().sourceAsMap();
            return sourceAsMap;
        }catch (JsonProcessingException e){
            e.getMessage();
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
        return error;
    }

    public void deleteLogById(String id) {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
    }

    public void deleteAll() {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX);
        DeleteResponse deleteResponse = null;
        try {
            deleteResponse = restHighLevelClient.delete(deleteRequest);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }

    }

    String range =
            "{" +
            "\"query\":" +
            "{" +
            "\"range\":" +
            "{" +
            "\"{{timestampField}}\":" +
            "{" +
            "\"gte\":{{starting}}," +
            "\"lte\":{{ending}}," +
            "\"boost\":2.0" +
            "}" +
            "}" +
            "}" +
            "}"
            ;

}
