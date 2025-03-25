package com.execodex.poc01.service;

import com.execodex.poc01.model.CvData;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CvParserService {

    private static final List<String> TECH_SKILLS = Arrays.asList(
            "Java", "Spring", "Python", "JavaScript", "React", "Angular",
            "SQL", "NoSQL", "Docker", "Kubernetes", "AWS", "Azure",
            "Spring-Boot", "Spring-Security", "Reactor Webflux", "Spring-Data",
            "JMS", "Spring MVC", "JAX-RS", "Spring Cloud", "Quarkus", "Hibernate"
    );

    public Mono<CvData> parseCvText(String text) {
        return Mono.fromCallable(() -> {
            CvData cvData = new CvData();

            // Extract basic information
            extractContactInfo(text, cvData);

            // Extract skills
            extractSkills(text, cvData);

            // Extract education
            extractEducation(text, cvData);

            // Extract work experience
            extractWorkExperience(text, cvData);

            // Extract summary
            extractSummary(text, cvData);

            return cvData;
        });
    }

    private void extractContactInfo(String text, CvData cvData) {
        // Name (first line typically contains name)
        String[] lines = text.split("\\r?\\n");
        if (lines.length > 0) {
            cvData.setName(lines[0].trim());
        }

        // Email
        Matcher emailMatcher = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}")
                .matcher(text);
        if (emailMatcher.find()) {
            cvData.setEmail(emailMatcher.group());
        }

        // Phone (international format support)
        Matcher phoneMatcher = Pattern.compile("(\\+\\d{1,3}[-.\\s]?)?\\d{2,3}[-.\\s]?\\d{3,4}[-.\\s]?\\d{3,4}")
                .matcher(text);
        if (phoneMatcher.find()) {
            cvData.setPhone(phoneMatcher.group());
        }
    }

    private void extractSkills(String text, CvData cvData) {
        List<String> foundSkills = TECH_SKILLS.stream()
                .filter(skill -> text.toLowerCase().contains(skill.toLowerCase()))
                .collect(Collectors.toList());
        cvData.setSkills(foundSkills);
    }

    private void extractEducation(String text, CvData cvData) {
        List<CvData.Education> educationList = new ArrayList<>();

        // Look for education section
        String educationPattern = "(?i)(Education:|Education\\s*\\n)(.*?)(?=\\n\\s*\\n|$)";
        Matcher sectionMatcher = Pattern.compile(educationPattern, Pattern.DOTALL).matcher(text);

        if (sectionMatcher.find()) {
            String educationSection = sectionMatcher.group(2);

            // Split into individual education entries
            String[] entries = educationSection.split("\\n\\s*•\\s*");

            for (String entry : entries) {
                if (entry.trim().isEmpty()) continue;

                CvData.Education education = new CvData.Education();

                // Extract institution (first line)
                String[] lines = entry.split("\\r?\\n");
                if (lines.length > 0) {
                    education.setInstitution(lines[0].trim());
                }

                // Extract degree and field of study
                if (lines.length > 1) {
                    String degreeLine = lines[1].trim();
                    if (degreeLine.contains("–")) {
                        String[] parts = degreeLine.split("–", 2);
                        education.setDegree(parts[0].trim());
                        education.setFieldOfStudy(parts[1].trim());
                    } else {
                        education.setDegree(degreeLine);
                    }
                }

                // Extract period (last line)
                if (lines.length > 2) {
                    education.setPeriod(lines[lines.length - 1].trim());
                }

                educationList.add(education);
            }
        }

        cvData.setEducation(educationList);
    }

    private void extractWorkExperience(String text, CvData cvData) {
        List<CvData.WorkExperience> experiences = new ArrayList<>();

        // Look for experience section
        String experiencePattern = "(?i)(Experience:|Work Experience:|Employment History:)(.*?)(?=\\n\\s*\\n|$)";
        Matcher sectionMatcher = Pattern.compile(experiencePattern, Pattern.DOTALL).matcher(text);

        if (sectionMatcher.find()) {
            String experienceSection = sectionMatcher.group(2);

            // Split into individual job entries
            String[] entries = experienceSection.split("\\n\\s*(?=\\w+\\s+\\w+,\\.*\\s+\\w+\\s+\\d{4}\\s*-)");

            for (String entry : entries) {
                if (entry.trim().isEmpty()) continue;

                CvData.WorkExperience experience = new CvData.WorkExperience();
                List<String> responsibilities = new ArrayList<>();
                List<String> technologies = new ArrayList<>();

                // Split into lines
                String[] lines = entry.split("\\r?\\n");

                // First line contains company and period
                if (lines.length > 0) {
                    String firstLine = lines[0].trim();
                    String[] parts = firstLine.split(",");
                    if (parts.length > 0) {
                        experience.setCompany(parts[0].trim());
                    }
                    if (parts.length > 1) {
                        experience.setPeriod(parts[1].trim());
                    }
                }

                // Second line contains position
                if (lines.length > 1) {
                    experience.setPosition(lines[1].trim().replace("Role:", "").trim());
                }

                // Parse bullet points
                for (int i = 2; i < lines.length; i++) {
                    String line = lines[i].trim();
                    if (line.startsWith("•") || line.startsWith("-")) {
                        responsibilities.add(line.substring(1).trim());
                    } else if (line.contains("Tech used:") || line.contains("Technologies used:")) {
                        String techPart = line.split(":")[1].trim();
                        technologies.addAll(Arrays.asList(techPart.split(",\\s*")));
                    }
                }

                experience.setResponsibilities(responsibilities);
                experience.setTechnologies(technologies);
                experiences.add(experience);
            }
        }

        cvData.setWorkExperience(experiences);
    }

    private void extractSummary(String text, CvData cvData) {
        // Look for summary section
        String summaryPattern = "(?i)(Summary:|About:|Profile:)(.*?)(?=\\n\\s*\\n|$)";
        Matcher matcher = Pattern.compile(summaryPattern, Pattern.DOTALL).matcher(text);

        if (matcher.find()) {
            cvData.setSummary(matcher.group(2).trim());
        }
    }
}