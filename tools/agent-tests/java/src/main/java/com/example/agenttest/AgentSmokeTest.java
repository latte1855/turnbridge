package com.example.agenttest;

import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.util.List;

/**
 * Agent 冒煙測試（繁中註解 + JavaDoc）。
 * <p>驗證：999 不拆單切檔流程、HMAC 計算、上傳 API 回傳 importId。</p>
 */
public final class AgentSmokeTest {

    private AgentSmokeTest() {}

    /**
     * 入口：示意流程（實務上可改為 JUnit/CI 執行）。
     * @param args ignore
     * @throws Exception 任意錯誤
     */
    public static void main(String[] args) throws Exception {
        String csv = "tools/agent-tests/node/samples/invoice_legacy_A0401.csv";
        List<List<CSVRecord>> parts = CsvSplitter.split(csv, 999);
        System.out.println("切成 " + parts.size() + " 檔");

        // TODO: 串接 HttpClient 上傳 multipart，並印出 importId
        // TODO: 以 HmacSigner 對 Webhook 假 payload 計算簽章，驗證與後端一致
        File dummy = new File(csv);
        HttpClientUtil.uploadCsv(dummy, "REPLACE_TOKEN", "https://turnbridge.local/api/v1/upload/invoice");
    }
}
