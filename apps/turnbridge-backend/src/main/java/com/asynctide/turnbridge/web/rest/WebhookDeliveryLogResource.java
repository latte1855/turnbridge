package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.WebhookDeliveryLogRepository;
import com.asynctide.turnbridge.service.WebhookDeliveryLogQueryService;
import com.asynctide.turnbridge.service.WebhookDeliveryLogService;
import com.asynctide.turnbridge.service.criteria.WebhookDeliveryLogCriteria;
import com.asynctide.turnbridge.service.dto.WebhookDeliveryLogDTO;
import com.asynctide.turnbridge.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.asynctide.turnbridge.domain.WebhookDeliveryLog}.
 */
@RestController
@RequestMapping("/api/webhook-delivery-logs")
public class WebhookDeliveryLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookDeliveryLogResource.class);

    private static final String ENTITY_NAME = "webhookDeliveryLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WebhookDeliveryLogService webhookDeliveryLogService;

    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;

    private final WebhookDeliveryLogQueryService webhookDeliveryLogQueryService;

    public WebhookDeliveryLogResource(
        WebhookDeliveryLogService webhookDeliveryLogService,
        WebhookDeliveryLogRepository webhookDeliveryLogRepository,
        WebhookDeliveryLogQueryService webhookDeliveryLogQueryService
    ) {
        this.webhookDeliveryLogService = webhookDeliveryLogService;
        this.webhookDeliveryLogRepository = webhookDeliveryLogRepository;
        this.webhookDeliveryLogQueryService = webhookDeliveryLogQueryService;
    }

    /**
     * {@code POST  /webhook-delivery-logs} : Create a new webhookDeliveryLog.
     *
     * @param webhookDeliveryLogDTO the webhookDeliveryLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new webhookDeliveryLogDTO, or with status {@code 400 (Bad Request)} if the webhookDeliveryLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WebhookDeliveryLogDTO> createWebhookDeliveryLog(@Valid @RequestBody WebhookDeliveryLogDTO webhookDeliveryLogDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save WebhookDeliveryLog : {}", webhookDeliveryLogDTO);
        if (webhookDeliveryLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new webhookDeliveryLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        webhookDeliveryLogDTO = webhookDeliveryLogService.save(webhookDeliveryLogDTO);
        return ResponseEntity.created(new URI("/api/webhook-delivery-logs/" + webhookDeliveryLogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, webhookDeliveryLogDTO.getId().toString()))
            .body(webhookDeliveryLogDTO);
    }

    /**
     * {@code PUT  /webhook-delivery-logs/:id} : Updates an existing webhookDeliveryLog.
     *
     * @param id the id of the webhookDeliveryLogDTO to save.
     * @param webhookDeliveryLogDTO the webhookDeliveryLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated webhookDeliveryLogDTO,
     * or with status {@code 400 (Bad Request)} if the webhookDeliveryLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the webhookDeliveryLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WebhookDeliveryLogDTO> updateWebhookDeliveryLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WebhookDeliveryLogDTO webhookDeliveryLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WebhookDeliveryLog : {}, {}", id, webhookDeliveryLogDTO);
        if (webhookDeliveryLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, webhookDeliveryLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!webhookDeliveryLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        webhookDeliveryLogDTO = webhookDeliveryLogService.update(webhookDeliveryLogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, webhookDeliveryLogDTO.getId().toString()))
            .body(webhookDeliveryLogDTO);
    }

    /**
     * {@code PATCH  /webhook-delivery-logs/:id} : Partial updates given fields of an existing webhookDeliveryLog, field will ignore if it is null
     *
     * @param id the id of the webhookDeliveryLogDTO to save.
     * @param webhookDeliveryLogDTO the webhookDeliveryLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated webhookDeliveryLogDTO,
     * or with status {@code 400 (Bad Request)} if the webhookDeliveryLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the webhookDeliveryLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the webhookDeliveryLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WebhookDeliveryLogDTO> partialUpdateWebhookDeliveryLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WebhookDeliveryLogDTO webhookDeliveryLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update WebhookDeliveryLog partially : {}, {}", id, webhookDeliveryLogDTO);
        if (webhookDeliveryLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, webhookDeliveryLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!webhookDeliveryLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WebhookDeliveryLogDTO> result = webhookDeliveryLogService.partialUpdate(webhookDeliveryLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, webhookDeliveryLogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /webhook-delivery-logs} : get all the webhookDeliveryLogs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of webhookDeliveryLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WebhookDeliveryLogDTO>> getAllWebhookDeliveryLogs(
        WebhookDeliveryLogCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WebhookDeliveryLogs by criteria: {}", criteria);

        Page<WebhookDeliveryLogDTO> page = webhookDeliveryLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /webhook-delivery-logs/count} : count all the webhookDeliveryLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWebhookDeliveryLogs(WebhookDeliveryLogCriteria criteria) {
        LOG.debug("REST request to count WebhookDeliveryLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(webhookDeliveryLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /webhook-delivery-logs/:id} : get the "id" webhookDeliveryLog.
     *
     * @param id the id of the webhookDeliveryLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the webhookDeliveryLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebhookDeliveryLogDTO> getWebhookDeliveryLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WebhookDeliveryLog : {}", id);
        Optional<WebhookDeliveryLogDTO> webhookDeliveryLogDTO = webhookDeliveryLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(webhookDeliveryLogDTO);
    }

    /**
     * {@code DELETE  /webhook-delivery-logs/:id} : delete the "id" webhookDeliveryLog.
     *
     * @param id the id of the webhookDeliveryLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWebhookDeliveryLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WebhookDeliveryLog : {}", id);
        webhookDeliveryLogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
