package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.InvoiceAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static com.asynctide.turnbridge.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.enumeration.InvoiceStatus;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.repository.InvoiceRepository;
import com.asynctide.turnbridge.service.InvoiceService;
import com.asynctide.turnbridge.service.dto.InvoiceDTO;
import com.asynctide.turnbridge.service.mapper.InvoiceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link InvoiceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InvoiceResourceIT {

    private static final String DEFAULT_INVOICE_NO = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_NO = "BBBBBBBBBB";

    private static final MessageFamily DEFAULT_MESSAGE_FAMILY = MessageFamily.F0401;
    private static final MessageFamily UPDATED_MESSAGE_FAMILY = MessageFamily.F0501;

    private static final String DEFAULT_BUYER_ID = "AAAAAAAAAA";
    private static final String UPDATED_BUYER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_BUYER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BUYER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SELLER_ID = "AAAAAAAAAA";
    private static final String UPDATED_SELLER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SELLER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SELLER_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_SALES_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_SALES_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_SALES_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TAX_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAX_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TAX_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_TAX_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TAX_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_NORMALIZED_JSON = "AAAAAAAAAA";
    private static final String UPDATED_NORMALIZED_JSON = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_PAYLOAD = "BBBBBBBBBB";

    private static final InvoiceStatus DEFAULT_INVOICE_STATUS = InvoiceStatus.DRAFT;
    private static final InvoiceStatus UPDATED_INVOICE_STATUS = InvoiceStatus.NORMALIZED;

    private static final Instant DEFAULT_ISSUED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ISSUED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LEGACY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_LEGACY_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/invoices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceRepository invoiceRepositoryMock;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Mock
    private InvoiceService invoiceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvoiceMockMvc;

    private Invoice invoice;

    private Invoice insertedInvoice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .invoiceNo(DEFAULT_INVOICE_NO)
            .messageFamily(DEFAULT_MESSAGE_FAMILY)
            .buyerId(DEFAULT_BUYER_ID)
            .buyerName(DEFAULT_BUYER_NAME)
            .sellerId(DEFAULT_SELLER_ID)
            .sellerName(DEFAULT_SELLER_NAME)
            .salesAmount(DEFAULT_SALES_AMOUNT)
            .taxAmount(DEFAULT_TAX_AMOUNT)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .taxType(DEFAULT_TAX_TYPE)
            .normalizedJson(DEFAULT_NORMALIZED_JSON)
            .originalPayload(DEFAULT_ORIGINAL_PAYLOAD)
            .invoiceStatus(DEFAULT_INVOICE_STATUS)
            .issuedAt(DEFAULT_ISSUED_AT)
            .legacyType(DEFAULT_LEGACY_TYPE);
        // Add required entity
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            importFile = ImportFileResourceIT.createEntity();
            em.persist(importFile);
            em.flush();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        invoice.setImportFile(importFile);
        return invoice;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createUpdatedEntity(EntityManager em) {
        Invoice updatedInvoice = new Invoice()
            .invoiceNo(UPDATED_INVOICE_NO)
            .messageFamily(UPDATED_MESSAGE_FAMILY)
            .buyerId(UPDATED_BUYER_ID)
            .buyerName(UPDATED_BUYER_NAME)
            .sellerId(UPDATED_SELLER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .salesAmount(UPDATED_SALES_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .taxType(UPDATED_TAX_TYPE)
            .normalizedJson(UPDATED_NORMALIZED_JSON)
            .originalPayload(UPDATED_ORIGINAL_PAYLOAD)
            .invoiceStatus(UPDATED_INVOICE_STATUS)
            .issuedAt(UPDATED_ISSUED_AT)
            .legacyType(UPDATED_LEGACY_TYPE);
        // Add required entity
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            importFile = ImportFileResourceIT.createUpdatedEntity();
            em.persist(importFile);
            em.flush();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        updatedInvoice.setImportFile(importFile);
        return updatedInvoice;
    }

    @BeforeEach
    void initTest() {
        invoice = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInvoice != null) {
            invoiceRepository.delete(insertedInvoice);
            insertedInvoice = null;
        }
    }

    @Test
    @Transactional
    void createInvoice() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);
        var returnedInvoiceDTO = om.readValue(
            restInvoiceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InvoiceDTO.class
        );

        // Validate the Invoice in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInvoice = invoiceMapper.toEntity(returnedInvoiceDTO);
        assertInvoiceUpdatableFieldsEquals(returnedInvoice, getPersistedInvoice(returnedInvoice));

        insertedInvoice = returnedInvoice;
    }

    @Test
    @Transactional
    void createInvoiceWithExistingId() throws Exception {
        // Create the Invoice with an existing ID
        invoice.setId(1L);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkInvoiceNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setInvoiceNo(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageFamilyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setMessageFamily(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInvoiceStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setInvoiceStatus(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInvoices() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().intValue())))
            .andExpect(jsonPath("$.[*].invoiceNo").value(hasItem(DEFAULT_INVOICE_NO)))
            .andExpect(jsonPath("$.[*].messageFamily").value(hasItem(DEFAULT_MESSAGE_FAMILY.toString())))
            .andExpect(jsonPath("$.[*].buyerId").value(hasItem(DEFAULT_BUYER_ID)))
            .andExpect(jsonPath("$.[*].buyerName").value(hasItem(DEFAULT_BUYER_NAME)))
            .andExpect(jsonPath("$.[*].sellerId").value(hasItem(DEFAULT_SELLER_ID)))
            .andExpect(jsonPath("$.[*].sellerName").value(hasItem(DEFAULT_SELLER_NAME)))
            .andExpect(jsonPath("$.[*].salesAmount").value(hasItem(sameNumber(DEFAULT_SALES_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxType").value(hasItem(DEFAULT_TAX_TYPE)))
            .andExpect(jsonPath("$.[*].normalizedJson").value(hasItem(DEFAULT_NORMALIZED_JSON)))
            .andExpect(jsonPath("$.[*].originalPayload").value(hasItem(DEFAULT_ORIGINAL_PAYLOAD)))
            .andExpect(jsonPath("$.[*].invoiceStatus").value(hasItem(DEFAULT_INVOICE_STATUS.toString())))
            .andExpect(jsonPath("$.[*].issuedAt").value(hasItem(DEFAULT_ISSUED_AT.toString())))
            .andExpect(jsonPath("$.[*].legacyType").value(hasItem(DEFAULT_LEGACY_TYPE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoicesWithEagerRelationshipsIsEnabled() throws Exception {
        when(invoiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(invoiceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoicesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(invoiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(invoiceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInvoice() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get the invoice
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL_ID, invoice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoice.getId().intValue()))
            .andExpect(jsonPath("$.invoiceNo").value(DEFAULT_INVOICE_NO))
            .andExpect(jsonPath("$.messageFamily").value(DEFAULT_MESSAGE_FAMILY.toString()))
            .andExpect(jsonPath("$.buyerId").value(DEFAULT_BUYER_ID))
            .andExpect(jsonPath("$.buyerName").value(DEFAULT_BUYER_NAME))
            .andExpect(jsonPath("$.sellerId").value(DEFAULT_SELLER_ID))
            .andExpect(jsonPath("$.sellerName").value(DEFAULT_SELLER_NAME))
            .andExpect(jsonPath("$.salesAmount").value(sameNumber(DEFAULT_SALES_AMOUNT)))
            .andExpect(jsonPath("$.taxAmount").value(sameNumber(DEFAULT_TAX_AMOUNT)))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.taxType").value(DEFAULT_TAX_TYPE))
            .andExpect(jsonPath("$.normalizedJson").value(DEFAULT_NORMALIZED_JSON))
            .andExpect(jsonPath("$.originalPayload").value(DEFAULT_ORIGINAL_PAYLOAD))
            .andExpect(jsonPath("$.invoiceStatus").value(DEFAULT_INVOICE_STATUS.toString()))
            .andExpect(jsonPath("$.issuedAt").value(DEFAULT_ISSUED_AT.toString()))
            .andExpect(jsonPath("$.legacyType").value(DEFAULT_LEGACY_TYPE));
    }

    @Test
    @Transactional
    void getInvoicesByIdFiltering() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        Long id = invoice.getId();

        defaultInvoiceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInvoiceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInvoiceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNo equals to
        defaultInvoiceFiltering("invoiceNo.equals=" + DEFAULT_INVOICE_NO, "invoiceNo.equals=" + UPDATED_INVOICE_NO);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNo in
        defaultInvoiceFiltering("invoiceNo.in=" + DEFAULT_INVOICE_NO + "," + UPDATED_INVOICE_NO, "invoiceNo.in=" + UPDATED_INVOICE_NO);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNo is not null
        defaultInvoiceFiltering("invoiceNo.specified=true", "invoiceNo.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNoContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNo contains
        defaultInvoiceFiltering("invoiceNo.contains=" + DEFAULT_INVOICE_NO, "invoiceNo.contains=" + UPDATED_INVOICE_NO);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceNoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceNo does not contain
        defaultInvoiceFiltering("invoiceNo.doesNotContain=" + UPDATED_INVOICE_NO, "invoiceNo.doesNotContain=" + DEFAULT_INVOICE_NO);
    }

    @Test
    @Transactional
    void getAllInvoicesByMessageFamilyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where messageFamily equals to
        defaultInvoiceFiltering("messageFamily.equals=" + DEFAULT_MESSAGE_FAMILY, "messageFamily.equals=" + UPDATED_MESSAGE_FAMILY);
    }

    @Test
    @Transactional
    void getAllInvoicesByMessageFamilyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where messageFamily in
        defaultInvoiceFiltering(
            "messageFamily.in=" + DEFAULT_MESSAGE_FAMILY + "," + UPDATED_MESSAGE_FAMILY,
            "messageFamily.in=" + UPDATED_MESSAGE_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllInvoicesByMessageFamilyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where messageFamily is not null
        defaultInvoiceFiltering("messageFamily.specified=true", "messageFamily.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerId equals to
        defaultInvoiceFiltering("buyerId.equals=" + DEFAULT_BUYER_ID, "buyerId.equals=" + UPDATED_BUYER_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerId in
        defaultInvoiceFiltering("buyerId.in=" + DEFAULT_BUYER_ID + "," + UPDATED_BUYER_ID, "buyerId.in=" + UPDATED_BUYER_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerId is not null
        defaultInvoiceFiltering("buyerId.specified=true", "buyerId.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerIdContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerId contains
        defaultInvoiceFiltering("buyerId.contains=" + DEFAULT_BUYER_ID, "buyerId.contains=" + UPDATED_BUYER_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerId does not contain
        defaultInvoiceFiltering("buyerId.doesNotContain=" + UPDATED_BUYER_ID, "buyerId.doesNotContain=" + DEFAULT_BUYER_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerName equals to
        defaultInvoiceFiltering("buyerName.equals=" + DEFAULT_BUYER_NAME, "buyerName.equals=" + UPDATED_BUYER_NAME);
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerName in
        defaultInvoiceFiltering("buyerName.in=" + DEFAULT_BUYER_NAME + "," + UPDATED_BUYER_NAME, "buyerName.in=" + UPDATED_BUYER_NAME);
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerName is not null
        defaultInvoiceFiltering("buyerName.specified=true", "buyerName.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerNameContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerName contains
        defaultInvoiceFiltering("buyerName.contains=" + DEFAULT_BUYER_NAME, "buyerName.contains=" + UPDATED_BUYER_NAME);
    }

    @Test
    @Transactional
    void getAllInvoicesByBuyerNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where buyerName does not contain
        defaultInvoiceFiltering("buyerName.doesNotContain=" + UPDATED_BUYER_NAME, "buyerName.doesNotContain=" + DEFAULT_BUYER_NAME);
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerId equals to
        defaultInvoiceFiltering("sellerId.equals=" + DEFAULT_SELLER_ID, "sellerId.equals=" + UPDATED_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerId in
        defaultInvoiceFiltering("sellerId.in=" + DEFAULT_SELLER_ID + "," + UPDATED_SELLER_ID, "sellerId.in=" + UPDATED_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerId is not null
        defaultInvoiceFiltering("sellerId.specified=true", "sellerId.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerIdContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerId contains
        defaultInvoiceFiltering("sellerId.contains=" + DEFAULT_SELLER_ID, "sellerId.contains=" + UPDATED_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerId does not contain
        defaultInvoiceFiltering("sellerId.doesNotContain=" + UPDATED_SELLER_ID, "sellerId.doesNotContain=" + DEFAULT_SELLER_ID);
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerName equals to
        defaultInvoiceFiltering("sellerName.equals=" + DEFAULT_SELLER_NAME, "sellerName.equals=" + UPDATED_SELLER_NAME);
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerName in
        defaultInvoiceFiltering("sellerName.in=" + DEFAULT_SELLER_NAME + "," + UPDATED_SELLER_NAME, "sellerName.in=" + UPDATED_SELLER_NAME);
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerName is not null
        defaultInvoiceFiltering("sellerName.specified=true", "sellerName.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerNameContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerName contains
        defaultInvoiceFiltering("sellerName.contains=" + DEFAULT_SELLER_NAME, "sellerName.contains=" + UPDATED_SELLER_NAME);
    }

    @Test
    @Transactional
    void getAllInvoicesBySellerNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where sellerName does not contain
        defaultInvoiceFiltering("sellerName.doesNotContain=" + UPDATED_SELLER_NAME, "sellerName.doesNotContain=" + DEFAULT_SELLER_NAME);
    }

    @Test
    @Transactional
    void getAllInvoicesBySalesAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where salesAmount equals to
        defaultInvoiceFiltering("salesAmount.equals=" + DEFAULT_SALES_AMOUNT, "salesAmount.equals=" + UPDATED_SALES_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesBySalesAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where salesAmount in
        defaultInvoiceFiltering(
            "salesAmount.in=" + DEFAULT_SALES_AMOUNT + "," + UPDATED_SALES_AMOUNT,
            "salesAmount.in=" + UPDATED_SALES_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoicesBySalesAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where salesAmount is not null
        defaultInvoiceFiltering("salesAmount.specified=true", "salesAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesBySalesAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where salesAmount is greater than or equal to
        defaultInvoiceFiltering(
            "salesAmount.greaterThanOrEqual=" + DEFAULT_SALES_AMOUNT,
            "salesAmount.greaterThanOrEqual=" + UPDATED_SALES_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoicesBySalesAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where salesAmount is less than or equal to
        defaultInvoiceFiltering(
            "salesAmount.lessThanOrEqual=" + DEFAULT_SALES_AMOUNT,
            "salesAmount.lessThanOrEqual=" + SMALLER_SALES_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoicesBySalesAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where salesAmount is less than
        defaultInvoiceFiltering("salesAmount.lessThan=" + UPDATED_SALES_AMOUNT, "salesAmount.lessThan=" + DEFAULT_SALES_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesBySalesAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where salesAmount is greater than
        defaultInvoiceFiltering("salesAmount.greaterThan=" + SMALLER_SALES_AMOUNT, "salesAmount.greaterThan=" + DEFAULT_SALES_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount equals to
        defaultInvoiceFiltering("taxAmount.equals=" + DEFAULT_TAX_AMOUNT, "taxAmount.equals=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount in
        defaultInvoiceFiltering("taxAmount.in=" + DEFAULT_TAX_AMOUNT + "," + UPDATED_TAX_AMOUNT, "taxAmount.in=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is not null
        defaultInvoiceFiltering("taxAmount.specified=true", "taxAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is greater than or equal to
        defaultInvoiceFiltering("taxAmount.greaterThanOrEqual=" + DEFAULT_TAX_AMOUNT, "taxAmount.greaterThanOrEqual=" + UPDATED_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is less than or equal to
        defaultInvoiceFiltering("taxAmount.lessThanOrEqual=" + DEFAULT_TAX_AMOUNT, "taxAmount.lessThanOrEqual=" + SMALLER_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is less than
        defaultInvoiceFiltering("taxAmount.lessThan=" + UPDATED_TAX_AMOUNT, "taxAmount.lessThan=" + DEFAULT_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxAmount is greater than
        defaultInvoiceFiltering("taxAmount.greaterThan=" + SMALLER_TAX_AMOUNT, "taxAmount.greaterThan=" + DEFAULT_TAX_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount equals to
        defaultInvoiceFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount in
        defaultInvoiceFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is not null
        defaultInvoiceFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is greater than or equal to
        defaultInvoiceFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is less than or equal to
        defaultInvoiceFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is less than
        defaultInvoiceFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is greater than
        defaultInvoiceFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT, "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxType equals to
        defaultInvoiceFiltering("taxType.equals=" + DEFAULT_TAX_TYPE, "taxType.equals=" + UPDATED_TAX_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxType in
        defaultInvoiceFiltering("taxType.in=" + DEFAULT_TAX_TYPE + "," + UPDATED_TAX_TYPE, "taxType.in=" + UPDATED_TAX_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxType is not null
        defaultInvoiceFiltering("taxType.specified=true", "taxType.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxType contains
        defaultInvoiceFiltering("taxType.contains=" + DEFAULT_TAX_TYPE, "taxType.contains=" + UPDATED_TAX_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByTaxTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where taxType does not contain
        defaultInvoiceFiltering("taxType.doesNotContain=" + UPDATED_TAX_TYPE, "taxType.doesNotContain=" + DEFAULT_TAX_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceStatus equals to
        defaultInvoiceFiltering("invoiceStatus.equals=" + DEFAULT_INVOICE_STATUS, "invoiceStatus.equals=" + UPDATED_INVOICE_STATUS);
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceStatus in
        defaultInvoiceFiltering(
            "invoiceStatus.in=" + DEFAULT_INVOICE_STATUS + "," + UPDATED_INVOICE_STATUS,
            "invoiceStatus.in=" + UPDATED_INVOICE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllInvoicesByInvoiceStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where invoiceStatus is not null
        defaultInvoiceFiltering("invoiceStatus.specified=true", "invoiceStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByIssuedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issuedAt equals to
        defaultInvoiceFiltering("issuedAt.equals=" + DEFAULT_ISSUED_AT, "issuedAt.equals=" + UPDATED_ISSUED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByIssuedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issuedAt in
        defaultInvoiceFiltering("issuedAt.in=" + DEFAULT_ISSUED_AT + "," + UPDATED_ISSUED_AT, "issuedAt.in=" + UPDATED_ISSUED_AT);
    }

    @Test
    @Transactional
    void getAllInvoicesByIssuedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where issuedAt is not null
        defaultInvoiceFiltering("issuedAt.specified=true", "issuedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByLegacyTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where legacyType equals to
        defaultInvoiceFiltering("legacyType.equals=" + DEFAULT_LEGACY_TYPE, "legacyType.equals=" + UPDATED_LEGACY_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByLegacyTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where legacyType in
        defaultInvoiceFiltering("legacyType.in=" + DEFAULT_LEGACY_TYPE + "," + UPDATED_LEGACY_TYPE, "legacyType.in=" + UPDATED_LEGACY_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByLegacyTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where legacyType is not null
        defaultInvoiceFiltering("legacyType.specified=true", "legacyType.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByLegacyTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where legacyType contains
        defaultInvoiceFiltering("legacyType.contains=" + DEFAULT_LEGACY_TYPE, "legacyType.contains=" + UPDATED_LEGACY_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByLegacyTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where legacyType does not contain
        defaultInvoiceFiltering("legacyType.doesNotContain=" + UPDATED_LEGACY_TYPE, "legacyType.doesNotContain=" + DEFAULT_LEGACY_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByImportFileIsEqualToSomething() throws Exception {
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            invoiceRepository.saveAndFlush(invoice);
            importFile = ImportFileResourceIT.createEntity();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        em.persist(importFile);
        em.flush();
        invoice.setImportFile(importFile);
        invoiceRepository.saveAndFlush(invoice);
        Long importFileId = importFile.getId();
        // Get all the invoiceList where importFile equals to importFileId
        defaultInvoiceShouldBeFound("importFileId.equals=" + importFileId);

        // Get all the invoiceList where importFile equals to (importFileId + 1)
        defaultInvoiceShouldNotBeFound("importFileId.equals=" + (importFileId + 1));
    }

    @Test
    @Transactional
    void getAllInvoicesByTenantIsEqualToSomething() throws Exception {
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            invoiceRepository.saveAndFlush(invoice);
            tenant = TenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        em.persist(tenant);
        em.flush();
        invoice.setTenant(tenant);
        invoiceRepository.saveAndFlush(invoice);
        Long tenantId = tenant.getId();
        // Get all the invoiceList where tenant equals to tenantId
        defaultInvoiceShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the invoiceList where tenant equals to (tenantId + 1)
        defaultInvoiceShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultInvoiceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInvoiceShouldBeFound(shouldBeFound);
        defaultInvoiceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInvoiceShouldBeFound(String filter) throws Exception {
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().intValue())))
            .andExpect(jsonPath("$.[*].invoiceNo").value(hasItem(DEFAULT_INVOICE_NO)))
            .andExpect(jsonPath("$.[*].messageFamily").value(hasItem(DEFAULT_MESSAGE_FAMILY.toString())))
            .andExpect(jsonPath("$.[*].buyerId").value(hasItem(DEFAULT_BUYER_ID)))
            .andExpect(jsonPath("$.[*].buyerName").value(hasItem(DEFAULT_BUYER_NAME)))
            .andExpect(jsonPath("$.[*].sellerId").value(hasItem(DEFAULT_SELLER_ID)))
            .andExpect(jsonPath("$.[*].sellerName").value(hasItem(DEFAULT_SELLER_NAME)))
            .andExpect(jsonPath("$.[*].salesAmount").value(hasItem(sameNumber(DEFAULT_SALES_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxAmount").value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT))))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxType").value(hasItem(DEFAULT_TAX_TYPE)))
            .andExpect(jsonPath("$.[*].normalizedJson").value(hasItem(DEFAULT_NORMALIZED_JSON)))
            .andExpect(jsonPath("$.[*].originalPayload").value(hasItem(DEFAULT_ORIGINAL_PAYLOAD)))
            .andExpect(jsonPath("$.[*].invoiceStatus").value(hasItem(DEFAULT_INVOICE_STATUS.toString())))
            .andExpect(jsonPath("$.[*].issuedAt").value(hasItem(DEFAULT_ISSUED_AT.toString())))
            .andExpect(jsonPath("$.[*].legacyType").value(hasItem(DEFAULT_LEGACY_TYPE)));

        // Check, that the count call also returns 1
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInvoiceShouldNotBeFound(String filter) throws Exception {
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInvoice() throws Exception {
        // Get the invoice
        restInvoiceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInvoice() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoice
        Invoice updatedInvoice = invoiceRepository.findById(invoice.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInvoice are not directly saved in db
        em.detach(updatedInvoice);
        updatedInvoice
            .invoiceNo(UPDATED_INVOICE_NO)
            .messageFamily(UPDATED_MESSAGE_FAMILY)
            .buyerId(UPDATED_BUYER_ID)
            .buyerName(UPDATED_BUYER_NAME)
            .sellerId(UPDATED_SELLER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .salesAmount(UPDATED_SALES_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .taxType(UPDATED_TAX_TYPE)
            .normalizedJson(UPDATED_NORMALIZED_JSON)
            .originalPayload(UPDATED_ORIGINAL_PAYLOAD)
            .invoiceStatus(UPDATED_INVOICE_STATUS)
            .issuedAt(UPDATED_ISSUED_AT)
            .legacyType(UPDATED_LEGACY_TYPE);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(updatedInvoice);

        restInvoiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInvoiceToMatchAllProperties(updatedInvoice);
    }

    @Test
    @Transactional
    void putNonExistingInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice
            .invoiceNo(UPDATED_INVOICE_NO)
            .messageFamily(UPDATED_MESSAGE_FAMILY)
            .sellerId(UPDATED_SELLER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .taxType(UPDATED_TAX_TYPE)
            .normalizedJson(UPDATED_NORMALIZED_JSON)
            .invoiceStatus(UPDATED_INVOICE_STATUS)
            .issuedAt(UPDATED_ISSUED_AT);

        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoice))
            )
            .andExpect(status().isOk());

        // Validate the Invoice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedInvoice, invoice), getPersistedInvoice(invoice));
    }

    @Test
    @Transactional
    void fullUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice
            .invoiceNo(UPDATED_INVOICE_NO)
            .messageFamily(UPDATED_MESSAGE_FAMILY)
            .buyerId(UPDATED_BUYER_ID)
            .buyerName(UPDATED_BUYER_NAME)
            .sellerId(UPDATED_SELLER_ID)
            .sellerName(UPDATED_SELLER_NAME)
            .salesAmount(UPDATED_SALES_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .taxType(UPDATED_TAX_TYPE)
            .normalizedJson(UPDATED_NORMALIZED_JSON)
            .originalPayload(UPDATED_ORIGINAL_PAYLOAD)
            .invoiceStatus(UPDATED_INVOICE_STATUS)
            .issuedAt(UPDATED_ISSUED_AT)
            .legacyType(UPDATED_LEGACY_TYPE);

        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoice))
            )
            .andExpect(status().isOk());

        // Validate the Invoice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceUpdatableFieldsEquals(partialUpdatedInvoice, getPersistedInvoice(partialUpdatedInvoice));
    }

    @Test
    @Transactional
    void patchNonExistingInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, invoiceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(longCount.incrementAndGet());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInvoice() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the invoice
        restInvoiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, invoice.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return invoiceRepository.count();
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

    protected Invoice getPersistedInvoice(Invoice invoice) {
        return invoiceRepository.findById(invoice.getId()).orElseThrow();
    }

    protected void assertPersistedInvoiceToMatchAllProperties(Invoice expectedInvoice) {
        assertInvoiceAllPropertiesEquals(expectedInvoice, getPersistedInvoice(expectedInvoice));
    }

    protected void assertPersistedInvoiceToMatchUpdatableProperties(Invoice expectedInvoice) {
        assertInvoiceAllUpdatablePropertiesEquals(expectedInvoice, getPersistedInvoice(expectedInvoice));
    }
}
