package com.asynctide.turnbridge.domain;

import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Turnkey 回饋訊息（ACK/ERROR/SUMMARY）
 */
@Entity
@Table(name = "turnkey_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TurnkeyMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 訊息 ID
     */
    @NotNull
    @Size(max = 64)
    @Column(name = "message_id", length = 64, nullable = false)
    private String messageId;

    /**
     * 訊息家族
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "message_family", nullable = false)
    private MessageFamily messageFamily;

    /**
     * 種類
     */
    @NotNull
    @Size(max = 32)
    @Column(name = "type", length = 32, nullable = false)
    private String type;

    /**
     * 錯誤碼
     */
    @Size(max = 32)
    @Column(name = "code", length = 32)
    private String code;

    /**
     * 訊息內容
     */
    @Column(name = "message")
    private String message;

    /**
     * XML 路徑
     */
    @Size(max = 512)
    @Column(name = "payload_path", length = 512)
    private String payloadPath;

    /**
     * 接收時間
     */
    @Column(name = "received_at")
    private Instant receivedAt;

    /**
     * 發票主檔
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "importFile", "tenant" }, allowSetters = true)
    private Invoice invoice;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TurnkeyMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public TurnkeyMessage messageId(String messageId) {
        this.setMessageId(messageId);
        return this;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public MessageFamily getMessageFamily() {
        return this.messageFamily;
    }

    public TurnkeyMessage messageFamily(MessageFamily messageFamily) {
        this.setMessageFamily(messageFamily);
        return this;
    }

    public void setMessageFamily(MessageFamily messageFamily) {
        this.messageFamily = messageFamily;
    }

    public String getType() {
        return this.type;
    }

    public TurnkeyMessage type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return this.code;
    }

    public TurnkeyMessage code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public TurnkeyMessage message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayloadPath() {
        return this.payloadPath;
    }

    public TurnkeyMessage payloadPath(String payloadPath) {
        this.setPayloadPath(payloadPath);
        return this;
    }

    public void setPayloadPath(String payloadPath) {
        this.payloadPath = payloadPath;
    }

    public Instant getReceivedAt() {
        return this.receivedAt;
    }

    public TurnkeyMessage receivedAt(Instant receivedAt) {
        this.setReceivedAt(receivedAt);
        return this;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public TurnkeyMessage invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TurnkeyMessage)) {
            return false;
        }
        return getId() != null && getId().equals(((TurnkeyMessage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TurnkeyMessage{" +
            "id=" + getId() +
            ", messageId='" + getMessageId() + "'" +
            ", messageFamily='" + getMessageFamily() + "'" +
            ", type='" + getType() + "'" +
            ", code='" + getCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", payloadPath='" + getPayloadPath() + "'" +
            ", receivedAt='" + getReceivedAt() + "'" +
            "}";
    }
}
