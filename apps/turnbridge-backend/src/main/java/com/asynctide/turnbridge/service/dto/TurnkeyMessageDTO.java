package com.asynctide.turnbridge.service.dto;

import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.asynctide.turnbridge.domain.TurnkeyMessage} entity.
 */
@Schema(description = "Turnkey 回饋訊息（ACK/ERROR/SUMMARY）")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TurnkeyMessageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 64)
    @Schema(description = "訊息 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String messageId;

    @NotNull
    @Schema(description = "訊息家族", requiredMode = Schema.RequiredMode.REQUIRED)
    private MessageFamily messageFamily;

    @NotNull
    @Size(max = 32)
    @Schema(description = "種類", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @Size(max = 32)
    @Schema(description = "錯誤碼")
    private String code;

    @Schema(description = "訊息內容")
    @Lob
    private String message;

    @Size(max = 512)
    @Schema(description = "XML 路徑")
    private String payloadPath;

    @Schema(description = "接收時間")
    private Instant receivedAt;

    @Schema(description = "發票主檔")
    private InvoiceDTO invoice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public MessageFamily getMessageFamily() {
        return messageFamily;
    }

    public void setMessageFamily(MessageFamily messageFamily) {
        this.messageFamily = messageFamily;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayloadPath() {
        return payloadPath;
    }

    public void setPayloadPath(String payloadPath) {
        this.payloadPath = payloadPath;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public InvoiceDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TurnkeyMessageDTO)) {
            return false;
        }

        TurnkeyMessageDTO turnkeyMessageDTO = (TurnkeyMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, turnkeyMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TurnkeyMessageDTO{" +
            "id=" + getId() +
            ", messageId='" + getMessageId() + "'" +
            ", messageFamily='" + getMessageFamily() + "'" +
            ", type='" + getType() + "'" +
            ", code='" + getCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", payloadPath='" + getPayloadPath() + "'" +
            ", receivedAt='" + getReceivedAt() + "'" +
            ", invoice=" + getInvoice() +
            "}";
    }
}
