package omics.gdk.epitope;

import omics.util.OmicsException;
import omics.util.protein.database.Protein;
import omics.util.protein.database.ProteinDB;

/**
 * @author JiaweiMao
 * @version 0.0.1
 * @since 27 4月 2022, 14:22
 */
public class ProteinLen
{
    public static void main(String[] args) throws OmicsException
    {
        ProteinDB proteins = ProteinDB.read("D:\\data\\fenzhongxin\\pedv_proteins\\Nucleoprotein-conserved.fasta");
        for (Protein protein : proteins) {
            System.out.println(protein.length());
        }

    }
}
