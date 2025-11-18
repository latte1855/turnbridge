package com.asynctide.turnbridge.service.upload;

import com.asynctide.turnbridge.domain.enumeration.ImportStatus;

/**
 * 上傳 API 的回應物件。
 */
public record UploadResponse(Long importId, ImportStatus status, String message) {}
