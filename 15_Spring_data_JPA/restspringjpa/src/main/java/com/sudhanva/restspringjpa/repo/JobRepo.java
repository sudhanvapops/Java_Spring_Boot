// Talk to DB 
// Only CRUD

package com.sudhanva.restspringjpa.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudhanva.restspringjpa.model.JobPost;
import java.util.List;



// @Repository
// public class JobRepo {

    // Data
    // List<JobPost> jobs = new ArrayList<>(
    //         List.of(
    //                 new JobPost(1, "Java Backend Developer",
    //                         "Build REST APIs", 2,
    //                         List.of("Java", "Spring")),

    //                 new JobPost(2, "Frontend Developer",
    //                         "Create UI", 1,
    //                         List.of("React", "JS")),

    //                 new JobPost(3, "DevOps Engineer",
    //                         "Deploy apps", 3,
    //                         List.of("Docker", "AWS"))));

    // public List<JobPost> getAllJobs() {
    //     return jobs;
    // }

    // public void addJob(JobPost job) {
    //     jobs.add(job);
    //     System.out.println("Job Lists: \n");
    //     System.out.println(jobs);
    // }

    // public JobPost getJob(int postId) {

    //     for (JobPost job : jobs) {
    //         if (job.getPostId() == postId) {
    //             return job;
    //         }
    //     }

    //     return null;
    // }

    // public JobPost updateJobPost(JobPost jobPost) {

    //     for (JobPost job : jobs) {
    //         if (job.getPostId() == jobPost.getPostId()) {
    //             job.setPostDesc(jobPost.getPostDesc());
    //             job.setPostProfile(jobPost.getPostProfile());
    //             job.setPostTechStack(jobPost.getPostTechStack());
    //             job.setReqExperience(jobPost.getReqExperience());
    //             return job;
    //         }
    //     }

    //     return null;
    // }

    // public JobPost deleteJob(int postId) {
    //     for (JobPost job : jobs) {
    //         if (job.getPostId() == postId) {
    //             JobPost post = job;
    //             jobs.remove(job);
    //             return post;
    //         }
    //     }
    //     return null;
    // }

// }


@Repository
public interface JobRepo extends JpaRepository<JobPost,Integer> {

    List<JobPost> findByPostProfileContainingOrPostDescContaining(String postProfile,String postDesc);

    List<JobPost> findByPostTechStack(String name);

}
