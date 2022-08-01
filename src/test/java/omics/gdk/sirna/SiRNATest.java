package omics.gdk.sirna;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JiaweiMao
 * @version 0.0.1
 * @since 11 5月 2022, 14:42
 */
class SiRNATest
{

    @Test
    void reverseComplement()
    {
        String seq = "UGUCAACGACCGACCUUGAdTdT";
                   // UCAAGGUCGGUCGUUGACAdTdT
        String overhang = "dTdT";
        String reverseComplement = SiRNA.reverseComplement(seq, overhang);
        System.out.println(reverseComplement);
    }
}