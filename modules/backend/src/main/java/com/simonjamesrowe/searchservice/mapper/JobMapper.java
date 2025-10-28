package com.simonjamesrowe.searchservice.mapper;

import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;

public final class JobMapper {

    private JobMapper() {
        // Utility class
    }

    public static IndexSiteRequest toIndexSiteRequest(JobResponseDTO job) {
        String imageUrl = "";
        if (job.companyImage() != null) {
            if (job.companyImage().formats() != null) {
                if (job.companyImage().formats().thumbnail() != null) {
                    imageUrl = job.companyImage().formats().thumbnail().url();
                } else if (job.companyImage().formats().small() != null) {
                    imageUrl = job.companyImage().formats().small().url();
                } else if (job.companyImage().formats().medium() != null) {
                    imageUrl = job.companyImage().formats().medium().url();
                } else if (job.companyImage().formats().large() != null) {
                    imageUrl = job.companyImage().formats().large().url();
                }
            } else {
                imageUrl = job.companyImage().url();
            }
        }

        String endYear = job.endDate() != null ? String.valueOf(job.endDate().getYear()) : "Present";
        String jobName = String.format("%s (%s) - %s - %s",
            job.title(),
            job.company(),
            job.startDate().getYear(),
            endYear
        );

        return new IndexSiteRequest(
            "job_" + job.id(),
            "/jobs/" + job.id(),
            jobName,
            "Jobs",
            imageUrl,
            job.shortDescription(),
            job.longDescription() != null ? job.longDescription() : ""
        );
    }
}