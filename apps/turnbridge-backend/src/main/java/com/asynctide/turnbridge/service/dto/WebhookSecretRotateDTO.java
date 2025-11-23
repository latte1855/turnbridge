package com.asynctide.turnbridge.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;

/**
 * Webhook Secret 旋轉後回傳給 Portal 的資訊。
 */
@Schema(description = "Webhook Secret 旋轉後回傳給 Portal 的資訊。")
public class WebhookSecretRotateDTO implements Serializable {

    private Long id;
    private String secret;
    private Instant rotatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Instant getRotatedAt() {
        return rotatedAt;
    }

    public void setRotatedAt(Instant rotatedAt) {
        this.rotatedAt = rotatedAt;
    }
}
