package com.asynctide.turnbridge.m0.web;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StoredObjectDownloadApiIT {

    @Autowired MockMvc mvc;
    @Autowired StoredObjectRepository repo;

    StoredObject so;


    private static String sha256Hex(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(bytes);
        StringBuilder sb = new StringBuilder(d.length * 2);
        for (byte b : d) sb.append(String.format("%02x", b));
        return sb.toString();
    }
    
    @BeforeEach
    void setUp() throws Exception {
        String baseDir = System.getProperty("java.io.tmpdir") + "/turnbridge-it-storage";
        Path path = Path.of(baseDir, "misc/hello.txt");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "hello");
        
        byte[] resultBytes = Files.readAllBytes(path);
        String shaResult = sha256Hex(resultBytes);

        so = new StoredObject();
        so.setBucket("misc");
        so.setObjectKey("hello.txt");
        so.setMediaType("text/plain");
        so.setContentLength(5L);
        so.setSha256(shaResult);
        so.setPurpose(StoragePurpose.OTHER);
        so.setFilename("hello.txt");
        so.setStorageClass("STANDARD");
        so.setEncryption("NONE");
        so.setCreatedDate(Instant.now());
        so.setLastModifiedDate(Instant.now());
        so = repo.saveAndFlush(so);
    }

    @Test
    void download_ok() throws Exception {
        byte[] bytes = mvc.perform(get("/api/stored-objects/{id}/download?bom=true", so.getId()))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.TEXT_PLAIN_VALUE))
            .andReturn().getResponse().getContentAsByteArray();

        // BOM 存在且內容接續 "hello"
        assertThat(bytes[0]).isEqualTo((byte)0xEF);
        assertThat(new String(bytes, java.nio.charset.StandardCharsets.UTF_8)).contains("hello");
    }
}
