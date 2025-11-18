package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.service.ImportFileItemQueryService;
import com.asynctide.turnbridge.service.ImportFileItemService;
import com.asynctide.turnbridge.service.criteria.ImportFileItemCriteria;
import com.asynctide.turnbridge.service.dto.ImportFileItemDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.ImportFileItem}.
 */
@RestController
@RequestMapping("/api/import-file-items")
public class ImportFileItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(ImportFileItemResource.class);

    private static final String ENTITY_NAME = "importFileItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImportFileItemService importFileItemService;

    private final ImportFileItemRepository importFileItemRepository;

    private final ImportFileItemQueryService importFileItemQueryService;

    public ImportFileItemResource(
        ImportFileItemService importFileItemService,
        ImportFileItemRepository importFileItemRepository,
        ImportFileItemQueryService importFileItemQueryService
    ) {
        this.importFileItemService = importFileItemService;
        this.importFileItemRepository = importFileItemRepository;
        this.importFileItemQueryService = importFileItemQueryService;
    }

    /**
     * {@code POST  /import-file-items} : Create a new importFileItem.
     *
     * @param importFileItemDTO the importFileItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new importFileItemDTO, or with status {@code 400 (Bad Request)} if the importFileItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ImportFileItemDTO> createImportFileItem(@Valid @RequestBody ImportFileItemDTO importFileItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ImportFileItem : {}", importFileItemDTO);
        if (importFileItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new importFileItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        importFileItemDTO = importFileItemService.save(importFileItemDTO);
        return ResponseEntity.created(new URI("/api/import-file-items/" + importFileItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, importFileItemDTO.getId().toString()))
            .body(importFileItemDTO);
    }

    /**
     * {@code PUT  /import-file-items/:id} : Updates an existing importFileItem.
     *
     * @param id the id of the importFileItemDTO to save.
     * @param importFileItemDTO the importFileItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importFileItemDTO,
     * or with status {@code 400 (Bad Request)} if the importFileItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the importFileItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ImportFileItemDTO> updateImportFileItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ImportFileItemDTO importFileItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ImportFileItem : {}, {}", id, importFileItemDTO);
        if (importFileItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importFileItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importFileItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        importFileItemDTO = importFileItemService.update(importFileItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importFileItemDTO.getId().toString()))
            .body(importFileItemDTO);
    }

    /**
     * {@code PATCH  /import-file-items/:id} : Partial updates given fields of an existing importFileItem, field will ignore if it is null
     *
     * @param id the id of the importFileItemDTO to save.
     * @param importFileItemDTO the importFileItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importFileItemDTO,
     * or with status {@code 400 (Bad Request)} if the importFileItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the importFileItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the importFileItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ImportFileItemDTO> partialUpdateImportFileItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ImportFileItemDTO importFileItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ImportFileItem partially : {}, {}", id, importFileItemDTO);
        if (importFileItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importFileItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importFileItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ImportFileItemDTO> result = importFileItemService.partialUpdate(importFileItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importFileItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /import-file-items} : get all the importFileItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of importFileItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ImportFileItemDTO>> getAllImportFileItems(
        ImportFileItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ImportFileItems by criteria: {}", criteria);

        Page<ImportFileItemDTO> page = importFileItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /import-file-items/count} : count all the importFileItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countImportFileItems(ImportFileItemCriteria criteria) {
        LOG.debug("REST request to count ImportFileItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(importFileItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /import-file-items/:id} : get the "id" importFileItem.
     *
     * @param id the id of the importFileItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the importFileItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ImportFileItemDTO> getImportFileItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ImportFileItem : {}", id);
        Optional<ImportFileItemDTO> importFileItemDTO = importFileItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(importFileItemDTO);
    }

    /**
     * {@code DELETE  /import-file-items/:id} : delete the "id" importFileItem.
     *
     * @param id the id of the importFileItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImportFileItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ImportFileItem : {}", id);
        importFileItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
