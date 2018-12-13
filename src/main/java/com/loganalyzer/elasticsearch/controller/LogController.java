package com.loganalyzer.elasticsearch.controller;

import com.loganalyzer.elasticsearch.bean.Log;
import com.loganalyzer.elasticsearch.dao.LogDao;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LogController {

    private LogDao logDao;

    public LogController(LogDao logDao) {
        this.logDao = logDao;
    }

    @GetMapping
    public Map<String, Object> getAllTypes(){
        return logDao.getAllTypes();
    }

    @PostMapping
    public Log insertLog(@RequestBody Log log) throws Exception{
        return logDao.insertLog(log);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getLogById(@PathVariable String id){
        return logDao.getLogById(id);
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateLogById(@RequestBody Log log, @PathVariable String id){
        return logDao.updateLogById(id, log);
    }

    @DeleteMapping("/{id}")
    public void deleteLogById(@PathVariable String id){
        logDao.deleteLogById(id);
    }
}