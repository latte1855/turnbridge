package com.asynctide.turnbridge.testsupport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 測試檔案工具。
 * 建立一個含 BOM 的 CSV 檔，模擬回饋內容。
 */
public final class TestFiles {

    private TestFiles() {}

    /**
     * 產生 UTF-8 with BOM 的測試 CSV。
     * @param path 目的地路徑（會確保上層目錄存在）
     */
    public static void writeCsvWithBom(Path path, String csvBody) throws IOException {
        Files.createDirectories(path.getParent());
        byte[] bom = new byte[] {(byte)0xEF, (byte)0xBB, (byte)0xBF};
        byte[] data = csvBody.getBytes(StandardCharsets.UTF_8);
        Files.write(path, concat(bom, data));
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
