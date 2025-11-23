package com.asynctide.turnbridge.service.turnkey;

import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import gov.nat.einvoice.tky.gateway.msg.EINVPayload;

/**
 * Turnkey XML 產出的封裝結果。
 *
 * @param messageFamily 訊息別
 * @param xml 產生的 XML 字串
 * @param payload 原始 Payload（可用於後續調整或驗證）
 */
public record TurnkeyXmlBuildResult(MessageFamily messageFamily, String xml, EINVPayload payload) {}
