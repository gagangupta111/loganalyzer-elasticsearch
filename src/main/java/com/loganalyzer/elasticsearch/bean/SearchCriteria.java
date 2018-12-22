package com.loganalyzer.elasticsearch.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.loganalyzer.elasticsearch.util.JsonDateDeSerializer;
import com.loganalyzer.elasticsearch.util.JsonDateSerializer;

import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect
public class SearchCriteria {

    private Long starting;
    private Long ending;
    private String level;
    private String className;
    private String methodName;
    private String classFile;
    private String line;
    private String logFile;
    private String message;

    private Integer start;
    private Integer size;

    public SearchCriteria() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getStarting() {
        return starting;
    }

    @JsonDeserialize(using=JsonDateDeSerializer.class)
    public void setStarting(Long starting) {
        this.starting = starting;
    }

    public Long getEnding() {
        return ending;
    }

    @JsonDeserialize(using=JsonDateDeSerializer.class)
    public void setEnding(Long ending) {
        this.ending = ending;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassFile() {
        return classFile;
    }

    public void setClassFile(String classFile) {
        this.classFile = classFile;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public Map<String, Object> map(){

        Map<String, Object> map = new HashMap<>();
        if (starting != null && ending != null){
            map.put("starting", starting);
            map.put("ending", ending);
        }

        if (level != null){
            map.put("level", level);
        }

        if (className != null){
            map.put("className", className);
        }

        if (methodName != null){
            map.put("methodName", methodName);
        }

        if (classFile != null){
            map.put("classFile", classFile);
        }

        if (line != null){
            map.put("line", line);
        }

        if (logFile != null){
            map.put("logFile", logFile);
        }

        return map;
    }
}
