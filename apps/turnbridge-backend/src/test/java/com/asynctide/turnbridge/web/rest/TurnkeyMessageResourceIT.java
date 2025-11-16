package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.TurnkeyMessageAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.TurnkeyMessage;
import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.repository.TurnkeyMessageRepository;
import com.asynctide.turnbridge.service.TurnkeyMessageService;
import com.asynctide.turnbridge.service.dto.TurnkeyMessageDTO;
import com.asynctide.turnbridge.service.mapper.TurnkeyMessageMapper;
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
 * Integration tests for the {@link TurnkeyMessageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TurnkeyMessageResourceIT {

    private static final String DEFAULT_MESSAGE_ID = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_ID = "BBBBBBBBBB";

    private static final MessageFamily DEFAULT_MESSAGE_FAMILY = MessageFamily.F0401;
    private static final MessageFamily UPDATED_MESSAGE_FAMILY = MessageFamily.F0501;

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYLOAD_PATH = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD_PATH = "BBBBBBBBBB";

    private static final Instant DEFAULT_RECEIVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECEIVED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/turnkey-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TurnkeyMessageRepository turnkeyMessageRepository;

    @Mock
    private TurnkeyMessageRepository turnkeyMessageRepositoryMock;

    @Autowired
    private TurnkeyMessageMapper turnkeyMessageMapper;

    @Mock
    private TurnkeyMessageService turnkeyMessageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTurnkeyMessageMockMvc;

    private TurnkeyMessage turnkeyMessage;

    private TurnkeyMessage insertedTurnkeyMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TurnkeyMessage createEntity() {
        return new TurnkeyMessage()
            .messageId(DEFAULT_MESSAGE_ID)
            .messageFamily(DEFAULT_MESSAGE_FAMILY)
            .type(DEFAULT_TYPE)
            .code(DEFAULT_CODE)
            .message(DEFAULT_MESSAGE)
            .payloadPath(DEFAULT_PAYLOAD_PATH)
            .receivedAt(DEFAULT_RECEIVED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TurnkeyMessage createUpdatedEntity() {
        return new TurnkeyMessage()
            .messageId(UPDATED_MESSAGE_ID)
            .messageFamily(UPDATED_MESSAGE_FAMILY)
            .type(UPDATED_TYPE)
            .code(UPDATED_CODE)
            .message(UPDATED_MESSAGE)
            .payloadPath(UPDATED_PAYLOAD_PATH)
            .receivedAt(UPDATED_RECEIVED_AT);
    }

    @BeforeEach
    void initTest() {
        turnkeyMessage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTurnkeyMessage != null) {
            turnkeyMessageRepository.delete(insertedTurnkeyMessage);
            insertedTurnkeyMessage = null;
        }
    }

    @Test
    @Transactional
    void createTurnkeyMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TurnkeyMessage
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);
        var returnedTurnkeyMessageDTO = om.readValue(
            restTurnkeyMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnkeyMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TurnkeyMessageDTO.class
        );

        // Validate the TurnkeyMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTurnkeyMessage = turnkeyMessageMapper.toEntity(returnedTurnkeyMessageDTO);
        assertTurnkeyMessageUpdatableFieldsEquals(returnedTurnkeyMessage, getPersistedTurnkeyMessage(returnedTurnkeyMessage));

        insertedTurnkeyMessage = returnedTurnkeyMessage;
    }

    @Test
    @Transactional
    void createTurnkeyMessageWithExistingId() throws Exception {
        // Create the TurnkeyMessage with an existing ID
        turnkeyMessage.setId(1L);
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTurnkeyMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnkeyMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TurnkeyMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMessageIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        turnkeyMessage.setMessageId(null);

        // Create the TurnkeyMessage, which fails.
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        restTurnkeyMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnkeyMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageFamilyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        turnkeyMessage.setMessageFamily(null);

        // Create the TurnkeyMessage, which fails.
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        restTurnkeyMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnkeyMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        turnkeyMessage.setType(null);

        // Create the TurnkeyMessage, which fails.
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        restTurnkeyMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnkeyMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessages() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList
        restTurnkeyMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(turnkeyMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].messageId").value(hasItem(DEFAULT_MESSAGE_ID)))
            .andExpect(jsonPath("$.[*].messageFamily").value(hasItem(DEFAULT_MESSAGE_FAMILY.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].payloadPath").value(hasItem(DEFAULT_PAYLOAD_PATH)))
            .andExpect(jsonPath("$.[*].receivedAt").value(hasItem(DEFAULT_RECEIVED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTurnkeyMessagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(turnkeyMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTurnkeyMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(turnkeyMessageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTurnkeyMessagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(turnkeyMessageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTurnkeyMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(turnkeyMessageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTurnkeyMessage() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get the turnkeyMessage
        restTurnkeyMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, turnkeyMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(turnkeyMessage.getId().intValue()))
            .andExpect(jsonPath("$.messageId").value(DEFAULT_MESSAGE_ID))
            .andExpect(jsonPath("$.messageFamily").value(DEFAULT_MESSAGE_FAMILY.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.payloadPath").value(DEFAULT_PAYLOAD_PATH))
            .andExpect(jsonPath("$.receivedAt").value(DEFAULT_RECEIVED_AT.toString()));
    }

    @Test
    @Transactional
    void getTurnkeyMessagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        Long id = turnkeyMessage.getId();

        defaultTurnkeyMessageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTurnkeyMessageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTurnkeyMessageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByMessageIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where messageId equals to
        defaultTurnkeyMessageFiltering("messageId.equals=" + DEFAULT_MESSAGE_ID, "messageId.equals=" + UPDATED_MESSAGE_ID);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByMessageIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where messageId in
        defaultTurnkeyMessageFiltering(
            "messageId.in=" + DEFAULT_MESSAGE_ID + "," + UPDATED_MESSAGE_ID,
            "messageId.in=" + UPDATED_MESSAGE_ID
        );
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByMessageIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where messageId is not null
        defaultTurnkeyMessageFiltering("messageId.specified=true", "messageId.specified=false");
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByMessageIdContainsSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where messageId contains
        defaultTurnkeyMessageFiltering("messageId.contains=" + DEFAULT_MESSAGE_ID, "messageId.contains=" + UPDATED_MESSAGE_ID);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByMessageIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where messageId does not contain
        defaultTurnkeyMessageFiltering("messageId.doesNotContain=" + UPDATED_MESSAGE_ID, "messageId.doesNotContain=" + DEFAULT_MESSAGE_ID);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByMessageFamilyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where messageFamily equals to
        defaultTurnkeyMessageFiltering("messageFamily.equals=" + DEFAULT_MESSAGE_FAMILY, "messageFamily.equals=" + UPDATED_MESSAGE_FAMILY);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByMessageFamilyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where messageFamily in
        defaultTurnkeyMessageFiltering(
            "messageFamily.in=" + DEFAULT_MESSAGE_FAMILY + "," + UPDATED_MESSAGE_FAMILY,
            "messageFamily.in=" + UPDATED_MESSAGE_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByMessageFamilyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where messageFamily is not null
        defaultTurnkeyMessageFiltering("messageFamily.specified=true", "messageFamily.specified=false");
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where type equals to
        defaultTurnkeyMessageFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where type in
        defaultTurnkeyMessageFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where type is not null
        defaultTurnkeyMessageFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where type contains
        defaultTurnkeyMessageFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where type does not contain
        defaultTurnkeyMessageFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where code equals to
        defaultTurnkeyMessageFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where code in
        defaultTurnkeyMessageFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where code is not null
        defaultTurnkeyMessageFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where code contains
        defaultTurnkeyMessageFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where code does not contain
        defaultTurnkeyMessageFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByPayloadPathIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where payloadPath equals to
        defaultTurnkeyMessageFiltering("payloadPath.equals=" + DEFAULT_PAYLOAD_PATH, "payloadPath.equals=" + UPDATED_PAYLOAD_PATH);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByPayloadPathIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where payloadPath in
        defaultTurnkeyMessageFiltering(
            "payloadPath.in=" + DEFAULT_PAYLOAD_PATH + "," + UPDATED_PAYLOAD_PATH,
            "payloadPath.in=" + UPDATED_PAYLOAD_PATH
        );
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByPayloadPathIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where payloadPath is not null
        defaultTurnkeyMessageFiltering("payloadPath.specified=true", "payloadPath.specified=false");
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByPayloadPathContainsSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where payloadPath contains
        defaultTurnkeyMessageFiltering("payloadPath.contains=" + DEFAULT_PAYLOAD_PATH, "payloadPath.contains=" + UPDATED_PAYLOAD_PATH);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByPayloadPathNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where payloadPath does not contain
        defaultTurnkeyMessageFiltering(
            "payloadPath.doesNotContain=" + UPDATED_PAYLOAD_PATH,
            "payloadPath.doesNotContain=" + DEFAULT_PAYLOAD_PATH
        );
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByReceivedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where receivedAt equals to
        defaultTurnkeyMessageFiltering("receivedAt.equals=" + DEFAULT_RECEIVED_AT, "receivedAt.equals=" + UPDATED_RECEIVED_AT);
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByReceivedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where receivedAt in
        defaultTurnkeyMessageFiltering(
            "receivedAt.in=" + DEFAULT_RECEIVED_AT + "," + UPDATED_RECEIVED_AT,
            "receivedAt.in=" + UPDATED_RECEIVED_AT
        );
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByReceivedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        // Get all the turnkeyMessageList where receivedAt is not null
        defaultTurnkeyMessageFiltering("receivedAt.specified=true", "receivedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTurnkeyMessagesByInvoiceIsEqualToSomething() throws Exception {
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            turnkeyMessageRepository.saveAndFlush(turnkeyMessage);
            invoice = InvoiceResourceIT.createEntity(em);
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        em.persist(invoice);
        em.flush();
        turnkeyMessage.setInvoice(invoice);
        turnkeyMessageRepository.saveAndFlush(turnkeyMessage);
        Long invoiceId = invoice.getId();
        // Get all the turnkeyMessageList where invoice equals to invoiceId
        defaultTurnkeyMessageShouldBeFound("invoiceId.equals=" + invoiceId);

        // Get all the turnkeyMessageList where invoice equals to (invoiceId + 1)
        defaultTurnkeyMessageShouldNotBeFound("invoiceId.equals=" + (invoiceId + 1));
    }

    private void defaultTurnkeyMessageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTurnkeyMessageShouldBeFound(shouldBeFound);
        defaultTurnkeyMessageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTurnkeyMessageShouldBeFound(String filter) throws Exception {
        restTurnkeyMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(turnkeyMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].messageId").value(hasItem(DEFAULT_MESSAGE_ID)))
            .andExpect(jsonPath("$.[*].messageFamily").value(hasItem(DEFAULT_MESSAGE_FAMILY.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].payloadPath").value(hasItem(DEFAULT_PAYLOAD_PATH)))
            .andExpect(jsonPath("$.[*].receivedAt").value(hasItem(DEFAULT_RECEIVED_AT.toString())));

        // Check, that the count call also returns 1
        restTurnkeyMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTurnkeyMessageShouldNotBeFound(String filter) throws Exception {
        restTurnkeyMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTurnkeyMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTurnkeyMessage() throws Exception {
        // Get the turnkeyMessage
        restTurnkeyMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTurnkeyMessage() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the turnkeyMessage
        TurnkeyMessage updatedTurnkeyMessage = turnkeyMessageRepository.findById(turnkeyMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTurnkeyMessage are not directly saved in db
        em.detach(updatedTurnkeyMessage);
        updatedTurnkeyMessage
            .messageId(UPDATED_MESSAGE_ID)
            .messageFamily(UPDATED_MESSAGE_FAMILY)
            .type(UPDATED_TYPE)
            .code(UPDATED_CODE)
            .message(UPDATED_MESSAGE)
            .payloadPath(UPDATED_PAYLOAD_PATH)
            .receivedAt(UPDATED_RECEIVED_AT);
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(updatedTurnkeyMessage);

        restTurnkeyMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, turnkeyMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(turnkeyMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the TurnkeyMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTurnkeyMessageToMatchAllProperties(updatedTurnkeyMessage);
    }

    @Test
    @Transactional
    void putNonExistingTurnkeyMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnkeyMessage.setId(longCount.incrementAndGet());

        // Create the TurnkeyMessage
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTurnkeyMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, turnkeyMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(turnkeyMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TurnkeyMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTurnkeyMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnkeyMessage.setId(longCount.incrementAndGet());

        // Create the TurnkeyMessage
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurnkeyMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(turnkeyMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TurnkeyMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTurnkeyMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnkeyMessage.setId(longCount.incrementAndGet());

        // Create the TurnkeyMessage
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurnkeyMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turnkeyMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TurnkeyMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTurnkeyMessageWithPatch() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the turnkeyMessage using partial update
        TurnkeyMessage partialUpdatedTurnkeyMessage = new TurnkeyMessage();
        partialUpdatedTurnkeyMessage.setId(turnkeyMessage.getId());

        partialUpdatedTurnkeyMessage
            .messageFamily(UPDATED_MESSAGE_FAMILY)
            .payloadPath(UPDATED_PAYLOAD_PATH)
            .receivedAt(UPDATED_RECEIVED_AT);

        restTurnkeyMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTurnkeyMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTurnkeyMessage))
            )
            .andExpect(status().isOk());

        // Validate the TurnkeyMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTurnkeyMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTurnkeyMessage, turnkeyMessage),
            getPersistedTurnkeyMessage(turnkeyMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateTurnkeyMessageWithPatch() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the turnkeyMessage using partial update
        TurnkeyMessage partialUpdatedTurnkeyMessage = new TurnkeyMessage();
        partialUpdatedTurnkeyMessage.setId(turnkeyMessage.getId());

        partialUpdatedTurnkeyMessage
            .messageId(UPDATED_MESSAGE_ID)
            .messageFamily(UPDATED_MESSAGE_FAMILY)
            .type(UPDATED_TYPE)
            .code(UPDATED_CODE)
            .message(UPDATED_MESSAGE)
            .payloadPath(UPDATED_PAYLOAD_PATH)
            .receivedAt(UPDATED_RECEIVED_AT);

        restTurnkeyMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTurnkeyMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTurnkeyMessage))
            )
            .andExpect(status().isOk());

        // Validate the TurnkeyMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTurnkeyMessageUpdatableFieldsEquals(partialUpdatedTurnkeyMessage, getPersistedTurnkeyMessage(partialUpdatedTurnkeyMessage));
    }

    @Test
    @Transactional
    void patchNonExistingTurnkeyMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnkeyMessage.setId(longCount.incrementAndGet());

        // Create the TurnkeyMessage
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTurnkeyMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, turnkeyMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(turnkeyMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TurnkeyMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTurnkeyMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnkeyMessage.setId(longCount.incrementAndGet());

        // Create the TurnkeyMessage
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurnkeyMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(turnkeyMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TurnkeyMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTurnkeyMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        turnkeyMessage.setId(longCount.incrementAndGet());

        // Create the TurnkeyMessage
        TurnkeyMessageDTO turnkeyMessageDTO = turnkeyMessageMapper.toDto(turnkeyMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurnkeyMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(turnkeyMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TurnkeyMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTurnkeyMessage() throws Exception {
        // Initialize the database
        insertedTurnkeyMessage = turnkeyMessageRepository.saveAndFlush(turnkeyMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the turnkeyMessage
        restTurnkeyMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, turnkeyMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return turnkeyMessageRepository.count();
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

    protected TurnkeyMessage getPersistedTurnkeyMessage(TurnkeyMessage turnkeyMessage) {
        return turnkeyMessageRepository.findById(turnkeyMessage.getId()).orElseThrow();
    }

    protected void assertPersistedTurnkeyMessageToMatchAllProperties(TurnkeyMessage expectedTurnkeyMessage) {
        assertTurnkeyMessageAllPropertiesEquals(expectedTurnkeyMessage, getPersistedTurnkeyMessage(expectedTurnkeyMessage));
    }

    protected void assertPersistedTurnkeyMessageToMatchUpdatableProperties(TurnkeyMessage expectedTurnkeyMessage) {
        assertTurnkeyMessageAllUpdatablePropertiesEquals(expectedTurnkeyMessage, getPersistedTurnkeyMessage(expectedTurnkeyMessage));
    }
}
