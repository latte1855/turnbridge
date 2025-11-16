package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.service.ImportFileQueryService;
import com.asynctide.turnbridge.service.ImportFileService;
import com.asynctide.turnbridge.service.criteria.ImportFileCriteria;
import com.asynctide.turnbridge.service.dto.ImportFileDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.ImportFile}.
 */
@RestController
@RequestMapping("/api/import-files")
public class ImportFileResource {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileResource.class);

    private static final String ENTITY_NAME = "importFile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImportFileService importFileService;

    private final ImportFileRepository importFileRepository;

    private final ImportFileQueryService importFileQueryService;

    public ImportFileResource(
        ImportFileService importFileService,
        ImportFileRepository importFileRepository,
        ImportFileQueryService importFileQueryService
    ) {
        this.importFileService = importFileService;
        this.importFileRepository = importFileRepository;
        this.importFileQueryService = importFileQueryService;
    }

    /**
     * {@code POST  /import-files} : Create a new importFile.
     *
     * @param importFileDTO the importFileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new importFileDTO, or with status {@code 400 (Bad Request)} if the importFile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ImportFileDTO> createImportFile(@Valid @RequestBody ImportFileDTO importFileDTO) throws URISyntaxException {
        LOG.debug("REST request to save ImportFile : {}", importFileDTO);
        if (importFileDTO.getId() != null) {
            throw new BadRequestAlertException("A new importFile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        importFileDTO = importFileService.save(importFileDTO);
        return ResponseEntity.created(new URI("/api/import-files/" + importFileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, importFileDTO.getId().toString()))
            .body(importFileDTO);
    }

    /**
     * {@code PUT  /import-files/:id} : Updates an existing importFile.
     *
     * @param id the id of the importFileDTO to save.
     * @param importFileDTO the importFileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importFileDTO,
     * or with status {@code 400 (Bad Request)} if the importFileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the importFileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ImportFileDTO> updateImportFile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ImportFileDTO importFileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ImportFile : {}, {}", id, importFileDTO);
        if (importFileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importFileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importFileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        importFileDTO = importFileService.update(importFileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importFileDTO.getId().toString()))
            .body(importFileDTO);
    }

    /**
     * {@code PATCH  /import-files/:id} : Partial updates given fields of an existing importFile, field will ignore if it is null
     *
     * @param id the id of the importFileDTO to save.
     * @param importFileDTO the importFileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importFileDTO,
     * or with status {@code 400 (Bad Request)} if the importFileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the importFileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the importFileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ImportFileDTO> partialUpdateImportFile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ImportFileDTO importFileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ImportFile partially : {}, {}", id, importFileDTO);
        if (importFileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importFileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importFileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ImportFileDTO> result = importFileService.partialUpdate(importFileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importFileDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /import-files} : get all the importFiles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of importFiles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ImportFileDTO>> getAllImportFiles(
        ImportFileCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ImportFiles by criteria: {}", criteria);

        Page<ImportFileDTO> page = importFileQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /import-files/count} : count all the importFiles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countImportFiles(ImportFileCriteria criteria) {
        LOG.debug("REST request to count ImportFiles by criteria: {}", criteria);
        return ResponseEntity.ok().body(importFileQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /import-files/:id} : get the "id" importFile.
     *
     * @param id the id of the importFileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the importFileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ImportFileDTO> getImportFile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ImportFile : {}", id);
        Optional<ImportFileDTO> importFileDTO = importFileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(importFileDTO);
    }

    /**
     * {@code DELETE  /import-files/:id} : delete the "id" importFile.
     *
     * @param id the id of the importFileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImportFile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ImportFile : {}", id);
        importFileService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
