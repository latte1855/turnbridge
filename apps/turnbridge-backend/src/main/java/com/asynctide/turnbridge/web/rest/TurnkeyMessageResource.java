package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.TurnkeyMessageRepository;
import com.asynctide.turnbridge.service.TurnkeyMessageQueryService;
import com.asynctide.turnbridge.service.TurnkeyMessageService;
import com.asynctide.turnbridge.service.criteria.TurnkeyMessageCriteria;
import com.asynctide.turnbridge.service.dto.TurnkeyMessageDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.TurnkeyMessage}.
 */
@RestController
@RequestMapping("/api/turnkey-messages")
public class TurnkeyMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(TurnkeyMessageResource.class);

    private static final String ENTITY_NAME = "turnkeyMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TurnkeyMessageService turnkeyMessageService;

    private final TurnkeyMessageRepository turnkeyMessageRepository;

    private final TurnkeyMessageQueryService turnkeyMessageQueryService;

    public TurnkeyMessageResource(
        TurnkeyMessageService turnkeyMessageService,
        TurnkeyMessageRepository turnkeyMessageRepository,
        TurnkeyMessageQueryService turnkeyMessageQueryService
    ) {
        this.turnkeyMessageService = turnkeyMessageService;
        this.turnkeyMessageRepository = turnkeyMessageRepository;
        this.turnkeyMessageQueryService = turnkeyMessageQueryService;
    }

    /**
     * {@code POST  /turnkey-messages} : Create a new turnkeyMessage.
     *
     * @param turnkeyMessageDTO the turnkeyMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new turnkeyMessageDTO, or with status {@code 400 (Bad Request)} if the turnkeyMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TurnkeyMessageDTO> createTurnkeyMessage(@Valid @RequestBody TurnkeyMessageDTO turnkeyMessageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TurnkeyMessage : {}", turnkeyMessageDTO);
        if (turnkeyMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new turnkeyMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        turnkeyMessageDTO = turnkeyMessageService.save(turnkeyMessageDTO);
        return ResponseEntity.created(new URI("/api/turnkey-messages/" + turnkeyMessageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, turnkeyMessageDTO.getId().toString()))
            .body(turnkeyMessageDTO);
    }

    /**
     * {@code PUT  /turnkey-messages/:id} : Updates an existing turnkeyMessage.
     *
     * @param id the id of the turnkeyMessageDTO to save.
     * @param turnkeyMessageDTO the turnkeyMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated turnkeyMessageDTO,
     * or with status {@code 400 (Bad Request)} if the turnkeyMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the turnkeyMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TurnkeyMessageDTO> updateTurnkeyMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TurnkeyMessageDTO turnkeyMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TurnkeyMessage : {}, {}", id, turnkeyMessageDTO);
        if (turnkeyMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, turnkeyMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!turnkeyMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        turnkeyMessageDTO = turnkeyMessageService.update(turnkeyMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, turnkeyMessageDTO.getId().toString()))
            .body(turnkeyMessageDTO);
    }

    /**
     * {@code PATCH  /turnkey-messages/:id} : Partial updates given fields of an existing turnkeyMessage, field will ignore if it is null
     *
     * @param id the id of the turnkeyMessageDTO to save.
     * @param turnkeyMessageDTO the turnkeyMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated turnkeyMessageDTO,
     * or with status {@code 400 (Bad Request)} if the turnkeyMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the turnkeyMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the turnkeyMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TurnkeyMessageDTO> partialUpdateTurnkeyMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TurnkeyMessageDTO turnkeyMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TurnkeyMessage partially : {}, {}", id, turnkeyMessageDTO);
        if (turnkeyMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, turnkeyMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!turnkeyMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TurnkeyMessageDTO> result = turnkeyMessageService.partialUpdate(turnkeyMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, turnkeyMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /turnkey-messages} : get all the turnkeyMessages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of turnkeyMessages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TurnkeyMessageDTO>> getAllTurnkeyMessages(
        TurnkeyMessageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TurnkeyMessages by criteria: {}", criteria);

        Page<TurnkeyMessageDTO> page = turnkeyMessageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /turnkey-messages/count} : count all the turnkeyMessages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTurnkeyMessages(TurnkeyMessageCriteria criteria) {
        LOG.debug("REST request to count TurnkeyMessages by criteria: {}", criteria);
        return ResponseEntity.ok().body(turnkeyMessageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /turnkey-messages/:id} : get the "id" turnkeyMessage.
     *
     * @param id the id of the turnkeyMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the turnkeyMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TurnkeyMessageDTO> getTurnkeyMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TurnkeyMessage : {}", id);
        Optional<TurnkeyMessageDTO> turnkeyMessageDTO = turnkeyMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(turnkeyMessageDTO);
    }

    /**
     * {@code DELETE  /turnkey-messages/:id} : delete the "id" turnkeyMessage.
     *
     * @param id the id of the turnkeyMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTurnkeyMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TurnkeyMessage : {}", id);
        turnkeyMessageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
