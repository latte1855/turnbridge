package com.asynctide.turnbridge.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.security.AuthoritiesConstants;
import com.asynctide.turnbridge.service.turnkey.TurnkeyPickupMonitor;
import com.asynctide.turnbridge.service.turnkey.TurnkeyPickupMonitor.PickupSnapshot;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

/**
 * 測試 TurnkeyPickupResource。
 */
@IntegrationTest
@AutoConfigureMockMvc
class TurnkeyPickupResourceIT {

    @Autowired
    private MockMvc restMockMvc;

    @MockBean
    private TurnkeyPickupMonitor pickupMonitor;

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void getPickupStatus_shouldReturnSnapshot() throws Exception {
        PickupSnapshot snapshot = new PickupSnapshot(1700000000L, 3, 1, 4, 2, true, Map.of("SRC|F0401", 3L, "ERR|G0401", 2L));
        when(pickupMonitor.getLatestSnapshot()).thenReturn(Optional.of(snapshot));

        restMockMvc
            .perform(get("/api/turnkey/pickup-status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastScanEpoch").value(1700000000L))
            .andExpect(jsonPath("$.srcStuck").value(3))
            .andExpect(jsonPath("$.stages[0].stage").value("SRC"));
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void getPickupStatus_whenNoSnapshot_shouldReturnNoContent() throws Exception {
        when(pickupMonitor.getLatestSnapshot()).thenReturn(Optional.empty());

        restMockMvc.perform(get("/api/turnkey/pickup-status")).andExpect(status().isNoContent());
    }
}
