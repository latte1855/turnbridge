package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.WebhookEndpointRepository;
import com.asynctide.turnbridge.service.WebhookEndpointQueryService;
import com.asynctide.turnbridge.service.WebhookEndpointService;
import com.asynctide.turnbridge.service.criteria.WebhookEndpointCriteria;
import com.asynctide.turnbridge.service.dto.WebhookEndpointDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.WebhookEndpoint}.
 */
@RestController
@RequestMapping("/api/webhook-endpoints")
public class WebhookEndpointResource {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookEndpointResource.class);

    private static final String ENTITY_NAME = "webhookEndpoint";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WebhookEndpointService webhookEndpointService;

    private final WebhookEndpointRepository webhookEndpointRepository;

    private final WebhookEndpointQueryService webhookEndpointQueryService;

    public WebhookEndpointResource(
        WebhookEndpointService webhookEndpointService,
        WebhookEndpointRepository webhookEndpointRepository,
        WebhookEndpointQueryService webhookEndpointQueryService
    ) {
        this.webhookEndpointService = webhookEndpointService;
        this.webhookEndpointRepository = webhookEndpointRepository;
        this.webhookEndpointQueryService = webhookEndpointQueryService;
    }

    /**
     * {@code POST  /webhook-endpoints} : Create a new webhookEndpoint.
     *
     * @param webhookEndpointDTO the webhookEndpointDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new webhookEndpointDTO, or with status {@code 400 (Bad Request)} if the webhookEndpoint has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WebhookEndpointDTO> createWebhookEndpoint(@Valid @RequestBody WebhookEndpointDTO webhookEndpointDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save WebhookEndpoint : {}", webhookEndpointDTO);
        if (webhookEndpointDTO.getId() != null) {
            throw new BadRequestAlertException("A new webhookEndpoint cannot already have an ID", ENTITY_NAME, "idexists");
        }
        webhookEndpointDTO = webhookEndpointService.save(webhookEndpointDTO);
        return ResponseEntity.created(new URI("/api/webhook-endpoints/" + webhookEndpointDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, webhookEndpointDTO.getId().toString()))
            .body(webhookEndpointDTO);
    }

    /**
     * {@code PUT  /webhook-endpoints/:id} : Updates an existing webhookEndpoint.
     *
     * @param id the id of the webhookEndpointDTO to save.
     * @param webhookEndpointDTO the webhookEndpointDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated webhookEndpointDTO,
     * or with status {@code 400 (Bad Request)} if the webhookEndpointDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the webhookEndpointDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WebhookEndpointDTO> updateWebhookEndpoint(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WebhookEndpointDTO webhookEndpointDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WebhookEndpoint : {}, {}", id, webhookEndpointDTO);
        if (webhookEndpointDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, webhookEndpointDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!webhookEndpointRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        webhookEndpointDTO = webhookEndpointService.update(webhookEndpointDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, webhookEndpointDTO.getId().toString()))
            .body(webhookEndpointDTO);
    }

    /**
     * {@code PATCH  /webhook-endpoints/:id} : Partial updates given fields of an existing webhookEndpoint, field will ignore if it is null
     *
     * @param id the id of the webhookEndpointDTO to save.
     * @param webhookEndpointDTO the webhookEndpointDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated webhookEndpointDTO,
     * or with status {@code 400 (Bad Request)} if the webhookEndpointDTO is not valid,
     * or with status {@code 404 (Not Found)} if the webhookEndpointDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the webhookEndpointDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WebhookEndpointDTO> partialUpdateWebhookEndpoint(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WebhookEndpointDTO webhookEndpointDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update WebhookEndpoint partially : {}, {}", id, webhookEndpointDTO);
        if (webhookEndpointDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, webhookEndpointDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!webhookEndpointRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WebhookEndpointDTO> result = webhookEndpointService.partialUpdate(webhookEndpointDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, webhookEndpointDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /webhook-endpoints} : get all the webhookEndpoints.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of webhookEndpoints in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WebhookEndpointDTO>> getAllWebhookEndpoints(
        WebhookEndpointCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WebhookEndpoints by criteria: {}", criteria);

        Page<WebhookEndpointDTO> page = webhookEndpointQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /webhook-endpoints/count} : count all the webhookEndpoints.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWebhookEndpoints(WebhookEndpointCriteria criteria) {
        LOG.debug("REST request to count WebhookEndpoints by criteria: {}", criteria);
        return ResponseEntity.ok().body(webhookEndpointQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /webhook-endpoints/:id} : get the "id" webhookEndpoint.
     *
     * @param id the id of the webhookEndpointDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the webhookEndpointDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebhookEndpointDTO> getWebhookEndpoint(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WebhookEndpoint : {}", id);
        Optional<WebhookEndpointDTO> webhookEndpointDTO = webhookEndpointService.findOne(id);
        return ResponseUtil.wrapOrNotFound(webhookEndpointDTO);
    }

    /**
     * {@code DELETE  /webhook-endpoints/:id} : delete the "id" webhookEndpoint.
     *
     * @param id the id of the webhookEndpointDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWebhookEndpoint(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WebhookEndpoint : {}", id);
        webhookEndpointService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
