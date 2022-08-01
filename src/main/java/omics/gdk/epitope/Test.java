package omics.gdk.epitope;

import omics.util.io.csv.CsvReader;

import java.io.IOException;
import java.util.*;

/**
 * @author YourName
 * @version 0.0.1
 * @since 11 7月 2022, 14:29
 */
public class Test
{
    public static void main(String[] args) throws IOException
    {
        List<String> list = Arrays.asList(
                "TRGIFGAIAGFIENGWE",
                "QIQDVWAYNAELLVLLENQK",
                "KSWRNNILRTQESEC",
                "NNILRTQESECACVNGSCFT",
                "YHYEECSCYPDSSEI",
                "YDKEEIRRIWRQANNGEDAT",
                "RRSGAAGAAVK",
                "FWRGENGRKTRSAYERMCNILKGK ",
                "FLARSALILRGSVAHK",
                "TFSVQRNLPFERATIMAAFT",
                "PVTSGCFPIMHDRTKIRQLP",
                "RGILLPQKVWCASGRSKVIK",
                "SRGLFGAIAGFIEGGWQ",
                "MVTGLRNIPSIQSRGLFGAIAGFIE",
                "GIFGAIAGF",
                "YNAELLVLL",
                "GRFYIQMCTELKLSDYEG",
                "RLIQNSLTI",
                "RSALILRGSVAHKSC",
                "NPAHKSQLVWMACNSAAFED",
                "SRYWAIRTR",
                "SSANGVTTHYVS",
                "LKERGFFGAIAGFLE",
                "INKITKNLNSLSELEVKNLQ",
                "LPQSGRIVV",
                "LKERGFFGAIAGFLE");
        Set<String> peptide_set = new HashSet<>(list);
        CsvReader reader = new CsvReader("D:\\data\\nino\\epitope_result.csv");
        reader.readHeaders();
        while (reader.readRecord()) {
            String pep = reader.get(0);
            for (String s : peptide_set) {
                if (s.contains(pep)) {
                    System.out.println(pep + "\t" + s);
                    break;
                }
            }

        }
        reader.close();
    }
}
