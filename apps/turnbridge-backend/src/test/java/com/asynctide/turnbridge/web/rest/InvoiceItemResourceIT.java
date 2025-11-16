package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.InvoiceItemAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static com.asynctide.turnbridge.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.InvoiceItem;
import com.asynctide.turnbridge.repository.InvoiceItemRepository;
import com.asynctide.turnbridge.service.InvoiceItemService;
import com.asynctide.turnbridge.service.dto.InvoiceItemDTO;
import com.asynctide.turnbridge.service.mapper.InvoiceItemMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link InvoiceItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InvoiceItemResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(2);
    private static final BigDecimal SMALLER_QUANTITY = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_SEQUENCE = 1;
    private static final Integer UPDATED_SEQUENCE = 2;
    private static final Integer SMALLER_SEQUENCE = 1 - 1;

    private static final String ENTITY_API_URL = "/api/invoice-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Mock
    private InvoiceItemRepository invoiceItemRepositoryMock;

    @Autowired
    private InvoiceItemMapper invoiceItemMapper;

    @Mock
    private InvoiceItemService invoiceItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvoiceItemMockMvc;

    private InvoiceItem invoiceItem;

    private InvoiceItem insertedInvoiceItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceItem createEntity(EntityManager em) {
        InvoiceItem invoiceItem = new InvoiceItem()
            .description(DEFAULT_DESCRIPTION)
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .amount(DEFAULT_AMOUNT)
            .sequence(DEFAULT_SEQUENCE);
        // Add required entity
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            invoice = InvoiceResourceIT.createEntity(em);
            em.persist(invoice);
            em.flush();
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        invoiceItem.setInvoice(invoice);
        return invoiceItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceItem createUpdatedEntity(EntityManager em) {
        InvoiceItem updatedInvoiceItem = new InvoiceItem()
            .description(UPDATED_DESCRIPTION)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .amount(UPDATED_AMOUNT)
            .sequence(UPDATED_SEQUENCE);
        // Add required entity
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            invoice = InvoiceResourceIT.createUpdatedEntity(em);
            em.persist(invoice);
            em.flush();
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        updatedInvoiceItem.setInvoice(invoice);
        return updatedInvoiceItem;
    }

    @BeforeEach
    void initTest() {
        invoiceItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInvoiceItem != null) {
            invoiceItemRepository.delete(insertedInvoiceItem);
            insertedInvoiceItem = null;
        }
    }

    @Test
    @Transactional
    void createInvoiceItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InvoiceItem
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(invoiceItem);
        var returnedInvoiceItemDTO = om.readValue(
            restInvoiceItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InvoiceItemDTO.class
        );

        // Validate the InvoiceItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInvoiceItem = invoiceItemMapper.toEntity(returnedInvoiceItemDTO);
        assertInvoiceItemUpdatableFieldsEquals(returnedInvoiceItem, getPersistedInvoiceItem(returnedInvoiceItem));

        insertedInvoiceItem = returnedInvoiceItem;
    }

    @Test
    @Transactional
    void createInvoiceItemWithExistingId() throws Exception {
        // Create the InvoiceItem with an existing ID
        invoiceItem.setId(1L);
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(invoiceItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InvoiceItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoiceItem.setDescription(null);

        // Create the InvoiceItem, which fails.
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(invoiceItem);

        restInvoiceItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInvoiceItems() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList
        restInvoiceItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoiceItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].sequence").value(hasItem(DEFAULT_SEQUENCE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoiceItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(invoiceItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(invoiceItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoiceItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(invoiceItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(invoiceItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInvoiceItem() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get the invoiceItem
        restInvoiceItemMockMvc
            .perform(get(ENTITY_API_URL_ID, invoiceItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoiceItem.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.sequence").value(DEFAULT_SEQUENCE));
    }

    @Test
    @Transactional
    void getInvoiceItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        Long id = invoiceItem.getId();

        defaultInvoiceItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInvoiceItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInvoiceItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where description equals to
        defaultInvoiceItemFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where description in
        defaultInvoiceItemFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where description is not null
        defaultInvoiceItemFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where description contains
        defaultInvoiceItemFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where description does not contain
        defaultInvoiceItemFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where quantity equals to
        defaultInvoiceItemFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where quantity in
        defaultInvoiceItemFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where quantity is not null
        defaultInvoiceItemFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where quantity is greater than or equal to
        defaultInvoiceItemFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where quantity is less than or equal to
        defaultInvoiceItemFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where quantity is less than
        defaultInvoiceItemFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where quantity is greater than
        defaultInvoiceItemFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where unitPrice equals to
        defaultInvoiceItemFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where unitPrice in
        defaultInvoiceItemFiltering("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE, "unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where unitPrice is not null
        defaultInvoiceItemFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where unitPrice is greater than or equal to
        defaultInvoiceItemFiltering(
            "unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where unitPrice is less than or equal to
        defaultInvoiceItemFiltering("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where unitPrice is less than
        defaultInvoiceItemFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where unitPrice is greater than
        defaultInvoiceItemFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where amount equals to
        defaultInvoiceItemFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where amount in
        defaultInvoiceItemFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where amount is not null
        defaultInvoiceItemFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where amount is greater than or equal to
        defaultInvoiceItemFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where amount is less than or equal to
        defaultInvoiceItemFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where amount is less than
        defaultInvoiceItemFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where amount is greater than
        defaultInvoiceItemFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsBySequenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where sequence equals to
        defaultInvoiceItemFiltering("sequence.equals=" + DEFAULT_SEQUENCE, "sequence.equals=" + UPDATED_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsBySequenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where sequence in
        defaultInvoiceItemFiltering("sequence.in=" + DEFAULT_SEQUENCE + "," + UPDATED_SEQUENCE, "sequence.in=" + UPDATED_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsBySequenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where sequence is not null
        defaultInvoiceItemFiltering("sequence.specified=true", "sequence.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoiceItemsBySequenceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where sequence is greater than or equal to
        defaultInvoiceItemFiltering("sequence.greaterThanOrEqual=" + DEFAULT_SEQUENCE, "sequence.greaterThanOrEqual=" + UPDATED_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsBySequenceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where sequence is less than or equal to
        defaultInvoiceItemFiltering("sequence.lessThanOrEqual=" + DEFAULT_SEQUENCE, "sequence.lessThanOrEqual=" + SMALLER_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsBySequenceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where sequence is less than
        defaultInvoiceItemFiltering("sequence.lessThan=" + UPDATED_SEQUENCE, "sequence.lessThan=" + DEFAULT_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsBySequenceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        // Get all the invoiceItemList where sequence is greater than
        defaultInvoiceItemFiltering("sequence.greaterThan=" + SMALLER_SEQUENCE, "sequence.greaterThan=" + DEFAULT_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllInvoiceItemsByInvoiceIsEqualToSomething() throws Exception {
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            invoiceItemRepository.saveAndFlush(invoiceItem);
            invoice = InvoiceResourceIT.createEntity(em);
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        em.persist(invoice);
        em.flush();
        invoiceItem.setInvoice(invoice);
        invoiceItemRepository.saveAndFlush(invoiceItem);
        Long invoiceId = invoice.getId();
        // Get all the invoiceItemList where invoice equals to invoiceId
        defaultInvoiceItemShouldBeFound("invoiceId.equals=" + invoiceId);

        // Get all the invoiceItemList where invoice equals to (invoiceId + 1)
        defaultInvoiceItemShouldNotBeFound("invoiceId.equals=" + (invoiceId + 1));
    }

    private void defaultInvoiceItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInvoiceItemShouldBeFound(shouldBeFound);
        defaultInvoiceItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInvoiceItemShouldBeFound(String filter) throws Exception {
        restInvoiceItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoiceItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].sequence").value(hasItem(DEFAULT_SEQUENCE)));

        // Check, that the count call also returns 1
        restInvoiceItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInvoiceItemShouldNotBeFound(String filter) throws Exception {
        restInvoiceItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInvoiceItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInvoiceItem() throws Exception {
        // Get the invoiceItem
        restInvoiceItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInvoiceItem() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceItem
        InvoiceItem updatedInvoiceItem = invoiceItemRepository.findById(invoiceItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInvoiceItem are not directly saved in db
        em.detach(updatedInvoiceItem);
        updatedInvoiceItem
            .description(UPDATED_DESCRIPTION)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .amount(UPDATED_AMOUNT)
            .sequence(UPDATED_SEQUENCE);
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(updatedInvoiceItem);

        restInvoiceItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInvoiceItemToMatchAllProperties(updatedInvoiceItem);
    }

    @Test
    @Transactional
    void putNonExistingInvoiceItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceItem.setId(longCount.incrementAndGet());

        // Create the InvoiceItem
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(invoiceItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvoiceItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceItem.setId(longCount.incrementAndGet());

        // Create the InvoiceItem
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(invoiceItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvoiceItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceItem.setId(longCount.incrementAndGet());

        // Create the InvoiceItem
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(invoiceItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InvoiceItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInvoiceItemWithPatch() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceItem using partial update
        InvoiceItem partialUpdatedInvoiceItem = new InvoiceItem();
        partialUpdatedInvoiceItem.setId(invoiceItem.getId());

        partialUpdatedInvoiceItem
            .description(UPDATED_DESCRIPTION)
            .quantity(UPDATED_QUANTITY)
            .amount(UPDATED_AMOUNT)
            .sequence(UPDATED_SEQUENCE);

        restInvoiceItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoiceItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoiceItem))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInvoiceItem, invoiceItem),
            getPersistedInvoiceItem(invoiceItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateInvoiceItemWithPatch() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceItem using partial update
        InvoiceItem partialUpdatedInvoiceItem = new InvoiceItem();
        partialUpdatedInvoiceItem.setId(invoiceItem.getId());

        partialUpdatedInvoiceItem
            .description(UPDATED_DESCRIPTION)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .amount(UPDATED_AMOUNT)
            .sequence(UPDATED_SEQUENCE);

        restInvoiceItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoiceItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoiceItem))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceItemUpdatableFieldsEquals(partialUpdatedInvoiceItem, getPersistedInvoiceItem(partialUpdatedInvoiceItem));
    }

    @Test
    @Transactional
    void patchNonExistingInvoiceItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceItem.setId(longCount.incrementAndGet());

        // Create the InvoiceItem
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(invoiceItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, invoiceItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvoiceItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceItem.setId(longCount.incrementAndGet());

        // Create the InvoiceItem
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(invoiceItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvoiceItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceItem.setId(longCount.incrementAndGet());

        // Create the InvoiceItem
        InvoiceItemDTO invoiceItemDTO = invoiceItemMapper.toDto(invoiceItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(invoiceItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InvoiceItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInvoiceItem() throws Exception {
        // Initialize the database
        insertedInvoiceItem = invoiceItemRepository.saveAndFlush(invoiceItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the invoiceItem
        restInvoiceItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, invoiceItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return invoiceItemRepository.count();
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

    protected InvoiceItem getPersistedInvoiceItem(InvoiceItem invoiceItem) {
        return invoiceItemRepository.findById(invoiceItem.getId()).orElseThrow();
    }

    protected void assertPersistedInvoiceItemToMatchAllProperties(InvoiceItem expectedInvoiceItem) {
        assertInvoiceItemAllPropertiesEquals(expectedInvoiceItem, getPersistedInvoiceItem(expectedInvoiceItem));
    }

    protected void assertPersistedInvoiceItemToMatchUpdatableProperties(InvoiceItem expectedInvoiceItem) {
        assertInvoiceItemAllUpdatablePropertiesEquals(expectedInvoiceItem, getPersistedInvoiceItem(expectedInvoiceItem));
    }
}
