package com.asynctide.turnbridge.service.dto;

import java.io.Serializable;

/**
 * Turnkey Pickup 指標中的 stage/family 細項。
 */
public class TurnkeyPickupStageDTO implements Serializable {

    private String stage;
    private String family;
    private long count;

    public TurnkeyPickupStageDTO() {}

    public TurnkeyPickupStageDTO(String stage, String family, long count) {
        this.stage = stage;
        this.family = family;
        this.count = count;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
