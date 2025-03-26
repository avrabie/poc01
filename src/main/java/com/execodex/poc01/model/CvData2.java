package com.execodex.poc01.model;


import lombok.Data;

import java.util.List;

import lombok.Data;
import java.util.List;

@Data
public class CvData2 {
    private PersonalInformation personal_information;
    private Skills skills;
    private List<String> summary;
    private List<Experience> experience;
    private List<Education> education;
    private List<Publication> publications;

    @Data
    public static class PersonalInformation {
        private String name;
        private String title;
        private List<String> phone_numbers;
        private String email;
        private SocialMedia social_media;

        @Data
        public static class SocialMedia {
            private String stackoverflow;
            private String github;
        }
    }

    @Data
    public static class Skills {
        private List<String> programming_languages;
        private List<String> java_frameworks;
        private List<String> technologies;
        private List<String> package_management;
        private List<String> version_control;
        private List<String> databases;
        private List<String> containerization;
        private List<String> orchestration;
    }

    @Data
    public static class Experience {
        private String company;
        private String start_date;
        private String end_date;
        private String dates;
        private String client;
        private String role;
        private List<String> description;
    }

    @Data
    public static class Education {
        private String institution;
        private String degree;
        private String thesis;
        private String dates;
        private String concentration;
    }

    @Data
    public static class Publication {
        private String title;
        private String journal;
        private String pages;
        private String isbn;
        private String location;
        private String year;
    }
}