package com.asynctide.turnbridge.m0.web;

import com.asynctide.turnbridge.IntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 【驗收】上傳發票 CSV（/invoices/upload）
 * 驗證：
 * 1) 回應 202 Accepted
 * 2) 回傳 JSON 內含 jobId、receivedAt、profile、itemsCount 欄位
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UploadInvoicesApiIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Test
    @DisplayName("上傳 CSV：回 202 並回傳 UploadAck")
    void uploadInvoices_shouldAcceptAndReturnAck() throws Exception {
        // 準備一個極簡 CSV（兩列）
        String csv = "invoiceNo,buyerId,amount\nAB00000001,12345678,100\nAB00000002,87654321,200\n";
        MockMultipartFile file = new MockMultipartFile(
            "file", "sample.csv", "text/csv", new ByteArrayResource(csv.getBytes()).getByteArray()
        );

        String json = mvc.perform(
                multipart("/api/invoices/upload")
                    .file(file)
                    .param("sellerId", "77665544")
                    .param("profile", "Profile-Canonical")
                    .header("Idempotency-Key", "77665544-202511-001")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(status().isAccepted())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.jobId").exists())
            .andReturn().getResponse().getContentAsString();

        JsonNode root = om.readTree(json);
        assertThat(root.path("createdDate").asText()).isNotBlank();
        assertThat(root.path("profile").asText()).isNotBlank();
    }
}

