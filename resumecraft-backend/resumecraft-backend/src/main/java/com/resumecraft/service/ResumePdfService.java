
package com.resumecraft.service;

import com.resumecraft.model.ResumeRequest;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ResumePdfService {

    // ===== Modern Professional Navy Theme =====

    private static final Color NAVY = new Color(31, 55, 80);
    private static final Color BLACK = new Color(30, 30, 30);
    private static final Color GRAY = new Color(90, 98, 108);
    private static final Color LIGHT_GRAY = new Color(190, 198, 207);

    private static final Font NAME_FONT =
            new Font(Font.HELVETICA, 23f, Font.BOLD, NAVY);

    private static final Font TITLE_FONT =
            new Font(Font.HELVETICA, 11f, Font.NORMAL, GRAY);

    private static final Font SECTION_FONT =
            new Font(Font.HELVETICA, 11.5f, Font.BOLD, NAVY);

    private static final Font ENTRY_FONT =
            new Font(Font.HELVETICA, 10f, Font.BOLD, BLACK);

    private static final Font BODY_FONT =
            new Font(Font.HELVETICA, 9.5f, Font.NORMAL, BLACK);

    private static final Font CONTACT_FONT =
            new Font(Font.HELVETICA, 8.5f, Font.NORMAL, GRAY);

    private static final Font DETAIL_FONT =
            new Font(Font.HELVETICA, 8.5f, Font.ITALIC, GRAY);

    // ===== PDF Generation =====

    public byte[] generatePdf(ResumeRequest request) throws Exception {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Resume data is required."
            );
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document = new Document(
                PageSize.A4,
                42,
                42,
                32,
                32
        );

        PdfWriter writer = PdfWriter.getInstance(
                document,
                outputStream
        );

        document.open();

        try {
            addHeader(document, writer, request);
            addSummary(document, writer, request.summary());
            addSkills(document, writer, request.skills());

            // Optional: omitted when no experience is provided.
            addWorkExperience(
                    document,
                    writer,
                    request.workExperience()
            );

            addProjects(document, writer, request.projects());
            addEducation(document, writer, request.education());

            addCertifications(
                    document,
                    writer,
                    request.certifications()
            );

            addLanguages(document, writer, request.languages());

        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    // ===== Header =====

    private void addHeader(
            Document document,
            PdfWriter writer,
            ResumeRequest request
    ) throws Exception {

        Paragraph name = new Paragraph(
                valueOrDefault(request.fullName(), "Your Name"),
                NAME_FONT
        );

        name.setAlignment(Element.ALIGN_CENTER);
        name.setLeading(27);
        name.setSpacingAfter(3);
        document.add(name);

        if (hasText(request.jobTitle())) {
            Paragraph title = new Paragraph(
                    request.jobTitle().trim(),
                    TITLE_FONT
            );

            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(7);
            document.add(title);
        }

        Paragraph contact = new Paragraph();
        contact.setAlignment(Element.ALIGN_CENTER);
        contact.setFont(CONTACT_FONT);
        contact.setLeading(11);

        addContact(contact, request.location());
        addContact(contact, request.phone());
        addContact(contact, request.email());

        if (contact.size() > 0) {
            contact.setSpacingAfter(9);
            document.add(contact);
        }

        addDivider(document, writer);
    }

    private void addContact(Paragraph paragraph, String value) {

        if (!hasText(value)) {
            return;
        }

        if (paragraph.size() > 0) {
            paragraph.add(new Chunk("  |  ", CONTACT_FONT));
        }

        paragraph.add(new Chunk(value.trim(), CONTACT_FONT));
    }

    // ===== Professional Summary =====

    private void addSummary(
            Document document,
            PdfWriter writer,
            String summary
    ) throws Exception {

        if (!hasText(summary)) {
            return;
        }

        addSectionTitle(document, writer, "Professional Summary");

        Paragraph paragraph = new Paragraph(
                summary.trim(),
                BODY_FONT
        );

        paragraph.setLeading(13);
        paragraph.setSpacingBefore(3);
        paragraph.setSpacingAfter(3);
        document.add(paragraph);
    }

    // ===== Technical Skills =====

    private void addSkills(
            Document document,
            PdfWriter writer,
            String skills
    ) throws Exception {

        if (!hasText(skills)) {
            return;
        }

        addSectionTitle(document, writer, "Technical Skills");

        Paragraph paragraph = new Paragraph();
        paragraph.setFont(BODY_FONT);
        paragraph.setLeading(13);

        for (String skillItem : skills.split(",")) {
            String skill = skillItem.trim();

            if (skill.isEmpty()) {
                continue;
            }

            if (paragraph.size() > 0) {
                paragraph.add(new Chunk("  •  ", BODY_FONT));
            }

            paragraph.add(new Chunk(skill, BODY_FONT));
        }

        if (paragraph.size() > 0) {
            paragraph.setSpacingBefore(3);
            paragraph.setSpacingAfter(3);
            document.add(paragraph);
        }
    }

    // ===== Optional Experience =====

    private void addWorkExperience(
            Document document,
            PdfWriter writer,
            List<ResumeRequest.WorkExperience> entries
    ) throws Exception {

        // Do not show the section for null, empty or blank entries.
        if (!hasAnyExperience(entries)) {
            return;
        }

        addSectionTitle(document, writer, "Experience");

        for (ResumeRequest.WorkExperience item : entries) {

            if (item == null || (
                    !hasText(item.jobTitle())
                            && !hasText(item.company())
                            && !hasText(item.description())
                            && !hasText(item.startDate())
                            && !hasText(item.endDate())
            )) {
                continue;
            }

            // Build the complete bold heading before adding it.
            Paragraph heading = new Paragraph();
            heading.setFont(ENTRY_FONT);
            heading.setLeading(12);

            addText(heading, item.jobTitle(), ENTRY_FONT);

            if (hasText(item.jobTitle())
                    && hasText(item.company())) {
                heading.add(new Chunk(" — ", ENTRY_FONT));
            }

            addText(heading, item.company(), ENTRY_FONT);

            if (heading.size() > 0) {
                heading.setSpacingAfter(2);
                document.add(heading);
            }

            String dates = joinNonEmpty(
                    item.startDate(),
                    item.endDate(),
                    " – "
            );

            if (hasText(dates)) {
                Paragraph dateLine = new Paragraph(
                        dates,
                        DETAIL_FONT
                );

                dateLine.setSpacingAfter(3);
                document.add(dateLine);
            }

            if (hasText(item.description())) {
                Paragraph description = new Paragraph(
                        item.description().trim(),
                        BODY_FONT
                );

                description.setLeading(12);
                description.setIndentationLeft(8);
                description.setSpacingAfter(7);
                document.add(description);
            }
        }
    }

    private boolean hasAnyExperience(
            List<ResumeRequest.WorkExperience> entries
    ) {

        if (entries == null || entries.isEmpty()) {
            return false;
        }

        return entries.stream().anyMatch(item ->
                item != null && (
                        hasText(item.jobTitle())
                                || hasText(item.company())
                                || hasText(item.description())
                                || hasText(item.startDate())
                                || hasText(item.endDate())
                )
        );
    }

    // ===== Projects =====

    private void addProjects(
            Document document,
            PdfWriter writer,
            List<ResumeRequest.Project> entries
    ) throws Exception {

        if (entries == null || entries.stream().noneMatch(item ->
                item != null && (
                        hasText(item.name())
                                || hasText(item.description())
                ))) {
            return;
        }

        addSectionTitle(document, writer, "Projects");

        for (ResumeRequest.Project item : entries) {

            if (item == null || (
                    !hasText(item.name())
                            && !hasText(item.description())
            )) {
                continue;
            }

            Paragraph heading = new Paragraph(
            		item.name() == null ? "" : item.name().trim(),
                    ENTRY_FONT
            );

            heading.setLeading(12);
            heading.setSpacingAfter(2);
            document.add(heading);

            if (hasText(item.description())) {
                Paragraph description = new Paragraph(
                        item.description().trim(),
                        BODY_FONT
                );

                description.setLeading(12);
                description.setIndentationLeft(8);
                description.setSpacingAfter(7);
                document.add(description);
            }
        }
    }

    // ===== Education =====


private void addEducation(
        Document document,
        PdfWriter writer,
        List<ResumeRequest.Education> entries
) throws Exception {

    if (entries == null || entries.stream().noneMatch(item ->
            item != null && (
                    hasText(item.degree())
                            || hasText(item.institution())
                            || hasText(item.year())
                            || hasText(item.grade())
            ))) {
        return;
    }

    addSectionTitle(document, writer, "Education");

    for (ResumeRequest.Education item : entries) {

        if (item == null || (
                !hasText(item.degree())
                        && !hasText(item.institution())
                        && !hasText(item.year())
                        && !hasText(item.grade())
        )) {
            continue;
        }

        // First row: Degree | College Name | Year
        Paragraph heading = new Paragraph();
        heading.setFont(ENTRY_FONT);
        heading.setLeading(12);

        addText(heading, item.degree(), ENTRY_FONT);

        if (hasText(item.degree()) && hasText(item.institution())) {
            heading.add(new Chunk(" — ", ENTRY_FONT));
        }

        addText(heading, item.institution(), ENTRY_FONT);

        if (hasText(item.year())) {
            if (hasText(item.degree()) || hasText(item.institution())) {
                heading.add(new Chunk("  |  ", ENTRY_FONT));
            }
            addText(heading, item.year(), ENTRY_FONT);
        }

        if (heading.size() > 0) {
            heading.setSpacingAfter(2);
            document.add(heading);
        }

     // Second row: Percentage (if provided)
        if (hasText(item.grade())) {
            Paragraph detailLine = new Paragraph(
                    "Percentage/CGPA: " + item.grade(),
                    DETAIL_FONT
            );
            detailLine.setSpacingAfter(7);
            document.add(detailLine);
        } else {
            document.add(Chunk.NEWLINE);
        }
    }
}

    // ===== Certifications =====

    private void addCertifications(
            Document document,
            PdfWriter writer,
            List<ResumeRequest.Certification> entries
    ) throws Exception {

        if (entries == null || entries.stream().noneMatch(item ->
                item != null && (
                        hasText(item.name())
                                || hasText(item.organization())
                                || hasText(item.issueDate())
                                || hasText(item.credentialId())
                                || hasText(item.credentialUrl())
                ))) {
            return;
        }

        addSectionTitle(document, writer, "Certifications");

        for (ResumeRequest.Certification item : entries) {

            if (item == null || (
                    !hasText(item.name())
                            && !hasText(item.organization())
                            && !hasText(item.issueDate())
                            && !hasText(item.credentialId())
                            && !hasText(item.credentialUrl())
            )) {
                continue;
            }

            // Name/organization on the left, date on the right.
            PdfPTable headingTable = new PdfPTable(
                    new float[]{4f, 1f}
            );

            headingTable.setWidthPercentage(100);
            headingTable.setSpacingAfter(5);

            Paragraph heading = new Paragraph();
            heading.setFont(ENTRY_FONT);
            heading.setLeading(12);

            addText(heading, item.name(), ENTRY_FONT);

            if (hasText(item.name())
                    && hasText(item.organization())) {
                heading.add(new Chunk(" — ", ENTRY_FONT));
            }

            addText(heading, item.organization(), ENTRY_FONT);

            PdfPCell nameCell = new PdfPCell(heading);
            nameCell.setBorder(Rectangle.NO_BORDER);
            nameCell.setPadding(0);
            nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            headingTable.addCell(nameCell);

            Paragraph date = new Paragraph(
                    hasText(item.issueDate())
                            ? item.issueDate().trim()
                            : "",
                    DETAIL_FONT
            );

            date.setAlignment(Element.ALIGN_RIGHT);

            PdfPCell dateCell = new PdfPCell(date);
            dateCell.setBorder(Rectangle.NO_BORDER);
            dateCell.setPadding(0);
            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            headingTable.addCell(dateCell);

            document.add(headingTable);

            if (hasText(item.credentialId())) {
                Paragraph credentialId = new Paragraph(
                        "Credential ID: " + item.credentialId().trim(),
                        CONTACT_FONT
                );

                credentialId.setSpacingAfter(2);
                document.add(credentialId);
            }

            if (hasText(item.credentialUrl())) {
                Paragraph credentialUrl = new Paragraph(
                        "Credential URL: " + item.credentialUrl().trim(),
                        CONTACT_FONT
                );

                credentialUrl.setSpacingAfter(5);
                document.add(credentialUrl);
            }
        }
    }

    // ===== Languages =====


private void addLanguages(
        Document document,
        PdfWriter writer,
        List<ResumeRequest.Language> entries
) throws Exception {

    if (entries == null || entries.stream().noneMatch(item ->
            item != null && (
                    hasText(item.name())
                            || hasText(item.proficiency())
            ))) {
        return;
    }

    addSectionTitle(document, writer, "Languages");

    for (ResumeRequest.Language item : entries) {

        if (item == null || (
                !hasText(item.name())
                        && !hasText(item.proficiency())
        )) {
            continue;
        }

        Paragraph language = new Paragraph();
        language.setLeading(14);
        language.setSpacingAfter(2);

        if (hasText(item.name())) {
            language.add(new Chunk(
                    item.name().trim(),
                    ENTRY_FONT
            ));
        }

        if (hasText(item.name())
                && hasText(item.proficiency())) {
            language.add(new Chunk(" — ", BODY_FONT));
        }

        if (hasText(item.proficiency())) {
            language.add(new Chunk(
                    item.proficiency().trim(),
                    BODY_FONT
            ));
        }

        document.add(language);
    }
}


    // ===== Section Heading and Divider =====

    private void addSectionTitle(
            Document document,
            PdfWriter writer,
            String title
    ) throws Exception {

        Paragraph heading = new Paragraph(
                title.toUpperCase(),
                SECTION_FONT
        );

        heading.setAlignment(Element.ALIGN_LEFT);
        heading.setLeading(14);
        heading.setSpacingBefore(7);
        heading.setSpacingAfter(3);

        document.add(heading);
        addDivider(document, writer);
    }

    private void addDivider(
            Document document,
            PdfWriter writer
    ) throws Exception {

        LineSeparator line = new LineSeparator();

        line.setLineColor(LIGHT_GRAY);
        line.setLineWidth(0.8f);
        line.setPercentage(100);
        line.setAlignment(Element.ALIGN_LEFT);
        line.setOffset(-1f);

        Paragraph divider = new Paragraph(new Chunk(line));
        divider.setLeading(1f);
        divider.setSpacingBefore(0);
        divider.setSpacingAfter(5);

        document.add(divider);
    }

    // ===== Shared Helpers =====

    private void addText(
            Paragraph paragraph,
            String value,
            Font font
    ) {
        if (hasText(value)) {
            paragraph.add(new Chunk(value.trim(), font));
        }
    }

    private String joinNonEmpty(
            String first,
            String second,
            String separator
    ) {

        boolean hasFirst = hasText(first);
        boolean hasSecond = hasText(second);

        if (hasFirst && hasSecond) {
            return first.trim() + separator + second.trim();
        }

        if (hasFirst) {
            return first.trim();
        }

        if (hasSecond) {
            return second.trim();
        }

        return "";
    }

    private String valueOrDefault(
            String value,
            String fallback
    ) {
        return hasText(value) ? value.trim() : fallback;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
