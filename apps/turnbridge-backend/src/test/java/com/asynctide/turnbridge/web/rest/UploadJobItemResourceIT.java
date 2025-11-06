package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.UploadJobItemAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static com.asynctide.turnbridge.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.UploadJobItem;
import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.domain.enumeration.TaxType;
import com.asynctide.turnbridge.repository.UploadJobItemRepository;
import com.asynctide.turnbridge.service.UploadJobItemService;
import com.asynctide.turnbridge.service.dto.UploadJobItemDTO;
import com.asynctide.turnbridge.service.mapper.UploadJobItemMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link UploadJobItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UploadJobItemResourceIT {

    private static final Integer DEFAULT_LINE_NO = 1;
    private static final Integer UPDATED_LINE_NO = 2;
    private static final Integer SMALLER_LINE_NO = 1 - 1;

    private static final String DEFAULT_TRACE_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRACE_ID = "BBBBBBBBBB";

    private static final JobItemStatus DEFAULT_STATUS = JobItemStatus.QUEUED;
    private static final JobItemStatus UPDATED_STATUS = JobItemStatus.OK;

    private static final String DEFAULT_RESULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_RESULT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_RESULT_MSG = "AAAAAAAAAA";
    private static final String UPDATED_RESULT_MSG = "BBBBBBBBBB";

    private static final String DEFAULT_BUYER_ID = "AAAAAAAAAA";
    private static final String UPDATED_BUYER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_BUYER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BUYER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY = "AAA";
    private static final String UPDATED_CURRENCY = "BBB";

    private static final BigDecimal DEFAULT_AMOUNT_EXCL = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_EXCL = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT_EXCL = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TAX_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAX_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TAX_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_AMOUNT_INCL = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_INCL = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT_INCL = new BigDecimal(1 - 1);

    private static final TaxType DEFAULT_TAX_TYPE = TaxType.TAXABLE;
    private static final TaxType UPDATED_TAX_TYPE = TaxType.ZERO;

    private static final LocalDate DEFAULT_INVOICE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_INVOICE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_INVOICE_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_INVOICE_NO = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_NO = "BBBBBBBBBB";

    private static final String DEFAULT_ASSIGNED_PREFIX = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNED_PREFIX = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_RAW_PAYLOAD = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_HASH = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_RAW_HASH = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final String DEFAULT_PROFILE_DETECTED = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE_DETECTED = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/upload-job-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UploadJobItemRepository uploadJobItemRepository;

    @Mock
    private UploadJobItemRepository uploadJobItemRepositoryMock;

    @Autowired
    private UploadJobItemMapper uploadJobItemMapper;

    @Mock
    private UploadJobItemService uploadJobItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUploadJobItemMockMvc;

    private UploadJobItem uploadJobItem;

    private UploadJobItem insertedUploadJobItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UploadJobItem createEntity(EntityManager em) {
        UploadJobItem uploadJobItem = new UploadJobItem()
            .lineNo(DEFAULT_LINE_NO)
            .traceId(DEFAULT_TRACE_ID)
            .status(DEFAULT_STATUS)
            .resultCode(DEFAULT_RESULT_CODE)
            .resultMsg(DEFAULT_RESULT_MSG)
            .buyerId(DEFAULT_BUYER_ID)
            .buyerName(DEFAULT_BUYER_NAME)
            .currency(DEFAULT_CURRENCY)
            .amountExcl(DEFAULT_AMOUNT_EXCL)
            .taxAmount(DEFAULT_TAX_AMOUNT)
            .amountIncl(DEFAULT_AMOUNT_INCL)
            .taxType(DEFAULT_TAX_TYPE)
            .invoiceDate(DEFAULT_INVOICE_DATE)
            .invoiceNo(DEFAULT_INVOICE_NO)
            .assignedPrefix(DEFAULT_ASSIGNED_PREFIX)
            .rawPayload(DEFAULT_RAW_PAYLOAD)
            .rawHash(DEFAULT_RAW_HASH)
            .profileDetected(DEFAULT_PROFILE_DETECTED);
        // Add required entity
        UploadJob uploadJob;
        if (TestUtil.findAll(em, UploadJob.class).isEmpty()) {
            uploadJob = UploadJobResourceIT.createEntity(em);
            em.persist(uploadJob);
            em.flush();
        } else {
            uploadJob = TestUtil.findAll(em, UploadJob.class).get(0);
        }
        uploadJobItem.setJob(uploadJob);
        return uploadJobItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UploadJobItem createUpdatedEntity(EntityManager em) {
        UploadJobItem updatedUploadJobItem = new UploadJobItem()
            .lineNo(UPDATED_LINE_NO)
            .traceId(UPDATED_TRACE_ID)
            .status(UPDATED_STATUS)
            .resultCode(UPDATED_RESULT_CODE)
            .resultMsg(UPDATED_RESULT_MSG)
            .buyerId(UPDATED_BUYER_ID)
            .buyerName(UPDATED_BUYER_NAME)
            .currency(UPDATED_CURRENCY)
            .amountExcl(UPDATED_AMOUNT_EXCL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .amountIncl(UPDATED_AMOUNT_INCL)
            .taxType(UPDATED_TAX_TYPE)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .invoiceNo(UPDATED_INVOICE_NO)
            .assignedPrefix(UPDATED_ASSIGNED_PREFIX)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .rawHash(UPDATED_RAW_HASH)
            .profileDetected(UPDATED_PROFILE_DETECTED);
        // Add required entity
        UploadJob uploadJob;
        if (TestUtil.findAll(em, UploadJob.class).isEmpty()) {
            uploadJob = UploadJobResourceIT.createUpdatedEntity(em);
            em.persist(uploadJob);
            em.flush();
        } else {
            uploadJob = TestUtil.findAll(em, UploadJob.class).get(0);
        }
        updatedUploadJobItem.setJob(uploadJob);
        return updatedUploadJobItem;
    }

    @BeforeEach
    void initTest() {
        uploadJobItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedUploadJobItem != null) {
            uploadJobItemRepository.delete(insertedUploadJobItem);
            insertedUploadJobItem = null;
        }
    }

    @Test
    @Transactional
    void createUploadJobItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UploadJobItem
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);
        var returnedUploadJobItemDTO = om.readValue(
            restUploadJobItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UploadJobItemDTO.class
        );

        // Validate the UploadJobItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUploadJobItem = uploadJobItemMapper.toEntity(returnedUploadJobItemDTO);
        assertUploadJobItemUpdatableFieldsEquals(returnedUploadJobItem, getPersistedUploadJobItem(returnedUploadJobItem));

        insertedUploadJobItem = returnedUploadJobItem;
    }

    @Test
    @Transactional
    void createUploadJobItemWithExistingId() throws Exception {
        // Create the UploadJobItem with an existing ID
        uploadJobItem.setId(1L);
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUploadJobItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UploadJobItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJobItem.setLineNo(null);

        // Create the UploadJobItem, which fails.
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        restUploadJobItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTraceIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJobItem.setTraceId(null);

        // Create the UploadJobItem, which fails.
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        restUploadJobItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uploadJobItem.setStatus(null);

        // Create the UploadJobItem, which fails.
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        restUploadJobItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUploadJobItems() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList
        restUploadJobItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uploadJobItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineNo").value(hasItem(DEFAULT_LINE_NO)))
            .andExpect(jsonPath("$.[*].traceId").value(hasItem(DEFAULT_TRACE_ID)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].resultCode").value(hasItem(DEFAULT_RESULT_CODE)))
            .andExpect(jsonPath("$.[*].resultMsg").value(hasItem(DEFAULT_RESULT_MSG)))
            .andExpect(jsonPath("$.[*].buyerId").value(hasItem(DEFAULT_BUYER_ID)))
            .andExpect(jsonPath("$.[*].buyerName").value(hasItem(DEFAULT_BUYER_NAME)))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].amountExcl").value(hasItem(sameNumber(DEFAULT_AMOUNT_EXCL))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].amountIncl").value(hasItem(sameNumber(DEFAULT_AMOUNT_INCL))))
            .andExpect(jsonPath("$.[*].taxType").value(hasItem(DEFAULT_TAX_TYPE.toString())))
            .andExpect(jsonPath("$.[*].invoiceDate").value(hasItem(DEFAULT_INVOICE_DATE.toString())))
            .andExpect(jsonPath("$.[*].invoiceNo").value(hasItem(DEFAULT_INVOICE_NO)))
            .andExpect(jsonPath("$.[*].assignedPrefix").value(hasItem(DEFAULT_ASSIGNED_PREFIX)))
            .andExpect(jsonPath("$.[*].rawPayload").value(hasItem(DEFAULT_RAW_PAYLOAD)))
            .andExpect(jsonPath("$.[*].rawHash").value(hasItem(DEFAULT_RAW_HASH)))
            .andExpect(jsonPath("$.[*].profileDetected").value(hasItem(DEFAULT_PROFILE_DETECTED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUploadJobItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(uploadJobItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUploadJobItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(uploadJobItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUploadJobItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(uploadJobItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUploadJobItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(uploadJobItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUploadJobItem() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get the uploadJobItem
        restUploadJobItemMockMvc
            .perform(get(ENTITY_API_URL_ID, uploadJobItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(uploadJobItem.getId().intValue()))
            .andExpect(jsonPath("$.lineNo").value(DEFAULT_LINE_NO))
            .andExpect(jsonPath("$.traceId").value(DEFAULT_TRACE_ID))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.resultCode").value(DEFAULT_RESULT_CODE))
            .andExpect(jsonPath("$.resultMsg").value(DEFAULT_RESULT_MSG))
            .andExpect(jsonPath("$.buyerId").value(DEFAULT_BUYER_ID))
            .andExpect(jsonPath("$.buyerName").value(DEFAULT_BUYER_NAME))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.amountExcl").value(sameNumber(DEFAULT_AMOUNT_EXCL)))
            .andExpect(jsonPath("$.taxAmount").value(sameNumber(DEFAULT_TAX_AMOUNT)))
            .andExpect(jsonPath("$.amountIncl").value(sameNumber(DEFAULT_AMOUNT_INCL)))
            .andExpect(jsonPath("$.taxType").value(DEFAULT_TAX_TYPE.toString()))
            .andExpect(jsonPath("$.invoiceDate").value(DEFAULT_INVOICE_DATE.toString()))
            .andExpect(jsonPath("$.invoiceNo").value(DEFAULT_INVOICE_NO))
            .andExpect(jsonPath("$.assignedPrefix").value(DEFAULT_ASSIGNED_PREFIX))
            .andExpect(jsonPath("$.rawPayload").value(DEFAULT_RAW_PAYLOAD))
            .andExpect(jsonPath("$.rawHash").value(DEFAULT_RAW_HASH))
            .andExpect(jsonPath("$.profileDetected").value(DEFAULT_PROFILE_DETECTED));
    }

    @Test
    @Transactional
    void getUploadJobItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        Long id = uploadJobItem.getId();

        defaultUploadJobItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultUploadJobItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultUploadJobItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByLineNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where lineNo equals to
        defaultUploadJobItemFiltering("lineNo.equals=" + DEFAULT_LINE_NO, "lineNo.equals=" + UPDATED_LINE_NO);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByLineNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where lineNo in
        defaultUploadJobItemFiltering("lineNo.in=" + DEFAULT_LINE_NO + "," + UPDATED_LINE_NO, "lineNo.in=" + UPDATED_LINE_NO);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByLineNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where lineNo is not null
        defaultUploadJobItemFiltering("lineNo.specified=true", "lineNo.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByLineNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where lineNo is greater than or equal to
        defaultUploadJobItemFiltering("lineNo.greaterThanOrEqual=" + DEFAULT_LINE_NO, "lineNo.greaterThanOrEqual=" + UPDATED_LINE_NO);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByLineNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where lineNo is less than or equal to
        defaultUploadJobItemFiltering("lineNo.lessThanOrEqual=" + DEFAULT_LINE_NO, "lineNo.lessThanOrEqual=" + SMALLER_LINE_NO);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByLineNoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where lineNo is less than
        defaultUploadJobItemFiltering("lineNo.lessThan=" + UPDATED_LINE_NO, "lineNo.lessThan=" + DEFAULT_LINE_NO);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByLineNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where lineNo is greater than
        defaultUploadJobItemFiltering("lineNo.greaterThan=" + SMALLER_LINE_NO, "lineNo.greaterThan=" + DEFAULT_LINE_NO);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTraceIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where traceId equals to
        defaultUploadJobItemFiltering("traceId.equals=" + DEFAULT_TRACE_ID, "traceId.equals=" + UPDATED_TRACE_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTraceIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where traceId in
        defaultUploadJobItemFiltering("traceId.in=" + DEFAULT_TRACE_ID + "," + UPDATED_TRACE_ID, "traceId.in=" + UPDATED_TRACE_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTraceIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where traceId is not null
        defaultUploadJobItemFiltering("traceId.specified=true", "traceId.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTraceIdContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where traceId contains
        defaultUploadJobItemFiltering("traceId.contains=" + DEFAULT_TRACE_ID, "traceId.contains=" + UPDATED_TRACE_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTraceIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where traceId does not contain
        defaultUploadJobItemFiltering("traceId.doesNotContain=" + UPDATED_TRACE_ID, "traceId.doesNotContain=" + DEFAULT_TRACE_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where status equals to
        defaultUploadJobItemFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where status in
        defaultUploadJobItemFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where status is not null
        defaultUploadJobItemFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultCode equals to
        defaultUploadJobItemFiltering("resultCode.equals=" + DEFAULT_RESULT_CODE, "resultCode.equals=" + UPDATED_RESULT_CODE);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultCode in
        defaultUploadJobItemFiltering(
            "resultCode.in=" + DEFAULT_RESULT_CODE + "," + UPDATED_RESULT_CODE,
            "resultCode.in=" + UPDATED_RESULT_CODE
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultCode is not null
        defaultUploadJobItemFiltering("resultCode.specified=true", "resultCode.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultCode contains
        defaultUploadJobItemFiltering("resultCode.contains=" + DEFAULT_RESULT_CODE, "resultCode.contains=" + UPDATED_RESULT_CODE);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultCode does not contain
        defaultUploadJobItemFiltering(
            "resultCode.doesNotContain=" + UPDATED_RESULT_CODE,
            "resultCode.doesNotContain=" + DEFAULT_RESULT_CODE
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultMsgIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultMsg equals to
        defaultUploadJobItemFiltering("resultMsg.equals=" + DEFAULT_RESULT_MSG, "resultMsg.equals=" + UPDATED_RESULT_MSG);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultMsgIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultMsg in
        defaultUploadJobItemFiltering(
            "resultMsg.in=" + DEFAULT_RESULT_MSG + "," + UPDATED_RESULT_MSG,
            "resultMsg.in=" + UPDATED_RESULT_MSG
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultMsgIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultMsg is not null
        defaultUploadJobItemFiltering("resultMsg.specified=true", "resultMsg.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultMsgContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultMsg contains
        defaultUploadJobItemFiltering("resultMsg.contains=" + DEFAULT_RESULT_MSG, "resultMsg.contains=" + UPDATED_RESULT_MSG);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByResultMsgNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where resultMsg does not contain
        defaultUploadJobItemFiltering("resultMsg.doesNotContain=" + UPDATED_RESULT_MSG, "resultMsg.doesNotContain=" + DEFAULT_RESULT_MSG);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerId equals to
        defaultUploadJobItemFiltering("buyerId.equals=" + DEFAULT_BUYER_ID, "buyerId.equals=" + UPDATED_BUYER_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerId in
        defaultUploadJobItemFiltering("buyerId.in=" + DEFAULT_BUYER_ID + "," + UPDATED_BUYER_ID, "buyerId.in=" + UPDATED_BUYER_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerId is not null
        defaultUploadJobItemFiltering("buyerId.specified=true", "buyerId.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerIdContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerId contains
        defaultUploadJobItemFiltering("buyerId.contains=" + DEFAULT_BUYER_ID, "buyerId.contains=" + UPDATED_BUYER_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerId does not contain
        defaultUploadJobItemFiltering("buyerId.doesNotContain=" + UPDATED_BUYER_ID, "buyerId.doesNotContain=" + DEFAULT_BUYER_ID);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerName equals to
        defaultUploadJobItemFiltering("buyerName.equals=" + DEFAULT_BUYER_NAME, "buyerName.equals=" + UPDATED_BUYER_NAME);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerName in
        defaultUploadJobItemFiltering(
            "buyerName.in=" + DEFAULT_BUYER_NAME + "," + UPDATED_BUYER_NAME,
            "buyerName.in=" + UPDATED_BUYER_NAME
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerName is not null
        defaultUploadJobItemFiltering("buyerName.specified=true", "buyerName.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerNameContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerName contains
        defaultUploadJobItemFiltering("buyerName.contains=" + DEFAULT_BUYER_NAME, "buyerName.contains=" + UPDATED_BUYER_NAME);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByBuyerNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where buyerName does not contain
        defaultUploadJobItemFiltering("buyerName.doesNotContain=" + UPDATED_BUYER_NAME, "buyerName.doesNotContain=" + DEFAULT_BUYER_NAME);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where currency equals to
        defaultUploadJobItemFiltering("currency.equals=" + DEFAULT_CURRENCY, "currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where currency in
        defaultUploadJobItemFiltering("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY, "currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where currency is not null
        defaultUploadJobItemFiltering("currency.specified=true", "currency.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByCurrencyContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where currency contains
        defaultUploadJobItemFiltering("currency.contains=" + DEFAULT_CURRENCY, "currency.contains=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where currency does not contain
        defaultUploadJobItemFiltering("currency.doesNotContain=" + UPDATED_CURRENCY, "currency.doesNotContain=" + DEFAULT_CURRENCY);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountExclIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountExcl equals to
        defaultUploadJobItemFiltering("amountExcl.equals=" + DEFAULT_AMOUNT_EXCL, "amountExcl.equals=" + UPDATED_AMOUNT_EXCL);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountExclIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountExcl in
        defaultUploadJobItemFiltering(
            "amountExcl.in=" + DEFAULT_AMOUNT_EXCL + "," + UPDATED_AMOUNT_EXCL,
            "amountExcl.in=" + UPDATED_AMOUNT_EXCL
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountExclIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountExcl is not null
        defaultUploadJobItemFiltering("amountExcl.specified=true", "amountExcl.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountExclIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountExcl is greater than or equal to
        defaultUploadJobItemFiltering(
            "amountExcl.greaterThanOrEqual=" + DEFAULT_AMOUNT_EXCL,
            "amountExcl.greaterThanOrEqual=" + UPDATED_AMOUNT_EXCL
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountExclIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountExcl is less than or equal to
        defaultUploadJobItemFiltering(
            "amountExcl.lessThanOrEqual=" + DEFAULT_AMOUNT_EXCL,
            "amountExcl.lessThanOrEqual=" + SMALLER_AMOUNT_EXCL
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountExclIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountExcl is less than
        defaultUploadJobItemFiltering("amountExcl.lessThan=" + UPDATED_AMOUNT_EXCL, "amountExcl.lessThan=" + DEFAULT_AMOUNT_EXCL);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountExclIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountExcl is greater than
        defaultUploadJobItemFiltering("amountExcl.greaterThan=" + SMALLER_AMOUNT_EXCL, "amountExcl.greaterThan=" + DEFAULT_AMOUNT_EXCL);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxAmount equals to
        defaultUploadJobItemFiltering("taxAmount.equals=" + DEFAULT_TAX_AMOUNT, "taxAmount.equals=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxAmount in
        defaultUploadJobItemFiltering(
            "taxAmount.in=" + DEFAULT_TAX_AMOUNT + "," + UPDATED_TAX_AMOUNT,
            "taxAmount.in=" + UPDATED_TAX_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxAmount is not null
        defaultUploadJobItemFiltering("taxAmount.specified=true", "taxAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxAmount is greater than or equal to
        defaultUploadJobItemFiltering(
            "taxAmount.greaterThanOrEqual=" + DEFAULT_TAX_AMOUNT,
            "taxAmount.greaterThanOrEqual=" + UPDATED_TAX_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxAmount is less than or equal to
        defaultUploadJobItemFiltering("taxAmount.lessThanOrEqual=" + DEFAULT_TAX_AMOUNT, "taxAmount.lessThanOrEqual=" + SMALLER_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxAmount is less than
        defaultUploadJobItemFiltering("taxAmount.lessThan=" + UPDATED_TAX_AMOUNT, "taxAmount.lessThan=" + DEFAULT_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxAmount is greater than
        defaultUploadJobItemFiltering("taxAmount.greaterThan=" + SMALLER_TAX_AMOUNT, "taxAmount.greaterThan=" + DEFAULT_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountInclIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountIncl equals to
        defaultUploadJobItemFiltering("amountIncl.equals=" + DEFAULT_AMOUNT_INCL, "amountIncl.equals=" + UPDATED_AMOUNT_INCL);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountInclIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountIncl in
        defaultUploadJobItemFiltering(
            "amountIncl.in=" + DEFAULT_AMOUNT_INCL + "," + UPDATED_AMOUNT_INCL,
            "amountIncl.in=" + UPDATED_AMOUNT_INCL
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountInclIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountIncl is not null
        defaultUploadJobItemFiltering("amountIncl.specified=true", "amountIncl.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountInclIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountIncl is greater than or equal to
        defaultUploadJobItemFiltering(
            "amountIncl.greaterThanOrEqual=" + DEFAULT_AMOUNT_INCL,
            "amountIncl.greaterThanOrEqual=" + UPDATED_AMOUNT_INCL
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountInclIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountIncl is less than or equal to
        defaultUploadJobItemFiltering(
            "amountIncl.lessThanOrEqual=" + DEFAULT_AMOUNT_INCL,
            "amountIncl.lessThanOrEqual=" + SMALLER_AMOUNT_INCL
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountInclIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountIncl is less than
        defaultUploadJobItemFiltering("amountIncl.lessThan=" + UPDATED_AMOUNT_INCL, "amountIncl.lessThan=" + DEFAULT_AMOUNT_INCL);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAmountInclIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where amountIncl is greater than
        defaultUploadJobItemFiltering("amountIncl.greaterThan=" + SMALLER_AMOUNT_INCL, "amountIncl.greaterThan=" + DEFAULT_AMOUNT_INCL);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxType equals to
        defaultUploadJobItemFiltering("taxType.equals=" + DEFAULT_TAX_TYPE, "taxType.equals=" + UPDATED_TAX_TYPE);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxType in
        defaultUploadJobItemFiltering("taxType.in=" + DEFAULT_TAX_TYPE + "," + UPDATED_TAX_TYPE, "taxType.in=" + UPDATED_TAX_TYPE);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByTaxTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where taxType is not null
        defaultUploadJobItemFiltering("taxType.specified=true", "taxType.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceDate equals to
        defaultUploadJobItemFiltering("invoiceDate.equals=" + DEFAULT_INVOICE_DATE, "invoiceDate.equals=" + UPDATED_INVOICE_DATE);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceDate in
        defaultUploadJobItemFiltering(
            "invoiceDate.in=" + DEFAULT_INVOICE_DATE + "," + UPDATED_INVOICE_DATE,
            "invoiceDate.in=" + UPDATED_INVOICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceDate is not null
        defaultUploadJobItemFiltering("invoiceDate.specified=true", "invoiceDate.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceDate is greater than or equal to
        defaultUploadJobItemFiltering(
            "invoiceDate.greaterThanOrEqual=" + DEFAULT_INVOICE_DATE,
            "invoiceDate.greaterThanOrEqual=" + UPDATED_INVOICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceDate is less than or equal to
        defaultUploadJobItemFiltering(
            "invoiceDate.lessThanOrEqual=" + DEFAULT_INVOICE_DATE,
            "invoiceDate.lessThanOrEqual=" + SMALLER_INVOICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceDate is less than
        defaultUploadJobItemFiltering("invoiceDate.lessThan=" + UPDATED_INVOICE_DATE, "invoiceDate.lessThan=" + DEFAULT_INVOICE_DATE);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceDate is greater than
        defaultUploadJobItemFiltering("invoiceDate.greaterThan=" + SMALLER_INVOICE_DATE, "invoiceDate.greaterThan=" + DEFAULT_INVOICE_DATE);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceNo equals to
        defaultUploadJobItemFiltering("invoiceNo.equals=" + DEFAULT_INVOICE_NO, "invoiceNo.equals=" + UPDATED_INVOICE_NO);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceNo in
        defaultUploadJobItemFiltering(
            "invoiceNo.in=" + DEFAULT_INVOICE_NO + "," + UPDATED_INVOICE_NO,
            "invoiceNo.in=" + UPDATED_INVOICE_NO
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceNo is not null
        defaultUploadJobItemFiltering("invoiceNo.specified=true", "invoiceNo.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceNoContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceNo contains
        defaultUploadJobItemFiltering("invoiceNo.contains=" + DEFAULT_INVOICE_NO, "invoiceNo.contains=" + UPDATED_INVOICE_NO);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByInvoiceNoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where invoiceNo does not contain
        defaultUploadJobItemFiltering("invoiceNo.doesNotContain=" + UPDATED_INVOICE_NO, "invoiceNo.doesNotContain=" + DEFAULT_INVOICE_NO);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAssignedPrefixIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where assignedPrefix equals to
        defaultUploadJobItemFiltering(
            "assignedPrefix.equals=" + DEFAULT_ASSIGNED_PREFIX,
            "assignedPrefix.equals=" + UPDATED_ASSIGNED_PREFIX
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAssignedPrefixIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where assignedPrefix in
        defaultUploadJobItemFiltering(
            "assignedPrefix.in=" + DEFAULT_ASSIGNED_PREFIX + "," + UPDATED_ASSIGNED_PREFIX,
            "assignedPrefix.in=" + UPDATED_ASSIGNED_PREFIX
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAssignedPrefixIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where assignedPrefix is not null
        defaultUploadJobItemFiltering("assignedPrefix.specified=true", "assignedPrefix.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAssignedPrefixContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where assignedPrefix contains
        defaultUploadJobItemFiltering(
            "assignedPrefix.contains=" + DEFAULT_ASSIGNED_PREFIX,
            "assignedPrefix.contains=" + UPDATED_ASSIGNED_PREFIX
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByAssignedPrefixNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where assignedPrefix does not contain
        defaultUploadJobItemFiltering(
            "assignedPrefix.doesNotContain=" + UPDATED_ASSIGNED_PREFIX,
            "assignedPrefix.doesNotContain=" + DEFAULT_ASSIGNED_PREFIX
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByRawHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where rawHash equals to
        defaultUploadJobItemFiltering("rawHash.equals=" + DEFAULT_RAW_HASH, "rawHash.equals=" + UPDATED_RAW_HASH);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByRawHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where rawHash in
        defaultUploadJobItemFiltering("rawHash.in=" + DEFAULT_RAW_HASH + "," + UPDATED_RAW_HASH, "rawHash.in=" + UPDATED_RAW_HASH);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByRawHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where rawHash is not null
        defaultUploadJobItemFiltering("rawHash.specified=true", "rawHash.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByRawHashContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where rawHash contains
        defaultUploadJobItemFiltering("rawHash.contains=" + DEFAULT_RAW_HASH, "rawHash.contains=" + UPDATED_RAW_HASH);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByRawHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where rawHash does not contain
        defaultUploadJobItemFiltering("rawHash.doesNotContain=" + UPDATED_RAW_HASH, "rawHash.doesNotContain=" + DEFAULT_RAW_HASH);
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByProfileDetectedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where profileDetected equals to
        defaultUploadJobItemFiltering(
            "profileDetected.equals=" + DEFAULT_PROFILE_DETECTED,
            "profileDetected.equals=" + UPDATED_PROFILE_DETECTED
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByProfileDetectedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where profileDetected in
        defaultUploadJobItemFiltering(
            "profileDetected.in=" + DEFAULT_PROFILE_DETECTED + "," + UPDATED_PROFILE_DETECTED,
            "profileDetected.in=" + UPDATED_PROFILE_DETECTED
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByProfileDetectedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where profileDetected is not null
        defaultUploadJobItemFiltering("profileDetected.specified=true", "profileDetected.specified=false");
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByProfileDetectedContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where profileDetected contains
        defaultUploadJobItemFiltering(
            "profileDetected.contains=" + DEFAULT_PROFILE_DETECTED,
            "profileDetected.contains=" + UPDATED_PROFILE_DETECTED
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByProfileDetectedNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        // Get all the uploadJobItemList where profileDetected does not contain
        defaultUploadJobItemFiltering(
            "profileDetected.doesNotContain=" + UPDATED_PROFILE_DETECTED,
            "profileDetected.doesNotContain=" + DEFAULT_PROFILE_DETECTED
        );
    }

    @Test
    @Transactional
    void getAllUploadJobItemsByJobIsEqualToSomething() throws Exception {
        UploadJob job;
        if (TestUtil.findAll(em, UploadJob.class).isEmpty()) {
            uploadJobItemRepository.saveAndFlush(uploadJobItem);
            job = UploadJobResourceIT.createEntity(em);
        } else {
            job = TestUtil.findAll(em, UploadJob.class).get(0);
        }
        em.persist(job);
        em.flush();
        uploadJobItem.setJob(job);
        uploadJobItemRepository.saveAndFlush(uploadJobItem);
        Long jobId = job.getId();
        // Get all the uploadJobItemList where job equals to jobId
        defaultUploadJobItemShouldBeFound("jobId.equals=" + jobId);

        // Get all the uploadJobItemList where job equals to (jobId + 1)
        defaultUploadJobItemShouldNotBeFound("jobId.equals=" + (jobId + 1));
    }

    private void defaultUploadJobItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultUploadJobItemShouldBeFound(shouldBeFound);
        defaultUploadJobItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUploadJobItemShouldBeFound(String filter) throws Exception {
        restUploadJobItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uploadJobItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineNo").value(hasItem(DEFAULT_LINE_NO)))
            .andExpect(jsonPath("$.[*].traceId").value(hasItem(DEFAULT_TRACE_ID)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].resultCode").value(hasItem(DEFAULT_RESULT_CODE)))
            .andExpect(jsonPath("$.[*].resultMsg").value(hasItem(DEFAULT_RESULT_MSG)))
            .andExpect(jsonPath("$.[*].buyerId").value(hasItem(DEFAULT_BUYER_ID)))
            .andExpect(jsonPath("$.[*].buyerName").value(hasItem(DEFAULT_BUYER_NAME)))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].amountExcl").value(hasItem(sameNumber(DEFAULT_AMOUNT_EXCL))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].amountIncl").value(hasItem(sameNumber(DEFAULT_AMOUNT_INCL))))
            .andExpect(jsonPath("$.[*].taxType").value(hasItem(DEFAULT_TAX_TYPE.toString())))
            .andExpect(jsonPath("$.[*].invoiceDate").value(hasItem(DEFAULT_INVOICE_DATE.toString())))
            .andExpect(jsonPath("$.[*].invoiceNo").value(hasItem(DEFAULT_INVOICE_NO)))
            .andExpect(jsonPath("$.[*].assignedPrefix").value(hasItem(DEFAULT_ASSIGNED_PREFIX)))
            .andExpect(jsonPath("$.[*].rawPayload").value(hasItem(DEFAULT_RAW_PAYLOAD)))
            .andExpect(jsonPath("$.[*].rawHash").value(hasItem(DEFAULT_RAW_HASH)))
            .andExpect(jsonPath("$.[*].profileDetected").value(hasItem(DEFAULT_PROFILE_DETECTED)));

        // Check, that the count call also returns 1
        restUploadJobItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUploadJobItemShouldNotBeFound(String filter) throws Exception {
        restUploadJobItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUploadJobItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUploadJobItem() throws Exception {
        // Get the uploadJobItem
        restUploadJobItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUploadJobItem() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the uploadJobItem
        UploadJobItem updatedUploadJobItem = uploadJobItemRepository.findById(uploadJobItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUploadJobItem are not directly saved in db
        em.detach(updatedUploadJobItem);
        updatedUploadJobItem
            .lineNo(UPDATED_LINE_NO)
            .traceId(UPDATED_TRACE_ID)
            .status(UPDATED_STATUS)
            .resultCode(UPDATED_RESULT_CODE)
            .resultMsg(UPDATED_RESULT_MSG)
            .buyerId(UPDATED_BUYER_ID)
            .buyerName(UPDATED_BUYER_NAME)
            .currency(UPDATED_CURRENCY)
            .amountExcl(UPDATED_AMOUNT_EXCL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .amountIncl(UPDATED_AMOUNT_INCL)
            .taxType(UPDATED_TAX_TYPE)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .invoiceNo(UPDATED_INVOICE_NO)
            .assignedPrefix(UPDATED_ASSIGNED_PREFIX)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .rawHash(UPDATED_RAW_HASH)
            .profileDetected(UPDATED_PROFILE_DETECTED);
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(updatedUploadJobItem);

        restUploadJobItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, uploadJobItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(uploadJobItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the UploadJobItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUploadJobItemToMatchAllProperties(updatedUploadJobItem);
    }

    @Test
    @Transactional
    void putNonExistingUploadJobItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJobItem.setId(longCount.incrementAndGet());

        // Create the UploadJobItem
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUploadJobItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, uploadJobItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(uploadJobItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UploadJobItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUploadJobItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJobItem.setId(longCount.incrementAndGet());

        // Create the UploadJobItem
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadJobItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(uploadJobItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UploadJobItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUploadJobItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJobItem.setId(longCount.incrementAndGet());

        // Create the UploadJobItem
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadJobItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uploadJobItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UploadJobItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUploadJobItemWithPatch() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the uploadJobItem using partial update
        UploadJobItem partialUpdatedUploadJobItem = new UploadJobItem();
        partialUpdatedUploadJobItem.setId(uploadJobItem.getId());

        partialUpdatedUploadJobItem
            .lineNo(UPDATED_LINE_NO)
            .resultCode(UPDATED_RESULT_CODE)
            .resultMsg(UPDATED_RESULT_MSG)
            .buyerId(UPDATED_BUYER_ID)
            .amountIncl(UPDATED_AMOUNT_INCL)
            .assignedPrefix(UPDATED_ASSIGNED_PREFIX)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .rawHash(UPDATED_RAW_HASH);

        restUploadJobItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUploadJobItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUploadJobItem))
            )
            .andExpect(status().isOk());

        // Validate the UploadJobItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUploadJobItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUploadJobItem, uploadJobItem),
            getPersistedUploadJobItem(uploadJobItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateUploadJobItemWithPatch() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the uploadJobItem using partial update
        UploadJobItem partialUpdatedUploadJobItem = new UploadJobItem();
        partialUpdatedUploadJobItem.setId(uploadJobItem.getId());

        partialUpdatedUploadJobItem
            .lineNo(UPDATED_LINE_NO)
            .traceId(UPDATED_TRACE_ID)
            .status(UPDATED_STATUS)
            .resultCode(UPDATED_RESULT_CODE)
            .resultMsg(UPDATED_RESULT_MSG)
            .buyerId(UPDATED_BUYER_ID)
            .buyerName(UPDATED_BUYER_NAME)
            .currency(UPDATED_CURRENCY)
            .amountExcl(UPDATED_AMOUNT_EXCL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .amountIncl(UPDATED_AMOUNT_INCL)
            .taxType(UPDATED_TAX_TYPE)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .invoiceNo(UPDATED_INVOICE_NO)
            .assignedPrefix(UPDATED_ASSIGNED_PREFIX)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .rawHash(UPDATED_RAW_HASH)
            .profileDetected(UPDATED_PROFILE_DETECTED);

        restUploadJobItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUploadJobItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUploadJobItem))
            )
            .andExpect(status().isOk());

        // Validate the UploadJobItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUploadJobItemUpdatableFieldsEquals(partialUpdatedUploadJobItem, getPersistedUploadJobItem(partialUpdatedUploadJobItem));
    }

    @Test
    @Transactional
    void patchNonExistingUploadJobItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJobItem.setId(longCount.incrementAndGet());

        // Create the UploadJobItem
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUploadJobItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, uploadJobItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(uploadJobItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UploadJobItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUploadJobItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJobItem.setId(longCount.incrementAndGet());

        // Create the UploadJobItem
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadJobItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(uploadJobItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UploadJobItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUploadJobItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uploadJobItem.setId(longCount.incrementAndGet());

        // Create the UploadJobItem
        UploadJobItemDTO uploadJobItemDTO = uploadJobItemMapper.toDto(uploadJobItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadJobItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(uploadJobItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UploadJobItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUploadJobItem() throws Exception {
        // Initialize the database
        insertedUploadJobItem = uploadJobItemRepository.saveAndFlush(uploadJobItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the uploadJobItem
        restUploadJobItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, uploadJobItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return uploadJobItemRepository.count();
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

    protected UploadJobItem getPersistedUploadJobItem(UploadJobItem uploadJobItem) {
        return uploadJobItemRepository.findById(uploadJobItem.getId()).orElseThrow();
    }

    protected void assertPersistedUploadJobItemToMatchAllProperties(UploadJobItem expectedUploadJobItem) {
        assertUploadJobItemAllPropertiesEquals(expectedUploadJobItem, getPersistedUploadJobItem(expectedUploadJobItem));
    }

    protected void assertPersistedUploadJobItemToMatchUpdatableProperties(UploadJobItem expectedUploadJobItem) {
        assertUploadJobItemAllUpdatablePropertiesEquals(expectedUploadJobItem, getPersistedUploadJobItem(expectedUploadJobItem));
    }
}
