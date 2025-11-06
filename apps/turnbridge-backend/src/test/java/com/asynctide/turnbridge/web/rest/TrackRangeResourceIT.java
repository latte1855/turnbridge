package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.TrackRangeAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.TrackRange;
import com.asynctide.turnbridge.domain.enumeration.TrackRangeStatus;
import com.asynctide.turnbridge.repository.TrackRangeRepository;
import com.asynctide.turnbridge.service.dto.TrackRangeDTO;
import com.asynctide.turnbridge.service.mapper.TrackRangeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link TrackRangeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TrackRangeResourceIT {

    private static final String DEFAULT_SELLER_ID = "AAAAAAAAAA";
    private static final String UPDATED_SELLER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_PERIOD = "760093";
    private static final String UPDATED_PERIOD = "310843";

    private static final String DEFAULT_PREFIX = "AAAAAAAAAA";
    private static final String UPDATED_PREFIX = "BBBBBBBBBB";

    private static final Long DEFAULT_START_NO = 1L;
    private static final Long UPDATED_START_NO = 2L;
    private static final Long SMALLER_START_NO = 1L - 1L;

    private static final Long DEFAULT_END_NO = 1L;
    private static final Long UPDATED_END_NO = 2L;
    private static final Long SMALLER_END_NO = 1L - 1L;

    private static final Long DEFAULT_CURRENT_NO = 0L;
    private static final Long UPDATED_CURRENT_NO = 1L;
    private static final Long SMALLER_CURRENT_NO = 0L - 1L;

    private static final TrackRangeStatus DEFAULT_STATUS = TrackRangeStatus.ACTIVE;
    private static final TrackRangeStatus UPDATED_STATUS = TrackRangeStatus.EXHAUSTED;

    private static final Integer DEFAULT_VERSION = 0;
    private static final Integer UPDATED_VERSION = 1;
    private static final Integer SMALLER_VERSION = 0 - 1;

    private static final String DEFAULT_LOCK_OWNER = "AAAAAAAAAA";
    private static final String UPDATED_LOCK_OWNER = "BBBBBBBBBB";

    private static final Instant DEFAULT_LOCK_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LOCK_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/track-ranges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrackRangeRepository trackRangeRepository;

    @Autowired
    private TrackRangeMapper trackRangeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTrackRangeMockMvc;

    private TrackRange trackRange;

    private TrackRange insertedTrackRange;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrackRange createEntity() {
        return new TrackRange()
            .sellerId(DEFAULT_SELLER_ID)
            .period(DEFAULT_PERIOD)
            .prefix(DEFAULT_PREFIX)
            .startNo(DEFAULT_START_NO)
            .endNo(DEFAULT_END_NO)
            .currentNo(DEFAULT_CURRENT_NO)
            .status(DEFAULT_STATUS)
            .version(DEFAULT_VERSION)
            .lockOwner(DEFAULT_LOCK_OWNER)
            .lockAt(DEFAULT_LOCK_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrackRange createUpdatedEntity() {
        return new TrackRange()
            .sellerId(UPDATED_SELLER_ID)
            .period(UPDATED_PERIOD)
            .prefix(UPDATED_PREFIX)
            .startNo(UPDATED_START_NO)
            .endNo(UPDATED_END_NO)
            .currentNo(UPDATED_CURRENT_NO)
            .status(UPDATED_STATUS)
            .version(UPDATED_VERSION)
            .lockOwner(UPDATED_LOCK_OWNER)
            .lockAt(UPDATED_LOCK_AT);
    }

    @BeforeEach
    void initTest() {
        trackRange = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTrackRange != null) {
            trackRangeRepository.delete(insertedTrackRange);
            insertedTrackRange = null;
        }
    }

    @Test
    @Transactional
    void createTrackRange() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TrackRange
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);
        var returnedTrackRangeDTO = om.readValue(
            restTrackRangeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TrackRangeDTO.class
        );

        // Validate the TrackRange in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrackRange = trackRangeMapper.toEntity(returnedTrackRangeDTO);
        assertTrackRangeUpdatableFieldsEquals(returnedTrackRange, getPersistedTrackRange(returnedTrackRange));

        insertedTrackRange = returnedTrackRange;
    }

    @Test
    @Transactional
    void createTrackRangeWithExistingId() throws Exception {
        // Create the TrackRange with an existing ID
        trackRange.setId(1L);
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTrackRangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TrackRange in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSellerIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackRange.setSellerId(null);

        // Create the TrackRange, which fails.
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        restTrackRangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPeriodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackRange.setPeriod(null);

        // Create the TrackRange, which fails.
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        restTrackRangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrefixIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackRange.setPrefix(null);

        // Create the TrackRange, which fails.
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        restTrackRangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackRange.setStartNo(null);

        // Create the TrackRange, which fails.
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        restTrackRangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackRange.setEndNo(null);

        // Create the TrackRange, which fails.
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        restTrackRangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrentNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackRange.setCurrentNo(null);

        // Create the TrackRange, which fails.
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        restTrackRangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackRange.setStatus(null);

        // Create the TrackRange, which fails.
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        restTrackRangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trackRange.setVersion(null);

        // Create the TrackRange, which fails.
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        restTrackRangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTrackRanges() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList
        restTrackRangeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trackRange.getId().intValue())))
            .andExpect(jsonPath("$.[*].sellerId").value(hasItem(DEFAULT_SELLER_ID)))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].prefix").value(hasItem(DEFAULT_PREFIX)))
            .andExpect(jsonPath("$.[*].startNo").value(hasItem(DEFAULT_START_NO.intValue())))
            .andExpect(jsonPath("$.[*].endNo").value(hasItem(DEFAULT_END_NO.intValue())))
            .andExpect(jsonPath("$.[*].currentNo").value(hasItem(DEFAULT_CURRENT_NO.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].lockOwner").value(hasItem(DEFAULT_LOCK_OWNER)))
            .andExpect(jsonPath("$.[*].lockAt").value(hasItem(DEFAULT_LOCK_AT.toString())));
    }

    @Test
    @Transactional
    void getTrackRange() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get the trackRange
        restTrackRangeMockMvc
            .perform(get(ENTITY_API_URL_ID, trackRange.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(trackRange.getId().intValue()))
            .andExpect(jsonPath("$.sellerId").value(DEFAULT_SELLER_ID))
            .andExpect(jsonPath("$.period").value(DEFAULT_PERIOD))
            .andExpect(jsonPath("$.prefix").value(DEFAULT_PREFIX))
            .andExpect(jsonPath("$.startNo").value(DEFAULT_START_NO.intValue()))
            .andExpect(jsonPath("$.endNo").value(DEFAULT_END_NO.intValue()))
            .andExpect(jsonPath("$.currentNo").value(DEFAULT_CURRENT_NO.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.lockOwner").value(DEFAULT_LOCK_OWNER))
            .andExpect(jsonPath("$.lockAt").value(DEFAULT_LOCK_AT.toString()));
    }

    @Test
    @Transactional
    void getTrackRangesByIdFiltering() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        Long id = trackRange.getId();

        defaultTrackRangeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrackRangeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrackRangeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTrackRangesBySellerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where sellerId equals to
        defaultTrackRangeFiltering("sellerId.equals=" + DEFAULT_SELLER_ID, "sellerId.equals=" + UPDATED_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllTrackRangesBySellerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where sellerId in
        defaultTrackRangeFiltering("sellerId.in=" + DEFAULT_SELLER_ID + "," + UPDATED_SELLER_ID, "sellerId.in=" + UPDATED_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllTrackRangesBySellerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where sellerId is not null
        defaultTrackRangeFiltering("sellerId.specified=true", "sellerId.specified=false");
    }

    @Test
    @Transactional
    void getAllTrackRangesBySellerIdContainsSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where sellerId contains
        defaultTrackRangeFiltering("sellerId.contains=" + DEFAULT_SELLER_ID, "sellerId.contains=" + UPDATED_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllTrackRangesBySellerIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where sellerId does not contain
        defaultTrackRangeFiltering("sellerId.doesNotContain=" + UPDATED_SELLER_ID, "sellerId.doesNotContain=" + DEFAULT_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllTrackRangesByPeriodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where period equals to
        defaultTrackRangeFiltering("period.equals=" + DEFAULT_PERIOD, "period.equals=" + UPDATED_PERIOD);
    }

    @Test
    @Transactional
    void getAllTrackRangesByPeriodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where period in
        defaultTrackRangeFiltering("period.in=" + DEFAULT_PERIOD + "," + UPDATED_PERIOD, "period.in=" + UPDATED_PERIOD);
    }

    @Test
    @Transactional
    void getAllTrackRangesByPeriodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where period is not null
        defaultTrackRangeFiltering("period.specified=true", "period.specified=false");
    }

    @Test
    @Transactional
    void getAllTrackRangesByPeriodContainsSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where period contains
        defaultTrackRangeFiltering("period.contains=" + DEFAULT_PERIOD, "period.contains=" + UPDATED_PERIOD);
    }

    @Test
    @Transactional
    void getAllTrackRangesByPeriodNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where period does not contain
        defaultTrackRangeFiltering("period.doesNotContain=" + UPDATED_PERIOD, "period.doesNotContain=" + DEFAULT_PERIOD);
    }

    @Test
    @Transactional
    void getAllTrackRangesByPrefixIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where prefix equals to
        defaultTrackRangeFiltering("prefix.equals=" + DEFAULT_PREFIX, "prefix.equals=" + UPDATED_PREFIX);
    }

    @Test
    @Transactional
    void getAllTrackRangesByPrefixIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where prefix in
        defaultTrackRangeFiltering("prefix.in=" + DEFAULT_PREFIX + "," + UPDATED_PREFIX, "prefix.in=" + UPDATED_PREFIX);
    }

    @Test
    @Transactional
    void getAllTrackRangesByPrefixIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where prefix is not null
        defaultTrackRangeFiltering("prefix.specified=true", "prefix.specified=false");
    }

    @Test
    @Transactional
    void getAllTrackRangesByPrefixContainsSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where prefix contains
        defaultTrackRangeFiltering("prefix.contains=" + DEFAULT_PREFIX, "prefix.contains=" + UPDATED_PREFIX);
    }

    @Test
    @Transactional
    void getAllTrackRangesByPrefixNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where prefix does not contain
        defaultTrackRangeFiltering("prefix.doesNotContain=" + UPDATED_PREFIX, "prefix.doesNotContain=" + DEFAULT_PREFIX);
    }

    @Test
    @Transactional
    void getAllTrackRangesByStartNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where startNo equals to
        defaultTrackRangeFiltering("startNo.equals=" + DEFAULT_START_NO, "startNo.equals=" + UPDATED_START_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByStartNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where startNo in
        defaultTrackRangeFiltering("startNo.in=" + DEFAULT_START_NO + "," + UPDATED_START_NO, "startNo.in=" + UPDATED_START_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByStartNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where startNo is not null
        defaultTrackRangeFiltering("startNo.specified=true", "startNo.specified=false");
    }

    @Test
    @Transactional
    void getAllTrackRangesByStartNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where startNo is greater than or equal to
        defaultTrackRangeFiltering("startNo.greaterThanOrEqual=" + DEFAULT_START_NO, "startNo.greaterThanOrEqual=" + UPDATED_START_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByStartNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where startNo is less than or equal to
        defaultTrackRangeFiltering("startNo.lessThanOrEqual=" + DEFAULT_START_NO, "startNo.lessThanOrEqual=" + SMALLER_START_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByStartNoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where startNo is less than
        defaultTrackRangeFiltering("startNo.lessThan=" + UPDATED_START_NO, "startNo.lessThan=" + DEFAULT_START_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByStartNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where startNo is greater than
        defaultTrackRangeFiltering("startNo.greaterThan=" + SMALLER_START_NO, "startNo.greaterThan=" + DEFAULT_START_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByEndNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where endNo equals to
        defaultTrackRangeFiltering("endNo.equals=" + DEFAULT_END_NO, "endNo.equals=" + UPDATED_END_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByEndNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where endNo in
        defaultTrackRangeFiltering("endNo.in=" + DEFAULT_END_NO + "," + UPDATED_END_NO, "endNo.in=" + UPDATED_END_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByEndNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where endNo is not null
        defaultTrackRangeFiltering("endNo.specified=true", "endNo.specified=false");
    }

    @Test
    @Transactional
    void getAllTrackRangesByEndNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where endNo is greater than or equal to
        defaultTrackRangeFiltering("endNo.greaterThanOrEqual=" + DEFAULT_END_NO, "endNo.greaterThanOrEqual=" + UPDATED_END_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByEndNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where endNo is less than or equal to
        defaultTrackRangeFiltering("endNo.lessThanOrEqual=" + DEFAULT_END_NO, "endNo.lessThanOrEqual=" + SMALLER_END_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByEndNoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where endNo is less than
        defaultTrackRangeFiltering("endNo.lessThan=" + UPDATED_END_NO, "endNo.lessThan=" + DEFAULT_END_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByEndNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where endNo is greater than
        defaultTrackRangeFiltering("endNo.greaterThan=" + SMALLER_END_NO, "endNo.greaterThan=" + DEFAULT_END_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByCurrentNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where currentNo equals to
        defaultTrackRangeFiltering("currentNo.equals=" + DEFAULT_CURRENT_NO, "currentNo.equals=" + UPDATED_CURRENT_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByCurrentNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where currentNo in
        defaultTrackRangeFiltering("currentNo.in=" + DEFAULT_CURRENT_NO + "," + UPDATED_CURRENT_NO, "currentNo.in=" + UPDATED_CURRENT_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByCurrentNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where currentNo is not null
        defaultTrackRangeFiltering("currentNo.specified=true", "currentNo.specified=false");
    }

    @Test
    @Transactional
    void getAllTrackRangesByCurrentNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where currentNo is greater than or equal to
        defaultTrackRangeFiltering(
            "currentNo.greaterThanOrEqual=" + DEFAULT_CURRENT_NO,
            "currentNo.greaterThanOrEqual=" + UPDATED_CURRENT_NO
        );
    }

    @Test
    @Transactional
    void getAllTrackRangesByCurrentNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where currentNo is less than or equal to
        defaultTrackRangeFiltering("currentNo.lessThanOrEqual=" + DEFAULT_CURRENT_NO, "currentNo.lessThanOrEqual=" + SMALLER_CURRENT_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByCurrentNoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where currentNo is less than
        defaultTrackRangeFiltering("currentNo.lessThan=" + UPDATED_CURRENT_NO, "currentNo.lessThan=" + DEFAULT_CURRENT_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByCurrentNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where currentNo is greater than
        defaultTrackRangeFiltering("currentNo.greaterThan=" + SMALLER_CURRENT_NO, "currentNo.greaterThan=" + DEFAULT_CURRENT_NO);
    }

    @Test
    @Transactional
    void getAllTrackRangesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where status equals to
        defaultTrackRangeFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTrackRangesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where status in
        defaultTrackRangeFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTrackRangesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where status is not null
        defaultTrackRangeFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllTrackRangesByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where version equals to
        defaultTrackRangeFiltering("version.equals=" + DEFAULT_VERSION, "version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTrackRangesByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where version in
        defaultTrackRangeFiltering("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION, "version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTrackRangesByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where version is not null
        defaultTrackRangeFiltering("version.specified=true", "version.specified=false");
    }

    @Test
    @Transactional
    void getAllTrackRangesByVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where version is greater than or equal to
        defaultTrackRangeFiltering("version.greaterThanOrEqual=" + DEFAULT_VERSION, "version.greaterThanOrEqual=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTrackRangesByVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where version is less than or equal to
        defaultTrackRangeFiltering("version.lessThanOrEqual=" + DEFAULT_VERSION, "version.lessThanOrEqual=" + SMALLER_VERSION);
    }

    @Test
    @Transactional
    void getAllTrackRangesByVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where version is less than
        defaultTrackRangeFiltering("version.lessThan=" + UPDATED_VERSION, "version.lessThan=" + DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void getAllTrackRangesByVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where version is greater than
        defaultTrackRangeFiltering("version.greaterThan=" + SMALLER_VERSION, "version.greaterThan=" + DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void getAllTrackRangesByLockOwnerIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where lockOwner equals to
        defaultTrackRangeFiltering("lockOwner.equals=" + DEFAULT_LOCK_OWNER, "lockOwner.equals=" + UPDATED_LOCK_OWNER);
    }

    @Test
    @Transactional
    void getAllTrackRangesByLockOwnerIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where lockOwner in
        defaultTrackRangeFiltering("lockOwner.in=" + DEFAULT_LOCK_OWNER + "," + UPDATED_LOCK_OWNER, "lockOwner.in=" + UPDATED_LOCK_OWNER);
    }

    @Test
    @Transactional
    void getAllTrackRangesByLockOwnerIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where lockOwner is not null
        defaultTrackRangeFiltering("lockOwner.specified=true", "lockOwner.specified=false");
    }

    @Test
    @Transactional
    void getAllTrackRangesByLockOwnerContainsSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where lockOwner contains
        defaultTrackRangeFiltering("lockOwner.contains=" + DEFAULT_LOCK_OWNER, "lockOwner.contains=" + UPDATED_LOCK_OWNER);
    }

    @Test
    @Transactional
    void getAllTrackRangesByLockOwnerNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where lockOwner does not contain
        defaultTrackRangeFiltering("lockOwner.doesNotContain=" + UPDATED_LOCK_OWNER, "lockOwner.doesNotContain=" + DEFAULT_LOCK_OWNER);
    }

    @Test
    @Transactional
    void getAllTrackRangesByLockAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where lockAt equals to
        defaultTrackRangeFiltering("lockAt.equals=" + DEFAULT_LOCK_AT, "lockAt.equals=" + UPDATED_LOCK_AT);
    }

    @Test
    @Transactional
    void getAllTrackRangesByLockAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where lockAt in
        defaultTrackRangeFiltering("lockAt.in=" + DEFAULT_LOCK_AT + "," + UPDATED_LOCK_AT, "lockAt.in=" + UPDATED_LOCK_AT);
    }

    @Test
    @Transactional
    void getAllTrackRangesByLockAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        // Get all the trackRangeList where lockAt is not null
        defaultTrackRangeFiltering("lockAt.specified=true", "lockAt.specified=false");
    }

    private void defaultTrackRangeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTrackRangeShouldBeFound(shouldBeFound);
        defaultTrackRangeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrackRangeShouldBeFound(String filter) throws Exception {
        restTrackRangeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trackRange.getId().intValue())))
            .andExpect(jsonPath("$.[*].sellerId").value(hasItem(DEFAULT_SELLER_ID)))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].prefix").value(hasItem(DEFAULT_PREFIX)))
            .andExpect(jsonPath("$.[*].startNo").value(hasItem(DEFAULT_START_NO.intValue())))
            .andExpect(jsonPath("$.[*].endNo").value(hasItem(DEFAULT_END_NO.intValue())))
            .andExpect(jsonPath("$.[*].currentNo").value(hasItem(DEFAULT_CURRENT_NO.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].lockOwner").value(hasItem(DEFAULT_LOCK_OWNER)))
            .andExpect(jsonPath("$.[*].lockAt").value(hasItem(DEFAULT_LOCK_AT.toString())));

        // Check, that the count call also returns 1
        restTrackRangeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTrackRangeShouldNotBeFound(String filter) throws Exception {
        restTrackRangeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTrackRangeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTrackRange() throws Exception {
        // Get the trackRange
        restTrackRangeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTrackRange() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trackRange
        TrackRange updatedTrackRange = trackRangeRepository.findById(trackRange.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTrackRange are not directly saved in db
        em.detach(updatedTrackRange);
        updatedTrackRange
            .sellerId(UPDATED_SELLER_ID)
            .period(UPDATED_PERIOD)
            .prefix(UPDATED_PREFIX)
            .startNo(UPDATED_START_NO)
            .endNo(UPDATED_END_NO)
            .currentNo(UPDATED_CURRENT_NO)
            .status(UPDATED_STATUS)
            .version(UPDATED_VERSION)
            .lockOwner(UPDATED_LOCK_OWNER)
            .lockAt(UPDATED_LOCK_AT);
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(updatedTrackRange);

        restTrackRangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, trackRangeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(trackRangeDTO))
            )
            .andExpect(status().isOk());

        // Validate the TrackRange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrackRangeToMatchAllProperties(updatedTrackRange);
    }

    @Test
    @Transactional
    void putNonExistingTrackRange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackRange.setId(longCount.incrementAndGet());

        // Create the TrackRange
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTrackRangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, trackRangeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(trackRangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TrackRange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTrackRange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackRange.setId(longCount.incrementAndGet());

        // Create the TrackRange
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrackRangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(trackRangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TrackRange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTrackRange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackRange.setId(longCount.incrementAndGet());

        // Create the TrackRange
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrackRangeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TrackRange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTrackRangeWithPatch() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trackRange using partial update
        TrackRange partialUpdatedTrackRange = new TrackRange();
        partialUpdatedTrackRange.setId(trackRange.getId());

        partialUpdatedTrackRange.period(UPDATED_PERIOD).endNo(UPDATED_END_NO).lockOwner(UPDATED_LOCK_OWNER);

        restTrackRangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrackRange.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrackRange))
            )
            .andExpect(status().isOk());

        // Validate the TrackRange in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrackRangeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrackRange, trackRange),
            getPersistedTrackRange(trackRange)
        );
    }

    @Test
    @Transactional
    void fullUpdateTrackRangeWithPatch() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trackRange using partial update
        TrackRange partialUpdatedTrackRange = new TrackRange();
        partialUpdatedTrackRange.setId(trackRange.getId());

        partialUpdatedTrackRange
            .sellerId(UPDATED_SELLER_ID)
            .period(UPDATED_PERIOD)
            .prefix(UPDATED_PREFIX)
            .startNo(UPDATED_START_NO)
            .endNo(UPDATED_END_NO)
            .currentNo(UPDATED_CURRENT_NO)
            .status(UPDATED_STATUS)
            .version(UPDATED_VERSION)
            .lockOwner(UPDATED_LOCK_OWNER)
            .lockAt(UPDATED_LOCK_AT);

        restTrackRangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrackRange.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrackRange))
            )
            .andExpect(status().isOk());

        // Validate the TrackRange in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrackRangeUpdatableFieldsEquals(partialUpdatedTrackRange, getPersistedTrackRange(partialUpdatedTrackRange));
    }

    @Test
    @Transactional
    void patchNonExistingTrackRange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackRange.setId(longCount.incrementAndGet());

        // Create the TrackRange
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTrackRangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, trackRangeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(trackRangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TrackRange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTrackRange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackRange.setId(longCount.incrementAndGet());

        // Create the TrackRange
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrackRangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(trackRangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TrackRange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTrackRange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trackRange.setId(longCount.incrementAndGet());

        // Create the TrackRange
        TrackRangeDTO trackRangeDTO = trackRangeMapper.toDto(trackRange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrackRangeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(trackRangeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TrackRange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTrackRange() throws Exception {
        // Initialize the database
        insertedTrackRange = trackRangeRepository.saveAndFlush(trackRange);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the trackRange
        restTrackRangeMockMvc
            .perform(delete(ENTITY_API_URL_ID, trackRange.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return trackRangeRepository.count();
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

    protected TrackRange getPersistedTrackRange(TrackRange trackRange) {
        return trackRangeRepository.findById(trackRange.getId()).orElseThrow();
    }

    protected void assertPersistedTrackRangeToMatchAllProperties(TrackRange expectedTrackRange) {
        assertTrackRangeAllPropertiesEquals(expectedTrackRange, getPersistedTrackRange(expectedTrackRange));
    }

    protected void assertPersistedTrackRangeToMatchUpdatableProperties(TrackRange expectedTrackRange) {
        assertTrackRangeAllUpdatablePropertiesEquals(expectedTrackRange, getPersistedTrackRange(expectedTrackRange));
    }
}
