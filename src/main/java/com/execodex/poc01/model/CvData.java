package com.execodex.poc01.model;


import lombok.Data;

import java.util.List;

@Data
public class CvData {
    private String name;
    private String email;
    private String phone;
    private List<String> skills;
    private List<Education> education;
    private List<WorkExperience> workExperience;
    private String summary;

    // Getters and setters
    @Data
    public static class Education {
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private String period;

        // Getters and setters
    }

    @Data
    public static class WorkExperience {
        private String company;
        private String position;
        private String period;
        private List<String> responsibilities;
        private List<String> technologies;

        // Getters and setters
    }
}