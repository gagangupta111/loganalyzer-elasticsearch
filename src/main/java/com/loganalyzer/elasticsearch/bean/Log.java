package com.loganalyzer.elasticsearch.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.loganalyzer.elasticsearch.util.JsonDateSerializer;

import java.sql.Timestamp;

public class Log {

    private Timestamp timestamp;
    private String level;
    private String className;
    private String methodName;
    private String classFile;
    private String line;
    private String logFile;

    public Log() {
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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
