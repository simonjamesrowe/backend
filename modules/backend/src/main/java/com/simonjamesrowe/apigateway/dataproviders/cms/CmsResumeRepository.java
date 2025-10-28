package com.simonjamesrowe.apigateway.dataproviders.cms;

import com.simonjamesrowe.apigateway.core.model.ResumeData;
import com.simonjamesrowe.apigateway.core.repository.ResumeRepository;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.ProfileResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.model.cms.dto.SocialMediaResponseDTO;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CmsResumeRepository implements ResumeRepository {

    private final ICmsRestApi cmsRestApi;

    @NewSpan("getResumeData")
    @Override
    public ResumeData getResumeData() {
        List<JobResponseDTO> jobs = cmsRestApi.getAllJobs()
                .stream()
                .filter(JobResponseDTO::includeOnResume)
                .toList();

        ProfileResponseDTO profile = cmsRestApi.getProfiles().get(0);

        List<SocialMediaResponseDTO> socialMedias = cmsRestApi.getAllSocialMedias()
                .stream()
                .filter(SocialMediaResponseDTO::includeOnResume)
                .toList();

        List<SkillResponseDTO> skills = cmsRestApi.getAllSkills()
                .stream()
                .filter(SkillResponseDTO::includeOnResume)
                .sorted(Comparator.comparing(SkillResponseDTO::order))
                .toList();

        return buildResumeData(jobs, profile, socialMedias, skills);
    }

    private ResumeData buildResumeData(List<JobResponseDTO> jobs, ProfileResponseDTO profile,
                                     List<SocialMediaResponseDTO> socialMedias, List<SkillResponseDTO> skills) {

        List<ResumeData.Link> links = new ArrayList<>();
        links.add(new ResumeData.Link("www.simonjamesrowe.com"));
        socialMedias.stream()
                .filter(SocialMediaResponseDTO::includeOnResume)
                .map(sm -> new ResumeData.Link(sm.link()))
                .forEach(links::add);

        return new ResumeData(
                profile.name(),
                profile.phoneNumber(),
                profile.primaryEmail(),
                profile.headline(),
                skills.stream().map(this::toSkill).toList(),
                jobs.stream()
                        .filter(job -> !job.education())
                        .sorted(Comparator.comparing(JobResponseDTO::startDate).reversed())
                        .map(this::toJob)
                        .toList(),
                jobs.stream()
                        .filter(JobResponseDTO::education)
                        .map(this::toJob)
                        .toList(),
                links
        );
    }

    private ResumeData.Skill toSkill(SkillResponseDTO skillResponseDTO) {
        return new ResumeData.Skill(
                skillResponseDTO.name(),
                skillResponseDTO.rating()
        );
    }

    private ResumeData.Job toJob(JobResponseDTO response) {
        return new ResumeData.Job(
                response.title(),
                response.company(),
                "https://www.simonjamesrowe.com/jobs/" + response.id(),
                response.startDate(),
                response.endDate(),
                response.shortDescription(),
                response.location()
        );
    }
}