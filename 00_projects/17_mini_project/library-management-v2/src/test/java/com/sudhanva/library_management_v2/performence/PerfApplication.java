package com.sudhanva.library_management_v2.performence;

import org.springframework.boot.SpringApplication;

import com.sudhanva.library_management_v2.LibraryManagementV2Application;


// This is used to start my lms app with additional test configuration
// why not using @Test Anotations
// this is a application Launcher not test class
public class PerfApplication {
    
    public static void main(String[] args) {

        // From: using the existing aplication 
        // existsing: my lms in main class
        SpringApplication.from(LibraryManagementV2Application::main)
            // with this configuration
            .with(PerfContainerConfig.class)
            // This will use the application-perf.yml
            // in addiation to any other active profile
            .withAdditionalProfiles("perf")
            // Run the application and start the application context
            // with args
            .run(args);
    }
}
