package com.sudhanva.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.server.model.JobPost;
import com.sudhanva.server.service.JobService;


@RestController
public class JobController {

    @Autowired
    private JobService jobservice;


    @GetMapping("jobPosts")
    public List<JobPost> getAllJobPosts(){
        return jobservice.getAllJob();
    }

}
