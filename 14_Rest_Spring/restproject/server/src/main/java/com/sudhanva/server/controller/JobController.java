package com.sudhanva.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.server.model.JobPost;
import com.sudhanva.server.service.JobService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class JobController {

    @Autowired
    private JobService jobservice;

    @GetMapping("jobPosts")
    public List<JobPost> getAllJobPosts(){
        return jobservice.getAllJob();
    }

    @GetMapping("jobPost/{id}")
    public JobPost getJobPost(
        @PathVariable int id
    ) {
        return jobservice.getJob(id);
    }
    

    @PostMapping("jobPost")
    public JobPost addJob(@RequestBody JobPost jobPost) {
        jobservice.addJob(jobPost);
        return jobservice.getJob(jobPost.getPostId());
    }
    

    @PutMapping("jobPost")
    public JobPost updateJobPost(
        @RequestBody JobPost jobPost
    ){

        jobservice.updateJob(jobPost);
        return jobservice.getJob(jobPost.getPostId());
    }


    @DeleteMapping("jobPost/{postId}")
    public JobPost deleteJob(
        @PathVariable int postId
    ){
        return jobservice.deleteJob(postId);
    }

}
