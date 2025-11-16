package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.service.ImportFileLogQueryService;
import com.asynctide.turnbridge.service.ImportFileLogService;
import com.asynctide.turnbridge.service.criteria.ImportFileLogCriteria;
import com.asynctide.turnbridge.service.dto.ImportFileLogDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.ImportFileLog}.
 */
@RestController
@RequestMapping("/api/import-file-logs")
public class ImportFileLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileLogResource.class);

    private static final String ENTITY_NAME = "importFileLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImportFileLogService importFileLogService;

    private final ImportFileLogRepository importFileLogRepository;

    private final ImportFileLogQueryService importFileLogQueryService;

    public ImportFileLogResource(
        ImportFileLogService importFileLogService,
        ImportFileLogRepository importFileLogRepository,
        ImportFileLogQueryService importFileLogQueryService
    ) {
        this.importFileLogService = importFileLogService;
        this.importFileLogRepository = importFileLogRepository;
        this.importFileLogQueryService = importFileLogQueryService;
    }

    /**
     * {@code POST  /import-file-logs} : Create a new importFileLog.
     *
     * @param importFileLogDTO the importFileLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new importFileLogDTO, or with status {@code 400 (Bad Request)} if the importFileLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ImportFileLogDTO> createImportFileLog(@Valid @RequestBody ImportFileLogDTO importFileLogDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ImportFileLog : {}", importFileLogDTO);
        if (importFileLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new importFileLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        importFileLogDTO = importFileLogService.save(importFileLogDTO);
        return ResponseEntity.created(new URI("/api/import-file-logs/" + importFileLogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, importFileLogDTO.getId().toString()))
            .body(importFileLogDTO);
    }

    /**
     * {@code PUT  /import-file-logs/:id} : Updates an existing importFileLog.
     *
     * @param id the id of the importFileLogDTO to save.
     * @param importFileLogDTO the importFileLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importFileLogDTO,
     * or with status {@code 400 (Bad Request)} if the importFileLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the importFileLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ImportFileLogDTO> updateImportFileLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ImportFileLogDTO importFileLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ImportFileLog : {}, {}", id, importFileLogDTO);
        if (importFileLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importFileLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importFileLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        importFileLogDTO = importFileLogService.update(importFileLogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importFileLogDTO.getId().toString()))
            .body(importFileLogDTO);
    }

    /**
     * {@code PATCH  /import-file-logs/:id} : Partial updates given fields of an existing importFileLog, field will ignore if it is null
     *
     * @param id the id of the importFileLogDTO to save.
     * @param importFileLogDTO the importFileLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importFileLogDTO,
     * or with status {@code 400 (Bad Request)} if the importFileLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the importFileLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the importFileLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ImportFileLogDTO> partialUpdateImportFileLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ImportFileLogDTO importFileLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ImportFileLog partially : {}, {}", id, importFileLogDTO);
        if (importFileLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importFileLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importFileLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ImportFileLogDTO> result = importFileLogService.partialUpdate(importFileLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importFileLogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /import-file-logs} : get all the importFileLogs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of importFileLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ImportFileLogDTO>> getAllImportFileLogs(
        ImportFileLogCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ImportFileLogs by criteria: {}", criteria);

        Page<ImportFileLogDTO> page = importFileLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /import-file-logs/count} : count all the importFileLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countImportFileLogs(ImportFileLogCriteria criteria) {
        LOG.debug("REST request to count ImportFileLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(importFileLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /import-file-logs/:id} : get the "id" importFileLog.
     *
     * @param id the id of the importFileLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the importFileLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ImportFileLogDTO> getImportFileLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ImportFileLog : {}", id);
        Optional<ImportFileLogDTO> importFileLogDTO = importFileLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(importFileLogDTO);
    }

    /**
     * {@code DELETE  /import-file-logs/:id} : delete the "id" importFileLog.
     *
     * @param id the id of the importFileLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImportFileLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ImportFileLog : {}", id);
        importFileLogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
