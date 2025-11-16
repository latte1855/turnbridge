package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.ManualActionRepository;
import com.asynctide.turnbridge.service.ManualActionQueryService;
import com.asynctide.turnbridge.service.ManualActionService;
import com.asynctide.turnbridge.service.criteria.ManualActionCriteria;
import com.asynctide.turnbridge.service.dto.ManualActionDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.asynctide.turnbridge.domain.ManualAction}.
 */
@RestController
@RequestMapping("/api/manual-actions")
public class ManualActionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ManualActionResource.class);

    private static final String ENTITY_NAME = "manualAction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ManualActionService manualActionService;

    private final ManualActionRepository manualActionRepository;

    private final ManualActionQueryService manualActionQueryService;

    public ManualActionResource(
        ManualActionService manualActionService,
        ManualActionRepository manualActionRepository,
        ManualActionQueryService manualActionQueryService
    ) {
        this.manualActionService = manualActionService;
        this.manualActionRepository = manualActionRepository;
        this.manualActionQueryService = manualActionQueryService;
    }

    /**
     * {@code POST  /manual-actions} : Create a new manualAction.
     *
     * @param manualActionDTO the manualActionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new manualActionDTO, or with status {@code 400 (Bad Request)} if the manualAction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ManualActionDTO> createManualAction(@Valid @RequestBody ManualActionDTO manualActionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ManualAction : {}", manualActionDTO);
        if (manualActionDTO.getId() != null) {
            throw new BadRequestAlertException("A new manualAction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        manualActionDTO = manualActionService.save(manualActionDTO);
        return ResponseEntity.created(new URI("/api/manual-actions/" + manualActionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, manualActionDTO.getId().toString()))
            .body(manualActionDTO);
    }

    /**
     * {@code PUT  /manual-actions/:id} : Updates an existing manualAction.
     *
     * @param id the id of the manualActionDTO to save.
     * @param manualActionDTO the manualActionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated manualActionDTO,
     * or with status {@code 400 (Bad Request)} if the manualActionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the manualActionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ManualActionDTO> updateManualAction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ManualActionDTO manualActionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ManualAction : {}, {}", id, manualActionDTO);
        if (manualActionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, manualActionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!manualActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        manualActionDTO = manualActionService.update(manualActionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, manualActionDTO.getId().toString()))
            .body(manualActionDTO);
    }

    /**
     * {@code PATCH  /manual-actions/:id} : Partial updates given fields of an existing manualAction, field will ignore if it is null
     *
     * @param id the id of the manualActionDTO to save.
     * @param manualActionDTO the manualActionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated manualActionDTO,
     * or with status {@code 400 (Bad Request)} if the manualActionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the manualActionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the manualActionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ManualActionDTO> partialUpdateManualAction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ManualActionDTO manualActionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ManualAction partially : {}, {}", id, manualActionDTO);
        if (manualActionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, manualActionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!manualActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ManualActionDTO> result = manualActionService.partialUpdate(manualActionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, manualActionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /manual-actions} : get all the manualActions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of manualActions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ManualActionDTO>> getAllManualActions(ManualActionCriteria criteria) {
        LOG.debug("REST request to get ManualActions by criteria: {}", criteria);

        List<ManualActionDTO> entityList = manualActionQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /manual-actions/count} : count all the manualActions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countManualActions(ManualActionCriteria criteria) {
        LOG.debug("REST request to count ManualActions by criteria: {}", criteria);
        return ResponseEntity.ok().body(manualActionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /manual-actions/:id} : get the "id" manualAction.
     *
     * @param id the id of the manualActionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the manualActionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ManualActionDTO> getManualAction(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ManualAction : {}", id);
        Optional<ManualActionDTO> manualActionDTO = manualActionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(manualActionDTO);
    }

    /**
     * {@code DELETE  /manual-actions/:id} : delete the "id" manualAction.
     *
     * @param id the id of the manualActionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManualAction(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ManualAction : {}", id);
        manualActionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
