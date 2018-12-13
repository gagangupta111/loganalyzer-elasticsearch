package com.loganalyzer.elasticsearch.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.loganalyzer.elasticsearch.util.JsonDateDeSerializer;
import com.loganalyzer.elasticsearch.util.JsonDateSerializer;

import java.sql.Timestamp;

@JsonAutoDetect
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Log {

    private String id;
    @JsonFormat(pattern="yyyy-MMM-dd EEE HH:mm:ss.SSS")
    private Timestamp timestamp;
    private String level;
    private String className;
    private String methodName;
    private String classFile;
    private String line;
    private String logFile;
    private String message;

    public Log() {
    }

    public Log(String id, Timestamp timestamp, String level, String className, String methodName,
               String classFile, String line, String logFile, String message) {
        this.id = id;
        this.timestamp = timestamp;
        this.level = level;
        this.className = className;
        this.methodName = methodName;
        this.classFile = classFile;
        this.line = line;
        this.logFile = logFile;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @JsonDeserialize(using=JsonDateDeSerializer.class)
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

    @Override
    public String toString() {
        return "Log{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", level='" + level + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", classFile='" + classFile + '\'' +
                ", line='" + line + '\'' +
                ", logFile='" + logFile + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
