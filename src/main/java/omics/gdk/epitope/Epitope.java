package omics.gdk.epitope;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JiaweiMao
 * @version 0.0.1
 * @since 26 4月 2022, 15:13
 */
public class Epitope
{
    interface Filter
    {
        /**
         * Return true if the epitope pass the filter
         */
        boolean test(Epitope epitope);
    }

    public static class StrongBinderFilter implements Filter
    {
        @Override
        public boolean test(Epitope epitope)
        {
            return epitope.isStrong();
        }
    }

    public static class WeakBinderFilter implements Filter
    {
        @Override
        public boolean test(Epitope epitope)
        {
            return epitope.isWeak();
        }
    }

    /**
     * threshold for string binder, percent
     */
    private static final double STRONG_BINDER_RANK = 0.5;

    private static final double WEAK_BINDER_RANK = 2;

    /**
     * residue number (from 0) of the peptide in the protein sequence
     */
    private int pos;
    /**
     * Specified MHC molecule / Allele name.
     */
    private String hla;
    /**
     * Amino acid sequence of the potential ligand.
     */
    private String peptide;
    /**
     * Protein identifier, i.e. the name of the FASTA entry.
     */
    private String id;
    /**
     * The minimal 9 amino acid binding core directly in contact with the MHC.
     */
    private String core;
    /**
     * Interaction core. This is the sequence of the binding core including eventual insertions of deletions.
     */
    private String icore;

    private double elRank;

    private double elScore;

    private List<String> hlaList;

    public Epitope() {}

    /**
     * Constructor
     *
     * @param pos     residue number (from 0) of the peptide in the protein sequence
     * @param peptide Amino acid sequence of the potential ligand.
     * @param id      Protein identifier, i.e. the name of the FASTA entry.
     */
    public Epitope(int pos, String peptide, String id)
    {
        this.pos = pos;
        this.peptide = peptide;
        this.id = id;
    }

    public void addHLA(String hla)
    {
        if (hlaList == null) {
            hlaList = new ArrayList<>();
        }
        hlaList.add(hla);
    }

    /**
     * @return additional HLA
     */
    public List<String> getHLAList()
    {
        return hlaList;
    }

    public int getPos()
    {
        return pos;
    }

    public void setPos(int pos)
    {
        this.pos = pos;
    }

    /**
     * the complete amino acid sequence evaluated by NetMHCpan. Peptides are the full sequences submitted as a peptide
     * list, or the result of digestion of source proteins (Fasta submission)
     *
     * @return Amino acid sequence of the potential ligand.
     */
    public String getPeptide()
    {
        return peptide;
    }

    public void setPeptide(String peptide)
    {
        this.peptide = peptide;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getHla()
    {
        return hla;
    }

    public Epitope setHla(String hla)
    {
        this.hla = hla;
        return this;
    }

    /**
     * The iCore is a substring of Peptide, encompassing all residues between P1 and P-omega of the MHC.
     * For all intents and purposes, this is the minimal candidate ligand/epitope that should be considered for further validation.
     *
     * @return Interaction core
     */
    public String getIcore()
    {
        return icore;
    }

    public void setIcore(String icore)
    {
        this.icore = icore;
    }

    /**
     * The Core is always 9 amino acids long, and is a construction used for sequence aligment and identification of binding anchors.
     */
    public String getCore()
    {
        return core;
    }

    public Epitope setCore(String core)
    {
        this.core = core;
        return this;
    }

    /**
     * @return true if this epitope is classified as a strong binder.
     */
    public boolean isStrong()
    {
        return elRank < STRONG_BINDER_RANK;
    }

    /**
     * @return true if this epitope is classified as a weak binder.
     */
    public boolean isWeak()
    {
        return elRank < WEAK_BINDER_RANK && elRank > STRONG_BINDER_RANK;
    }

    /**
     * Rank of the predicted binding score compared to a set of random natural peptides. This measure is not
     * affected by inherent bias of certain molecules towards higher or lower mean predicted affinities.
     * Strong binders are defined as having %rank<0.5, and weak binders with %rank<2. We advise to select candidate
     * binders based on %Rank rather than Score
     *
     * @return EL RANK in percent.
     */
    public double getELRank()
    {
        return elRank;
    }

    public void setELRank(double elRank)
    {
        this.elRank = elRank;
    }

    public double getELScore()
    {
        return elScore;
    }

    public void setELScore(double elScore)
    {
        this.elScore = elScore;
    }
}
