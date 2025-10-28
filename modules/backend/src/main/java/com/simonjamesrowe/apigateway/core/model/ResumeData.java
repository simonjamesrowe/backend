package com.simonjamesrowe.apigateway.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeData {
    private String name;
    private String phone;
    private String email;
    private String headline;
    private List<Skill> skills;
    private List<Job> jobs;
    private List<Job> education;
    private List<Link> links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Skill {
        private String name;
        private Double rating;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Job {
        private String role;
        private String company;
        private String link;
        private LocalDate start;
        private LocalDate end;
        private String shortDescription;
        private String location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Link {
        private String url;
    }
}