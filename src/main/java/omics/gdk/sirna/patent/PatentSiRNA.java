package omics.gdk.sirna.patent;

import omics.gdk.sirna.SiRNA;

/**
 * @author JiaweiMao
 * @version 0.0.1
 * @since 11 5月 2022, 13:14
 */
public class PatentSiRNA
{
    private String patentNumber;

    private String openDay;

    private String applicant;

    private SiRNA siRNA;

    private String codingStrand;
    private String templateStrand;

    public PatentSiRNA(String patentNumber, String openDay, String applicant)
    {
        this.patentNumber = patentNumber;
        this.openDay = openDay;
        this.applicant = applicant;
    }

    public String getCodingStrand()
    {
        return codingStrand;
    }

    public void setCodingStrand(String codingStrand)
    {
        this.codingStrand = codingStrand;
    }

    public String getTemplateStrand()
    {
        return templateStrand;
    }

    public void setTemplateStrand(String templateStrand)
    {
        this.templateStrand = templateStrand;
    }

    public String getPatentNumber()
    {
        return patentNumber;
    }

    public String getOpenDay()
    {
        return openDay;
    }

    public String getApplicant()
    {
        return applicant;
    }

    public SiRNA getSiRNA()
    {
        return siRNA;
    }

    public void setSiRNA(SiRNA siRNA)
    {
        this.siRNA = siRNA;
    }
}
