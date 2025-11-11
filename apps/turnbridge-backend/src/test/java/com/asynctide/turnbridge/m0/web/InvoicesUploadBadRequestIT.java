package com.asynctide.turnbridge.m0.web;

import com.asynctide.turnbridge.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InvoicesUploadBadRequestIT {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("不合法的附檔名 → 400")
    void reject_bad_extension() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "evil.exe", "application/octet-stream", new byte[]{1,2,3}
        );
        mvc.perform(
            multipart("/api/invoices/upload")
                .file(file)
                .param("sellerId", "24567891")
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isBadRequest());
    }
}
