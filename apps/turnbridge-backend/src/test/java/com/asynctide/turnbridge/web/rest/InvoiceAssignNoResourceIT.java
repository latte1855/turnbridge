package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.InvoiceAssignNoAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.InvoiceAssignNo;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.repository.InvoiceAssignNoRepository;
import com.asynctide.turnbridge.service.InvoiceAssignNoService;
import com.asynctide.turnbridge.service.dto.InvoiceAssignNoDTO;
import com.asynctide.turnbridge.service.mapper.InvoiceAssignNoMapper;
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
 * Integration tests for the {@link InvoiceAssignNoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InvoiceAssignNoResourceIT {

    private static final String DEFAULT_TRACK = "AA";
    private static final String UPDATED_TRACK = "BB";

    private static final String DEFAULT_PERIOD = "772383";
    private static final String UPDATED_PERIOD = "742832";

    private static final String DEFAULT_FROM_NO = "AAAAAAAAAA";
    private static final String UPDATED_FROM_NO = "BBBBBBBBBB";

    private static final String DEFAULT_TO_NO = "AAAAAAAAAA";
    private static final String UPDATED_TO_NO = "BBBBBBBBBB";

    private static final Integer DEFAULT_USED_COUNT = 0;
    private static final Integer UPDATED_USED_COUNT = 1;
    private static final Integer SMALLER_USED_COUNT = 0 - 1;

    private static final Integer DEFAULT_ROLL_SIZE = 0;
    private static final Integer UPDATED_ROLL_SIZE = 1;
    private static final Integer SMALLER_ROLL_SIZE = 0 - 1;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/invoice-assign-nos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InvoiceAssignNoRepository invoiceAssignNoRepository;

    @Mock
    private InvoiceAssignNoRepository invoiceAssignNoRepositoryMock;

    @Autowired
    private InvoiceAssignNoMapper invoiceAssignNoMapper;

    @Mock
    private InvoiceAssignNoService invoiceAssignNoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvoiceAssignNoMockMvc;

    private InvoiceAssignNo invoiceAssignNo;

    private InvoiceAssignNo insertedInvoiceAssignNo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceAssignNo createEntity(EntityManager em) {
        InvoiceAssignNo invoiceAssignNo = new InvoiceAssignNo()
            .track(DEFAULT_TRACK)
            .period(DEFAULT_PERIOD)
            .fromNo(DEFAULT_FROM_NO)
            .toNo(DEFAULT_TO_NO)
            .usedCount(DEFAULT_USED_COUNT)
            .rollSize(DEFAULT_ROLL_SIZE)
            .status(DEFAULT_STATUS);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createEntity();
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        invoiceAssignNo.setTenant(tenant);
        return invoiceAssignNo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceAssignNo createUpdatedEntity(EntityManager em) {
        InvoiceAssignNo updatedInvoiceAssignNo = new InvoiceAssignNo()
            .track(UPDATED_TRACK)
            .period(UPDATED_PERIOD)
            .fromNo(UPDATED_FROM_NO)
            .toNo(UPDATED_TO_NO)
            .usedCount(UPDATED_USED_COUNT)
            .rollSize(UPDATED_ROLL_SIZE)
            .status(UPDATED_STATUS);
        // Add required entity
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            tenant = TenantResourceIT.createUpdatedEntity();
            em.persist(tenant);
            em.flush();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        updatedInvoiceAssignNo.setTenant(tenant);
        return updatedInvoiceAssignNo;
    }

    @BeforeEach
    void initTest() {
        invoiceAssignNo = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInvoiceAssignNo != null) {
            invoiceAssignNoRepository.delete(insertedInvoiceAssignNo);
            insertedInvoiceAssignNo = null;
        }
    }

    @Test
    @Transactional
    void createInvoiceAssignNo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InvoiceAssignNo
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);
        var returnedInvoiceAssignNoDTO = om.readValue(
            restInvoiceAssignNoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceAssignNoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InvoiceAssignNoDTO.class
        );

        // Validate the InvoiceAssignNo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInvoiceAssignNo = invoiceAssignNoMapper.toEntity(returnedInvoiceAssignNoDTO);
        assertInvoiceAssignNoUpdatableFieldsEquals(returnedInvoiceAssignNo, getPersistedInvoiceAssignNo(returnedInvoiceAssignNo));

        insertedInvoiceAssignNo = returnedInvoiceAssignNo;
    }

    @Test
    @Transactional
    void createInvoiceAssignNoWithExistingId() throws Exception {
        // Create the InvoiceAssignNo with an existing ID
        invoiceAssignNo.setId(1L);
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceAssignNoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceAssignNoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InvoiceAssignNo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTrackIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoiceAssignNo.setTrack(null);

        // Create the InvoiceAssignNo, which fails.
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        restInvoiceAssignNoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceAssignNoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPeriodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoiceAssignNo.setPeriod(null);

        // Create the InvoiceAssignNo, which fails.
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        restInvoiceAssignNoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceAssignNoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFromNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoiceAssignNo.setFromNo(null);

        // Create the InvoiceAssignNo, which fails.
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        restInvoiceAssignNoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceAssignNoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkToNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoiceAssignNo.setToNo(null);

        // Create the InvoiceAssignNo, which fails.
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        restInvoiceAssignNoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceAssignNoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNos() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList
        restInvoiceAssignNoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoiceAssignNo.getId().intValue())))
            .andExpect(jsonPath("$.[*].track").value(hasItem(DEFAULT_TRACK)))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].fromNo").value(hasItem(DEFAULT_FROM_NO)))
            .andExpect(jsonPath("$.[*].toNo").value(hasItem(DEFAULT_TO_NO)))
            .andExpect(jsonPath("$.[*].usedCount").value(hasItem(DEFAULT_USED_COUNT)))
            .andExpect(jsonPath("$.[*].rollSize").value(hasItem(DEFAULT_ROLL_SIZE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoiceAssignNosWithEagerRelationshipsIsEnabled() throws Exception {
        when(invoiceAssignNoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceAssignNoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(invoiceAssignNoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoiceAssignNosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(invoiceAssignNoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceAssignNoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(invoiceAssignNoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInvoiceAssignNo() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get the invoiceAssignNo
        restInvoiceAssignNoMockMvc
            .perform(get(ENTITY_API_URL_ID, invoiceAssignNo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoiceAssignNo.getId().intValue()))
            .andExpect(jsonPath("$.track").value(DEFAULT_TRACK))
            .andExpect(jsonPath("$.period").value(DEFAULT_PERIOD))
            .andExpect(jsonPath("$.fromNo").value(DEFAULT_FROM_NO))
            .andExpect(jsonPath("$.toNo").value(DEFAULT_TO_NO))
            .andExpect(jsonPath("$.usedCount").value(DEFAULT_USED_COUNT))
            .andExpect(jsonPath("$.rollSize").value(DEFAULT_ROLL_SIZE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getInvoiceAssignNosByIdFiltering() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        Long id = invoiceAssignNo.getId();

        defaultInvoiceAssignNoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInvoiceAssignNoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInvoiceAssignNoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByTrackIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where track equals to
        defaultInvoiceAssignNoFiltering("track.equals=" + DEFAULT_TRACK, "track.equals=" + UPDATED_TRACK);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByTrackIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where track in
        defaultInvoiceAssignNoFiltering("track.in=" + DEFAULT_TRACK + "," + UPDATED_TRACK, "track.in=" + UPDATED_TRACK);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByTrackIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where track is not null
        defaultInvoiceAssignNoFiltering("track.specified=true", "track.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByTrackContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where track contains
        defaultInvoiceAssignNoFiltering("track.contains=" + DEFAULT_TRACK, "track.contains=" + UPDATED_TRACK);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByTrackNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where track does not contain
        defaultInvoiceAssignNoFiltering("track.doesNotContain=" + UPDATED_TRACK, "track.doesNotContain=" + DEFAULT_TRACK);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByPeriodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where period equals to
        defaultInvoiceAssignNoFiltering("period.equals=" + DEFAULT_PERIOD, "period.equals=" + UPDATED_PERIOD);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByPeriodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where period in
        defaultInvoiceAssignNoFiltering("period.in=" + DEFAULT_PERIOD + "," + UPDATED_PERIOD, "period.in=" + UPDATED_PERIOD);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByPeriodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where period is not null
        defaultInvoiceAssignNoFiltering("period.specified=true", "period.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByPeriodContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where period contains
        defaultInvoiceAssignNoFiltering("period.contains=" + DEFAULT_PERIOD, "period.contains=" + UPDATED_PERIOD);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByPeriodNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where period does not contain
        defaultInvoiceAssignNoFiltering("period.doesNotContain=" + UPDATED_PERIOD, "period.doesNotContain=" + DEFAULT_PERIOD);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByFromNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where fromNo equals to
        defaultInvoiceAssignNoFiltering("fromNo.equals=" + DEFAULT_FROM_NO, "fromNo.equals=" + UPDATED_FROM_NO);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByFromNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where fromNo in
        defaultInvoiceAssignNoFiltering("fromNo.in=" + DEFAULT_FROM_NO + "," + UPDATED_FROM_NO, "fromNo.in=" + UPDATED_FROM_NO);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByFromNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where fromNo is not null
        defaultInvoiceAssignNoFiltering("fromNo.specified=true", "fromNo.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByFromNoContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where fromNo contains
        defaultInvoiceAssignNoFiltering("fromNo.contains=" + DEFAULT_FROM_NO, "fromNo.contains=" + UPDATED_FROM_NO);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByFromNoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where fromNo does not contain
        defaultInvoiceAssignNoFiltering("fromNo.doesNotContain=" + UPDATED_FROM_NO, "fromNo.doesNotContain=" + DEFAULT_FROM_NO);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByToNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where toNo equals to
        defaultInvoiceAssignNoFiltering("toNo.equals=" + DEFAULT_TO_NO, "toNo.equals=" + UPDATED_TO_NO);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByToNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where toNo in
        defaultInvoiceAssignNoFiltering("toNo.in=" + DEFAULT_TO_NO + "," + UPDATED_TO_NO, "toNo.in=" + UPDATED_TO_NO);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByToNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where toNo is not null
        defaultInvoiceAssignNoFiltering("toNo.specified=true", "toNo.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByToNoContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where toNo contains
        defaultInvoiceAssignNoFiltering("toNo.contains=" + DEFAULT_TO_NO, "toNo.contains=" + UPDATED_TO_NO);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByToNoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where toNo does not contain
        defaultInvoiceAssignNoFiltering("toNo.doesNotContain=" + UPDATED_TO_NO, "toNo.doesNotContain=" + DEFAULT_TO_NO);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByUsedCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where usedCount equals to
        defaultInvoiceAssignNoFiltering("usedCount.equals=" + DEFAULT_USED_COUNT, "usedCount.equals=" + UPDATED_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByUsedCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where usedCount in
        defaultInvoiceAssignNoFiltering(
            "usedCount.in=" + DEFAULT_USED_COUNT + "," + UPDATED_USED_COUNT,
            "usedCount.in=" + UPDATED_USED_COUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByUsedCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where usedCount is not null
        defaultInvoiceAssignNoFiltering("usedCount.specified=true", "usedCount.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByUsedCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where usedCount is greater than or equal to
        defaultInvoiceAssignNoFiltering(
            "usedCount.greaterThanOrEqual=" + DEFAULT_USED_COUNT,
            "usedCount.greaterThanOrEqual=" + UPDATED_USED_COUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByUsedCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where usedCount is less than or equal to
        defaultInvoiceAssignNoFiltering(
            "usedCount.lessThanOrEqual=" + DEFAULT_USED_COUNT,
            "usedCount.lessThanOrEqual=" + SMALLER_USED_COUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByUsedCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where usedCount is less than
        defaultInvoiceAssignNoFiltering("usedCount.lessThan=" + UPDATED_USED_COUNT, "usedCount.lessThan=" + DEFAULT_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByUsedCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where usedCount is greater than
        defaultInvoiceAssignNoFiltering("usedCount.greaterThan=" + SMALLER_USED_COUNT, "usedCount.greaterThan=" + DEFAULT_USED_COUNT);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByRollSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where rollSize equals to
        defaultInvoiceAssignNoFiltering("rollSize.equals=" + DEFAULT_ROLL_SIZE, "rollSize.equals=" + UPDATED_ROLL_SIZE);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByRollSizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where rollSize in
        defaultInvoiceAssignNoFiltering("rollSize.in=" + DEFAULT_ROLL_SIZE + "," + UPDATED_ROLL_SIZE, "rollSize.in=" + UPDATED_ROLL_SIZE);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByRollSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where rollSize is not null
        defaultInvoiceAssignNoFiltering("rollSize.specified=true", "rollSize.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByRollSizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where rollSize is greater than or equal to
        defaultInvoiceAssignNoFiltering(
            "rollSize.greaterThanOrEqual=" + DEFAULT_ROLL_SIZE,
            "rollSize.greaterThanOrEqual=" + UPDATED_ROLL_SIZE
        );
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByRollSizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where rollSize is less than or equal to
        defaultInvoiceAssignNoFiltering("rollSize.lessThanOrEqual=" + DEFAULT_ROLL_SIZE, "rollSize.lessThanOrEqual=" + SMALLER_ROLL_SIZE);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByRollSizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where rollSize is less than
        defaultInvoiceAssignNoFiltering("rollSize.lessThan=" + UPDATED_ROLL_SIZE, "rollSize.lessThan=" + DEFAULT_ROLL_SIZE);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByRollSizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where rollSize is greater than
        defaultInvoiceAssignNoFiltering("rollSize.greaterThan=" + SMALLER_ROLL_SIZE, "rollSize.greaterThan=" + DEFAULT_ROLL_SIZE);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where status equals to
        defaultInvoiceAssignNoFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where status in
        defaultInvoiceAssignNoFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where status is not null
        defaultInvoiceAssignNoFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where status contains
        defaultInvoiceAssignNoFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        // Get all the invoiceAssignNoList where status does not contain
        defaultInvoiceAssignNoFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllInvoiceAssignNosByTenantIsEqualToSomething() throws Exception {
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);
            tenant = TenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        em.persist(tenant);
        em.flush();
        invoiceAssignNo.setTenant(tenant);
        invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);
        Long tenantId = tenant.getId();
        // Get all the invoiceAssignNoList where tenant equals to tenantId
        defaultInvoiceAssignNoShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the invoiceAssignNoList where tenant equals to (tenantId + 1)
        defaultInvoiceAssignNoShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultInvoiceAssignNoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInvoiceAssignNoShouldBeFound(shouldBeFound);
        defaultInvoiceAssignNoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInvoiceAssignNoShouldBeFound(String filter) throws Exception {
        restInvoiceAssignNoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoiceAssignNo.getId().intValue())))
            .andExpect(jsonPath("$.[*].track").value(hasItem(DEFAULT_TRACK)))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].fromNo").value(hasItem(DEFAULT_FROM_NO)))
            .andExpect(jsonPath("$.[*].toNo").value(hasItem(DEFAULT_TO_NO)))
            .andExpect(jsonPath("$.[*].usedCount").value(hasItem(DEFAULT_USED_COUNT)))
            .andExpect(jsonPath("$.[*].rollSize").value(hasItem(DEFAULT_ROLL_SIZE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));

        // Check, that the count call also returns 1
        restInvoiceAssignNoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInvoiceAssignNoShouldNotBeFound(String filter) throws Exception {
        restInvoiceAssignNoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInvoiceAssignNoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInvoiceAssignNo() throws Exception {
        // Get the invoiceAssignNo
        restInvoiceAssignNoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInvoiceAssignNo() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceAssignNo
        InvoiceAssignNo updatedInvoiceAssignNo = invoiceAssignNoRepository.findById(invoiceAssignNo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInvoiceAssignNo are not directly saved in db
        em.detach(updatedInvoiceAssignNo);
        updatedInvoiceAssignNo
            .track(UPDATED_TRACK)
            .period(UPDATED_PERIOD)
            .fromNo(UPDATED_FROM_NO)
            .toNo(UPDATED_TO_NO)
            .usedCount(UPDATED_USED_COUNT)
            .rollSize(UPDATED_ROLL_SIZE)
            .status(UPDATED_STATUS);
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(updatedInvoiceAssignNo);

        restInvoiceAssignNoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceAssignNoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceAssignNoDTO))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceAssignNo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInvoiceAssignNoToMatchAllProperties(updatedInvoiceAssignNo);
    }

    @Test
    @Transactional
    void putNonExistingInvoiceAssignNo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceAssignNo.setId(longCount.incrementAndGet());

        // Create the InvoiceAssignNo
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceAssignNoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceAssignNoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceAssignNoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceAssignNo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvoiceAssignNo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceAssignNo.setId(longCount.incrementAndGet());

        // Create the InvoiceAssignNo
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceAssignNoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceAssignNoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceAssignNo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvoiceAssignNo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceAssignNo.setId(longCount.incrementAndGet());

        // Create the InvoiceAssignNo
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceAssignNoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceAssignNoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InvoiceAssignNo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInvoiceAssignNoWithPatch() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceAssignNo using partial update
        InvoiceAssignNo partialUpdatedInvoiceAssignNo = new InvoiceAssignNo();
        partialUpdatedInvoiceAssignNo.setId(invoiceAssignNo.getId());

        partialUpdatedInvoiceAssignNo.track(UPDATED_TRACK).period(UPDATED_PERIOD).rollSize(UPDATED_ROLL_SIZE).status(UPDATED_STATUS);

        restInvoiceAssignNoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoiceAssignNo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoiceAssignNo))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceAssignNo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceAssignNoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInvoiceAssignNo, invoiceAssignNo),
            getPersistedInvoiceAssignNo(invoiceAssignNo)
        );
    }

    @Test
    @Transactional
    void fullUpdateInvoiceAssignNoWithPatch() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceAssignNo using partial update
        InvoiceAssignNo partialUpdatedInvoiceAssignNo = new InvoiceAssignNo();
        partialUpdatedInvoiceAssignNo.setId(invoiceAssignNo.getId());

        partialUpdatedInvoiceAssignNo
            .track(UPDATED_TRACK)
            .period(UPDATED_PERIOD)
            .fromNo(UPDATED_FROM_NO)
            .toNo(UPDATED_TO_NO)
            .usedCount(UPDATED_USED_COUNT)
            .rollSize(UPDATED_ROLL_SIZE)
            .status(UPDATED_STATUS);

        restInvoiceAssignNoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoiceAssignNo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoiceAssignNo))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceAssignNo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceAssignNoUpdatableFieldsEquals(
            partialUpdatedInvoiceAssignNo,
            getPersistedInvoiceAssignNo(partialUpdatedInvoiceAssignNo)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInvoiceAssignNo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceAssignNo.setId(longCount.incrementAndGet());

        // Create the InvoiceAssignNo
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceAssignNoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, invoiceAssignNoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceAssignNoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceAssignNo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvoiceAssignNo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceAssignNo.setId(longCount.incrementAndGet());

        // Create the InvoiceAssignNo
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceAssignNoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceAssignNoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceAssignNo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvoiceAssignNo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceAssignNo.setId(longCount.incrementAndGet());

        // Create the InvoiceAssignNo
        InvoiceAssignNoDTO invoiceAssignNoDTO = invoiceAssignNoMapper.toDto(invoiceAssignNo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceAssignNoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(invoiceAssignNoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InvoiceAssignNo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInvoiceAssignNo() throws Exception {
        // Initialize the database
        insertedInvoiceAssignNo = invoiceAssignNoRepository.saveAndFlush(invoiceAssignNo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the invoiceAssignNo
        restInvoiceAssignNoMockMvc
            .perform(delete(ENTITY_API_URL_ID, invoiceAssignNo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return invoiceAssignNoRepository.count();
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

    protected InvoiceAssignNo getPersistedInvoiceAssignNo(InvoiceAssignNo invoiceAssignNo) {
        return invoiceAssignNoRepository.findById(invoiceAssignNo.getId()).orElseThrow();
    }

    protected void assertPersistedInvoiceAssignNoToMatchAllProperties(InvoiceAssignNo expectedInvoiceAssignNo) {
        assertInvoiceAssignNoAllPropertiesEquals(expectedInvoiceAssignNo, getPersistedInvoiceAssignNo(expectedInvoiceAssignNo));
    }

    protected void assertPersistedInvoiceAssignNoToMatchUpdatableProperties(InvoiceAssignNo expectedInvoiceAssignNo) {
        assertInvoiceAssignNoAllUpdatablePropertiesEquals(expectedInvoiceAssignNo, getPersistedInvoiceAssignNo(expectedInvoiceAssignNo));
    }
}
