package com.asynctide.turnbridge.service.upload;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.csv.CSVRecord;
import com.asynctide.turnbridge.service.upload.MessageTypeUtils;

/**
 * 支援根據 legacyType 決定欄位對應的 profile。
 */
public final class ColumnProfileRegistry {

    private static final Map<String, ColumnProfile> PROFILES = new HashMap<>();

    static {
        PROFILES.put("C0401", new ColumnProfile(List.of(
            "type",
            "invoiceNo",
            "invoiceDate",
            "invoiceTime",
            "sellerId",
            "sellerName",
            "buyerId",
            "buyerName",
            "invoiceCategory",
            "donate",
            "carrierType",
            "carrierId",
            "carrierId2",
            "printed",
            "donationTarget",
            "randomCode",
            "productName",
            "quantity",
            "unitPrice",
            "amount",
            "detailSequence",
            "taxableAmount",
            "taxExemption",
            "zeroTaxAmount",
            "taxType",
            "taxRate",
            "taxAmount",
            "totalAmount",
            "discount",
            "creditCardCode"
        )));
        PROFILES.put("C0501", new ColumnProfile(List.of(
            "type",
            "invoiceNo",
            "invoiceDate",
            "buyerId",
            "sellerId",
            "cancelDate",
            "cancelTime",
            "cancelReason",
            "cancelApproval",
            "remark"
        )));
        PROFILES.put("C0701", PROFILES.get("C0501"));
        
        System.out.println(PROFILES);
    }

    private ColumnProfileRegistry() {}

    public static ColumnProfile forRecord(CSVRecord record) {
    	System.out.println("record.isMapped(\"type\") ? " + record.isMapped("type"));
        String headerKey = record.isMapped("type") ? record.get("type") : record.get(0);
    	System.out.println("headerKey ? " + headerKey);
        String key = MessageTypeUtils.baseType(Optional.ofNullable(headerKey).orElse(""));
    	System.out.println("key ? " + key);
    	System.out.println("挑選到的 profle ? " + PROFILES.getOrDefault(key, ColumnProfile.defaultProfile()));
        return PROFILES.getOrDefault(key, ColumnProfile.defaultProfile());
    }

    public record ColumnProfile(List<String> headers) {

        Map<String, Integer> indexMap() {
            Map<String, Integer> indexes = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                indexes.put(headers.get(i).toLowerCase(Locale.ROOT), i);
            }
            return indexes;
        }

        static ColumnProfile defaultProfile() {
            return new ColumnProfile(List.of(
                "type",
                "invoiceNo",
                "sellerId",
                "buyerId",
                "salesAmount",
                "taxAmount",
                "totalAmount",
                "taxType",
                "invoiceDate",
                "rawLine",
                "legacyType"
            ));
        }
    }
}
