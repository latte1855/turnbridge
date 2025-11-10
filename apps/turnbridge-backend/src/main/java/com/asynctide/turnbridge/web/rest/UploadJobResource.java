package com.asynctide.turnbridge.web.rest;

import com.asynctide.turnbridge.app.UploadJobAppService;
import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.repository.UploadJobItemRepository;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.asynctide.turnbridge.service.StoredObjectService;
import com.asynctide.turnbridge.service.UploadJobItemQueryService;
import com.asynctide.turnbridge.service.UploadJobItemService;
import com.asynctide.turnbridge.service.UploadJobQueryService;
import com.asynctide.turnbridge.service.UploadJobService;
import com.asynctide.turnbridge.service.criteria.UploadJobCriteria;
import com.asynctide.turnbridge.service.criteria.UploadJobItemCriteria;
import com.asynctide.turnbridge.service.dto.StoredObjectDTO;
import com.asynctide.turnbridge.service.dto.UploadJobDTO;
import com.asynctide.turnbridge.service.dto.UploadJobItemDTO;
import com.asynctide.turnbridge.service.dto.UploadJobStatsDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.asynctide.turnbridge.domain.UploadJob}.
 * 上傳批次（UploadJob）REST API。
 * 
 * <p>注意：所有清單端點皆回傳 DTO，避免 Entity 在序列化過程中被修改造成非預期 flush。</p>
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

    private final UploadJobAppService appService;

    private final UploadJobItemRepository uploadJobItemRepository;
    
    private final UploadJobItemService uploadJobItemService;
    
    private final UploadJobItemQueryService uploadJobItemQueryService;
    
    private final StoredObjectService storedObjectService;

    public UploadJobResource(
        UploadJobService uploadJobService,
        UploadJobRepository uploadJobRepository,
        UploadJobQueryService uploadJobQueryService,
        UploadJobAppService appService,
        UploadJobItemRepository uploadJobItemRepository,
        UploadJobItemService uploadJobItemService,
        UploadJobItemQueryService uploadJobItemQueryService,
        StoredObjectService storedObjectService
    ) {
        this.uploadJobService = uploadJobService;
        this.uploadJobRepository = uploadJobRepository;
        this.uploadJobQueryService = uploadJobQueryService;
        this.appService = appService;
        this.uploadJobItemRepository = uploadJobItemRepository;
        this.uploadJobItemService = uploadJobItemService;
        this.uploadJobItemQueryService = uploadJobItemQueryService;
        this.storedObjectService = storedObjectService;
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
    
    // -----------------------------
    // 批次明細查詢（DTO + 分頁 headers）
    // -----------------------------

    /** 依數字 id 查詢明細（可選 status；回傳 DTO List）。 */
    @GetMapping("/{id}/items")
    public ResponseEntity<List<UploadJobItemDTO>> findItemsByNumericId(
        @PathVariable Long id,
        @RequestParam(required = false) JobItemStatus status,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        UploadJobItemCriteria criteria = new UploadJobItemCriteria();
        criteria.jobId().setEquals(id);
        if (status != null) criteria.status().setEquals(status);

        Page<UploadJobItemDTO> page = uploadJobItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /** 依字串 jobId 查詢明細（可選 status；回傳 DTO List）。 */
    @GetMapping("/by-job-id/{jobId}/items")
    public ResponseEntity<List<UploadJobItemDTO>> findItemsByJobId(
        @PathVariable String jobId,
        @RequestParam(required = false) JobItemStatus status,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        Page<UploadJobItemDTO> page = uploadJobItemService.findByJobJobId(jobId, status, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    // -----------------------------
    // 「原始上傳檔」下載（兩種路徑）
    // -----------------------------
    /** 以字串 jobId 下載「原始上傳檔」。 */
    @GetMapping("/by-job-id/{jobId}/original")
    public ResponseEntity<byte[]> downloadOriginalByJobId(
        @PathVariable String jobId,
        @RequestParam(defaultValue = "false") boolean bom
    ) throws Exception {
        UploadJobDTO job = uploadJobService.findOneByJobId(jobId).orElseThrow();
        StoredObjectDTO so = job.getOriginalFile();
        if (so == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "尚無原始檔");
        return buildBinaryResponse(so, bom);
    }
    
    /** 以數字 id 下載「原始上傳檔」。 */
    @GetMapping("/{id}/original")
    public ResponseEntity<byte[]> downloadOriginalById(
        @PathVariable Long id,
        @RequestParam(defaultValue = "false") boolean bom
    ) throws Exception {
        UploadJobDTO job = uploadJobService.findOne(id).orElseThrow();
        StoredObjectDTO so = job.getOriginalFile();
        if (so == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "尚無原始檔");
        return buildBinaryResponse(so, bom);
    }
    

    // -----------------------------
    // 「統計檔」下載
    // -----------------------------
    /** 統計：成功/失敗/待處理（數字 id）。 */
    @GetMapping("/{id}/stats")
    public ResponseEntity<UploadJobStatsDTO> statsById(@PathVariable Long id) {
        long total = uploadJobItemRepository.countByJobId(id);
        long ok = uploadJobItemRepository.countByJobIdAndStatus(id, JobItemStatus.OK);
        long error = uploadJobItemRepository.countByJobIdAndStatus(id, JobItemStatus.ERROR);
        long queued = uploadJobItemRepository.countByJobIdAndStatus(id, JobItemStatus.QUEUED);
        return ResponseEntity.ok(new UploadJobStatsDTO(total, ok, error, queued));
    }

    /** 統計：成功/失敗/待處理（字串 jobId）。 */
    @GetMapping("/by-job-id/{jobId}/stats")
    public ResponseEntity<UploadJobStatsDTO> statsByJobId(@PathVariable String jobId) {
        long total = uploadJobItemRepository.countByJobJobId(jobId);
        long ok = uploadJobItemRepository.countByJobJobIdAndStatus(jobId, JobItemStatus.OK);
        long error = uploadJobItemRepository.countByJobJobIdAndStatus(jobId, JobItemStatus.ERROR);
        long queued = uploadJobItemRepository.countByJobJobIdAndStatus(jobId, JobItemStatus.QUEUED);
        return ResponseEntity.ok(new UploadJobStatsDTO(total, ok, error, queued));
    }

    /** 重試：把 ERROR 的明細改成 QUEUED（字串 jobId）。 */
    @PostMapping("/by-job-id/{jobId}/retry-failed")
    public ResponseEntity<Void> retryFailed(@PathVariable String jobId) {
        int affected = uploadJobItemService.requeueFailedByJobJobId(jobId);
        LOG.info("Retry failed items for jobId={}, affected={}", jobId, affected);
        return ResponseEntity.accepted().build(); // 202 Accepted
    }

    // -----------------------------
    // 回饋檔下載（兩種路徑）
    // -----------------------------

    /** 以數字 id 下載回饋檔。 */
    @GetMapping("/{id}/result")
    public ResponseEntity<byte[]> downloadResultById(
        @PathVariable Long id,
        @RequestParam(defaultValue = "false") boolean bom
    ) throws Exception {
        UploadJobDTO job = uploadJobService.findOne(id).orElseThrow();
        return buildResultResponse(job, bom);
    }

    /** 以字串 jobId 下載回饋檔（對應你的測試呼叫方式）。 */
    @GetMapping("/by-job-id/{jobId}/result")
    public ResponseEntity<byte[]> downloadResultByJobId(
        @PathVariable String jobId,
        @RequestParam(defaultValue = "false") boolean bom
    ) throws Exception {
        UploadJobDTO job = uploadJobService.findOneByJobId(jobId).orElseThrow();
        return buildResultResponse(job, bom);
    }


    // === 共用位元檔回應 ===
    
    // 共用回應建構
    private ResponseEntity<byte[]> buildResultResponse(UploadJobDTO job, boolean bom) throws Exception {
    	StoredObjectDTO so = job.getResultFile();
        if (so == null) return ResponseEntity.notFound().build();
        return buildBinaryResponse(so, bom);
    }
    
    private ResponseEntity<byte[]> buildBinaryResponse(StoredObjectDTO so, boolean bom) throws Exception {
        // 若 mapper 填充不完整，補一次（你已經注入 storedObjectService）
        so = storedObjectService.findOne(so.getId()).orElse(so);

        byte[] body;
        try (InputStream in = appService.openStoredObject(so)) {
            body = in.readAllBytes();
        } catch (IllegalStateException e) {
            // 「物件不存在於 Storage」→ 轉 404
            if ("物件不存在於 Storage".equals(e.getMessage())) {
            	LOG.error("物件不存在於 Storage", e);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw e;
        }

        MediaType mt = MediaType.parseMediaType(
            so.getMediaType() == null ? "application/octet-stream" : so.getMediaType()
        );
        if (bom && "text".equalsIgnoreCase(mt.getType())) {
            byte[] bomBytes = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
            byte[] merged = new byte[bomBytes.length + body.length];
            System.arraycopy(bomBytes, 0, merged, 0, bomBytes.length);
            System.arraycopy(body, 0, merged, bomBytes.length, body.length);
            body = merged;
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + so.getFilename() + "\"")
            .contentType(mt)
            .contentLength(body.length)
            .body(body);
    }
    
    
}
