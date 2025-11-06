package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.UploadJobItemRepository;
import com.asynctide.turnbridge.service.UploadJobItemQueryService;
import com.asynctide.turnbridge.service.UploadJobItemService;
import com.asynctide.turnbridge.service.criteria.UploadJobItemCriteria;
import com.asynctide.turnbridge.service.dto.UploadJobItemDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.UploadJobItem}.
 */
@RestController
@RequestMapping("/api/upload-job-items")
public class UploadJobItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(UploadJobItemResource.class);

    private static final String ENTITY_NAME = "uploadJobItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UploadJobItemService uploadJobItemService;

    private final UploadJobItemRepository uploadJobItemRepository;

    private final UploadJobItemQueryService uploadJobItemQueryService;

    public UploadJobItemResource(
        UploadJobItemService uploadJobItemService,
        UploadJobItemRepository uploadJobItemRepository,
        UploadJobItemQueryService uploadJobItemQueryService
    ) {
        this.uploadJobItemService = uploadJobItemService;
        this.uploadJobItemRepository = uploadJobItemRepository;
        this.uploadJobItemQueryService = uploadJobItemQueryService;
    }

    /**
     * {@code POST  /upload-job-items} : Create a new uploadJobItem.
     *
     * @param uploadJobItemDTO the uploadJobItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uploadJobItemDTO, or with status {@code 400 (Bad Request)} if the uploadJobItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UploadJobItemDTO> createUploadJobItem(@Valid @RequestBody UploadJobItemDTO uploadJobItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UploadJobItem : {}", uploadJobItemDTO);
        if (uploadJobItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new uploadJobItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        uploadJobItemDTO = uploadJobItemService.save(uploadJobItemDTO);
        return ResponseEntity.created(new URI("/api/upload-job-items/" + uploadJobItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, uploadJobItemDTO.getId().toString()))
            .body(uploadJobItemDTO);
    }

    /**
     * {@code PUT  /upload-job-items/:id} : Updates an existing uploadJobItem.
     *
     * @param id the id of the uploadJobItemDTO to save.
     * @param uploadJobItemDTO the uploadJobItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uploadJobItemDTO,
     * or with status {@code 400 (Bad Request)} if the uploadJobItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uploadJobItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UploadJobItemDTO> updateUploadJobItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UploadJobItemDTO uploadJobItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UploadJobItem : {}, {}", id, uploadJobItemDTO);
        if (uploadJobItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uploadJobItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uploadJobItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        uploadJobItemDTO = uploadJobItemService.update(uploadJobItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uploadJobItemDTO.getId().toString()))
            .body(uploadJobItemDTO);
    }

    /**
     * {@code PATCH  /upload-job-items/:id} : Partial updates given fields of an existing uploadJobItem, field will ignore if it is null
     *
     * @param id the id of the uploadJobItemDTO to save.
     * @param uploadJobItemDTO the uploadJobItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uploadJobItemDTO,
     * or with status {@code 400 (Bad Request)} if the uploadJobItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the uploadJobItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the uploadJobItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UploadJobItemDTO> partialUpdateUploadJobItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UploadJobItemDTO uploadJobItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UploadJobItem partially : {}, {}", id, uploadJobItemDTO);
        if (uploadJobItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uploadJobItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uploadJobItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UploadJobItemDTO> result = uploadJobItemService.partialUpdate(uploadJobItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uploadJobItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /upload-job-items} : get all the uploadJobItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uploadJobItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UploadJobItemDTO>> getAllUploadJobItems(
        UploadJobItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get UploadJobItems by criteria: {}", criteria);

        Page<UploadJobItemDTO> page = uploadJobItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /upload-job-items/count} : count all the uploadJobItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countUploadJobItems(UploadJobItemCriteria criteria) {
        LOG.debug("REST request to count UploadJobItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(uploadJobItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /upload-job-items/:id} : get the "id" uploadJobItem.
     *
     * @param id the id of the uploadJobItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uploadJobItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UploadJobItemDTO> getUploadJobItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UploadJobItem : {}", id);
        Optional<UploadJobItemDTO> uploadJobItemDTO = uploadJobItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(uploadJobItemDTO);
    }

    /**
     * {@code DELETE  /upload-job-items/:id} : delete the "id" uploadJobItem.
     *
     * @param id the id of the uploadJobItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUploadJobItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UploadJobItem : {}", id);
        uploadJobItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
