package omics.gdk.codon;

import omics.util.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JiaweiMao
 * @version 0.0.1
 * @since 28 4月 2022, 20:08
 */
public class SeqTools
{
    private static final Map<Character, Character> DNA_COMPLEMENT_MAP = new HashMap<>(4);
    private static final Map<Character, Character> RNA_COMPLEMENT_MAP = new HashMap<>(4);

    static {
        DNA_COMPLEMENT_MAP.put('A', 'T');
        DNA_COMPLEMENT_MAP.put('T', 'A');
        DNA_COMPLEMENT_MAP.put('C', 'G');
        DNA_COMPLEMENT_MAP.put('G', 'C');

        RNA_COMPLEMENT_MAP.put('A', 'U');
        RNA_COMPLEMENT_MAP.put('U', 'A');
        RNA_COMPLEMENT_MAP.put('C', 'G');
        RNA_COMPLEMENT_MAP.put('G', 'C');
    }

    /**
     * @param seq nucleotide sequence
     * @return number of T in the sequence
     */
    public static int countOfT(String seq)
    {
        return StringUtils.countMatches(seq, 'T');
    }

    /**
     * Return true if the sequence contains 'U'
     *
     * @param seq base sequence
     * @return true if the sequence contains 'U'
     */
    public static boolean hasU(String seq)
    {
        return seq.indexOf('U') >= 0;
    }

    /**
     * convert dna sequence too rna sequence, namely replace 'T' with 'U'
     *
     * @param dna dna base sequence
     * @return rna base sequence
     */
    public static String dna2rna(String dna)
    {
        return dna.replace('T', 'U');
    }

    public static String rna2dna(String rna)
    {
        return rna.replace('U', 'T');
    }

    public static String complement(String seq)
    {
        char[] complement = new char[seq.length()];

        if (hasU(seq)) {
            for (int i = 0; i < seq.length(); i++) {
                Character c = seq.charAt(i);
                Character compc = RNA_COMPLEMENT_MAP.get(c);
                if (compc == null) {
                    compc = c;
                }
                complement[i] = compc;
            }
        } else {
            for (int i = 0; i < seq.length(); i++) {
                char c = seq.charAt(i);
                Character compc = DNA_COMPLEMENT_MAP.get(c);
                if (compc == null) {
                    compc = c;
                }
                complement[i] = compc;
            }
        }

        return new String(complement);
    }

    /**
     * Return the reverse complement of given sequence
     *
     * @param seq base sequence
     * @return reverse complement of the sequence
     */
    public static String reverseComplement(String seq)
    {
        String comp = complement(seq);
        return StringUtils.reverse(comp);
    }

}
