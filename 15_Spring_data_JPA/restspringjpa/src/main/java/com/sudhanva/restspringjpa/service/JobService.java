package com.sudhanva.restspringjpa.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.restspringjpa.model.JobPost;
import com.sudhanva.restspringjpa.repo.JobRepo;

import jakarta.transaction.Transactional;

@Service
public class JobService {

    @Autowired
    private JobRepo jobrepo;

    // private final JobRepo jobRepo;
    // public JobService(JobRepo jobRepo){
    // this.jobRepo = jobRepo;
    // }

    public void addJob(JobPost job) {
        jobrepo.save(job);
    }

    public List<JobPost> getAllJob() {
        return jobrepo.findAll();
    }

    public JobPost getJob(int postId) {
        return jobrepo.findById(postId).orElseThrow(() -> new RuntimeException("Not found: " + postId));
    }

    public void updateJob(JobPost jobPost) {
        jobrepo.save(jobPost);
    }

    // To ensure find and delete happens in the same transaction
    @Transactional
    public JobPost deleteJob(int postId) {

        JobPost jobPost = jobrepo.findById(postId).orElseThrow(() -> new RuntimeException("Job not found"));

        jobrepo.delete(jobPost);

        return jobPost;
    }

    public List<JobPost> search(String keyword){
        return jobrepo.findByPostProfileContainingOrPostDescContaining(keyword,keyword);
    }

    public List<JobPost> searchByTech(String keyword){
        return jobrepo.findByPostTechStack(keyword);
    }

    public List<JobPost> load() {
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
                                List.of("Docker", "AWS")),

                        new JobPost(4, "Full Stack Developer",
                                "Develop end-to-end web applications", 3,
                                List.of("Java", "Spring Boot", "React", "MySQL")),

                        new JobPost(5, "Python Developer",
                                "Build backend services and automation scripts", 2,
                                List.of("Python", "Django", "PostgreSQL")),

                        new JobPost(6, "Data Analyst",
                                "Analyze business data and generate reports", 1,
                                List.of("SQL", "Excel", "Power BI")),

                        new JobPost(7, "Machine Learning Engineer",
                                "Develop and deploy ML models", 4,
                                List.of("Python", "TensorFlow", "Scikit-learn")),

                        new JobPost(8, "Android Developer",
                                "Develop Android mobile applications", 2,
                                List.of("Java", "Kotlin", "Android SDK")),

                        new JobPost(9, "Cloud Engineer",
                                "Manage cloud infrastructure and services", 3,
                                List.of("AWS", "Terraform", "Linux")),

                        new JobPost(10, "QA Automation Engineer",
                                "Write automated test cases and improve quality", 2,
                                List.of("Selenium", "Java", "TestNG")),

                        new JobPost(11, "Database Administrator",
                                "Manage and optimize database systems", 4,
                                List.of("MySQL", "PostgreSQL", "MongoDB")),

                        new JobPost(12, "Cyber Security Analyst",
                                "Monitor and secure organizational systems", 3,
                                List.of("Network Security", "SIEM", "Linux")),

                        new JobPost(13, "Software Engineer Intern",
                                "Assist in software development projects", 0,
                                List.of("Java", "Git", "SQL")),

                        new JobPost(14, "Backend Engineer",
                                "Design scalable microservices", 5,
                                List.of("Java", "Spring Boot", "Kafka", "Docker")),

                        new JobPost(15, "Site Reliability Engineer",
                                "Maintain system reliability and performance", 4,
                                List.of("Kubernetes", "Docker", "AWS", "Prometheus"))));

        return jobrepo.saveAll(jobs);
    }


}
