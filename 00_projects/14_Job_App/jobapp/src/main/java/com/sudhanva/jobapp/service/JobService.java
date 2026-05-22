package com.sudhanva.jobapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.jobapp.model.JobPost;
import com.sudhanva.jobapp.repo.JobRepo;



@Service
public class JobService {


    @Autowired
    private JobRepo jobrepo;
    
    public void addJob(JobPost job){
        jobrepo.addJob(job);
    }

    public List<JobPost> getAllJob(){
        return jobrepo.getAllJobs();
    }
}
