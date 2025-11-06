package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.asynctide.turnbridge.service.UploadJobQueryService;
import com.asynctide.turnbridge.service.UploadJobService;
import com.asynctide.turnbridge.service.criteria.UploadJobCriteria;
import com.asynctide.turnbridge.service.dto.UploadJobDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.UploadJob}.
 */
@RestController
@RequestMapping("/api/upload-jobs")
public class UploadJobResource {

    private static final Logger LOG = LoggerFactory.getLogger(UploadJobResource.class);

    private static final String ENTITY_NAME = "uploadJob";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UploadJobService uploadJobService;

    private final UploadJobRepository uploadJobRepository;

    private final UploadJobQueryService uploadJobQueryService;

    public UploadJobResource(
        UploadJobService uploadJobService,
        UploadJobRepository uploadJobRepository,
        UploadJobQueryService uploadJobQueryService
    ) {
        this.uploadJobService = uploadJobService;
        this.uploadJobRepository = uploadJobRepository;
        this.uploadJobQueryService = uploadJobQueryService;
    }

    /**
     * {@code POST  /upload-jobs} : Create a new uploadJob.
     *
     * @param uploadJobDTO the uploadJobDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uploadJobDTO, or with status {@code 400 (Bad Request)} if the uploadJob has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UploadJobDTO> createUploadJob(@Valid @RequestBody UploadJobDTO uploadJobDTO) throws URISyntaxException {
        LOG.debug("REST request to save UploadJob : {}", uploadJobDTO);
        if (uploadJobDTO.getId() != null) {
            throw new BadRequestAlertException("A new uploadJob cannot already have an ID", ENTITY_NAME, "idexists");
        }
        uploadJobDTO = uploadJobService.save(uploadJobDTO);
        return ResponseEntity.created(new URI("/api/upload-jobs/" + uploadJobDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, uploadJobDTO.getId().toString()))
            .body(uploadJobDTO);
    }

    /**
     * {@code PUT  /upload-jobs/:id} : Updates an existing uploadJob.
     *
     * @param id the id of the uploadJobDTO to save.
     * @param uploadJobDTO the uploadJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uploadJobDTO,
     * or with status {@code 400 (Bad Request)} if the uploadJobDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uploadJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UploadJobDTO> updateUploadJob(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UploadJobDTO uploadJobDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UploadJob : {}, {}", id, uploadJobDTO);
        if (uploadJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uploadJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uploadJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        uploadJobDTO = uploadJobService.update(uploadJobDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uploadJobDTO.getId().toString()))
            .body(uploadJobDTO);
    }

    /**
     * {@code PATCH  /upload-jobs/:id} : Partial updates given fields of an existing uploadJob, field will ignore if it is null
     *
     * @param id the id of the uploadJobDTO to save.
     * @param uploadJobDTO the uploadJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uploadJobDTO,
     * or with status {@code 400 (Bad Request)} if the uploadJobDTO is not valid,
     * or with status {@code 404 (Not Found)} if the uploadJobDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the uploadJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UploadJobDTO> partialUpdateUploadJob(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UploadJobDTO uploadJobDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UploadJob partially : {}, {}", id, uploadJobDTO);
        if (uploadJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uploadJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uploadJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UploadJobDTO> result = uploadJobService.partialUpdate(uploadJobDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uploadJobDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /upload-jobs} : get all the uploadJobs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uploadJobs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UploadJobDTO>> getAllUploadJobs(
        UploadJobCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get UploadJobs by criteria: {}", criteria);

        Page<UploadJobDTO> page = uploadJobQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /upload-jobs/count} : count all the uploadJobs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countUploadJobs(UploadJobCriteria criteria) {
        LOG.debug("REST request to count UploadJobs by criteria: {}", criteria);
        return ResponseEntity.ok().body(uploadJobQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /upload-jobs/:id} : get the "id" uploadJob.
     *
     * @param id the id of the uploadJobDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uploadJobDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UploadJobDTO> getUploadJob(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UploadJob : {}", id);
        Optional<UploadJobDTO> uploadJobDTO = uploadJobService.findOne(id);
        return ResponseUtil.wrapOrNotFound(uploadJobDTO);
    }

    /**
     * {@code DELETE  /upload-jobs/:id} : delete the "id" uploadJob.
     *
     * @param id the id of the uploadJobDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUploadJob(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UploadJob : {}", id);
        uploadJobService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
