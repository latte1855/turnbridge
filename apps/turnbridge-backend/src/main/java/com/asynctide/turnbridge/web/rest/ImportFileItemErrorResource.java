package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.service.ImportFileItemErrorQueryService;
import com.asynctide.turnbridge.service.ImportFileItemErrorService;
import com.asynctide.turnbridge.service.criteria.ImportFileItemErrorCriteria;
import com.asynctide.turnbridge.service.dto.ImportFileItemErrorDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.ImportFileItemError}.
 */
@RestController
@RequestMapping("/api/import-file-item-errors")
public class ImportFileItemErrorResource {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileItemErrorResource.class);

    private static final String ENTITY_NAME = "importFileItemError";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImportFileItemErrorService importFileItemErrorService;

    private final ImportFileItemErrorRepository importFileItemErrorRepository;

    private final ImportFileItemErrorQueryService importFileItemErrorQueryService;

    public ImportFileItemErrorResource(
        ImportFileItemErrorService importFileItemErrorService,
        ImportFileItemErrorRepository importFileItemErrorRepository,
        ImportFileItemErrorQueryService importFileItemErrorQueryService
    ) {
        this.importFileItemErrorService = importFileItemErrorService;
        this.importFileItemErrorRepository = importFileItemErrorRepository;
        this.importFileItemErrorQueryService = importFileItemErrorQueryService;
    }

    /**
     * {@code POST  /import-file-item-errors} : Create a new importFileItemError.
     *
     * @param importFileItemErrorDTO the importFileItemErrorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new importFileItemErrorDTO, or with status {@code 400 (Bad Request)} if the importFileItemError has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ImportFileItemErrorDTO> createImportFileItemError(
        @Valid @RequestBody ImportFileItemErrorDTO importFileItemErrorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ImportFileItemError : {}", importFileItemErrorDTO);
        if (importFileItemErrorDTO.getId() != null) {
            throw new BadRequestAlertException("A new importFileItemError cannot already have an ID", ENTITY_NAME, "idexists");
        }
        importFileItemErrorDTO = importFileItemErrorService.save(importFileItemErrorDTO);
        return ResponseEntity.created(new URI("/api/import-file-item-errors/" + importFileItemErrorDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, importFileItemErrorDTO.getId().toString()))
            .body(importFileItemErrorDTO);
    }

    /**
     * {@code PUT  /import-file-item-errors/:id} : Updates an existing importFileItemError.
     *
     * @param id the id of the importFileItemErrorDTO to save.
     * @param importFileItemErrorDTO the importFileItemErrorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importFileItemErrorDTO,
     * or with status {@code 400 (Bad Request)} if the importFileItemErrorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the importFileItemErrorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ImportFileItemErrorDTO> updateImportFileItemError(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ImportFileItemErrorDTO importFileItemErrorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ImportFileItemError : {}, {}", id, importFileItemErrorDTO);
        if (importFileItemErrorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importFileItemErrorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importFileItemErrorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        importFileItemErrorDTO = importFileItemErrorService.update(importFileItemErrorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importFileItemErrorDTO.getId().toString()))
            .body(importFileItemErrorDTO);
    }

    /**
     * {@code PATCH  /import-file-item-errors/:id} : Partial updates given fields of an existing importFileItemError, field will ignore if it is null
     *
     * @param id the id of the importFileItemErrorDTO to save.
     * @param importFileItemErrorDTO the importFileItemErrorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importFileItemErrorDTO,
     * or with status {@code 400 (Bad Request)} if the importFileItemErrorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the importFileItemErrorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the importFileItemErrorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ImportFileItemErrorDTO> partialUpdateImportFileItemError(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ImportFileItemErrorDTO importFileItemErrorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ImportFileItemError partially : {}, {}", id, importFileItemErrorDTO);
        if (importFileItemErrorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importFileItemErrorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importFileItemErrorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ImportFileItemErrorDTO> result = importFileItemErrorService.partialUpdate(importFileItemErrorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importFileItemErrorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /import-file-item-errors} : get all the importFileItemErrors.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of importFileItemErrors in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ImportFileItemErrorDTO>> getAllImportFileItemErrors(
        ImportFileItemErrorCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ImportFileItemErrors by criteria: {}", criteria);

        Page<ImportFileItemErrorDTO> page = importFileItemErrorQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /import-file-item-errors/count} : count all the importFileItemErrors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countImportFileItemErrors(ImportFileItemErrorCriteria criteria) {
        LOG.debug("REST request to count ImportFileItemErrors by criteria: {}", criteria);
        return ResponseEntity.ok().body(importFileItemErrorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /import-file-item-errors/:id} : get the "id" importFileItemError.
     *
     * @param id the id of the importFileItemErrorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the importFileItemErrorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ImportFileItemErrorDTO> getImportFileItemError(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ImportFileItemError : {}", id);
        Optional<ImportFileItemErrorDTO> importFileItemErrorDTO = importFileItemErrorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(importFileItemErrorDTO);
    }

    /**
     * {@code DELETE  /import-file-item-errors/:id} : delete the "id" importFileItemError.
     *
     * @param id the id of the importFileItemErrorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImportFileItemError(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ImportFileItemError : {}", id);
        importFileItemErrorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
