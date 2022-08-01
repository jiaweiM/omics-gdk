package omics.gdk.sirna;

import omics.gdk.codon.SeqTools;

/**
 * Small interfering RNA.
 * <p>
 * 20-25 nucleotides long, with dinucleotide overhang in the 3'OH terminal.
 *
 * @author JiaweiMao
 * @version 0.0.1
 * @since 11 5月 2022, 11:06
 */
public class SiRNA
{
    public static String reverseComplement(String rna, String overhang)
    {
        int id = rna.lastIndexOf(overhang);
        String core = rna.substring(0, id);
        String reverseComplement = SeqTools.reverseComplement(core);
        return reverseComplement + overhang;
    }

    /**
     * Guide strand, also called sense strand, the core sequence is same as a fragment in mRNA.
     */
    private String guidedStrand;
    /**
     * passenger strand, also called antisense strand.
     */
    private String passengerStrand;

    private String targetDNAStrand = null;
    /**
     * dTdT or UU are very popular.
     * 0	不包含 UU 或 dTdT 序列
     * 1	开头包含 UU 或 dTdT 序列
     * 2	末尾包含 UU 或 dTdT 序列
     */
    private String guideStrandOverhang = null;

    private String passengerStrandOverhang = null;

    public SiRNA(String guidedStrand, String passengerStrand)
    {
        this.guidedStrand = guidedStrand;
        this.passengerStrand = passengerStrand;
    }

    public String getDNACodingStrand()
    {
        if (targetDNAStrand == null) {
            String targetmRNA = guidedStrand;
            if (guideStrandOverhang != null) {
                int hangId = guidedStrand.lastIndexOf(guideStrandOverhang);
                targetmRNA = guidedStrand.substring(0, hangId);
            }
            targetDNAStrand = SeqTools.rna2dna(targetmRNA);
        }
        return targetDNAStrand;
    }

    public String getGuideStrandOverhang()
    {
        return guideStrandOverhang;
    }

    public void setOverhang(String overhang)
    {
        this.guideStrandOverhang = overhang;
        this.passengerStrandOverhang = overhang;
    }

    public void setGuideStrandOverhang(String guideStrandOverhang)
    {
        this.guideStrandOverhang = guideStrandOverhang;
    }

    public String getPassengerStrandOverhang()
    {
        return passengerStrandOverhang;
    }

    public void setPassengerStrandOverhang(String passengerStrandOverhang)
    {
        this.passengerStrandOverhang = passengerStrandOverhang;
    }

    /**
     * @return the guided strand, namely sense strand.
     */
    public String getGuidedStrand()
    {
        return guidedStrand;
    }

    public void setGuidedStrand(String guidedStrand)
    {
        this.guidedStrand = guidedStrand;
    }

    /**
     * @return the passenger strand, namely antisense strand,
     */
    public String getPassengerStrand()
    {
        return passengerStrand;
    }

    public void setPassengerStrand(String passengerStrand)
    {
        this.passengerStrand = passengerStrand;
    }
}
