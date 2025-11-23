package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.security.AuthoritiesConstants;
import com.asynctide.turnbridge.service.dto.TurnkeyPickupStageDTO;
import com.asynctide.turnbridge.service.dto.TurnkeyPickupStatusDTO;
import com.asynctide.turnbridge.service.turnkey.TurnkeyPickupMonitor;
import com.asynctide.turnbridge.service.turnkey.TurnkeyPickupMonitor.PickupSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供 Turnkey Pickup 監控資訊給 Portal/Ops 使用。
 */
@RestController
@RequestMapping("/api/turnkey")
public class TurnkeyPickupResource {

    private final TurnkeyPickupMonitor pickupMonitor;

    public TurnkeyPickupResource(TurnkeyPickupMonitor pickupMonitor) {
        this.pickupMonitor = pickupMonitor;
    }

    /**
     * 取得最近一次巡檢結果。
     */
    @GetMapping("/pickup-status")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<TurnkeyPickupStatusDTO> getPickupStatus() {
        return pickupMonitor
            .getLatestSnapshot()
            .map(this::toDto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private TurnkeyPickupStatusDTO toDto(PickupSnapshot snapshot) {
        TurnkeyPickupStatusDTO dto = new TurnkeyPickupStatusDTO();
        dto.setLastScanEpoch(snapshot.lastScanEpoch());
        dto.setSrcStuck(snapshot.srcStuck());
        dto.setPackStuck(snapshot.packStuck());
        dto.setUploadPending(snapshot.uploadPending());
        dto.setErr(snapshot.err());
        dto.setAlertTriggered(snapshot.alertTriggered());
        dto.setStages(toStageDtos(snapshot.stageFamilyCounts()));
        return dto;
    }

    private List<TurnkeyPickupStageDTO> toStageDtos(Map<String, Long> counts) {
        List<TurnkeyPickupStageDTO> stages = new ArrayList<>();
        counts.forEach((key, value) -> {
            String[] parts = key.split("\\|");
            String stage = parts.length > 0 ? parts[0] : "";
            String family = parts.length > 1 ? parts[1] : "";
            stages.add(new TurnkeyPickupStageDTO(stage, family, value != null ? value : 0L));
        });
        return stages;
    }
}
