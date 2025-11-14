package com.example.agenttest;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV 切檔工具（繁中註解）— 以「明細」為單位計數，每檔最多 999 筆，且同一張發票不可拆分。
 * <p>規則：當達到 999 邊界且下一行屬於新發票時換檔；若仍屬同一張，整張挪至下一檔。</p>
 */
public final class CsvSplitter {

    private CsvSplitter() {}

    /**
     * 讀入 CSV 並回傳多個分割後的檔案資料集合。
     * @param csvPath 原始 CSV 路徑
     * @param max 每檔最大明細數（預設 999）
     * @return 分割後的列表，每個元素為一檔的行資料（含表頭）
     * @throws IOException 讀取失敗拋出
     */
    public static List<List<CSVRecord>> split(String csvPath, int max) throws IOException {
        try (Reader r = new FileReader(csvPath)) {
            Iterable<CSVRecord> iterable = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(r);
            List<CSVRecord> all = new ArrayList<>();
            iterable.forEach(all::add);

            List<List<CSVRecord>> buckets = new ArrayList<>();
            List<CSVRecord> bucket = new ArrayList<>();
            String currentInvoice = null;

            for (CSVRecord rec : all) {
                String inv = rec.isMapped("InvoiceNo") ? rec.get("InvoiceNo")
                        : (rec.isMapped("invoiceNo") ? rec.get("invoiceNo") : "");
                boolean sameInvoice = currentInvoice != null && currentInvoice.equals(inv);

                if (bucket.size() >= max && !sameInvoice) {
                    buckets.add(bucket);
                    bucket = new ArrayList<>();
                }
                if (!sameInvoice && bucket.size() >= max) {
                    buckets.add(bucket);
                    bucket = new ArrayList<>();
                }
                bucket.add(rec);
                currentInvoice = inv;
            }
            if (!bucket.isEmpty()) {
                buckets.add(bucket);
            }
            return buckets;
        }
    }
}
