package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.repository.InvoiceAssignNoRepository;
import com.asynctide.turnbridge.service.InvoiceAssignNoQueryService;
import com.asynctide.turnbridge.service.InvoiceAssignNoService;
import com.asynctide.turnbridge.service.criteria.InvoiceAssignNoCriteria;
import com.asynctide.turnbridge.service.dto.InvoiceAssignNoDTO;
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
 * REST controller for managing {@link com.asynctide.turnbridge.domain.InvoiceAssignNo}.
 */
@RestController
@RequestMapping("/api/invoice-assign-nos")
public class InvoiceAssignNoResource {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceAssignNoResource.class);

    private static final String ENTITY_NAME = "invoiceAssignNo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InvoiceAssignNoService invoiceAssignNoService;

    private final InvoiceAssignNoRepository invoiceAssignNoRepository;

    private final InvoiceAssignNoQueryService invoiceAssignNoQueryService;

    public InvoiceAssignNoResource(
        InvoiceAssignNoService invoiceAssignNoService,
        InvoiceAssignNoRepository invoiceAssignNoRepository,
        InvoiceAssignNoQueryService invoiceAssignNoQueryService
    ) {
        this.invoiceAssignNoService = invoiceAssignNoService;
        this.invoiceAssignNoRepository = invoiceAssignNoRepository;
        this.invoiceAssignNoQueryService = invoiceAssignNoQueryService;
    }

    /**
     * {@code POST  /invoice-assign-nos} : Create a new invoiceAssignNo.
     *
     * @param invoiceAssignNoDTO the invoiceAssignNoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new invoiceAssignNoDTO, or with status {@code 400 (Bad Request)} if the invoiceAssignNo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InvoiceAssignNoDTO> createInvoiceAssignNo(@Valid @RequestBody InvoiceAssignNoDTO invoiceAssignNoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save InvoiceAssignNo : {}", invoiceAssignNoDTO);
        if (invoiceAssignNoDTO.getId() != null) {
            throw new BadRequestAlertException("A new invoiceAssignNo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        invoiceAssignNoDTO = invoiceAssignNoService.save(invoiceAssignNoDTO);
        return ResponseEntity.created(new URI("/api/invoice-assign-nos/" + invoiceAssignNoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, invoiceAssignNoDTO.getId().toString()))
            .body(invoiceAssignNoDTO);
    }

    /**
     * {@code PUT  /invoice-assign-nos/:id} : Updates an existing invoiceAssignNo.
     *
     * @param id the id of the invoiceAssignNoDTO to save.
     * @param invoiceAssignNoDTO the invoiceAssignNoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceAssignNoDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceAssignNoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the invoiceAssignNoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceAssignNoDTO> updateInvoiceAssignNo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InvoiceAssignNoDTO invoiceAssignNoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update InvoiceAssignNo : {}, {}", id, invoiceAssignNoDTO);
        if (invoiceAssignNoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoiceAssignNoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!invoiceAssignNoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        invoiceAssignNoDTO = invoiceAssignNoService.update(invoiceAssignNoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, invoiceAssignNoDTO.getId().toString()))
            .body(invoiceAssignNoDTO);
    }

    /**
     * {@code PATCH  /invoice-assign-nos/:id} : Partial updates given fields of an existing invoiceAssignNo, field will ignore if it is null
     *
     * @param id the id of the invoiceAssignNoDTO to save.
     * @param invoiceAssignNoDTO the invoiceAssignNoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceAssignNoDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceAssignNoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the invoiceAssignNoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the invoiceAssignNoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InvoiceAssignNoDTO> partialUpdateInvoiceAssignNo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InvoiceAssignNoDTO invoiceAssignNoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InvoiceAssignNo partially : {}, {}", id, invoiceAssignNoDTO);
        if (invoiceAssignNoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoiceAssignNoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!invoiceAssignNoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InvoiceAssignNoDTO> result = invoiceAssignNoService.partialUpdate(invoiceAssignNoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, invoiceAssignNoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /invoice-assign-nos} : get all the invoiceAssignNos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of invoiceAssignNos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InvoiceAssignNoDTO>> getAllInvoiceAssignNos(InvoiceAssignNoCriteria criteria) {
        LOG.debug("REST request to get InvoiceAssignNos by criteria: {}", criteria);

        List<InvoiceAssignNoDTO> entityList = invoiceAssignNoQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /invoice-assign-nos/count} : count all the invoiceAssignNos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInvoiceAssignNos(InvoiceAssignNoCriteria criteria) {
        LOG.debug("REST request to count InvoiceAssignNos by criteria: {}", criteria);
        return ResponseEntity.ok().body(invoiceAssignNoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /invoice-assign-nos/:id} : get the "id" invoiceAssignNo.
     *
     * @param id the id of the invoiceAssignNoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the invoiceAssignNoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceAssignNoDTO> getInvoiceAssignNo(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InvoiceAssignNo : {}", id);
        Optional<InvoiceAssignNoDTO> invoiceAssignNoDTO = invoiceAssignNoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(invoiceAssignNoDTO);
    }

    /**
     * {@code DELETE  /invoice-assign-nos/:id} : delete the "id" invoiceAssignNo.
     *
     * @param id the id of the invoiceAssignNoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoiceAssignNo(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InvoiceAssignNo : {}", id);
        invoiceAssignNoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
