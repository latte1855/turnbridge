package com.asynctide.turnbridge.service.upload;

import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 提供 legacyType（C0401 / C0501 / C0701）到 MIG4.x MessageFamily 的對應。
 */
public final class LegacyMessageFamilyMapper {

    private static final Map<String, MessageFamily> MAPPING = Stream.of(
            Map.entry("C0401", MessageFamily.F0401),
            Map.entry("C0501", MessageFamily.F0501),
            Map.entry("C0701", MessageFamily.F0701),
            Map.entry("F0401", MessageFamily.F0401),
            Map.entry("F0501", MessageFamily.F0501),
            Map.entry("F0701", MessageFamily.F0701),
            Map.entry("G0401", MessageFamily.G0401),
            Map.entry("G0501", MessageFamily.G0501)
        )
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private LegacyMessageFamilyMapper() {}

    public static MessageFamily map(String baseType) {
        if (baseType == null) {
            return null;
        }
        return MAPPING.get(baseType.trim().toUpperCase());
    }

    public static Set<String> supportedLegacyTypes() {
        return MAPPING.keySet().stream().collect(Collectors.toSet());
    }
}
