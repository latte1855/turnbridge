package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.service.WebhookDashboardService;
import com.asynctide.turnbridge.service.dto.WebhookDashboardDTOs.TbSummaryDTO;
import com.asynctide.turnbridge.service.dto.WebhookDashboardDTOs.WebhookDlqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Webhook Dashboard")
public class WebhookDashboardResource {

    private final WebhookDashboardService dashboardService;

    public WebhookDashboardResource(WebhookDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/webhook-tb-summary")
    @Operation(summary = "Get TB summary")
    public ResponseEntity<List<TbSummaryDTO>> getSummary() {
        return ResponseEntity.ok(dashboardService.getTbSummary());
    }

    @GetMapping("/webhook-dlq")
    @Operation(summary = "Get Webhook DLQ")
    public ResponseEntity<List<WebhookDlqDTO>> getDlq(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WebhookDlqDTO> page = dashboardService.getDlq(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/webhook-dlq/{id}/resend")
    @Operation(summary = "Resend DLQ entry")
    public ResponseEntity<Void> resend(@PathVariable Long id) {
        dashboardService.resendDelivery(id);
        return ResponseEntity.noContent().build();
    }
}
