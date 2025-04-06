package com.execodex.poc01.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDescription {
    private String jobTitle;
    private RequiredSkills requiredSkills;
    private RequiredExperience requiredExperience;
    private PreferredSkills preferredSkills;
    private SoftSkills softSkills;
    private Education education;
    private List<String> responsibilities;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequiredSkills {
        private List<String> programmingLanguages;
        private List<String> frameworks;
        private List<String> architecture;
        private List<String> apiDevelopment;
        private List<String> containerization;
        private List<String> orchestration;
        private List<String> databases;
        private List<String> developmentMethodologies;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequiredExperience {
        private int yearsOfExperience;
        private List<String> experienceWith;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreferredSkills {
        private List<String> cloudPlatforms;
        private List<String> ciCdTools;
        private List<String> versionControl;
        private List<String> monitoringTools;
        private List<String> security;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SoftSkills {
        private List<String> skills;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Education {
        private String degree;
        private List<String> field;
    }
}
