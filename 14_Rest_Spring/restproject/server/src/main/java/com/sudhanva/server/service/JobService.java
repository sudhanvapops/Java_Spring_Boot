package com.sudhanva.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.server.model.JobPost;
import com.sudhanva.server.repo.JobRepo;



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

    public JobPost getJob(int postId) {
        return jobrepo.getJob(postId);
    }
}
