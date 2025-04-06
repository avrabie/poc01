package com.execodex.poc01.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class JobDescription {
    private JobInformation jobInformation;
    private Requirements requirements;
    private List<String> responsibilities;
    private List<String> benefits;


    @Data
    public static class JobInformation {
        private String jobTitle;
        private String company;
        private String location;
        private Boolean remoteOption;
        private String employmentType;
        private String salaryRange;


    }

    @Data
    public static class Requirements {
        private MustHave mustHave;
        private NiceToHave niceToHave;


        @Data
        public static class MustHave {
            private List<String> skills;
            private List<String> technologies;
            private List<String> education;
            private String experienceLevel;
            private String yearsOfExperience;
            private List<String> certifications;


        }

        @Data
        public static class NiceToHave {
            private List<String> skills;
            private List<String> technologies;
            private List<String> certifications;

        }
    }
}