package com.sudhanva.jobapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.sudhanva.jobapp.model.JobPost;
import com.sudhanva.jobapp.service.JobService;



@Controller
public class JobController {

    @Autowired
    private JobService jobService;
    
    @GetMapping({"/","home"})
    public String home(){
        return "home";
    }

    @GetMapping({"addjob"})
    public String addJob(){
        return "addjob";
    }

    @PostMapping("handleForm")
    // Spring automatically creates and fills jobPost from form fields (data binding).
    public String handleForm(JobPost jobPost){
        jobService.addJob(jobPost);
        return "success";
    }

    @GetMapping("viewalljobs")
    public String viewJobs(Model m) {
        List<JobPost> jobs = jobService.getAllJob();
        // In Views we are accepting thier in the name jobPosts
        m.addAttribute("jobPosts", jobs);
        return "viewalljobs";
    }
    

}
