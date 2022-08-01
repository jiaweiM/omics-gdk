package omics.gdk.codon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JiaweiMao
 * @version 0.0.1
 * @since 29 4月 2022, 11:15
 */
class SeqToolsTest
{

    @Test
    void hasU()
    {
        String seq = "CAGAGGUGAAGCGAAGUG";
        assertTrue(SeqTools.hasU(seq));

        String seq2 = "GCACTTCGCTTCACCTCTG";
        assertFalse(SeqTools.hasU(seq2));
    }

    @Test
    void dna2rna()
    {
        String dna = "GCACTTCGCTTCACCTCTG";
        String rna = SeqTools.dna2rna(dna);
        assertEquals(rna, "GCACUUCGCUUCACCUCUG");
    }

    @Test
    void rna2dna()
    {
    }

    @Test
    void complement()
    {
        String dna = "GCACTTCGCTTCACCTCTG";
        String compDna = SeqTools.complement(dna);
        assertEquals(compDna, "CGTGAAGCGAAGTGGAGAC");
    }

    @Test
    void reverseComplement(){
        String dna = "GCACTTCGCTTCACCTCTG";
        String dna2 = SeqTools.reverseComplement(dna);
        System.out.println(dna2);
    }
}