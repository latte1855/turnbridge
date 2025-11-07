package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.service.StoredObjectQueryService;
import com.asynctide.turnbridge.service.StoredObjectService;
import com.asynctide.turnbridge.service.criteria.StoredObjectCriteria;
import com.asynctide.turnbridge.service.dto.StoredObjectDTO;
import com.asynctide.turnbridge.storage.StorageProvider;
import com.asynctide.turnbridge.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.InputStream;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.asynctide.turnbridge.domain.StoredObject}.
 */
@RestController
@RequestMapping("/api/stored-objects")
public class StoredObjectResource {

    private static final Logger LOG = LoggerFactory.getLogger(StoredObjectResource.class);

    private static final String ENTITY_NAME = "storedObject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StoredObjectService storedObjectService;

    private final StoredObjectRepository storedObjectRepository;

    private final StoredObjectQueryService storedObjectQueryService;

    private final StorageProvider storage;

    public StoredObjectResource(
        StoredObjectService storedObjectService,
        StoredObjectRepository storedObjectRepository,
        StoredObjectQueryService storedObjectQueryService,
        StorageProvider storage
    ) {
        this.storedObjectService = storedObjectService;
        this.storedObjectRepository = storedObjectRepository;
        this.storedObjectQueryService = storedObjectQueryService;
        this.storage = storage;
    }

    /**
     * {@code POST  /stored-objects} : Create a new storedObject.
     *
     * @param storedObjectDTO the storedObjectDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new storedObjectDTO, or with status {@code 400 (Bad Request)} if the storedObject has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StoredObjectDTO> createStoredObject(@Valid @RequestBody StoredObjectDTO storedObjectDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StoredObject : {}", storedObjectDTO);
        if (storedObjectDTO.getId() != null) {
            throw new BadRequestAlertException("A new storedObject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        storedObjectDTO = storedObjectService.save(storedObjectDTO);
        return ResponseEntity.created(new URI("/api/stored-objects/" + storedObjectDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, storedObjectDTO.getId().toString()))
            .body(storedObjectDTO);
    }

    /**
     * {@code PUT  /stored-objects/:id} : Updates an existing storedObject.
     *
     * @param id the id of the storedObjectDTO to save.
     * @param storedObjectDTO the storedObjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storedObjectDTO,
     * or with status {@code 400 (Bad Request)} if the storedObjectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the storedObjectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StoredObjectDTO> updateStoredObject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StoredObjectDTO storedObjectDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StoredObject : {}, {}", id, storedObjectDTO);
        if (storedObjectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storedObjectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storedObjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        storedObjectDTO = storedObjectService.update(storedObjectDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, storedObjectDTO.getId().toString()))
            .body(storedObjectDTO);
    }

    /**
     * {@code PATCH  /stored-objects/:id} : Partial updates given fields of an existing storedObject, field will ignore if it is null
     *
     * @param id the id of the storedObjectDTO to save.
     * @param storedObjectDTO the storedObjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storedObjectDTO,
     * or with status {@code 400 (Bad Request)} if the storedObjectDTO is not valid,
     * or with status {@code 404 (Not Found)} if the storedObjectDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the storedObjectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StoredObjectDTO> partialUpdateStoredObject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StoredObjectDTO storedObjectDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StoredObject partially : {}, {}", id, storedObjectDTO);
        if (storedObjectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storedObjectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!storedObjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StoredObjectDTO> result = storedObjectService.partialUpdate(storedObjectDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, storedObjectDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stored-objects} : get all the storedObjects.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of storedObjects in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StoredObjectDTO>> getAllStoredObjects(
        StoredObjectCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StoredObjects by criteria: {}", criteria);

        Page<StoredObjectDTO> page = storedObjectQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stored-objects/count} : count all the storedObjects.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStoredObjects(StoredObjectCriteria criteria) {
        LOG.debug("REST request to count StoredObjects by criteria: {}", criteria);
        return ResponseEntity.ok().body(storedObjectQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stored-objects/:id} : get the "id" storedObject.
     *
     * @param id the id of the storedObjectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storedObjectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoredObjectDTO> getStoredObject(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StoredObject : {}", id);
        Optional<StoredObjectDTO> storedObjectDTO = storedObjectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(storedObjectDTO);
    }

    /**
     * {@code DELETE  /stored-objects/:id} : delete the "id" storedObject.
     *
     * @param id the id of the storedObjectDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStoredObject(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StoredObject : {}", id);
        storedObjectService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /stored-objects/:id/download} : download the "id" storedObject.
     *
     *  @param id the id of the storedObjectDTO to download.
     *  @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storedObjectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
        StoredObject so = storedObjectRepository.findById(id).orElseThrow();
        InputStream in = storage.open(so.getBucket(), so.getObjectKey()).orElseThrow();
        byte[] bytes = in.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + so.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(so.getMediaType()))
                .contentLength(bytes.length)
                .body(bytes);
    }
}
