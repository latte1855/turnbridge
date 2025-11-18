package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.WebhookDeliveryLogAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.WebhookDeliveryLog;
import com.asynctide.turnbridge.domain.WebhookEndpoint;
import com.asynctide.turnbridge.domain.enumeration.DeliveryResult;
import com.asynctide.turnbridge.repository.WebhookDeliveryLogRepository;
import com.asynctide.turnbridge.service.WebhookDeliveryLogService;
import com.asynctide.turnbridge.service.dto.WebhookDeliveryLogDTO;
import com.asynctide.turnbridge.service.mapper.WebhookDeliveryLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WebhookDeliveryLogResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WebhookDeliveryLogResourceIT {

    private static final String DEFAULT_DELIVERY_ID = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_ID = "BBBBBBBBBB";

    private static final String DEFAULT_EVENT = "AAAAAAAAAA";
    private static final String UPDATED_EVENT = "BBBBBBBBBB";

    private static final String DEFAULT_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD = "BBBBBBBBBB";

    private static final DeliveryResult DEFAULT_STATUS = DeliveryResult.SUCCESS;
    private static final DeliveryResult UPDATED_STATUS = DeliveryResult.FAILED;

    private static final Integer DEFAULT_HTTP_STATUS = 1;
    private static final Integer UPDATED_HTTP_STATUS = 2;
    private static final Integer SMALLER_HTTP_STATUS = 1 - 1;

    private static final Integer DEFAULT_ATTEMPTS = 0;
    private static final Integer UPDATED_ATTEMPTS = 1;
    private static final Integer SMALLER_ATTEMPTS = 0 - 1;

    private static final String DEFAULT_LAST_ERROR = "AAAAAAAAAA";
    private static final String UPDATED_LAST_ERROR = "BBBBBBBBBB";

    private static final Instant DEFAULT_DELIVERED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELIVERED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/webhook-delivery-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WebhookDeliveryLogRepository webhookDeliveryLogRepository;

    @Mock
    private WebhookDeliveryLogRepository webhookDeliveryLogRepositoryMock;

    @Autowired
    private WebhookDeliveryLogMapper webhookDeliveryLogMapper;

    @Mock
    private WebhookDeliveryLogService webhookDeliveryLogServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWebhookDeliveryLogMockMvc;

    private WebhookDeliveryLog webhookDeliveryLog;

    private WebhookDeliveryLog insertedWebhookDeliveryLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WebhookDeliveryLog createEntity(EntityManager em) {
        WebhookDeliveryLog webhookDeliveryLog = new WebhookDeliveryLog()
            .deliveryId(DEFAULT_DELIVERY_ID)
            .event(DEFAULT_EVENT)
            .payload(DEFAULT_PAYLOAD)
            .status(DEFAULT_STATUS)
            .httpStatus(DEFAULT_HTTP_STATUS)
            .attempts(DEFAULT_ATTEMPTS)
            .lastError(DEFAULT_LAST_ERROR)
            .deliveredAt(DEFAULT_DELIVERED_AT);
        // Add required entity
        WebhookEndpoint webhookEndpoint;
        if (TestUtil.findAll(em, WebhookEndpoint.class).isEmpty()) {
            webhookEndpoint = WebhookEndpointResourceIT.createEntity();
            em.persist(webhookEndpoint);
            em.flush();
        } else {
            webhookEndpoint = TestUtil.findAll(em, WebhookEndpoint.class).get(0);
        }
        webhookDeliveryLog.setWebhookEndpoint(webhookEndpoint);
        return webhookDeliveryLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WebhookDeliveryLog createUpdatedEntity(EntityManager em) {
        WebhookDeliveryLog updatedWebhookDeliveryLog = new WebhookDeliveryLog()
            .deliveryId(UPDATED_DELIVERY_ID)
            .event(UPDATED_EVENT)
            .payload(UPDATED_PAYLOAD)
            .status(UPDATED_STATUS)
            .httpStatus(UPDATED_HTTP_STATUS)
            .attempts(UPDATED_ATTEMPTS)
            .lastError(UPDATED_LAST_ERROR)
            .deliveredAt(UPDATED_DELIVERED_AT);
        // Add required entity
        WebhookEndpoint webhookEndpoint;
        if (TestUtil.findAll(em, WebhookEndpoint.class).isEmpty()) {
            webhookEndpoint = WebhookEndpointResourceIT.createUpdatedEntity();
            em.persist(webhookEndpoint);
            em.flush();
        } else {
            webhookEndpoint = TestUtil.findAll(em, WebhookEndpoint.class).get(0);
        }
        updatedWebhookDeliveryLog.setWebhookEndpoint(webhookEndpoint);
        return updatedWebhookDeliveryLog;
    }

    @BeforeEach
    void initTest() {
        webhookDeliveryLog = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedWebhookDeliveryLog != null) {
            webhookDeliveryLogRepository.delete(insertedWebhookDeliveryLog);
            insertedWebhookDeliveryLog = null;
        }
    }

    @Test
    @Transactional
    void createWebhookDeliveryLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WebhookDeliveryLog
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);
        var returnedWebhookDeliveryLogDTO = om.readValue(
            restWebhookDeliveryLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryLogDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WebhookDeliveryLogDTO.class
        );

        // Validate the WebhookDeliveryLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWebhookDeliveryLog = webhookDeliveryLogMapper.toEntity(returnedWebhookDeliveryLogDTO);
        assertWebhookDeliveryLogUpdatableFieldsEquals(
            returnedWebhookDeliveryLog,
            getPersistedWebhookDeliveryLog(returnedWebhookDeliveryLog)
        );

        insertedWebhookDeliveryLog = returnedWebhookDeliveryLog;
    }

    @Test
    @Transactional
    void createWebhookDeliveryLogWithExistingId() throws Exception {
        // Create the WebhookDeliveryLog with an existing ID
        webhookDeliveryLog.setId(1L);
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWebhookDeliveryLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDeliveryIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookDeliveryLog.setDeliveryId(null);

        // Create the WebhookDeliveryLog, which fails.
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        restWebhookDeliveryLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEventIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookDeliveryLog.setEvent(null);

        // Create the WebhookDeliveryLog, which fails.
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        restWebhookDeliveryLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookDeliveryLog.setStatus(null);

        // Create the WebhookDeliveryLog, which fails.
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        restWebhookDeliveryLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogs() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList
        restWebhookDeliveryLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webhookDeliveryLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].deliveryId").value(hasItem(DEFAULT_DELIVERY_ID)))
            .andExpect(jsonPath("$.[*].event").value(hasItem(DEFAULT_EVENT)))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].httpStatus").value(hasItem(DEFAULT_HTTP_STATUS)))
            .andExpect(jsonPath("$.[*].attempts").value(hasItem(DEFAULT_ATTEMPTS)))
            .andExpect(jsonPath("$.[*].lastError").value(hasItem(DEFAULT_LAST_ERROR)))
            .andExpect(jsonPath("$.[*].deliveredAt").value(hasItem(DEFAULT_DELIVERED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWebhookDeliveryLogsWithEagerRelationshipsIsEnabled() throws Exception {
        when(webhookDeliveryLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWebhookDeliveryLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(webhookDeliveryLogServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWebhookDeliveryLogsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(webhookDeliveryLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWebhookDeliveryLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(webhookDeliveryLogRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getWebhookDeliveryLog() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get the webhookDeliveryLog
        restWebhookDeliveryLogMockMvc
            .perform(get(ENTITY_API_URL_ID, webhookDeliveryLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(webhookDeliveryLog.getId().intValue()))
            .andExpect(jsonPath("$.deliveryId").value(DEFAULT_DELIVERY_ID))
            .andExpect(jsonPath("$.event").value(DEFAULT_EVENT))
            .andExpect(jsonPath("$.payload").value(DEFAULT_PAYLOAD))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.httpStatus").value(DEFAULT_HTTP_STATUS))
            .andExpect(jsonPath("$.attempts").value(DEFAULT_ATTEMPTS))
            .andExpect(jsonPath("$.lastError").value(DEFAULT_LAST_ERROR))
            .andExpect(jsonPath("$.deliveredAt").value(DEFAULT_DELIVERED_AT.toString()));
    }

    @Test
    @Transactional
    void getWebhookDeliveryLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        Long id = webhookDeliveryLog.getId();

        defaultWebhookDeliveryLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWebhookDeliveryLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWebhookDeliveryLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByDeliveryIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where deliveryId equals to
        defaultWebhookDeliveryLogFiltering("deliveryId.equals=" + DEFAULT_DELIVERY_ID, "deliveryId.equals=" + UPDATED_DELIVERY_ID);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByDeliveryIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where deliveryId in
        defaultWebhookDeliveryLogFiltering(
            "deliveryId.in=" + DEFAULT_DELIVERY_ID + "," + UPDATED_DELIVERY_ID,
            "deliveryId.in=" + UPDATED_DELIVERY_ID
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByDeliveryIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where deliveryId is not null
        defaultWebhookDeliveryLogFiltering("deliveryId.specified=true", "deliveryId.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByDeliveryIdContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where deliveryId contains
        defaultWebhookDeliveryLogFiltering("deliveryId.contains=" + DEFAULT_DELIVERY_ID, "deliveryId.contains=" + UPDATED_DELIVERY_ID);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByDeliveryIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where deliveryId does not contain
        defaultWebhookDeliveryLogFiltering(
            "deliveryId.doesNotContain=" + UPDATED_DELIVERY_ID,
            "deliveryId.doesNotContain=" + DEFAULT_DELIVERY_ID
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByEventIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where event equals to
        defaultWebhookDeliveryLogFiltering("event.equals=" + DEFAULT_EVENT, "event.equals=" + UPDATED_EVENT);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByEventIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where event in
        defaultWebhookDeliveryLogFiltering("event.in=" + DEFAULT_EVENT + "," + UPDATED_EVENT, "event.in=" + UPDATED_EVENT);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByEventIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where event is not null
        defaultWebhookDeliveryLogFiltering("event.specified=true", "event.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByEventContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where event contains
        defaultWebhookDeliveryLogFiltering("event.contains=" + DEFAULT_EVENT, "event.contains=" + UPDATED_EVENT);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByEventNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where event does not contain
        defaultWebhookDeliveryLogFiltering("event.doesNotContain=" + UPDATED_EVENT, "event.doesNotContain=" + DEFAULT_EVENT);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where status equals to
        defaultWebhookDeliveryLogFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where status in
        defaultWebhookDeliveryLogFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where status is not null
        defaultWebhookDeliveryLogFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByHttpStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where httpStatus equals to
        defaultWebhookDeliveryLogFiltering("httpStatus.equals=" + DEFAULT_HTTP_STATUS, "httpStatus.equals=" + UPDATED_HTTP_STATUS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByHttpStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where httpStatus in
        defaultWebhookDeliveryLogFiltering(
            "httpStatus.in=" + DEFAULT_HTTP_STATUS + "," + UPDATED_HTTP_STATUS,
            "httpStatus.in=" + UPDATED_HTTP_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByHttpStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where httpStatus is not null
        defaultWebhookDeliveryLogFiltering("httpStatus.specified=true", "httpStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByHttpStatusIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where httpStatus is greater than or equal to
        defaultWebhookDeliveryLogFiltering(
            "httpStatus.greaterThanOrEqual=" + DEFAULT_HTTP_STATUS,
            "httpStatus.greaterThanOrEqual=" + UPDATED_HTTP_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByHttpStatusIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where httpStatus is less than or equal to
        defaultWebhookDeliveryLogFiltering(
            "httpStatus.lessThanOrEqual=" + DEFAULT_HTTP_STATUS,
            "httpStatus.lessThanOrEqual=" + SMALLER_HTTP_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByHttpStatusIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where httpStatus is less than
        defaultWebhookDeliveryLogFiltering("httpStatus.lessThan=" + UPDATED_HTTP_STATUS, "httpStatus.lessThan=" + DEFAULT_HTTP_STATUS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByHttpStatusIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where httpStatus is greater than
        defaultWebhookDeliveryLogFiltering(
            "httpStatus.greaterThan=" + SMALLER_HTTP_STATUS,
            "httpStatus.greaterThan=" + DEFAULT_HTTP_STATUS
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByAttemptsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where attempts equals to
        defaultWebhookDeliveryLogFiltering("attempts.equals=" + DEFAULT_ATTEMPTS, "attempts.equals=" + UPDATED_ATTEMPTS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByAttemptsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where attempts in
        defaultWebhookDeliveryLogFiltering("attempts.in=" + DEFAULT_ATTEMPTS + "," + UPDATED_ATTEMPTS, "attempts.in=" + UPDATED_ATTEMPTS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByAttemptsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where attempts is not null
        defaultWebhookDeliveryLogFiltering("attempts.specified=true", "attempts.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByAttemptsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where attempts is greater than or equal to
        defaultWebhookDeliveryLogFiltering(
            "attempts.greaterThanOrEqual=" + DEFAULT_ATTEMPTS,
            "attempts.greaterThanOrEqual=" + UPDATED_ATTEMPTS
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByAttemptsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where attempts is less than or equal to
        defaultWebhookDeliveryLogFiltering("attempts.lessThanOrEqual=" + DEFAULT_ATTEMPTS, "attempts.lessThanOrEqual=" + SMALLER_ATTEMPTS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByAttemptsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where attempts is less than
        defaultWebhookDeliveryLogFiltering("attempts.lessThan=" + UPDATED_ATTEMPTS, "attempts.lessThan=" + DEFAULT_ATTEMPTS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByAttemptsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where attempts is greater than
        defaultWebhookDeliveryLogFiltering("attempts.greaterThan=" + SMALLER_ATTEMPTS, "attempts.greaterThan=" + DEFAULT_ATTEMPTS);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByLastErrorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where lastError equals to
        defaultWebhookDeliveryLogFiltering("lastError.equals=" + DEFAULT_LAST_ERROR, "lastError.equals=" + UPDATED_LAST_ERROR);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByLastErrorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where lastError in
        defaultWebhookDeliveryLogFiltering(
            "lastError.in=" + DEFAULT_LAST_ERROR + "," + UPDATED_LAST_ERROR,
            "lastError.in=" + UPDATED_LAST_ERROR
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByLastErrorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where lastError is not null
        defaultWebhookDeliveryLogFiltering("lastError.specified=true", "lastError.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByLastErrorContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where lastError contains
        defaultWebhookDeliveryLogFiltering("lastError.contains=" + DEFAULT_LAST_ERROR, "lastError.contains=" + UPDATED_LAST_ERROR);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByLastErrorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where lastError does not contain
        defaultWebhookDeliveryLogFiltering(
            "lastError.doesNotContain=" + UPDATED_LAST_ERROR,
            "lastError.doesNotContain=" + DEFAULT_LAST_ERROR
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByDeliveredAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where deliveredAt equals to
        defaultWebhookDeliveryLogFiltering("deliveredAt.equals=" + DEFAULT_DELIVERED_AT, "deliveredAt.equals=" + UPDATED_DELIVERED_AT);
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByDeliveredAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where deliveredAt in
        defaultWebhookDeliveryLogFiltering(
            "deliveredAt.in=" + DEFAULT_DELIVERED_AT + "," + UPDATED_DELIVERED_AT,
            "deliveredAt.in=" + UPDATED_DELIVERED_AT
        );
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByDeliveredAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        // Get all the webhookDeliveryLogList where deliveredAt is not null
        defaultWebhookDeliveryLogFiltering("deliveredAt.specified=true", "deliveredAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookDeliveryLogsByWebhookEndpointIsEqualToSomething() throws Exception {
        WebhookEndpoint webhookEndpoint;
        if (TestUtil.findAll(em, WebhookEndpoint.class).isEmpty()) {
            webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);
            webhookEndpoint = WebhookEndpointResourceIT.createEntity();
        } else {
            webhookEndpoint = TestUtil.findAll(em, WebhookEndpoint.class).get(0);
        }
        em.persist(webhookEndpoint);
        em.flush();
        webhookDeliveryLog.setWebhookEndpoint(webhookEndpoint);
        webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);
        Long webhookEndpointId = webhookEndpoint.getId();
        // Get all the webhookDeliveryLogList where webhookEndpoint equals to webhookEndpointId
        defaultWebhookDeliveryLogShouldBeFound("webhookEndpointId.equals=" + webhookEndpointId);

        // Get all the webhookDeliveryLogList where webhookEndpoint equals to (webhookEndpointId + 1)
        defaultWebhookDeliveryLogShouldNotBeFound("webhookEndpointId.equals=" + (webhookEndpointId + 1));
    }

    private void defaultWebhookDeliveryLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWebhookDeliveryLogShouldBeFound(shouldBeFound);
        defaultWebhookDeliveryLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWebhookDeliveryLogShouldBeFound(String filter) throws Exception {
        restWebhookDeliveryLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webhookDeliveryLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].deliveryId").value(hasItem(DEFAULT_DELIVERY_ID)))
            .andExpect(jsonPath("$.[*].event").value(hasItem(DEFAULT_EVENT)))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].httpStatus").value(hasItem(DEFAULT_HTTP_STATUS)))
            .andExpect(jsonPath("$.[*].attempts").value(hasItem(DEFAULT_ATTEMPTS)))
            .andExpect(jsonPath("$.[*].lastError").value(hasItem(DEFAULT_LAST_ERROR)))
            .andExpect(jsonPath("$.[*].deliveredAt").value(hasItem(DEFAULT_DELIVERED_AT.toString())));

        // Check, that the count call also returns 1
        restWebhookDeliveryLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWebhookDeliveryLogShouldNotBeFound(String filter) throws Exception {
        restWebhookDeliveryLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWebhookDeliveryLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWebhookDeliveryLog() throws Exception {
        // Get the webhookDeliveryLog
        restWebhookDeliveryLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWebhookDeliveryLog() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookDeliveryLog
        WebhookDeliveryLog updatedWebhookDeliveryLog = webhookDeliveryLogRepository.findById(webhookDeliveryLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWebhookDeliveryLog are not directly saved in db
        em.detach(updatedWebhookDeliveryLog);
        updatedWebhookDeliveryLog
            .deliveryId(UPDATED_DELIVERY_ID)
            .event(UPDATED_EVENT)
            .payload(UPDATED_PAYLOAD)
            .status(UPDATED_STATUS)
            .httpStatus(UPDATED_HTTP_STATUS)
            .attempts(UPDATED_ATTEMPTS)
            .lastError(UPDATED_LAST_ERROR)
            .deliveredAt(UPDATED_DELIVERED_AT);
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(updatedWebhookDeliveryLog);

        restWebhookDeliveryLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, webhookDeliveryLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookDeliveryLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the WebhookDeliveryLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWebhookDeliveryLogToMatchAllProperties(updatedWebhookDeliveryLog);
    }

    @Test
    @Transactional
    void putNonExistingWebhookDeliveryLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryLog.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryLog
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWebhookDeliveryLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, webhookDeliveryLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookDeliveryLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWebhookDeliveryLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryLog.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryLog
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookDeliveryLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookDeliveryLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWebhookDeliveryLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryLog.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryLog
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookDeliveryLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookDeliveryLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WebhookDeliveryLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWebhookDeliveryLogWithPatch() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookDeliveryLog using partial update
        WebhookDeliveryLog partialUpdatedWebhookDeliveryLog = new WebhookDeliveryLog();
        partialUpdatedWebhookDeliveryLog.setId(webhookDeliveryLog.getId());

        partialUpdatedWebhookDeliveryLog.event(UPDATED_EVENT).attempts(UPDATED_ATTEMPTS).lastError(UPDATED_LAST_ERROR);

        restWebhookDeliveryLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWebhookDeliveryLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWebhookDeliveryLog))
            )
            .andExpect(status().isOk());

        // Validate the WebhookDeliveryLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWebhookDeliveryLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWebhookDeliveryLog, webhookDeliveryLog),
            getPersistedWebhookDeliveryLog(webhookDeliveryLog)
        );
    }

    @Test
    @Transactional
    void fullUpdateWebhookDeliveryLogWithPatch() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookDeliveryLog using partial update
        WebhookDeliveryLog partialUpdatedWebhookDeliveryLog = new WebhookDeliveryLog();
        partialUpdatedWebhookDeliveryLog.setId(webhookDeliveryLog.getId());

        partialUpdatedWebhookDeliveryLog
            .deliveryId(UPDATED_DELIVERY_ID)
            .event(UPDATED_EVENT)
            .payload(UPDATED_PAYLOAD)
            .status(UPDATED_STATUS)
            .httpStatus(UPDATED_HTTP_STATUS)
            .attempts(UPDATED_ATTEMPTS)
            .lastError(UPDATED_LAST_ERROR)
            .deliveredAt(UPDATED_DELIVERED_AT);

        restWebhookDeliveryLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWebhookDeliveryLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWebhookDeliveryLog))
            )
            .andExpect(status().isOk());

        // Validate the WebhookDeliveryLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWebhookDeliveryLogUpdatableFieldsEquals(
            partialUpdatedWebhookDeliveryLog,
            getPersistedWebhookDeliveryLog(partialUpdatedWebhookDeliveryLog)
        );
    }

    @Test
    @Transactional
    void patchNonExistingWebhookDeliveryLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryLog.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryLog
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWebhookDeliveryLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, webhookDeliveryLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(webhookDeliveryLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWebhookDeliveryLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryLog.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryLog
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookDeliveryLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(webhookDeliveryLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookDeliveryLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWebhookDeliveryLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookDeliveryLog.setId(longCount.incrementAndGet());

        // Create the WebhookDeliveryLog
        WebhookDeliveryLogDTO webhookDeliveryLogDTO = webhookDeliveryLogMapper.toDto(webhookDeliveryLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookDeliveryLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(webhookDeliveryLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WebhookDeliveryLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWebhookDeliveryLog() throws Exception {
        // Initialize the database
        insertedWebhookDeliveryLog = webhookDeliveryLogRepository.saveAndFlush(webhookDeliveryLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the webhookDeliveryLog
        restWebhookDeliveryLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, webhookDeliveryLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return webhookDeliveryLogRepository.count();
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

    protected WebhookDeliveryLog getPersistedWebhookDeliveryLog(WebhookDeliveryLog webhookDeliveryLog) {
        return webhookDeliveryLogRepository.findById(webhookDeliveryLog.getId()).orElseThrow();
    }

    protected void assertPersistedWebhookDeliveryLogToMatchAllProperties(WebhookDeliveryLog expectedWebhookDeliveryLog) {
        assertWebhookDeliveryLogAllPropertiesEquals(expectedWebhookDeliveryLog, getPersistedWebhookDeliveryLog(expectedWebhookDeliveryLog));
    }

    protected void assertPersistedWebhookDeliveryLogToMatchUpdatableProperties(WebhookDeliveryLog expectedWebhookDeliveryLog) {
        assertWebhookDeliveryLogAllUpdatablePropertiesEquals(
            expectedWebhookDeliveryLog,
            getPersistedWebhookDeliveryLog(expectedWebhookDeliveryLog)
        );
    }
}
