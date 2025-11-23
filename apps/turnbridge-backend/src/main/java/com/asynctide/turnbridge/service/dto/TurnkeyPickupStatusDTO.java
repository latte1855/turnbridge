package com.asynctide.turnbridge.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 代表 TurnkeyPickupMonitor 最新巡檢結果。
 */
public class TurnkeyPickupStatusDTO implements Serializable {

    private long lastScanEpoch;
    private long srcStuck;
    private long packStuck;
    private long uploadPending;
    private long err;
    private boolean alertTriggered;
    private List<TurnkeyPickupStageDTO> stages = new ArrayList<>();

    public long getLastScanEpoch() {
        return lastScanEpoch;
    }

    public void setLastScanEpoch(long lastScanEpoch) {
        this.lastScanEpoch = lastScanEpoch;
    }

    public long getSrcStuck() {
        return srcStuck;
    }

    public void setSrcStuck(long srcStuck) {
        this.srcStuck = srcStuck;
    }

    public long getPackStuck() {
        return packStuck;
    }

    public void setPackStuck(long packStuck) {
        this.packStuck = packStuck;
    }

    public long getUploadPending() {
        return uploadPending;
    }

    public void setUploadPending(long uploadPending) {
        this.uploadPending = uploadPending;
    }

    public long getErr() {
        return err;
    }

    public void setErr(long err) {
        this.err = err;
    }

    public boolean isAlertTriggered() {
        return alertTriggered;
    }

    public void setAlertTriggered(boolean alertTriggered) {
        this.alertTriggered = alertTriggered;
    }

    public List<TurnkeyPickupStageDTO> getStages() {
        return stages;
    }

    public void setStages(List<TurnkeyPickupStageDTO> stages) {
        this.stages = stages;
    }
}
