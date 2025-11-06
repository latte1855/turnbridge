package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.TrackRangeRepository;
import com.asynctide.turnbridge.service.TrackRangeQueryService;
import com.asynctide.turnbridge.service.TrackRangeService;
import com.asynctide.turnbridge.service.criteria.TrackRangeCriteria;
import com.asynctide.turnbridge.service.dto.TrackRangeDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.TrackRange}.
 */
@RestController
@RequestMapping("/api/track-ranges")
public class TrackRangeResource {

    private static final Logger LOG = LoggerFactory.getLogger(TrackRangeResource.class);

    private static final String ENTITY_NAME = "trackRange";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrackRangeService trackRangeService;

    private final TrackRangeRepository trackRangeRepository;

    private final TrackRangeQueryService trackRangeQueryService;

    public TrackRangeResource(
        TrackRangeService trackRangeService,
        TrackRangeRepository trackRangeRepository,
        TrackRangeQueryService trackRangeQueryService
    ) {
        this.trackRangeService = trackRangeService;
        this.trackRangeRepository = trackRangeRepository;
        this.trackRangeQueryService = trackRangeQueryService;
    }

    /**
     * {@code POST  /track-ranges} : Create a new trackRange.
     *
     * @param trackRangeDTO the trackRangeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackRangeDTO, or with status {@code 400 (Bad Request)} if the trackRange has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TrackRangeDTO> createTrackRange(@Valid @RequestBody TrackRangeDTO trackRangeDTO) throws URISyntaxException {
        LOG.debug("REST request to save TrackRange : {}", trackRangeDTO);
        if (trackRangeDTO.getId() != null) {
            throw new BadRequestAlertException("A new trackRange cannot already have an ID", ENTITY_NAME, "idexists");
        }
        trackRangeDTO = trackRangeService.save(trackRangeDTO);
        return ResponseEntity.created(new URI("/api/track-ranges/" + trackRangeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, trackRangeDTO.getId().toString()))
            .body(trackRangeDTO);
    }

    /**
     * {@code PUT  /track-ranges/:id} : Updates an existing trackRange.
     *
     * @param id the id of the trackRangeDTO to save.
     * @param trackRangeDTO the trackRangeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackRangeDTO,
     * or with status {@code 400 (Bad Request)} if the trackRangeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trackRangeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TrackRangeDTO> updateTrackRange(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrackRangeDTO trackRangeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TrackRange : {}, {}", id, trackRangeDTO);
        if (trackRangeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trackRangeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!trackRangeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        trackRangeDTO = trackRangeService.update(trackRangeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackRangeDTO.getId().toString()))
            .body(trackRangeDTO);
    }

    /**
     * {@code PATCH  /track-ranges/:id} : Partial updates given fields of an existing trackRange, field will ignore if it is null
     *
     * @param id the id of the trackRangeDTO to save.
     * @param trackRangeDTO the trackRangeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackRangeDTO,
     * or with status {@code 400 (Bad Request)} if the trackRangeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trackRangeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trackRangeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TrackRangeDTO> partialUpdateTrackRange(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrackRangeDTO trackRangeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TrackRange partially : {}, {}", id, trackRangeDTO);
        if (trackRangeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trackRangeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!trackRangeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TrackRangeDTO> result = trackRangeService.partialUpdate(trackRangeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackRangeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /track-ranges} : get all the trackRanges.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trackRanges in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TrackRangeDTO>> getAllTrackRanges(
        TrackRangeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TrackRanges by criteria: {}", criteria);

        Page<TrackRangeDTO> page = trackRangeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /track-ranges/count} : count all the trackRanges.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTrackRanges(TrackRangeCriteria criteria) {
        LOG.debug("REST request to count TrackRanges by criteria: {}", criteria);
        return ResponseEntity.ok().body(trackRangeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /track-ranges/:id} : get the "id" trackRange.
     *
     * @param id the id of the trackRangeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackRangeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrackRangeDTO> getTrackRange(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TrackRange : {}", id);
        Optional<TrackRangeDTO> trackRangeDTO = trackRangeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trackRangeDTO);
    }

    /**
     * {@code DELETE  /track-ranges/:id} : delete the "id" trackRange.
     *
     * @param id the id of the trackRangeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrackRange(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TrackRange : {}", id);
        trackRangeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
