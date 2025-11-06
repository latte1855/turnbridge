package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.UploadJobAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.enumeration.UploadJobStatus;
import com.asynctide.turnbridge.repository.UploadJobRepository;
import com.asynctide.turnbridge.service.dto.UploadJobDTO;
import com.asynctide.turnbridge.service.mapper.UploadJobMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UploadJobResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UploadJobResourceIT {

    private static final String DEFAULT_JOB_ID = "AAAAAAAAAA";
    private static final String UPDATED_JOB_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SELLER_ID = "AAAAAAAAAA";
    private static final String UPDATED_SELLER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SELLER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SELLER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PERIOD = "987852";
    private static final String UPDATED_PERIOD = "040564";

    private static final String DEFAULT_PROFILE = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_MEDIA_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_MEDIA_TYPE = "BBBBBBBBBB";

    private static final UploadJobStatus DEFAULT_STATUS = UploadJobStatus.RECEIVED;
    private static final UploadJobStatus UPDATED_STATUS = UploadJobStatus.PARSING;

    private static final Integer DEFAULT_TOTAL = 0;
    private static final Integer UPDATED_TOTAL = 1;
    private static final Integer SMALLER_TOTAL = 0 - 1;

    private static final Integer DEFAULT_ACCEPTED = 0;
    private static final Integer UPDATED_ACCEPTED = 1;
    private static final Integer SMALLER_ACCEPTED = 0 - 1;

    private static final Integer DEFAULT_FAILED = 0;
    private static final Integer UPDATED_FAILED = 1;
    private static final Integer SMALLER_FAILED = 0 - 1;

    private static final Integer DEFAULT_SENT = 0;
    private static final Integer UPDATED_SENT = 1;
    private static final Integer SMALLER_SENT = 0 - 1;

    private static final String DEFAULT_REMARK = "AAAAAAAAAA";
    private static final String UPDATED_REMARK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/upload-jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UploadJobRepository uploadJobRepository;

    @Autowired
    private UploadJobMapper uploadJobMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUploadJobMockMvc;

    private UploadJob uploadJob;

    private UploadJob insertedUploadJob;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UploadJob createEntity(EntityManager em) {
        UploadJob uploadJob = new UploadJob()
            .jobId(DEFAULT_JOB_ID)
            .sellerId(DEFAULT_SELLER_ID)
            .sellerName(DEFAULT_SELLER_NAME)
            .period(DEFAULT_PERIOD)
            .profile(DEFAULT_PROFILE)
            .sourceFilename(DEFAULT_SOURCE_FILENAME)
            .sourceMediaType(DEFAULT_SOURCE_MEDIA_TYPE)
            .status(DEFAULT_STATUS)
            .total(DEFAULT_TOTAL)
            .accepted(DEFAULT_ACCEPTED)
            .failed(DEFAULT_FAILED)
            .sent(DEFAULT_SENT)
            .remark(DEFAULT_REMARK);
        // Add required entity
        StoredObject storedObject;
        if (TestUtil.findAll(em, StoredObject.class).isEmpty()) {
            storedObject = StoredObjectResourceIT.createEntity();
            em.persist(storedObject);
            em.flush();
        } else {
            storedObject = TestUtil.findAll(em, StoredObject.class).get(0);
        }
        uploadJob.setOriginalFile(storedObject);
        return uploadJob;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UploadJob createUpdatedEntity(EntityManager em) {
        UploadJob updatedUploadJob = new UploadJob()
            .jobId(UPDATED_JOB_ID)
            .sellerId(UPDATED_SELLER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .period(UPDATED_PERIOD)
            .profile(UPDATED_PROFILE)
            .sourceFilename(UPDATED_SOURCE_FILENAME)
            .sourceMediaType(UPDATED_SOURCE_MEDIA_TYPE)
            .status(UPDATED_STATUS)
            .total(UPDATED_TOTAL)
            .accepted(UPDATED_ACCEPTED)
            .failed(UPDATED_FAILED)
            .sent(UPDATED_SENT)
            .remark(UPDATED_REMARK);
        // Add required entity
        StoredObject storedObject;
        if (TestUtil.findAll(em, StoredObject.class).isEmpty()) {
            storedObject = StoredObjectResourceIT.createUpdatedEntity();
            em.persist(storedObject);
            em.flush();
        } else {
            storedObject = TestUtil.findAll(em, StoredObject.class).get(0);
        }
        updatedUploadJob.setOriginalFile(storedObject);
        return updatedUploadJob;
    }

    @BeforeEach
    void initTest() {
        uploadJob = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedUploadJob != null) {
            uploadJobRepository.delete(insertedUploadJob);
            insertedUploadJob = null;
        }
    }

    @Test
    @Transactional
    void createUploadJob() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UploadJob
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);
        var returnedUploadJobDTO = om.readValue(
            restUploadJobMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UploadJobDTO.class
        );

        // Validate the UploadJob in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUploadJob = uploadJobMapper.toEntity(returnedUploadJobDTO);
        assertUploadJobUpdatableFieldsEquals(returnedUploadJob, getPersistedUploadJob(returnedUploadJob));

        insertedUploadJob = returnedUploadJob;
    }

    @Test
    @Transactional
    void createUploadJobWithExistingId() throws Exception {
        // Create the UploadJob with an existing ID
        uploadJob.setId(1L);
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUploadJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UploadJob in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkJobIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJob.setJobId(null);

        // Create the UploadJob, which fails.
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        restUploadJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSellerIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJob.setSellerId(null);

        // Create the UploadJob, which fails.
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        restUploadJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJob.setStatus(null);

        // Create the UploadJob, which fails.
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        restUploadJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJob.setTotal(null);

        // Create the UploadJob, which fails.
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        restUploadJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAcceptedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJob.setAccepted(null);

        // Create the UploadJob, which fails.
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        restUploadJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFailedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJob.setFailed(null);

        // Create the UploadJob, which fails.
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        restUploadJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJob.setSent(null);

        // Create the UploadJob, which fails.
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        restUploadJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUploadJobs() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList
        restUploadJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uploadJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobId").value(hasItem(DEFAULT_JOB_ID)))
            .andExpect(jsonPath("$.[*].sellerId").value(hasItem(DEFAULT_SELLER_ID)))
            .andExpect(jsonPath("$.[*].sellerName").value(hasItem(DEFAULT_SELLER_NAME)))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].profile").value(hasItem(DEFAULT_PROFILE)))
            .andExpect(jsonPath("$.[*].sourceFilename").value(hasItem(DEFAULT_SOURCE_FILENAME)))
            .andExpect(jsonPath("$.[*].sourceMediaType").value(hasItem(DEFAULT_SOURCE_MEDIA_TYPE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.[*].accepted").value(hasItem(DEFAULT_ACCEPTED)))
            .andExpect(jsonPath("$.[*].failed").value(hasItem(DEFAULT_FAILED)))
            .andExpect(jsonPath("$.[*].sent").value(hasItem(DEFAULT_SENT)))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)));
    }

    @Test
    @Transactional
    void getUploadJob() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get the uploadJob
        restUploadJobMockMvc
            .perform(get(ENTITY_API_URL_ID, uploadJob.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(uploadJob.getId().intValue()))
            .andExpect(jsonPath("$.jobId").value(DEFAULT_JOB_ID))
            .andExpect(jsonPath("$.sellerId").value(DEFAULT_SELLER_ID))
            .andExpect(jsonPath("$.sellerName").value(DEFAULT_SELLER_NAME))
            .andExpect(jsonPath("$.period").value(DEFAULT_PERIOD))
            .andExpect(jsonPath("$.profile").value(DEFAULT_PROFILE))
            .andExpect(jsonPath("$.sourceFilename").value(DEFAULT_SOURCE_FILENAME))
            .andExpect(jsonPath("$.sourceMediaType").value(DEFAULT_SOURCE_MEDIA_TYPE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL))
            .andExpect(jsonPath("$.accepted").value(DEFAULT_ACCEPTED))
            .andExpect(jsonPath("$.failed").value(DEFAULT_FAILED))
            .andExpect(jsonPath("$.sent").value(DEFAULT_SENT))
            .andExpect(jsonPath("$.remark").value(DEFAULT_REMARK));
    }

    @Test
    @Transactional
    void getUploadJobsByIdFiltering() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        Long id = uploadJob.getId();

        defaultUploadJobFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultUploadJobFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultUploadJobFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUploadJobsByJobIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where jobId equals to
        defaultUploadJobFiltering("jobId.equals=" + DEFAULT_JOB_ID, "jobId.equals=" + UPDATED_JOB_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobsByJobIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where jobId in
        defaultUploadJobFiltering("jobId.in=" + DEFAULT_JOB_ID + "," + UPDATED_JOB_ID, "jobId.in=" + UPDATED_JOB_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobsByJobIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where jobId is not null
        defaultUploadJobFiltering("jobId.specified=true", "jobId.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsByJobIdContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where jobId contains
        defaultUploadJobFiltering("jobId.contains=" + DEFAULT_JOB_ID, "jobId.contains=" + UPDATED_JOB_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobsByJobIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where jobId does not contain
        defaultUploadJobFiltering("jobId.doesNotContain=" + UPDATED_JOB_ID, "jobId.doesNotContain=" + DEFAULT_JOB_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerId equals to
        defaultUploadJobFiltering("sellerId.equals=" + DEFAULT_SELLER_ID, "sellerId.equals=" + UPDATED_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerId in
        defaultUploadJobFiltering("sellerId.in=" + DEFAULT_SELLER_ID + "," + UPDATED_SELLER_ID, "sellerId.in=" + UPDATED_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerId is not null
        defaultUploadJobFiltering("sellerId.specified=true", "sellerId.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerIdContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerId contains
        defaultUploadJobFiltering("sellerId.contains=" + DEFAULT_SELLER_ID, "sellerId.contains=" + UPDATED_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerId does not contain
        defaultUploadJobFiltering("sellerId.doesNotContain=" + UPDATED_SELLER_ID, "sellerId.doesNotContain=" + DEFAULT_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerName equals to
        defaultUploadJobFiltering("sellerName.equals=" + DEFAULT_SELLER_NAME, "sellerName.equals=" + UPDATED_SELLER_NAME);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerName in
        defaultUploadJobFiltering(
            "sellerName.in=" + DEFAULT_SELLER_NAME + "," + UPDATED_SELLER_NAME,
            "sellerName.in=" + UPDATED_SELLER_NAME
        );
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerName is not null
        defaultUploadJobFiltering("sellerName.specified=true", "sellerName.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerNameContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerName contains
        defaultUploadJobFiltering("sellerName.contains=" + DEFAULT_SELLER_NAME, "sellerName.contains=" + UPDATED_SELLER_NAME);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySellerNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sellerName does not contain
        defaultUploadJobFiltering("sellerName.doesNotContain=" + UPDATED_SELLER_NAME, "sellerName.doesNotContain=" + DEFAULT_SELLER_NAME);
    }

    @Test
    @Transactional
    void getAllUploadJobsByPeriodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where period equals to
        defaultUploadJobFiltering("period.equals=" + DEFAULT_PERIOD, "period.equals=" + UPDATED_PERIOD);
    }

    @Test
    @Transactional
    void getAllUploadJobsByPeriodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where period in
        defaultUploadJobFiltering("period.in=" + DEFAULT_PERIOD + "," + UPDATED_PERIOD, "period.in=" + UPDATED_PERIOD);
    }

    @Test
    @Transactional
    void getAllUploadJobsByPeriodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where period is not null
        defaultUploadJobFiltering("period.specified=true", "period.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsByPeriodContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where period contains
        defaultUploadJobFiltering("period.contains=" + DEFAULT_PERIOD, "period.contains=" + UPDATED_PERIOD);
    }

    @Test
    @Transactional
    void getAllUploadJobsByPeriodNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where period does not contain
        defaultUploadJobFiltering("period.doesNotContain=" + UPDATED_PERIOD, "period.doesNotContain=" + DEFAULT_PERIOD);
    }

    @Test
    @Transactional
    void getAllUploadJobsByProfileIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where profile equals to
        defaultUploadJobFiltering("profile.equals=" + DEFAULT_PROFILE, "profile.equals=" + UPDATED_PROFILE);
    }

    @Test
    @Transactional
    void getAllUploadJobsByProfileIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where profile in
        defaultUploadJobFiltering("profile.in=" + DEFAULT_PROFILE + "," + UPDATED_PROFILE, "profile.in=" + UPDATED_PROFILE);
    }

    @Test
    @Transactional
    void getAllUploadJobsByProfileIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where profile is not null
        defaultUploadJobFiltering("profile.specified=true", "profile.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsByProfileContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where profile contains
        defaultUploadJobFiltering("profile.contains=" + DEFAULT_PROFILE, "profile.contains=" + UPDATED_PROFILE);
    }

    @Test
    @Transactional
    void getAllUploadJobsByProfileNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where profile does not contain
        defaultUploadJobFiltering("profile.doesNotContain=" + UPDATED_PROFILE, "profile.doesNotContain=" + DEFAULT_PROFILE);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceFilenameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceFilename equals to
        defaultUploadJobFiltering("sourceFilename.equals=" + DEFAULT_SOURCE_FILENAME, "sourceFilename.equals=" + UPDATED_SOURCE_FILENAME);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceFilenameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceFilename in
        defaultUploadJobFiltering(
            "sourceFilename.in=" + DEFAULT_SOURCE_FILENAME + "," + UPDATED_SOURCE_FILENAME,
            "sourceFilename.in=" + UPDATED_SOURCE_FILENAME
        );
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceFilenameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceFilename is not null
        defaultUploadJobFiltering("sourceFilename.specified=true", "sourceFilename.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceFilenameContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceFilename contains
        defaultUploadJobFiltering(
            "sourceFilename.contains=" + DEFAULT_SOURCE_FILENAME,
            "sourceFilename.contains=" + UPDATED_SOURCE_FILENAME
        );
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceFilenameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceFilename does not contain
        defaultUploadJobFiltering(
            "sourceFilename.doesNotContain=" + UPDATED_SOURCE_FILENAME,
            "sourceFilename.doesNotContain=" + DEFAULT_SOURCE_FILENAME
        );
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceMediaTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceMediaType equals to
        defaultUploadJobFiltering(
            "sourceMediaType.equals=" + DEFAULT_SOURCE_MEDIA_TYPE,
            "sourceMediaType.equals=" + UPDATED_SOURCE_MEDIA_TYPE
        );
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceMediaTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceMediaType in
        defaultUploadJobFiltering(
            "sourceMediaType.in=" + DEFAULT_SOURCE_MEDIA_TYPE + "," + UPDATED_SOURCE_MEDIA_TYPE,
            "sourceMediaType.in=" + UPDATED_SOURCE_MEDIA_TYPE
        );
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceMediaTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceMediaType is not null
        defaultUploadJobFiltering("sourceMediaType.specified=true", "sourceMediaType.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceMediaTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceMediaType contains
        defaultUploadJobFiltering(
            "sourceMediaType.contains=" + DEFAULT_SOURCE_MEDIA_TYPE,
            "sourceMediaType.contains=" + UPDATED_SOURCE_MEDIA_TYPE
        );
    }

    @Test
    @Transactional
    void getAllUploadJobsBySourceMediaTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sourceMediaType does not contain
        defaultUploadJobFiltering(
            "sourceMediaType.doesNotContain=" + UPDATED_SOURCE_MEDIA_TYPE,
            "sourceMediaType.doesNotContain=" + DEFAULT_SOURCE_MEDIA_TYPE
        );
    }

    @Test
    @Transactional
    void getAllUploadJobsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where status equals to
        defaultUploadJobFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllUploadJobsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where status in
        defaultUploadJobFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllUploadJobsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where status is not null
        defaultUploadJobFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where total equals to
        defaultUploadJobFiltering("total.equals=" + DEFAULT_TOTAL, "total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllUploadJobsByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where total in
        defaultUploadJobFiltering("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL, "total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllUploadJobsByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where total is not null
        defaultUploadJobFiltering("total.specified=true", "total.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where total is greater than or equal to
        defaultUploadJobFiltering("total.greaterThanOrEqual=" + DEFAULT_TOTAL, "total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllUploadJobsByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where total is less than or equal to
        defaultUploadJobFiltering("total.lessThanOrEqual=" + DEFAULT_TOTAL, "total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllUploadJobsByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where total is less than
        defaultUploadJobFiltering("total.lessThan=" + UPDATED_TOTAL, "total.lessThan=" + DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void getAllUploadJobsByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where total is greater than
        defaultUploadJobFiltering("total.greaterThan=" + SMALLER_TOTAL, "total.greaterThan=" + DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void getAllUploadJobsByAcceptedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where accepted equals to
        defaultUploadJobFiltering("accepted.equals=" + DEFAULT_ACCEPTED, "accepted.equals=" + UPDATED_ACCEPTED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByAcceptedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where accepted in
        defaultUploadJobFiltering("accepted.in=" + DEFAULT_ACCEPTED + "," + UPDATED_ACCEPTED, "accepted.in=" + UPDATED_ACCEPTED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByAcceptedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where accepted is not null
        defaultUploadJobFiltering("accepted.specified=true", "accepted.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsByAcceptedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where accepted is greater than or equal to
        defaultUploadJobFiltering("accepted.greaterThanOrEqual=" + DEFAULT_ACCEPTED, "accepted.greaterThanOrEqual=" + UPDATED_ACCEPTED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByAcceptedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where accepted is less than or equal to
        defaultUploadJobFiltering("accepted.lessThanOrEqual=" + DEFAULT_ACCEPTED, "accepted.lessThanOrEqual=" + SMALLER_ACCEPTED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByAcceptedIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where accepted is less than
        defaultUploadJobFiltering("accepted.lessThan=" + UPDATED_ACCEPTED, "accepted.lessThan=" + DEFAULT_ACCEPTED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByAcceptedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where accepted is greater than
        defaultUploadJobFiltering("accepted.greaterThan=" + SMALLER_ACCEPTED, "accepted.greaterThan=" + DEFAULT_ACCEPTED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByFailedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where failed equals to
        defaultUploadJobFiltering("failed.equals=" + DEFAULT_FAILED, "failed.equals=" + UPDATED_FAILED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByFailedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where failed in
        defaultUploadJobFiltering("failed.in=" + DEFAULT_FAILED + "," + UPDATED_FAILED, "failed.in=" + UPDATED_FAILED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByFailedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where failed is not null
        defaultUploadJobFiltering("failed.specified=true", "failed.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsByFailedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where failed is greater than or equal to
        defaultUploadJobFiltering("failed.greaterThanOrEqual=" + DEFAULT_FAILED, "failed.greaterThanOrEqual=" + UPDATED_FAILED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByFailedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where failed is less than or equal to
        defaultUploadJobFiltering("failed.lessThanOrEqual=" + DEFAULT_FAILED, "failed.lessThanOrEqual=" + SMALLER_FAILED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByFailedIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where failed is less than
        defaultUploadJobFiltering("failed.lessThan=" + UPDATED_FAILED, "failed.lessThan=" + DEFAULT_FAILED);
    }

    @Test
    @Transactional
    void getAllUploadJobsByFailedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where failed is greater than
        defaultUploadJobFiltering("failed.greaterThan=" + SMALLER_FAILED, "failed.greaterThan=" + DEFAULT_FAILED);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sent equals to
        defaultUploadJobFiltering("sent.equals=" + DEFAULT_SENT, "sent.equals=" + UPDATED_SENT);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sent in
        defaultUploadJobFiltering("sent.in=" + DEFAULT_SENT + "," + UPDATED_SENT, "sent.in=" + UPDATED_SENT);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sent is not null
        defaultUploadJobFiltering("sent.specified=true", "sent.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsBySentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sent is greater than or equal to
        defaultUploadJobFiltering("sent.greaterThanOrEqual=" + DEFAULT_SENT, "sent.greaterThanOrEqual=" + UPDATED_SENT);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sent is less than or equal to
        defaultUploadJobFiltering("sent.lessThanOrEqual=" + DEFAULT_SENT, "sent.lessThanOrEqual=" + SMALLER_SENT);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySentIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sent is less than
        defaultUploadJobFiltering("sent.lessThan=" + UPDATED_SENT, "sent.lessThan=" + DEFAULT_SENT);
    }

    @Test
    @Transactional
    void getAllUploadJobsBySentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where sent is greater than
        defaultUploadJobFiltering("sent.greaterThan=" + SMALLER_SENT, "sent.greaterThan=" + DEFAULT_SENT);
    }

    @Test
    @Transactional
    void getAllUploadJobsByRemarkIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where remark equals to
        defaultUploadJobFiltering("remark.equals=" + DEFAULT_REMARK, "remark.equals=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    void getAllUploadJobsByRemarkIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where remark in
        defaultUploadJobFiltering("remark.in=" + DEFAULT_REMARK + "," + UPDATED_REMARK, "remark.in=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    void getAllUploadJobsByRemarkIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where remark is not null
        defaultUploadJobFiltering("remark.specified=true", "remark.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobsByRemarkContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where remark contains
        defaultUploadJobFiltering("remark.contains=" + DEFAULT_REMARK, "remark.contains=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    void getAllUploadJobsByRemarkNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        // Get all the uploadJobList where remark does not contain
        defaultUploadJobFiltering("remark.doesNotContain=" + UPDATED_REMARK, "remark.doesNotContain=" + DEFAULT_REMARK);
    }

    @Test
    @Transactional
    void getAllUploadJobsByOriginalFileIsEqualToSomething() throws Exception {
        StoredObject originalFile;
        if (TestUtil.findAll(em, StoredObject.class).isEmpty()) {
            uploadJobRepository.saveAndFlush(uploadJob);
            originalFile = StoredObjectResourceIT.createEntity();
        } else {
            originalFile = TestUtil.findAll(em, StoredObject.class).get(0);
        }
        em.persist(originalFile);
        em.flush();
        uploadJob.setOriginalFile(originalFile);
        uploadJobRepository.saveAndFlush(uploadJob);
        Long originalFileId = originalFile.getId();
        // Get all the uploadJobList where originalFile equals to originalFileId
        defaultUploadJobShouldBeFound("originalFileId.equals=" + originalFileId);

        // Get all the uploadJobList where originalFile equals to (originalFileId + 1)
        defaultUploadJobShouldNotBeFound("originalFileId.equals=" + (originalFileId + 1));
    }

    @Test
    @Transactional
    void getAllUploadJobsByResultFileIsEqualToSomething() throws Exception {
        StoredObject resultFile;
        if (TestUtil.findAll(em, StoredObject.class).isEmpty()) {
            uploadJobRepository.saveAndFlush(uploadJob);
            resultFile = StoredObjectResourceIT.createEntity();
        } else {
            resultFile = TestUtil.findAll(em, StoredObject.class).get(0);
        }
        em.persist(resultFile);
        em.flush();
        uploadJob.setResultFile(resultFile);
        uploadJobRepository.saveAndFlush(uploadJob);
        Long resultFileId = resultFile.getId();
        // Get all the uploadJobList where resultFile equals to resultFileId
        defaultUploadJobShouldBeFound("resultFileId.equals=" + resultFileId);

        // Get all the uploadJobList where resultFile equals to (resultFileId + 1)
        defaultUploadJobShouldNotBeFound("resultFileId.equals=" + (resultFileId + 1));
    }

    private void defaultUploadJobFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultUploadJobShouldBeFound(shouldBeFound);
        defaultUploadJobShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUploadJobShouldBeFound(String filter) throws Exception {
        restUploadJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uploadJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobId").value(hasItem(DEFAULT_JOB_ID)))
            .andExpect(jsonPath("$.[*].sellerId").value(hasItem(DEFAULT_SELLER_ID)))
            .andExpect(jsonPath("$.[*].sellerName").value(hasItem(DEFAULT_SELLER_NAME)))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].profile").value(hasItem(DEFAULT_PROFILE)))
            .andExpect(jsonPath("$.[*].sourceFilename").value(hasItem(DEFAULT_SOURCE_FILENAME)))
            .andExpect(jsonPath("$.[*].sourceMediaType").value(hasItem(DEFAULT_SOURCE_MEDIA_TYPE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.[*].accepted").value(hasItem(DEFAULT_ACCEPTED)))
            .andExpect(jsonPath("$.[*].failed").value(hasItem(DEFAULT_FAILED)))
            .andExpect(jsonPath("$.[*].sent").value(hasItem(DEFAULT_SENT)))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)));

        // Check, that the count call also returns 1
        restUploadJobMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUploadJobShouldNotBeFound(String filter) throws Exception {
        restUploadJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUploadJobMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUploadJob() throws Exception {
        // Get the uploadJob
        restUploadJobMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUploadJob() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the uploadJob
        UploadJob updatedUploadJob = uploadJobRepository.findById(uploadJob.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUploadJob are not directly saved in db
        em.detach(updatedUploadJob);
        updatedUploadJob
            .jobId(UPDATED_JOB_ID)
            .sellerId(UPDATED_SELLER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .period(UPDATED_PERIOD)
            .profile(UPDATED_PROFILE)
            .sourceFilename(UPDATED_SOURCE_FILENAME)
            .sourceMediaType(UPDATED_SOURCE_MEDIA_TYPE)
            .status(UPDATED_STATUS)
            .total(UPDATED_TOTAL)
            .accepted(UPDATED_ACCEPTED)
            .failed(UPDATED_FAILED)
            .sent(UPDATED_SENT)
            .remark(UPDATED_REMARK);
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(updatedUploadJob);

        restUploadJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, uploadJobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(uploadJobDTO))
            )
            .andExpect(status().isOk());

        // Validate the UploadJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUploadJobToMatchAllProperties(updatedUploadJob);
    }

    @Test
    @Transactional
    void putNonExistingUploadJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJob.setId(longCount.incrementAndGet());

        // Create the UploadJob
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUploadJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, uploadJobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(uploadJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UploadJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUploadJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJob.setId(longCount.incrementAndGet());

        // Create the UploadJob
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(uploadJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UploadJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUploadJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJob.setId(longCount.incrementAndGet());

        // Create the UploadJob
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadJobMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UploadJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUploadJobWithPatch() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the uploadJob using partial update
        UploadJob partialUpdatedUploadJob = new UploadJob();
        partialUpdatedUploadJob.setId(uploadJob.getId());

        partialUpdatedUploadJob
            .jobId(UPDATED_JOB_ID)
            .sellerId(UPDATED_SELLER_ID)
            .period(UPDATED_PERIOD)
            .profile(UPDATED_PROFILE)
            .sourceMediaType(UPDATED_SOURCE_MEDIA_TYPE);

        restUploadJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUploadJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUploadJob))
            )
            .andExpect(status().isOk());

        // Validate the UploadJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUploadJobUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUploadJob, uploadJob),
            getPersistedUploadJob(uploadJob)
        );
    }

    @Test
    @Transactional
    void fullUpdateUploadJobWithPatch() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the uploadJob using partial update
        UploadJob partialUpdatedUploadJob = new UploadJob();
        partialUpdatedUploadJob.setId(uploadJob.getId());

        partialUpdatedUploadJob
            .jobId(UPDATED_JOB_ID)
            .sellerId(UPDATED_SELLER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .period(UPDATED_PERIOD)
            .profile(UPDATED_PROFILE)
            .sourceFilename(UPDATED_SOURCE_FILENAME)
            .sourceMediaType(UPDATED_SOURCE_MEDIA_TYPE)
            .status(UPDATED_STATUS)
            .total(UPDATED_TOTAL)
            .accepted(UPDATED_ACCEPTED)
            .failed(UPDATED_FAILED)
            .sent(UPDATED_SENT)
            .remark(UPDATED_REMARK);

        restUploadJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUploadJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUploadJob))
            )
            .andExpect(status().isOk());

        // Validate the UploadJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUploadJobUpdatableFieldsEquals(partialUpdatedUploadJob, getPersistedUploadJob(partialUpdatedUploadJob));
    }

    @Test
    @Transactional
    void patchNonExistingUploadJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJob.setId(longCount.incrementAndGet());

        // Create the UploadJob
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUploadJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, uploadJobDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(uploadJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UploadJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUploadJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJob.setId(longCount.incrementAndGet());

        // Create the UploadJob
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(uploadJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UploadJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUploadJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJob.setId(longCount.incrementAndGet());

        // Create the UploadJob
        UploadJobDTO uploadJobDTO = uploadJobMapper.toDto(uploadJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadJobMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(uploadJobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UploadJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUploadJob() throws Exception {
        // Initialize the database
        insertedUploadJob = uploadJobRepository.saveAndFlush(uploadJob);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the uploadJob
        restUploadJobMockMvc
            .perform(delete(ENTITY_API_URL_ID, uploadJob.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return uploadJobRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected UploadJob getPersistedUploadJob(UploadJob uploadJob) {
        return uploadJobRepository.findById(uploadJob.getId()).orElseThrow();
    }

    protected void assertPersistedUploadJobToMatchAllProperties(UploadJob expectedUploadJob) {
        assertUploadJobAllPropertiesEquals(expectedUploadJob, getPersistedUploadJob(expectedUploadJob));
    }

    protected void assertPersistedUploadJobToMatchUpdatableProperties(UploadJob expectedUploadJob) {
        assertUploadJobAllUpdatablePropertiesEquals(expectedUploadJob, getPersistedUploadJob(expectedUploadJob));
    }
}
