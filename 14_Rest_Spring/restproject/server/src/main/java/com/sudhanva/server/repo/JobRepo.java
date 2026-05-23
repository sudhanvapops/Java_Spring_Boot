// Talk to DB 
// Only CRUD

package com.sudhanva.server.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sudhanva.server.model.JobPost;

@Repository
public class JobRepo {

    // Data
    List<JobPost> jobs = new ArrayList<>(
            List.of(
                    new JobPost(1, "Java Backend Developer",
                            "Build REST APIs", 2,
                            List.of("Java", "Spring")),

                    new JobPost(2, "Frontend Developer",
                            "Create UI", 1,
                            List.of("React", "JS")),

                    new JobPost(3, "DevOps Engineer",
                            "Deploy apps", 3,
                            List.of("Docker", "AWS"))));




    public List<JobPost> getAllJobs(){
        return jobs;
    }


    public void addJob(JobPost job){
        jobs.add(job);
        System.out.println("Job Lists: \n");
        System.out.println(jobs);
    }


}
