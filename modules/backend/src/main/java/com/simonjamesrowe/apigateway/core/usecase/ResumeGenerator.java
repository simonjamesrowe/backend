package com.simonjamesrowe.apigateway.core.usecase;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.simonjamesrowe.apigateway.core.model.ResumeData;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public final class ResumeGenerator {

    private ResumeGenerator() {
        // Utility class
    }

    private static final float FONT_7_PT = 7f;
    private static final float FONT_8_PT = 8f;
    private static final float FONT_9_PT = 9f;
    private static final float SKILLS_MARGIN_LEFT = 35f;

    private static final FontProgram FONT_PROGRAM;

    static {
        try {
            FONT_PROGRAM = FontProgramFactory.createFont(
                    new ClassPathResource("OpenSans-Regular.ttf").getInputStream().readAllBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load font", e);
        }
    }

    public static byte[] generate(ResumeData data) {
        PdfFont font = PdfFontFactory.createFont(FONT_PROGRAM, PdfEncodings.UTF8, EmbeddingStrategy.PREFER_EMBEDDED);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();
        Document document = new Document(pdfDoc);
        document.setFont(font);

        addSideBar(document, data);
        addExperience(document, data);
        addHeadline(document, data);

        pdfDoc.close();
        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    private static void addExperience(Document document, ResumeData data) {
        Div div = new Div();
        div.setHeight(UnitValue.createPointValue(842f));
        div.setWidth(UnitValue.createPointValue(380f));
        div.setFixedPosition(176f, 0f, UnitValue.createPointValue(380f));

        div.add(blank());
        div.add(employmentHeading("Employment History"));
        data.getJobs().forEach(job -> div.add(jobBlock(job)));

        if (!data.getEducation().isEmpty()) {
            div.add(employmentHeading("Education"));
            data.getEducation().forEach(education -> div.add(jobBlock(education)));
        }

        document.add(div);
    }

    private static Div jobBlock(ResumeData.Job job) {
        Div jobDiv = new Div();
        jobDiv.setMarginBottom(10f);
        jobDiv.setMarginRight(20f);
        jobDiv.setMarginLeft(10f);

        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100f));
        table.setBorder(Border.NO_BORDER);

        Cell titleCell = new Cell();
        titleCell.setWidth(UnitValue.createPercentValue(80f));
        titleCell.add(new Paragraph(new Link(
                job.getRole() + ", " + job.getCompany(),
                PdfAction.createURI(job.getLink())
        )));
        titleCell.setBorder(Border.NO_BORDER);
        titleCell.setFontSize(FONT_9_PT);
        titleCell.setBold();
        table.addCell(titleCell);

        Cell locationCell = new Cell();
        locationCell.setWidth(UnitValue.createPercentValue(20f));
        locationCell.setTextAlignment(TextAlignment.RIGHT);
        locationCell.add(new Paragraph(job.getLocation()));
        locationCell.setBorder(Border.NO_BORDER);
        locationCell.setFontSize(FONT_7_PT);
        table.addCell(locationCell);

        jobDiv.add(table);

        String endDate = job.getEnd() != null
                ? job.getEnd().format(DateTimeFormatter.ofPattern("MMM-yyyy"))
                : "Present";

        Paragraph dateParagraph = new Paragraph(
                job.getStart().format(DateTimeFormatter.ofPattern("MMM-yyyy")) + " to " + endDate
        );
        dateParagraph.setFontSize(FONT_8_PT);
        dateParagraph.setPadding(0f);
        dateParagraph.setMarginTop(0f);
        dateParagraph.setMarginLeft(2f);
        jobDiv.add(dateParagraph);

        Paragraph descriptionParagraph = new Paragraph(job.getShortDescription());
        descriptionParagraph.setFontSize(FONT_8_PT);
        descriptionParagraph.setPadding(0f);
        descriptionParagraph.setMarginTop(0f);
        descriptionParagraph.setMarginLeft(2f);
        jobDiv.add(descriptionParagraph);

        return jobDiv;
    }

    private static void addSideBar(Document document, ResumeData data) {
        Div div = new Div();
        div.setHeight(UnitValue.createPointValue(842f));
        div.setWidth(UnitValue.createPointValue(175f));
        div.setBackgroundColor(WebColors.getRGBColor("#dddddd"));
        div.setFixedPosition(0f, 0f, UnitValue.createPointValue(175f));

        div.add(blank());
        div.add(sidebarHeading("INFO"));
        div.add(sidebarContactInfo("phone", data.getPhone()));
        div.add(sidebarContactInfo("email", data.getEmail()));
        div.add(sidebarHeading("Links"));
        data.getLinks().forEach(link -> div.add(link(link.getUrl())));
        div.add(sidebarHeading("Skills"));
        data.getSkills().forEach(skill -> div.add(skill(skill)));

        document.add(div);
    }

    private static Div skill(ResumeData.Skill skill) {
        Div skillDiv = new Div();
        skillDiv.setMarginLeft(SKILLS_MARGIN_LEFT);

        Paragraph nameParagraph = new Paragraph(skill.getName());
        nameParagraph.setFontSize(FONT_7_PT);
        nameParagraph.setPaddingTop(0f);
        nameParagraph.setPaddingBottom(0f);
        nameParagraph.setMarginTop(0f);
        nameParagraph.setMarginBottom(0f);
        skillDiv.add(nameParagraph);

        Paragraph starsParagraph = new Paragraph(stars(skill.getRating()));
        starsParagraph.setPaddingTop(0f);
        starsParagraph.setBold();
        starsParagraph.setFontSize(10f);
        starsParagraph.setPaddingBottom(0f);
        skillDiv.add(starsParagraph);

        skillDiv.setMarginBottom(0f);
        return skillDiv;
    }

    private static String stars(Double rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating.intValue(); i++) {
            stars.append("* ");
        }
        return stars.toString();
    }

    private static Paragraph link(String url) {
        Paragraph linkParagraph = new Paragraph(new Link(url, PdfAction.createURI(url)));
        linkParagraph.setMarginLeft(SKILLS_MARGIN_LEFT);
        linkParagraph.setMarginRight(10f);
        linkParagraph.setFontSize(FONT_7_PT);
        return linkParagraph;
    }

    private static Div blank() {
        return blank(160f);
    }

    private static Div blank(float top) {
        Div blankDiv = new Div();
        blankDiv.setMarginTop(top);
        return blankDiv;
    }

    private static Div sidebarHeading(String heading) {
        Div headingDiv = new Div();
        headingDiv.setMarginRight(20f);
        headingDiv.setMarginLeft(SKILLS_MARGIN_LEFT);
        headingDiv.setMarginBottom(5f);
        headingDiv.add(new Paragraph(heading.toUpperCase()));
        headingDiv.setBorderBottom(new SolidBorder(1f));
        headingDiv.setFontSize(10f);
        return headingDiv;
    }

    private static Div employmentHeading(String heading) {
        Div headingDiv = new Div();
        headingDiv.setMarginRight(20f);
        headingDiv.setMarginLeft(10f);
        headingDiv.setMarginBottom(10f);
        headingDiv.add(new Paragraph(heading.toUpperCase()));
        headingDiv.setBorderBottom(new SolidBorder(1f));
        headingDiv.setFontSize(10f);
        return headingDiv;
    }

    private static Div sidebarContactInfo(String label, String value) {
        Div contactDiv = new Div();
        contactDiv.setMarginRight(20f);
        contactDiv.setMarginLeft(SKILLS_MARGIN_LEFT);
        contactDiv.setMarginBottom(10f);

        Paragraph labelParagraph = new Paragraph(label.toUpperCase());
        labelParagraph.setBold();
        labelParagraph.setFontSize(FONT_7_PT);
        labelParagraph.setPadding(0f);
        labelParagraph.setMargin(0f);
        contactDiv.add(labelParagraph);

        Paragraph valueParagraph = new Paragraph(value);
        valueParagraph.setFontSize(FONT_7_PT);
        valueParagraph.setPadding(0f);
        valueParagraph.setMargin(0f);
        contactDiv.add(valueParagraph);

        return contactDiv;
    }

    private static void addHeadline(Document document, ResumeData data) {
        Div div = new Div();
        div.setBorder(new SolidBorder(1.5f));
        div.setWidth(UnitValue.createPercentValue(75.0f));
        div.setPadding(20f);
        div.setBackgroundColor(WebColors.getRGBColor("#ffffff"));
        div.setFixedPosition(
                130f,
                document.getPageEffectiveArea(PageSize.A4).getHeight() - 50,
                UnitValue.createPercentValue(75.0f)
        );

        Paragraph nameParagraph = new Paragraph(data.getName().toUpperCase());
        Style nameStyle = new Style();
        nameStyle.setTextAlignment(TextAlignment.CENTER);
        nameStyle.setBold();
        nameStyle.setFontSize(20f);
        nameParagraph.addStyle(nameStyle);

        Paragraph headlineParagraph = new Paragraph(data.getHeadline().toUpperCase());
        Style headlineStyle = new Style();
        headlineStyle.setTextAlignment(TextAlignment.CENTER);
        headlineStyle.setFontSize(11f);
        headlineParagraph.addStyle(headlineStyle);

        div.add(nameParagraph);
        div.add(headlineParagraph);
        document.add(div);
    }
}