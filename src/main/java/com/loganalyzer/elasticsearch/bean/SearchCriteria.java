package com.loganalyzer.elasticsearch.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.loganalyzer.elasticsearch.util.JsonDateDeSerializer;

import java.sql.Timestamp;

@JsonAutoDetect
public class SearchCriteria {

    private Timestamp starting;
    private Timestamp ending;
    private String level;
    private String className;
    private String methodName;
    private String classFile;
    private String line;
    private String logFile;

    public SearchCriteria() {
    }

    public Timestamp getStarting() {
        return starting;
    }

    @JsonDeserialize(using=JsonDateDeSerializer.class)
    public void setStarting(Timestamp starting) {
        this.starting = starting;
    }

    public Timestamp getEnding() {
        return ending;
    }

    @JsonDeserialize(using=JsonDateDeSerializer.class)
    public void setEnding(Timestamp ending) {
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
}
