package com.sudhanva.restspringjpa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.restspringjpa.model.JobPost;
import com.sudhanva.restspringjpa.service.JobService;

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

    @GetMapping("load")
    public List<JobPost> loadJobs(){
        return jobservice.load();
    }

    @GetMapping("jobPost/{id}")
    public JobPost getJobPost(
        @PathVariable int id
    ) {
        return jobservice.getJob(id);
    }

    @GetMapping("jobPosts/keyword/{keyword}")
    public List<JobPost> searchByKeyword(
        @PathVariable String keyword){
            return jobservice.search(keyword);
    }

    @GetMapping("jobPosts/tech/{keyword}")
    public List<JobPost> searchByTech(
        @PathVariable String keyword){
            return jobservice.searchByTech(keyword);
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
 