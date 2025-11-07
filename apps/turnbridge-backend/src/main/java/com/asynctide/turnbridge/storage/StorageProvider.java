package com.asynctide.turnbridge.storage;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * 儲存體抽象介面。
 * <p>
 * 目的：屏蔽實際儲存方式（本機檔案系統、MinIO、S3），讓應用程式以統一方法
 * 進行物件的寫入、讀取與存在性查詢。
 *
 * <h3>設計重點</h3>
 * <ul>
 *   <li>方法均為同步呼叫，I/O 交由呼叫端使用非同步執行緒處理。</li>
 *   <li>metadata 採自由鍵值組（例如 sellerId、note、traceId）。</li>
 *   <li>不負責資料加密；若需要，於上層先行處理或選用支援加密的 Provider。</li>
 * </ul>
 */
public interface StorageProvider {

    /**
     * 將輸入串流寫入指定 bucket/objectKey。
     *
     * @param in            檔案輸入串流（呼叫端負責關閉）
     * @param contentLength 內容長度（bytes），若未知可傳 -1
     * @param mediaType     媒體型態（例：text/csv、application/zip）
     * @param bucket        儲存桶（邏輯命名）
     * @param objectKey     物件鍵（路徑+檔名）
     * @param metadata      其他中繼資料（可為空）
     * @return              寫入後的 {@link StoredObjectRef}
     */
    StoredObjectRef store(InputStream in, long contentLength, String mediaType,
                          String bucket, String objectKey, Map<String, String> metadata);

    /**
     * 開啟指定物件的讀取串流。
     *
     * @param bucket    儲存桶
     * @param objectKey 物件鍵
     * @return          存在則回傳 InputStream，否則回傳空 Optional
     */
    Optional<InputStream> open(String bucket, String objectKey);

    /**
     * 檢查物件是否存在。
     *
     * @param bucket    儲存桶
     * @param objectKey 物件鍵
     * @return          true 表存在
     */
    boolean exists(String bucket, String objectKey);
}
