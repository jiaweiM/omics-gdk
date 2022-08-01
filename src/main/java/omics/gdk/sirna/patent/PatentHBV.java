package omics.gdk.sirna.patent;

import omics.gdk.codon.SeqTools;
import omics.gdk.sirna.SiRNA;
import omics.util.utils.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JiaweiMao
 * @version 0.0.1
 * @since 29 4月 2022, 14:04
 */
public class PatentHBV
{
    public static void parse(String excel, String out) throws IOException
    {
        List<PatentSiRNA> patentSiRNAList = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(new File(excel));
        for (Sheet sheet : workbook) {
            int overhangId = -1;
            int antiOverhangId = -1;
            Row titleRow = sheet.getRow(0);
            for (int i = 0; i <= titleRow.getLastCellNum(); i++) {
                Cell cell = titleRow.getCell(i);
                if (cell == null)
                    continue;
                String value = cell.getStringCellValue();
                if (value.equalsIgnoreCase("overhang")) {
                    overhangId = i;
                } else if (value.equalsIgnoreCase("Antisense Overhang")) {
                    antiOverhangId = i;
                }
            }
            String patentNumber = null;
            String applicant = null;
            String openDay = null;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if(row == null)
                    continue;
                Cell cell = row.getCell(0);

                if (cell != null) {
                    if (cell.getCellType() == CellType.STRING) {
                        String patentStr = cell.getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(patentStr)) {
                            patentNumber = patentStr;
                            applicant = row.getCell(1).getStringCellValue().trim();
                            Cell dayCell = row.getCell(2);

                            if (dayCell.getCellType() == CellType.NUMERIC) {
                                if (DateUtil.isCellDateFormatted(dayCell)) {
                                    openDay = DateFormat.getDateInstance().format(dayCell.getDateCellValue());
                                } else {
                                    openDay = String.valueOf(dayCell.getNumericCellValue());
                                }
                            } else if (dayCell.getCellType() == CellType.STRING) {
                                openDay = dayCell.getStringCellValue();
                            }
                        }
                    }
                }

                String overhang = "";
                if (overhangId >= 0) {
                    Cell overhangCell = row.getCell(overhangId);
                    if (overhangCell != null) {
                        overhang = overhangCell.getStringCellValue();
                    }
                }
                String antiOverhang = "";
                if (antiOverhangId >= 0) {
                    Cell antiOverhangCell = row.getCell(antiOverhangId);
                    if (antiOverhangCell != null) {
                        antiOverhang = antiOverhangCell.getStringCellValue();
                    }
                }
                List<String> rnaList = new ArrayList<>(2);
                List<String> dnaList = new ArrayList<>(2);
                for (int k = 3; k <= row.getLastCellNum(); k++) {
                    if (k == overhangId || k == antiOverhangId)
                        continue;
                    Cell kCell = row.getCell(k);
                    if (kCell == null)
                        continue;
                    String value = kCell.getStringCellValue().trim();
                    if (StringUtils.isNotEmpty(value)) {
                        if (SeqTools.hasU(value)) {
                            rnaList.add(value);
                        } else {
                            dnaList.add(value);
                        }
                    }
                }

                if (rnaList.size() == 1 && dnaList.isEmpty()) {
                    String rna = rnaList.get(0);
                    String rna2;
                    if (StringUtils.isNotEmpty(overhang)) {
                        rna2 = SiRNA.reverseComplement(rna, overhang);
                    } else {
                        try {
                            rna2 = SeqTools.reverseComplement(rna);
                        } catch (Exception e) {
                            rna2 = "";
                            System.out.println(sheet.getSheetName());
                            System.out.println(rna);
                        }
                    }
                    SiRNA siRNA = new SiRNA(rna, rna2);
                    siRNA.setOverhang(overhang);

                    PatentSiRNA patentSiRNA = new PatentSiRNA(patentNumber, openDay, applicant);
                    patentSiRNA.setSiRNA(siRNA);

                    String dnaCodingStrand = siRNA.getDNACodingStrand();

                    patentSiRNA.setCodingStrand(dnaCodingStrand);
                    patentSiRNA.setTemplateStrand(SeqTools.reverseComplement(dnaCodingStrand));
                    patentSiRNAList.add(patentSiRNA);

                } else if (dnaList.size() == 1) {
                    String dna = dnaList.get(0);
                    String dna2 = SeqTools.reverseComplement(dna);
                    String rna = SeqTools.dna2rna(dna);
                    String rna2 = SeqTools.dna2rna(dna2);
                    SiRNA siRNA = new SiRNA(rna, rna2);
                    PatentSiRNA patentSiRNA = new PatentSiRNA(patentNumber, openDay, applicant);
                    patentSiRNA.setSiRNA(siRNA);
                    patentSiRNA.setCodingStrand(dna);
                    patentSiRNA.setTemplateStrand(dna2);
                    patentSiRNAList.add(patentSiRNA);
                } else if (rnaList.size() == 2) {
                    String rna1 = rnaList.get(0);
                    String rna2 = rnaList.get(1);

                    SiRNA siRNA = new SiRNA(rna1, rna2);
                    if (StringUtils.isNotEmpty(overhang)) {
                        siRNA.setOverhang(overhang);
                    }

                    if (StringUtils.isNotEmpty(antiOverhang)) {
                        siRNA.setPassengerStrandOverhang(antiOverhang);
                    }

                    PatentSiRNA patentSiRNA = new PatentSiRNA(patentNumber, openDay, applicant);
                    patentSiRNA.setSiRNA(siRNA);
                    String dnaCodingStrand = siRNA.getDNACodingStrand();
                    patentSiRNA.setCodingStrand(dnaCodingStrand);
                    try {
                        patentSiRNA.setTemplateStrand(SeqTools.reverseComplement(dnaCodingStrand));
                    } catch (Exception e) {
                        System.out.println(sheet.getSheetName());
                        System.out.println(dnaCodingStrand);
                    }
                    patentSiRNAList.add(patentSiRNA);
                }
            }
        }

        workbook.close();

        Workbook outBook = new XSSFWorkbook();
        Sheet outSheet = outBook.createSheet();
        Row titleRow = outSheet.createRow(0);
        String[] titles = new String[]{"Patent Number", "Applicant", "Open Day",
                "Coding strand (5'->3')", "Template strand (5'->3')",
                "Guide strand (5'->3')", "Passenger strand (5'->3')", "Guide Overhang", "Passenger Overhang"};
        for (int i = 0; i < titles.length; i++) {
            titleRow.createCell(i).setCellValue(titles[i]);
        }

        int rowNumber = 1;
        for (PatentSiRNA patentSiRNA : patentSiRNAList) {
            Row row = outSheet.createRow(rowNumber);
            rowNumber++;
            row.createCell(0).setCellValue(patentSiRNA.getPatentNumber());
            row.createCell(1).setCellValue(patentSiRNA.getApplicant());
            row.createCell(2).setCellValue(patentSiRNA.getOpenDay());

            SiRNA siRNA = patentSiRNA.getSiRNA();
            row.createCell(3).setCellValue(patentSiRNA.getCodingStrand());
            row.createCell(4).setCellValue(patentSiRNA.getTemplateStrand());
            row.createCell(5).setCellValue(siRNA.getGuidedStrand());
            row.createCell(6).setCellValue(siRNA.getPassengerStrand());
            row.createCell(7).setCellValue(siRNA.getGuideStrandOverhang());
            row.createCell(8).setCellValue(siRNA.getPassengerStrandOverhang());
        }

        outBook.write(new FileOutputStream(out));

        try (OutputStream fileOut = new FileOutputStream(out)) {
            outBook.write(fileOut);
        }
    }

    public static void main(String[] args) throws IOException
    {
        parse("D:\\doc\\HBV Patent\\4_siRNA专利序列.xlsx", "D:\\doc\\HBV Patent\\Patent_siRNA2.xlsx");
    }
}
