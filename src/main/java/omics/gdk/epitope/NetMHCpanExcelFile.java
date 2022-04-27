package omics.gdk.epitope;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import omics.util.io.csv.CsvWriter;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author JiaweiMao
 * @version 0.0.1
 * @since 26 4月 2022, 15:06
 */
public class NetMHCpanExcelFile
{

    public static void main(String[] args) throws IOException
    {
        String[] files = new String[]{
                "D:\\data\\fenzhongxin\\pedv_proteins\\nucleo_NetMHCpan_1.xlsx",
                "D:\\data\\fenzhongxin\\pedv_proteins\\nucleo_NetMHCpan_2.xlsx",
                "D:\\data\\fenzhongxin\\pedv_proteins\\nucleo_NetMHCpan_3.xlsx",
                "D:\\data\\fenzhongxin\\pedv_proteins\\nucleo_NetMHCpan_4.xlsx"
        };

        String outStrong = "D:\\data\\fenzhongxin\\pedv_proteins\\nucleo_epitope_strong.csv";
        String outWeak = "D:\\data\\fenzhongxin\\pedv_proteins\\nucleo_epitope_weak.csv";

        write(deduplicate(join(files, new Epitope.StrongBinderFilter())), outStrong);
        write(deduplicate(join(files, new Epitope.WeakBinderFilter())), outWeak);
    }

    public static void write(List<Epitope> epitopeList, String outCsv) throws IOException
    {
        System.out.println("Output " + epitopeList.size() + " Epitope");
        CsvWriter writer = new CsvWriter(outCsv);
        writer.writeRecord(new String[]{"Peptide", "Protein", "iCore", "EL Score", "EL Rank", "HLA[s]"});
        for (Epitope epitope : epitopeList) {
            writer.write(epitope.getPeptide());
            writer.write(epitope.getId());
            writer.write(epitope.getIcore());
            writer.write(epitope.getELScore());
            writer.write(epitope.getELRank());
            List<String> hlaList = epitope.getHLAList();
            if (hlaList == null) {
                writer.write(epitope.getHla());
            } else {
                hlaList.add(epitope.getHla());
                String hlastr = String.join(";", hlaList);
                writer.write(hlastr);
            }
            writer.endRecord();
        }
        writer.close();
    }

    public static List<Epitope> deduplicate(List<Epitope> epitopeList)
    {
        HashMap<String, Epitope> map = new HashMap<>();
        for (Epitope epitope : epitopeList) {
            String icore = epitope.getIcore();
            if (map.containsKey(icore)) {
                map.get(icore).addHLA(epitope.getHla());
            } else {
                map.put(icore, epitope);
            }
        }

        return new ArrayList<>(map.values());
    }

    /**
     * Join multiple NetMHCpan predicted result
     *
     * @param files excel files.
     */
    public static List<Epitope> join(String[] files, Epitope.Filter filter) throws IOException
    {
        List<Epitope> allEpitopeList = new ArrayList<>();
        for (String file : files) {
            NetMHCpanExcelFile excelFile = new NetMHCpanExcelFile(file);
            List<Epitope> epitopeList = excelFile.getEpitopeList();
            allEpitopeList.addAll(epitopeList.stream().filter(filter::test).toList());
        }
        return allEpitopeList;
    }

    private final String filePath;
    private List<Epitope> epitopeList;

    public NetMHCpanExcelFile(String filePath) throws IOException
    {
        this.filePath = filePath;
        init();
    }

    public List<Epitope> getEpitopeList()
    {
        return epitopeList;
    }

    private void init() throws IOException
    {
        Workbook workbook = WorkbookFactory.create(new File(filePath));
        Sheet sheet = workbook.getSheetAt(0);
        List<String> hlaList = getHLAList(sheet);

        System.out.println("# of HLA " + hlaList.size());

        int posLoc = -1;
        int pepLoc = -1;
        int idLoc = -1;
        IntList coreLocs = new IntArrayList();
        IntList icoreLocs = new IntArrayList();
        IntList scoreLocs = new IntArrayList();
        IntList rankLocs = new IntArrayList();

        Row titleRow = sheet.getRow(1);
        System.out.println("# of peptide " + (sheet.getLastRowNum() - 1));
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            Cell cell = titleRow.getCell(i);
            String value = cell.getStringCellValue();
            switch (value) {
                case "Pos" -> posLoc = i;
                case "Peptide" -> pepLoc = i;
                case "ID" -> idLoc = i;
                case "core" -> coreLocs.add(i);
                case "icore" -> icoreLocs.add(i);
                case "EL-score" -> scoreLocs.add(i);
                case "EL_Rank" -> rankLocs.add(i);
            }
        }

        this.epitopeList = new ArrayList<>();
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            int pos = (int) row.getCell(posLoc).getNumericCellValue();
            String pep = row.getCell(pepLoc).getStringCellValue();
            String id = row.getCell(idLoc).getStringCellValue();

            for (int k = 0; k < hlaList.size(); k++) {
                Epitope epitope = new Epitope(pos, pep, id);

                String hla = hlaList.get(k);

                String core;
                Cell coreCell = row.getCell(coreLocs.getInt(k));
                if (coreCell.getCellType() == CellType.FORMULA) {
                    core = coreCell.getCellFormula();
                } else {
                    core = coreCell.getStringCellValue();
                }

                String icore = row.getCell(icoreLocs.getInt(k)).getStringCellValue();
                double score = row.getCell(scoreLocs.getInt(k)).getNumericCellValue();
                double rank = row.getCell(rankLocs.getInt(k)).getNumericCellValue();

                epitope.setHla(hla)
                        .setCore(core)
                        .setIcore(icore);
                epitope.setELScore(score);
                epitope.setELRank(rank);
                epitopeList.add(epitope);
            }
        }

        workbook.close();
    }

    private static List<String> getHLAList(Sheet sheet)
    {
        List<String> hlaList = new ArrayList<>();
        Row row = sheet.getRow(0);
        for (Cell cell : row) {
            String value = cell.getStringCellValue();
            hlaList.add(value);
        }
        return hlaList;
    }
}
