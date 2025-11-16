package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.ManualActionAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.ManualAction;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.enumeration.ApprovalStatus;
import com.asynctide.turnbridge.domain.enumeration.ManualActionType;
import com.asynctide.turnbridge.repository.ManualActionRepository;
import com.asynctide.turnbridge.service.ManualActionService;
import com.asynctide.turnbridge.service.dto.ManualActionDTO;
import com.asynctide.turnbridge.service.mapper.ManualActionMapper;
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
 * Integration tests for the {@link ManualActionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ManualActionResourceIT {

    private static final ManualActionType DEFAULT_ACTION_TYPE = ManualActionType.RESEND_XML;
    private static final ManualActionType UPDATED_ACTION_TYPE = ManualActionType.ASSIGN_NO;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final ApprovalStatus DEFAULT_STATUS = ApprovalStatus.PENDING;
    private static final ApprovalStatus UPDATED_STATUS = ApprovalStatus.APPROVED;

    private static final String DEFAULT_REQUESTED_BY = "AAAAAAAAAA";
    private static final String UPDATED_REQUESTED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_REQUESTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUESTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_APPROVED_BY = "AAAAAAAAAA";
    private static final String UPDATED_APPROVED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_APPROVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_APPROVED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RESULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_RESULT_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/manual-actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ManualActionRepository manualActionRepository;

    @Mock
    private ManualActionRepository manualActionRepositoryMock;

    @Autowired
    private ManualActionMapper manualActionMapper;

    @Mock
    private ManualActionService manualActionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restManualActionMockMvc;

    private ManualAction manualAction;

    private ManualAction insertedManualAction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ManualAction createEntity(EntityManager em) {
        ManualAction manualAction = new ManualAction()
            .actionType(DEFAULT_ACTION_TYPE)
            .reason(DEFAULT_REASON)
            .status(DEFAULT_STATUS)
            .requestedBy(DEFAULT_REQUESTED_BY)
            .requestedAt(DEFAULT_REQUESTED_AT)
            .approvedBy(DEFAULT_APPROVED_BY)
            .approvedAt(DEFAULT_APPROVED_AT)
            .resultMessage(DEFAULT_RESULT_MESSAGE);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createEntity();
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        manualAction.setTenant(tenant);
        return manualAction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ManualAction createUpdatedEntity(EntityManager em) {
        ManualAction updatedManualAction = new ManualAction()
            .actionType(UPDATED_ACTION_TYPE)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedAt(UPDATED_REQUESTED_AT)
            .approvedBy(UPDATED_APPROVED_BY)
            .approvedAt(UPDATED_APPROVED_AT)
            .resultMessage(UPDATED_RESULT_MESSAGE);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createUpdatedEntity();
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        updatedManualAction.setTenant(tenant);
        return updatedManualAction;
    }

    @BeforeEach
    void initTest() {
        manualAction = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedManualAction != null) {
            manualActionRepository.delete(insertedManualAction);
            insertedManualAction = null;
        }
    }

    @Test
    @Transactional
    void createManualAction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ManualAction
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);
        var returnedManualActionDTO = om.readValue(
            restManualActionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(manualActionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ManualActionDTO.class
        );

        // Validate the ManualAction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedManualAction = manualActionMapper.toEntity(returnedManualActionDTO);
        assertManualActionUpdatableFieldsEquals(returnedManualAction, getPersistedManualAction(returnedManualAction));

        insertedManualAction = returnedManualAction;
    }

    @Test
    @Transactional
    void createManualActionWithExistingId() throws Exception {
        // Create the ManualAction with an existing ID
        manualAction.setId(1L);
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restManualActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(manualActionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ManualAction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActionTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        manualAction.setActionType(null);

        // Create the ManualAction, which fails.
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        restManualActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(manualActionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        manualAction.setReason(null);

        // Create the ManualAction, which fails.
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        restManualActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(manualActionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        manualAction.setStatus(null);

        // Create the ManualAction, which fails.
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        restManualActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(manualActionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllManualActions() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList
        restManualActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(manualAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].actionType").value(hasItem(DEFAULT_ACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].requestedBy").value(hasItem(DEFAULT_REQUESTED_BY)))
            .andExpect(jsonPath("$.[*].requestedAt").value(hasItem(DEFAULT_REQUESTED_AT.toString())))
            .andExpect(jsonPath("$.[*].approvedBy").value(hasItem(DEFAULT_APPROVED_BY)))
            .andExpect(jsonPath("$.[*].approvedAt").value(hasItem(DEFAULT_APPROVED_AT.toString())))
            .andExpect(jsonPath("$.[*].resultMessage").value(hasItem(DEFAULT_RESULT_MESSAGE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllManualActionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(manualActionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restManualActionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(manualActionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllManualActionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(manualActionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restManualActionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(manualActionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getManualAction() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get the manualAction
        restManualActionMockMvc
            .perform(get(ENTITY_API_URL_ID, manualAction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(manualAction.getId().intValue()))
            .andExpect(jsonPath("$.actionType").value(DEFAULT_ACTION_TYPE.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.requestedBy").value(DEFAULT_REQUESTED_BY))
            .andExpect(jsonPath("$.requestedAt").value(DEFAULT_REQUESTED_AT.toString()))
            .andExpect(jsonPath("$.approvedBy").value(DEFAULT_APPROVED_BY))
            .andExpect(jsonPath("$.approvedAt").value(DEFAULT_APPROVED_AT.toString()))
            .andExpect(jsonPath("$.resultMessage").value(DEFAULT_RESULT_MESSAGE));
    }

    @Test
    @Transactional
    void getManualActionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        Long id = manualAction.getId();

        defaultManualActionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultManualActionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultManualActionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllManualActionsByActionTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where actionType equals to
        defaultManualActionFiltering("actionType.equals=" + DEFAULT_ACTION_TYPE, "actionType.equals=" + UPDATED_ACTION_TYPE);
    }

    @Test
    @Transactional
    void getAllManualActionsByActionTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where actionType in
        defaultManualActionFiltering(
            "actionType.in=" + DEFAULT_ACTION_TYPE + "," + UPDATED_ACTION_TYPE,
            "actionType.in=" + UPDATED_ACTION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllManualActionsByActionTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where actionType is not null
        defaultManualActionFiltering("actionType.specified=true", "actionType.specified=false");
    }

    @Test
    @Transactional
    void getAllManualActionsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where reason equals to
        defaultManualActionFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllManualActionsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where reason in
        defaultManualActionFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllManualActionsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where reason is not null
        defaultManualActionFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    @Transactional
    void getAllManualActionsByReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where reason contains
        defaultManualActionFiltering("reason.contains=" + DEFAULT_REASON, "reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllManualActionsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where reason does not contain
        defaultManualActionFiltering("reason.doesNotContain=" + UPDATED_REASON, "reason.doesNotContain=" + DEFAULT_REASON);
    }

    @Test
    @Transactional
    void getAllManualActionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where status equals to
        defaultManualActionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllManualActionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where status in
        defaultManualActionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllManualActionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where status is not null
        defaultManualActionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllManualActionsByRequestedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where requestedBy equals to
        defaultManualActionFiltering("requestedBy.equals=" + DEFAULT_REQUESTED_BY, "requestedBy.equals=" + UPDATED_REQUESTED_BY);
    }

    @Test
    @Transactional
    void getAllManualActionsByRequestedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where requestedBy in
        defaultManualActionFiltering(
            "requestedBy.in=" + DEFAULT_REQUESTED_BY + "," + UPDATED_REQUESTED_BY,
            "requestedBy.in=" + UPDATED_REQUESTED_BY
        );
    }

    @Test
    @Transactional
    void getAllManualActionsByRequestedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where requestedBy is not null
        defaultManualActionFiltering("requestedBy.specified=true", "requestedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllManualActionsByRequestedByContainsSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where requestedBy contains
        defaultManualActionFiltering("requestedBy.contains=" + DEFAULT_REQUESTED_BY, "requestedBy.contains=" + UPDATED_REQUESTED_BY);
    }

    @Test
    @Transactional
    void getAllManualActionsByRequestedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where requestedBy does not contain
        defaultManualActionFiltering(
            "requestedBy.doesNotContain=" + UPDATED_REQUESTED_BY,
            "requestedBy.doesNotContain=" + DEFAULT_REQUESTED_BY
        );
    }

    @Test
    @Transactional
    void getAllManualActionsByRequestedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where requestedAt equals to
        defaultManualActionFiltering("requestedAt.equals=" + DEFAULT_REQUESTED_AT, "requestedAt.equals=" + UPDATED_REQUESTED_AT);
    }

    @Test
    @Transactional
    void getAllManualActionsByRequestedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where requestedAt in
        defaultManualActionFiltering(
            "requestedAt.in=" + DEFAULT_REQUESTED_AT + "," + UPDATED_REQUESTED_AT,
            "requestedAt.in=" + UPDATED_REQUESTED_AT
        );
    }

    @Test
    @Transactional
    void getAllManualActionsByRequestedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where requestedAt is not null
        defaultManualActionFiltering("requestedAt.specified=true", "requestedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllManualActionsByApprovedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where approvedBy equals to
        defaultManualActionFiltering("approvedBy.equals=" + DEFAULT_APPROVED_BY, "approvedBy.equals=" + UPDATED_APPROVED_BY);
    }

    @Test
    @Transactional
    void getAllManualActionsByApprovedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where approvedBy in
        defaultManualActionFiltering(
            "approvedBy.in=" + DEFAULT_APPROVED_BY + "," + UPDATED_APPROVED_BY,
            "approvedBy.in=" + UPDATED_APPROVED_BY
        );
    }

    @Test
    @Transactional
    void getAllManualActionsByApprovedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where approvedBy is not null
        defaultManualActionFiltering("approvedBy.specified=true", "approvedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllManualActionsByApprovedByContainsSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where approvedBy contains
        defaultManualActionFiltering("approvedBy.contains=" + DEFAULT_APPROVED_BY, "approvedBy.contains=" + UPDATED_APPROVED_BY);
    }

    @Test
    @Transactional
    void getAllManualActionsByApprovedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where approvedBy does not contain
        defaultManualActionFiltering(
            "approvedBy.doesNotContain=" + UPDATED_APPROVED_BY,
            "approvedBy.doesNotContain=" + DEFAULT_APPROVED_BY
        );
    }

    @Test
    @Transactional
    void getAllManualActionsByApprovedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where approvedAt equals to
        defaultManualActionFiltering("approvedAt.equals=" + DEFAULT_APPROVED_AT, "approvedAt.equals=" + UPDATED_APPROVED_AT);
    }

    @Test
    @Transactional
    void getAllManualActionsByApprovedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where approvedAt in
        defaultManualActionFiltering(
            "approvedAt.in=" + DEFAULT_APPROVED_AT + "," + UPDATED_APPROVED_AT,
            "approvedAt.in=" + UPDATED_APPROVED_AT
        );
    }

    @Test
    @Transactional
    void getAllManualActionsByApprovedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        // Get all the manualActionList where approvedAt is not null
        defaultManualActionFiltering("approvedAt.specified=true", "approvedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllManualActionsByTenantIsEqualToSomething() throws Exception {
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            manualActionRepository.saveAndFlush(manualAction);
            tenant = TenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        em.persist(tenant);
        em.flush();
        manualAction.setTenant(tenant);
        manualActionRepository.saveAndFlush(manualAction);
        Long tenantId = tenant.getId();
        // Get all the manualActionList where tenant equals to tenantId
        defaultManualActionShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the manualActionList where tenant equals to (tenantId + 1)
        defaultManualActionShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    @Test
    @Transactional
    void getAllManualActionsByInvoiceIsEqualToSomething() throws Exception {
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            manualActionRepository.saveAndFlush(manualAction);
            invoice = InvoiceResourceIT.createEntity(em);
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        em.persist(invoice);
        em.flush();
        manualAction.setInvoice(invoice);
        manualActionRepository.saveAndFlush(manualAction);
        Long invoiceId = invoice.getId();
        // Get all the manualActionList where invoice equals to invoiceId
        defaultManualActionShouldBeFound("invoiceId.equals=" + invoiceId);

        // Get all the manualActionList where invoice equals to (invoiceId + 1)
        defaultManualActionShouldNotBeFound("invoiceId.equals=" + (invoiceId + 1));
    }

    @Test
    @Transactional
    void getAllManualActionsByImportFileIsEqualToSomething() throws Exception {
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            manualActionRepository.saveAndFlush(manualAction);
            importFile = ImportFileResourceIT.createEntity();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        em.persist(importFile);
        em.flush();
        manualAction.setImportFile(importFile);
        manualActionRepository.saveAndFlush(manualAction);
        Long importFileId = importFile.getId();
        // Get all the manualActionList where importFile equals to importFileId
        defaultManualActionShouldBeFound("importFileId.equals=" + importFileId);

        // Get all the manualActionList where importFile equals to (importFileId + 1)
        defaultManualActionShouldNotBeFound("importFileId.equals=" + (importFileId + 1));
    }

    private void defaultManualActionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultManualActionShouldBeFound(shouldBeFound);
        defaultManualActionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultManualActionShouldBeFound(String filter) throws Exception {
        restManualActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(manualAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].actionType").value(hasItem(DEFAULT_ACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].requestedBy").value(hasItem(DEFAULT_REQUESTED_BY)))
            .andExpect(jsonPath("$.[*].requestedAt").value(hasItem(DEFAULT_REQUESTED_AT.toString())))
            .andExpect(jsonPath("$.[*].approvedBy").value(hasItem(DEFAULT_APPROVED_BY)))
            .andExpect(jsonPath("$.[*].approvedAt").value(hasItem(DEFAULT_APPROVED_AT.toString())))
            .andExpect(jsonPath("$.[*].resultMessage").value(hasItem(DEFAULT_RESULT_MESSAGE)));

        // Check, that the count call also returns 1
        restManualActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultManualActionShouldNotBeFound(String filter) throws Exception {
        restManualActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restManualActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingManualAction() throws Exception {
        // Get the manualAction
        restManualActionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingManualAction() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the manualAction
        ManualAction updatedManualAction = manualActionRepository.findById(manualAction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedManualAction are not directly saved in db
        em.detach(updatedManualAction);
        updatedManualAction
            .actionType(UPDATED_ACTION_TYPE)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedAt(UPDATED_REQUESTED_AT)
            .approvedBy(UPDATED_APPROVED_BY)
            .approvedAt(UPDATED_APPROVED_AT)
            .resultMessage(UPDATED_RESULT_MESSAGE);
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(updatedManualAction);

        restManualActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, manualActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(manualActionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ManualAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedManualActionToMatchAllProperties(updatedManualAction);
    }

    @Test
    @Transactional
    void putNonExistingManualAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manualAction.setId(longCount.incrementAndGet());

        // Create the ManualAction
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restManualActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, manualActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(manualActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ManualAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchManualAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manualAction.setId(longCount.incrementAndGet());

        // Create the ManualAction
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restManualActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(manualActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ManualAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamManualAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manualAction.setId(longCount.incrementAndGet());

        // Create the ManualAction
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restManualActionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(manualActionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ManualAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateManualActionWithPatch() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the manualAction using partial update
        ManualAction partialUpdatedManualAction = new ManualAction();
        partialUpdatedManualAction.setId(manualAction.getId());

        partialUpdatedManualAction.requestedBy(UPDATED_REQUESTED_BY).resultMessage(UPDATED_RESULT_MESSAGE);

        restManualActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedManualAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedManualAction))
            )
            .andExpect(status().isOk());

        // Validate the ManualAction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertManualActionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedManualAction, manualAction),
            getPersistedManualAction(manualAction)
        );
    }

    @Test
    @Transactional
    void fullUpdateManualActionWithPatch() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the manualAction using partial update
        ManualAction partialUpdatedManualAction = new ManualAction();
        partialUpdatedManualAction.setId(manualAction.getId());

        partialUpdatedManualAction
            .actionType(UPDATED_ACTION_TYPE)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedAt(UPDATED_REQUESTED_AT)
            .approvedBy(UPDATED_APPROVED_BY)
            .approvedAt(UPDATED_APPROVED_AT)
            .resultMessage(UPDATED_RESULT_MESSAGE);

        restManualActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedManualAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedManualAction))
            )
            .andExpect(status().isOk());

        // Validate the ManualAction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertManualActionUpdatableFieldsEquals(partialUpdatedManualAction, getPersistedManualAction(partialUpdatedManualAction));
    }

    @Test
    @Transactional
    void patchNonExistingManualAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manualAction.setId(longCount.incrementAndGet());

        // Create the ManualAction
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restManualActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, manualActionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(manualActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ManualAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchManualAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manualAction.setId(longCount.incrementAndGet());

        // Create the ManualAction
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restManualActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(manualActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ManualAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamManualAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manualAction.setId(longCount.incrementAndGet());

        // Create the ManualAction
        ManualActionDTO manualActionDTO = manualActionMapper.toDto(manualAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restManualActionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(manualActionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ManualAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteManualAction() throws Exception {
        // Initialize the database
        insertedManualAction = manualActionRepository.saveAndFlush(manualAction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the manualAction
        restManualActionMockMvc
            .perform(delete(ENTITY_API_URL_ID, manualAction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return manualActionRepository.count();
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

    protected ManualAction getPersistedManualAction(ManualAction manualAction) {
        return manualActionRepository.findById(manualAction.getId()).orElseThrow();
    }

    protected void assertPersistedManualActionToMatchAllProperties(ManualAction expectedManualAction) {
        assertManualActionAllPropertiesEquals(expectedManualAction, getPersistedManualAction(expectedManualAction));
    }

    protected void assertPersistedManualActionToMatchUpdatableProperties(ManualAction expectedManualAction) {
        assertManualActionAllUpdatablePropertiesEquals(expectedManualAction, getPersistedManualAction(expectedManualAction));
    }
}
