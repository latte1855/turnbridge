package com.asynctide.turnbridge.service.upload;

import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.ImportFileItemError;
import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 產生匯入結果檔（CSV/ZIP）。
 * <p>每筆 ImportFileItem 會輸出原始欄位與處理狀態，以利整批下載與稽核。</p>
 */
@Service
@Transactional(readOnly = true)
public class ImportResultService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final ImportFileRepository importFileRepository;
    private final ImportFileItemRepository importFileItemRepository;
    private final ImportFileItemErrorRepository importFileItemErrorRepository;
    private final ObjectMapper objectMapper;

    public ImportResultService(
        ImportFileRepository importFileRepository,
        ImportFileItemRepository importFileItemRepository,
        ImportFileItemErrorRepository importFileItemErrorRepository,
        ObjectMapper objectMapper
    ) {
        this.importFileRepository = importFileRepository;
        this.importFileItemRepository = importFileItemRepository;
        this.importFileItemErrorRepository = importFileItemErrorRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 針對單一 ImportFile 產生結果 CSV。
     * @param importFileId 匯入批次 ID
     * @return 結果檔案（檔名 + 內容 bytes）
     */
    public ResultFile generateCsv(Long importFileId) {
        ImportFile file = importFileRepository.findById(importFileId).orElseThrow(() -> new IllegalArgumentException("匯入批次不存在"));
        List<ImportFileItem> items = importFileItemRepository.findByImportFileIdOrderByLineIndexAsc(importFileId);
        List<Long> itemIds = items.stream().map(ImportFileItem::getId).toList();
        Map<Long, List<ImportFileItemError>> errorMap = importFileItemErrorRepository
            .findByImportFileItemIdIn(itemIds.isEmpty() ? List.of(-1L) : itemIds)
            .stream()
            .collect(Collectors.groupingBy(err -> err.getImportFileItem().getId()));

        LinkedHashSet<String> dynamicColumns = collectColumns(items);

        List<String> headers = new ArrayList<>(dynamicColumns);
        headers.add("status");
        headers.add("errorCode");
        headers.add("errorMessage");
        headers.add("fieldErrors");

        try {
            StringWriter writer = new StringWriter();
            try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(headers.toArray(new String[0])).build())) {
                for (ImportFileItem item : items) {
                    Map<String, Object> raw = readRawData(item.getRawData());
                    List<Object> row = new ArrayList<>();
                    for (String column : dynamicColumns) {
                        Object value = raw.get(column);
                        row.add(value == null ? "" : String.valueOf(value));
                    }
                    row.add(item.getStatus() != null ? item.getStatus().name() : "");
                    row.add(StringUtils.hasText(item.getErrorCode()) ? item.getErrorCode() : "");
                    row.add(StringUtils.hasText(item.getErrorMessage()) ? item.getErrorMessage() : "");
                    row.add(formatFieldErrors(errorMap.get(item.getId())));
                    printer.printRecord(row);
                }
            }
            String filename = buildResultFilename(file);
            return new ResultFile(filename, writer.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("無法產生結果 CSV", e);
        }
    }

    /**
     * 針對多個 ImportFile 打包 ZIP；每個批次仍以 CSV 呈現。
     * @param importFileIds 批次 ID 列表
     * @return ZIP 檔案
     */
    public ResultFile generateZip(List<Long> importFileIds) {
        if (importFileIds == null || importFileIds.isEmpty()) {
            throw new IllegalArgumentException("需提供至少一個匯入批次 ID");
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {
                for (Long id : importFileIds) {
                    ResultFile csv = generateCsv(id);
                    ZipEntry entry = new ZipEntry(csv.filename());
                    zos.putNextEntry(entry);
                    zos.write(csv.content());
                    zos.closeEntry();
                }
            }
            return new ResultFile("import-results.zip", baos.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("無法產生 ZIP 檔", e);
        }
    }

    /**
     * 建立單一 CSV 的 HTTP 回應（含 Content-Disposition）。
     */
    public ResponseEntity<ByteArrayResource> buildCsvResponse(Long importFileId) {
        ResultFile file = generateCsv(importFileId);
        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType("text/csv"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "\"")
            .body(new ByteArrayResource(file.content()));
    }

    /**
     * 建立 ZIP 下載的 HTTP 回應。
     */
    public ResponseEntity<ByteArrayResource> buildZipResponse(List<Long> importFileIds) {
        ResultFile file = generateZip(importFileIds);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "\"")
            .body(new ByteArrayResource(file.content()));
    }

    /**
     * 收集所有原始欄位：每行 rawData 可能包含不同欄位，因此需先統一欄位順序。
     */
    private LinkedHashSet<String> collectColumns(List<ImportFileItem> items) {
        LinkedHashSet<String> columns = new LinkedHashSet<>();
        for (ImportFileItem item : items) {
            Map<String, Object> raw = readRawData(item.getRawData());
            columns.addAll(raw.keySet());
        }
        return columns;
    }

    /**
     * 將 rawData JSON 轉為 Map，方便取欄位值。
     */
    private Map<String, Object> readRawData(String json) {
        if (!StringUtils.hasText(json)) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (IOException e) {
            throw new IllegalStateException("無法解析 rawData JSON", e);
        }
    }

    /**
     * 將欄位錯誤格式化為「Field(index):code-message」字串，供 CSV 顯示。
     */
    private String formatFieldErrors(List<ImportFileItemError> errors) {
        if (errors == null || errors.isEmpty()) {
            return "";
        }
        return errors
            .stream()
            .sorted(Comparator.comparing(ImportFileItemError::getColumnIndex))
            .map(err -> {
                String field = err.getFieldName() != null ? err.getFieldName() : "";
                String code = err.getErrorCode() != null ? err.getErrorCode() : "";
                String message = err.getMessage() != null ? err.getMessage() : "";
                return field + "(" + err.getColumnIndex() + "):" + code + (StringUtils.hasText(message) ? "-" + message : "");
            })
            .collect(Collectors.joining("; "));
    }

    private String buildResultFilename(ImportFile importFile) {
        String baseName = importFile.getOriginalFilename();
        if (!StringUtils.hasText(baseName)) {
            baseName = "import_" + importFile.getId() + ".csv";
        } else {
            baseName = baseName.replaceAll("\\.csv$", "") + "_result.csv";
        }
        return baseName;
    }

    /**
     * 結果檔案描述：包含檔名與內容。
     * @param filename 檔案名稱
     * @param content 內容 bytes
     */
    public record ResultFile(String filename, byte[] content) {}
}
