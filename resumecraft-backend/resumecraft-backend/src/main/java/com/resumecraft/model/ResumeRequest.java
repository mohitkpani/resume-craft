
package com.resumecraft.model;

import java.util.List;

public record ResumeRequest(

        // Personal information
        String fullName,
        String jobTitle,
        String email,
        String phone,
        String location,

        // Summary and skills
        String summary,
        String skills,

        // Resume sections
        List<Education> education,
        List<Project> projects,
        List<WorkExperience> workExperience,
        List<Certification> certifications,
        List<Language> languages

) {

    public record Education(
            String degree,
            String institution,
            String year,
            String grade
    ) {
    }

    public record Project(
            String name,
            String description
    ) {
    }

    public record WorkExperience(
            String jobTitle,
            String company,
            String description,
            String startDate,
            String endDate
    ) {
    }

    public record Certification(
            String name,
            String organization,
            String issueDate,
            String credentialId,
            String credentialUrl
    ) {
    }

    public record Language(
            String name,
            String proficiency
    ) {
    }
}
