package com.loganalyzer.elasticsearch.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loganalyzer.elasticsearch.bean.Log;
import com.loganalyzer.elasticsearch.bean.SearchCriteria;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.profile.ProfileShardResult;
import org.springframework.stereotype.Repository;

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

    private RestHighLevelClient restHighLevelClient;

    private ObjectMapper objectMapper;

    public LogDao( ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
        this.objectMapper = objectMapper;
        this.restHighLevelClient = restHighLevelClient;
    }

    public Log insertLog(Log log){
        log.setId(UUID.randomUUID().toString());
        System.out.println(log);
        Map<String, Object> dataMap = objectMapper.convertValue(log, Map.class);
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

    public List<Map<String, Object>> getAllTypes(SearchCriteria criteria){

        List<Map<String, Object>> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (criteria.getClassFile() != null){
            boolQuery.must(QueryBuilders
                    .regexpQuery("classFile", ".*" + criteria.getClassFile() + ".*")
                    );
        }

        if (criteria.getLevel() != null){
            boolQuery.must(QueryBuilders.matchQuery("level", criteria.getLevel()));
        }

        if (criteria.getLine() != null){
            boolQuery.must(QueryBuilders.matchQuery("line", criteria.getLine()));
        }

        if (criteria.getLogFile() != null){
            boolQuery.must(QueryBuilders.matchQuery("logFile", criteria.getLogFile()));
        }

        if (criteria.getMethodName() != null){
            boolQuery.must(QueryBuilders.matchQuery("methodName", criteria.getMethodName()));
        }

        if (criteria.getClassName() != null){
            boolQuery.must(QueryBuilders.matchQuery("className", criteria.getClassName()));
        }

        if (criteria.getMessage() != null){
            boolQuery.must(QueryBuilders.matchQuery("message", criteria.getMessage()));
        }

        searchSourceBuilder.query(boolQuery);

        if (criteria.getStart() != null){
            searchSourceBuilder.from(criteria.getStart());
        }

        if (criteria.getSize() != null){
            searchSourceBuilder.size(criteria.getSize());
        }

        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;

        try {
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }

        for (SearchHit hit : searchResponse.getHits().getHits()){
            list.add(getLogById(hit.getId()));
        }

        return list;
    }

    public Map<String, Object> getLogById(String id){
        GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse = null;
        try {
            getResponse = restHighLevelClient.get(getRequest);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        return sourceAsMap;
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

}