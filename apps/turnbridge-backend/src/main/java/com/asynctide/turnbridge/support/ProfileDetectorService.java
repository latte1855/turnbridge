package com.asynctide.turnbridge.support;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProfileDetectorService {

    // 以表頭判斷：Legacy / Canonical（可再擴充）
    public String detectByHeader(Set<String> headers) {
        Set<String> lower = new HashSet<>();
        headers.forEach(h -> lower.add(h.toLowerCase(Locale.ROOT)));

        // Canonical 例：invoiceNo, buyerId, amount
        if (lower.containsAll(Set.of("invoiceno", "buyerid", "amount"))) {
            return "Profile-Canonical";
        }
        // Legacy 例：inv_no, buyer_id, amt
        if (lower.containsAll(Set.of("inv_no", "buyer_id", "amt"))) {
            return "Profile-Legacy";
        }
        return "Profile-Canonical"; // default
    }
}
