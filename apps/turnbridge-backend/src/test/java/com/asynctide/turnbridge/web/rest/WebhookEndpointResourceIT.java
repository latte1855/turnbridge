package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.WebhookEndpointAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.WebhookEndpoint;
import com.asynctide.turnbridge.domain.enumeration.WebhookStatus;
import com.asynctide.turnbridge.repository.WebhookEndpointRepository;
import com.asynctide.turnbridge.service.WebhookEndpointService;
import com.asynctide.turnbridge.service.dto.WebhookEndpointDTO;
import com.asynctide.turnbridge.service.mapper.WebhookEndpointMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link WebhookEndpointResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WebhookEndpointResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TARGET_URL = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_URL = "BBBBBBBBBB";

    private static final String DEFAULT_SECRET = "AAAAAAAAAA";
    private static final String UPDATED_SECRET = "BBBBBBBBBB";

    private static final String DEFAULT_EVENTS = "AAAAAAAAAA";
    private static final String UPDATED_EVENTS = "BBBBBBBBBB";

    private static final WebhookStatus DEFAULT_STATUS = WebhookStatus.ACTIVE;
    private static final WebhookStatus UPDATED_STATUS = WebhookStatus.DISABLED;

    private static final String ENTITY_API_URL = "/api/webhook-endpoints";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WebhookEndpointRepository webhookEndpointRepository;

    @Mock
    private WebhookEndpointRepository webhookEndpointRepositoryMock;

    @Autowired
    private WebhookEndpointMapper webhookEndpointMapper;

    @Mock
    private WebhookEndpointService webhookEndpointServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWebhookEndpointMockMvc;

    private WebhookEndpoint webhookEndpoint;

    private WebhookEndpoint insertedWebhookEndpoint;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WebhookEndpoint createEntity() {
        return new WebhookEndpoint()
            .name(DEFAULT_NAME)
            .targetUrl(DEFAULT_TARGET_URL)
            .secret(DEFAULT_SECRET)
            .events(DEFAULT_EVENTS)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WebhookEndpoint createUpdatedEntity() {
        return new WebhookEndpoint()
            .name(UPDATED_NAME)
            .targetUrl(UPDATED_TARGET_URL)
            .secret(UPDATED_SECRET)
            .events(UPDATED_EVENTS)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        webhookEndpoint = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWebhookEndpoint != null) {
            webhookEndpointRepository.delete(insertedWebhookEndpoint);
            insertedWebhookEndpoint = null;
        }
    }

    @Test
    @Transactional
    void createWebhookEndpoint() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WebhookEndpoint
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);
        var returnedWebhookEndpointDTO = om.readValue(
            restWebhookEndpointMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookEndpointDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WebhookEndpointDTO.class
        );

        // Validate the WebhookEndpoint in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWebhookEndpoint = webhookEndpointMapper.toEntity(returnedWebhookEndpointDTO);
        assertWebhookEndpointUpdatableFieldsEquals(returnedWebhookEndpoint, getPersistedWebhookEndpoint(returnedWebhookEndpoint));

        insertedWebhookEndpoint = returnedWebhookEndpoint;
    }

    @Test
    @Transactional
    void createWebhookEndpointWithExistingId() throws Exception {
        // Create the WebhookEndpoint with an existing ID
        webhookEndpoint.setId(1L);
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWebhookEndpointMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookEndpointDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WebhookEndpoint in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookEndpoint.setName(null);

        // Create the WebhookEndpoint, which fails.
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        restWebhookEndpointMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookEndpointDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTargetUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookEndpoint.setTargetUrl(null);

        // Create the WebhookEndpoint, which fails.
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        restWebhookEndpointMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookEndpointDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEventsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookEndpoint.setEvents(null);

        // Create the WebhookEndpoint, which fails.
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        restWebhookEndpointMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookEndpointDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        webhookEndpoint.setStatus(null);

        // Create the WebhookEndpoint, which fails.
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        restWebhookEndpointMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookEndpointDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWebhookEndpoints() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList
        restWebhookEndpointMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webhookEndpoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].targetUrl").value(hasItem(DEFAULT_TARGET_URL)))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET)))
            .andExpect(jsonPath("$.[*].events").value(hasItem(DEFAULT_EVENTS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWebhookEndpointsWithEagerRelationshipsIsEnabled() throws Exception {
        when(webhookEndpointServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWebhookEndpointMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(webhookEndpointServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWebhookEndpointsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(webhookEndpointServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWebhookEndpointMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(webhookEndpointRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getWebhookEndpoint() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get the webhookEndpoint
        restWebhookEndpointMockMvc
            .perform(get(ENTITY_API_URL_ID, webhookEndpoint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(webhookEndpoint.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.targetUrl").value(DEFAULT_TARGET_URL))
            .andExpect(jsonPath("$.secret").value(DEFAULT_SECRET))
            .andExpect(jsonPath("$.events").value(DEFAULT_EVENTS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getWebhookEndpointsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        Long id = webhookEndpoint.getId();

        defaultWebhookEndpointFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWebhookEndpointFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWebhookEndpointFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where name equals to
        defaultWebhookEndpointFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where name in
        defaultWebhookEndpointFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where name is not null
        defaultWebhookEndpointFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where name contains
        defaultWebhookEndpointFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where name does not contain
        defaultWebhookEndpointFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByTargetUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where targetUrl equals to
        defaultWebhookEndpointFiltering("targetUrl.equals=" + DEFAULT_TARGET_URL, "targetUrl.equals=" + UPDATED_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByTargetUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where targetUrl in
        defaultWebhookEndpointFiltering(
            "targetUrl.in=" + DEFAULT_TARGET_URL + "," + UPDATED_TARGET_URL,
            "targetUrl.in=" + UPDATED_TARGET_URL
        );
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByTargetUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where targetUrl is not null
        defaultWebhookEndpointFiltering("targetUrl.specified=true", "targetUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByTargetUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where targetUrl contains
        defaultWebhookEndpointFiltering("targetUrl.contains=" + DEFAULT_TARGET_URL, "targetUrl.contains=" + UPDATED_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByTargetUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where targetUrl does not contain
        defaultWebhookEndpointFiltering("targetUrl.doesNotContain=" + UPDATED_TARGET_URL, "targetUrl.doesNotContain=" + DEFAULT_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsBySecretIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where secret equals to
        defaultWebhookEndpointFiltering("secret.equals=" + DEFAULT_SECRET, "secret.equals=" + UPDATED_SECRET);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsBySecretIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where secret in
        defaultWebhookEndpointFiltering("secret.in=" + DEFAULT_SECRET + "," + UPDATED_SECRET, "secret.in=" + UPDATED_SECRET);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsBySecretIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where secret is not null
        defaultWebhookEndpointFiltering("secret.specified=true", "secret.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsBySecretContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where secret contains
        defaultWebhookEndpointFiltering("secret.contains=" + DEFAULT_SECRET, "secret.contains=" + UPDATED_SECRET);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsBySecretNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where secret does not contain
        defaultWebhookEndpointFiltering("secret.doesNotContain=" + UPDATED_SECRET, "secret.doesNotContain=" + DEFAULT_SECRET);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByEventsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where events equals to
        defaultWebhookEndpointFiltering("events.equals=" + DEFAULT_EVENTS, "events.equals=" + UPDATED_EVENTS);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByEventsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where events in
        defaultWebhookEndpointFiltering("events.in=" + DEFAULT_EVENTS + "," + UPDATED_EVENTS, "events.in=" + UPDATED_EVENTS);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByEventsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where events is not null
        defaultWebhookEndpointFiltering("events.specified=true", "events.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByEventsContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where events contains
        defaultWebhookEndpointFiltering("events.contains=" + DEFAULT_EVENTS, "events.contains=" + UPDATED_EVENTS);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByEventsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where events does not contain
        defaultWebhookEndpointFiltering("events.doesNotContain=" + UPDATED_EVENTS, "events.doesNotContain=" + DEFAULT_EVENTS);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where status equals to
        defaultWebhookEndpointFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where status in
        defaultWebhookEndpointFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        // Get all the webhookEndpointList where status is not null
        defaultWebhookEndpointFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllWebhookEndpointsByTenantIsEqualToSomething() throws Exception {
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            webhookEndpointRepository.saveAndFlush(webhookEndpoint);
            tenant = TenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        em.persist(tenant);
        em.flush();
        webhookEndpoint.setTenant(tenant);
        webhookEndpointRepository.saveAndFlush(webhookEndpoint);
        Long tenantId = tenant.getId();
        // Get all the webhookEndpointList where tenant equals to tenantId
        defaultWebhookEndpointShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the webhookEndpointList where tenant equals to (tenantId + 1)
        defaultWebhookEndpointShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultWebhookEndpointFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWebhookEndpointShouldBeFound(shouldBeFound);
        defaultWebhookEndpointShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWebhookEndpointShouldBeFound(String filter) throws Exception {
        restWebhookEndpointMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(webhookEndpoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].targetUrl").value(hasItem(DEFAULT_TARGET_URL)))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET)))
            .andExpect(jsonPath("$.[*].events").value(hasItem(DEFAULT_EVENTS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restWebhookEndpointMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWebhookEndpointShouldNotBeFound(String filter) throws Exception {
        restWebhookEndpointMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWebhookEndpointMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWebhookEndpoint() throws Exception {
        // Get the webhookEndpoint
        restWebhookEndpointMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWebhookEndpoint() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookEndpoint
        WebhookEndpoint updatedWebhookEndpoint = webhookEndpointRepository.findById(webhookEndpoint.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWebhookEndpoint are not directly saved in db
        em.detach(updatedWebhookEndpoint);
        updatedWebhookEndpoint
            .name(UPDATED_NAME)
            .targetUrl(UPDATED_TARGET_URL)
            .secret(UPDATED_SECRET)
            .events(UPDATED_EVENTS)
            .status(UPDATED_STATUS);
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(updatedWebhookEndpoint);

        restWebhookEndpointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, webhookEndpointDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookEndpointDTO))
            )
            .andExpect(status().isOk());

        // Validate the WebhookEndpoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWebhookEndpointToMatchAllProperties(updatedWebhookEndpoint);
    }

    @Test
    @Transactional
    void putNonExistingWebhookEndpoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookEndpoint.setId(longCount.incrementAndGet());

        // Create the WebhookEndpoint
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWebhookEndpointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, webhookEndpointDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookEndpointDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookEndpoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWebhookEndpoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookEndpoint.setId(longCount.incrementAndGet());

        // Create the WebhookEndpoint
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookEndpointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(webhookEndpointDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookEndpoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWebhookEndpoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookEndpoint.setId(longCount.incrementAndGet());

        // Create the WebhookEndpoint
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookEndpointMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(webhookEndpointDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WebhookEndpoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWebhookEndpointWithPatch() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookEndpoint using partial update
        WebhookEndpoint partialUpdatedWebhookEndpoint = new WebhookEndpoint();
        partialUpdatedWebhookEndpoint.setId(webhookEndpoint.getId());

        partialUpdatedWebhookEndpoint.name(UPDATED_NAME).secret(UPDATED_SECRET).events(UPDATED_EVENTS);

        restWebhookEndpointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWebhookEndpoint.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWebhookEndpoint))
            )
            .andExpect(status().isOk());

        // Validate the WebhookEndpoint in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWebhookEndpointUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWebhookEndpoint, webhookEndpoint),
            getPersistedWebhookEndpoint(webhookEndpoint)
        );
    }

    @Test
    @Transactional
    void fullUpdateWebhookEndpointWithPatch() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the webhookEndpoint using partial update
        WebhookEndpoint partialUpdatedWebhookEndpoint = new WebhookEndpoint();
        partialUpdatedWebhookEndpoint.setId(webhookEndpoint.getId());

        partialUpdatedWebhookEndpoint
            .name(UPDATED_NAME)
            .targetUrl(UPDATED_TARGET_URL)
            .secret(UPDATED_SECRET)
            .events(UPDATED_EVENTS)
            .status(UPDATED_STATUS);

        restWebhookEndpointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWebhookEndpoint.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWebhookEndpoint))
            )
            .andExpect(status().isOk());

        // Validate the WebhookEndpoint in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWebhookEndpointUpdatableFieldsEquals(
            partialUpdatedWebhookEndpoint,
            getPersistedWebhookEndpoint(partialUpdatedWebhookEndpoint)
        );
    }

    @Test
    @Transactional
    void patchNonExistingWebhookEndpoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookEndpoint.setId(longCount.incrementAndGet());

        // Create the WebhookEndpoint
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWebhookEndpointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, webhookEndpointDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(webhookEndpointDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookEndpoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWebhookEndpoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookEndpoint.setId(longCount.incrementAndGet());

        // Create the WebhookEndpoint
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookEndpointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(webhookEndpointDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WebhookEndpoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWebhookEndpoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        webhookEndpoint.setId(longCount.incrementAndGet());

        // Create the WebhookEndpoint
        WebhookEndpointDTO webhookEndpointDTO = webhookEndpointMapper.toDto(webhookEndpoint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWebhookEndpointMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(webhookEndpointDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WebhookEndpoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWebhookEndpoint() throws Exception {
        // Initialize the database
        insertedWebhookEndpoint = webhookEndpointRepository.saveAndFlush(webhookEndpoint);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the webhookEndpoint
        restWebhookEndpointMockMvc
            .perform(delete(ENTITY_API_URL_ID, webhookEndpoint.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return webhookEndpointRepository.count();
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

    protected WebhookEndpoint getPersistedWebhookEndpoint(WebhookEndpoint webhookEndpoint) {
        return webhookEndpointRepository.findById(webhookEndpoint.getId()).orElseThrow();
    }

    protected void assertPersistedWebhookEndpointToMatchAllProperties(WebhookEndpoint expectedWebhookEndpoint) {
        assertWebhookEndpointAllPropertiesEquals(expectedWebhookEndpoint, getPersistedWebhookEndpoint(expectedWebhookEndpoint));
    }

    protected void assertPersistedWebhookEndpointToMatchUpdatableProperties(WebhookEndpoint expectedWebhookEndpoint) {
        assertWebhookEndpointAllUpdatablePropertiesEquals(expectedWebhookEndpoint, getPersistedWebhookEndpoint(expectedWebhookEndpoint));
    }
}
