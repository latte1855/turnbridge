package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.ImportFileItemErrorAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.ImportFileItemError;
import com.asynctide.turnbridge.repository.ImportFileItemErrorRepository;
import com.asynctide.turnbridge.service.ImportFileItemErrorService;
import com.asynctide.turnbridge.service.dto.ImportFileItemErrorDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileItemErrorMapper;
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
 * Integration tests for the {@link ImportFileItemErrorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ImportFileItemErrorResourceIT {

    private static final Integer DEFAULT_COLUMN_INDEX = 1;
    private static final Integer UPDATED_COLUMN_INDEX = 2;
    private static final Integer SMALLER_COLUMN_INDEX = 1 - 1;

    private static final String DEFAULT_FIELD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_SEVERITY = "AAAAAAAAAA";
    private static final String UPDATED_SEVERITY = "BBBBBBBBBB";

    private static final Instant DEFAULT_OCCURRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OCCURRED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/import-file-item-errors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ImportFileItemErrorRepository importFileItemErrorRepository;

    @Mock
    private ImportFileItemErrorRepository importFileItemErrorRepositoryMock;

    @Autowired
    private ImportFileItemErrorMapper importFileItemErrorMapper;

    @Mock
    private ImportFileItemErrorService importFileItemErrorServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImportFileItemErrorMockMvc;

    private ImportFileItemError importFileItemError;

    private ImportFileItemError insertedImportFileItemError;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportFileItemError createEntity(EntityManager em) {
        ImportFileItemError importFileItemError = new ImportFileItemError()
            .columnIndex(DEFAULT_COLUMN_INDEX)
            .fieldName(DEFAULT_FIELD_NAME)
            .errorCode(DEFAULT_ERROR_CODE)
            .message(DEFAULT_MESSAGE)
            .severity(DEFAULT_SEVERITY)
            .occurredAt(DEFAULT_OCCURRED_AT);
        // Add required entity
        ImportFileItem importFileItem;
        if (TestUtil.findAll(em, ImportFileItem.class).isEmpty()) {
            importFileItem = ImportFileItemResourceIT.createEntity(em);
            em.persist(importFileItem);
            em.flush();
        } else {
            importFileItem = TestUtil.findAll(em, ImportFileItem.class).get(0);
        }
        importFileItemError.setImportFileItem(importFileItem);
        return importFileItemError;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportFileItemError createUpdatedEntity(EntityManager em) {
        ImportFileItemError updatedImportFileItemError = new ImportFileItemError()
            .columnIndex(UPDATED_COLUMN_INDEX)
            .fieldName(UPDATED_FIELD_NAME)
            .errorCode(UPDATED_ERROR_CODE)
            .message(UPDATED_MESSAGE)
            .severity(UPDATED_SEVERITY)
            .occurredAt(UPDATED_OCCURRED_AT);
        // Add required entity
        ImportFileItem importFileItem;
        if (TestUtil.findAll(em, ImportFileItem.class).isEmpty()) {
            importFileItem = ImportFileItemResourceIT.createUpdatedEntity(em);
            em.persist(importFileItem);
            em.flush();
        } else {
            importFileItem = TestUtil.findAll(em, ImportFileItem.class).get(0);
        }
        updatedImportFileItemError.setImportFileItem(importFileItem);
        return updatedImportFileItemError;
    }

    @BeforeEach
    void initTest() {
        importFileItemError = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedImportFileItemError != null) {
            importFileItemErrorRepository.delete(insertedImportFileItemError);
            insertedImportFileItemError = null;
        }
    }

    @Test
    @Transactional
    void createImportFileItemError() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ImportFileItemError
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);
        var returnedImportFileItemErrorDTO = om.readValue(
            restImportFileItemErrorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemErrorDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ImportFileItemErrorDTO.class
        );

        // Validate the ImportFileItemError in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedImportFileItemError = importFileItemErrorMapper.toEntity(returnedImportFileItemErrorDTO);
        assertImportFileItemErrorUpdatableFieldsEquals(
            returnedImportFileItemError,
            getPersistedImportFileItemError(returnedImportFileItemError)
        );

        insertedImportFileItemError = returnedImportFileItemError;
    }

    @Test
    @Transactional
    void createImportFileItemErrorWithExistingId() throws Exception {
        // Create the ImportFileItemError with an existing ID
        importFileItemError.setId(1L);
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restImportFileItemErrorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemErrorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItemError in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkColumnIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFileItemError.setColumnIndex(null);

        // Create the ImportFileItemError, which fails.
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        restImportFileItemErrorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemErrorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFieldNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFileItemError.setFieldName(null);

        // Create the ImportFileItemError, which fails.
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        restImportFileItemErrorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemErrorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkErrorCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFileItemError.setErrorCode(null);

        // Create the ImportFileItemError, which fails.
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        restImportFileItemErrorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemErrorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrors() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList
        restImportFileItemErrorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importFileItemError.getId().intValue())))
            .andExpect(jsonPath("$.[*].columnIndex").value(hasItem(DEFAULT_COLUMN_INDEX)))
            .andExpect(jsonPath("$.[*].fieldName").value(hasItem(DEFAULT_FIELD_NAME)))
            .andExpect(jsonPath("$.[*].errorCode").value(hasItem(DEFAULT_ERROR_CODE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].severity").value(hasItem(DEFAULT_SEVERITY)))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllImportFileItemErrorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(importFileItemErrorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restImportFileItemErrorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(importFileItemErrorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllImportFileItemErrorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(importFileItemErrorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restImportFileItemErrorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(importFileItemErrorRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getImportFileItemError() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get the importFileItemError
        restImportFileItemErrorMockMvc
            .perform(get(ENTITY_API_URL_ID, importFileItemError.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(importFileItemError.getId().intValue()))
            .andExpect(jsonPath("$.columnIndex").value(DEFAULT_COLUMN_INDEX))
            .andExpect(jsonPath("$.fieldName").value(DEFAULT_FIELD_NAME))
            .andExpect(jsonPath("$.errorCode").value(DEFAULT_ERROR_CODE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.severity").value(DEFAULT_SEVERITY))
            .andExpect(jsonPath("$.occurredAt").value(DEFAULT_OCCURRED_AT.toString()));
    }

    @Test
    @Transactional
    void getImportFileItemErrorsByIdFiltering() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        Long id = importFileItemError.getId();

        defaultImportFileItemErrorFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultImportFileItemErrorFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultImportFileItemErrorFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByColumnIndexIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where columnIndex equals to
        defaultImportFileItemErrorFiltering("columnIndex.equals=" + DEFAULT_COLUMN_INDEX, "columnIndex.equals=" + UPDATED_COLUMN_INDEX);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByColumnIndexIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where columnIndex in
        defaultImportFileItemErrorFiltering(
            "columnIndex.in=" + DEFAULT_COLUMN_INDEX + "," + UPDATED_COLUMN_INDEX,
            "columnIndex.in=" + UPDATED_COLUMN_INDEX
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByColumnIndexIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where columnIndex is not null
        defaultImportFileItemErrorFiltering("columnIndex.specified=true", "columnIndex.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByColumnIndexIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where columnIndex is greater than or equal to
        defaultImportFileItemErrorFiltering(
            "columnIndex.greaterThanOrEqual=" + DEFAULT_COLUMN_INDEX,
            "columnIndex.greaterThanOrEqual=" + UPDATED_COLUMN_INDEX
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByColumnIndexIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where columnIndex is less than or equal to
        defaultImportFileItemErrorFiltering(
            "columnIndex.lessThanOrEqual=" + DEFAULT_COLUMN_INDEX,
            "columnIndex.lessThanOrEqual=" + SMALLER_COLUMN_INDEX
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByColumnIndexIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where columnIndex is less than
        defaultImportFileItemErrorFiltering("columnIndex.lessThan=" + UPDATED_COLUMN_INDEX, "columnIndex.lessThan=" + DEFAULT_COLUMN_INDEX);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByColumnIndexIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where columnIndex is greater than
        defaultImportFileItemErrorFiltering(
            "columnIndex.greaterThan=" + SMALLER_COLUMN_INDEX,
            "columnIndex.greaterThan=" + DEFAULT_COLUMN_INDEX
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByFieldNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where fieldName equals to
        defaultImportFileItemErrorFiltering("fieldName.equals=" + DEFAULT_FIELD_NAME, "fieldName.equals=" + UPDATED_FIELD_NAME);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByFieldNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where fieldName in
        defaultImportFileItemErrorFiltering(
            "fieldName.in=" + DEFAULT_FIELD_NAME + "," + UPDATED_FIELD_NAME,
            "fieldName.in=" + UPDATED_FIELD_NAME
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByFieldNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where fieldName is not null
        defaultImportFileItemErrorFiltering("fieldName.specified=true", "fieldName.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByFieldNameContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where fieldName contains
        defaultImportFileItemErrorFiltering("fieldName.contains=" + DEFAULT_FIELD_NAME, "fieldName.contains=" + UPDATED_FIELD_NAME);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByFieldNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where fieldName does not contain
        defaultImportFileItemErrorFiltering(
            "fieldName.doesNotContain=" + UPDATED_FIELD_NAME,
            "fieldName.doesNotContain=" + DEFAULT_FIELD_NAME
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByErrorCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where errorCode equals to
        defaultImportFileItemErrorFiltering("errorCode.equals=" + DEFAULT_ERROR_CODE, "errorCode.equals=" + UPDATED_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByErrorCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where errorCode in
        defaultImportFileItemErrorFiltering(
            "errorCode.in=" + DEFAULT_ERROR_CODE + "," + UPDATED_ERROR_CODE,
            "errorCode.in=" + UPDATED_ERROR_CODE
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByErrorCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where errorCode is not null
        defaultImportFileItemErrorFiltering("errorCode.specified=true", "errorCode.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByErrorCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where errorCode contains
        defaultImportFileItemErrorFiltering("errorCode.contains=" + DEFAULT_ERROR_CODE, "errorCode.contains=" + UPDATED_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByErrorCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where errorCode does not contain
        defaultImportFileItemErrorFiltering(
            "errorCode.doesNotContain=" + UPDATED_ERROR_CODE,
            "errorCode.doesNotContain=" + DEFAULT_ERROR_CODE
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where message equals to
        defaultImportFileItemErrorFiltering("message.equals=" + DEFAULT_MESSAGE, "message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where message in
        defaultImportFileItemErrorFiltering("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE, "message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where message is not null
        defaultImportFileItemErrorFiltering("message.specified=true", "message.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where message contains
        defaultImportFileItemErrorFiltering("message.contains=" + DEFAULT_MESSAGE, "message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where message does not contain
        defaultImportFileItemErrorFiltering("message.doesNotContain=" + UPDATED_MESSAGE, "message.doesNotContain=" + DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsBySeverityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where severity equals to
        defaultImportFileItemErrorFiltering("severity.equals=" + DEFAULT_SEVERITY, "severity.equals=" + UPDATED_SEVERITY);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsBySeverityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where severity in
        defaultImportFileItemErrorFiltering("severity.in=" + DEFAULT_SEVERITY + "," + UPDATED_SEVERITY, "severity.in=" + UPDATED_SEVERITY);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsBySeverityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where severity is not null
        defaultImportFileItemErrorFiltering("severity.specified=true", "severity.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsBySeverityContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where severity contains
        defaultImportFileItemErrorFiltering("severity.contains=" + DEFAULT_SEVERITY, "severity.contains=" + UPDATED_SEVERITY);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsBySeverityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where severity does not contain
        defaultImportFileItemErrorFiltering("severity.doesNotContain=" + UPDATED_SEVERITY, "severity.doesNotContain=" + DEFAULT_SEVERITY);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByOccurredAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where occurredAt equals to
        defaultImportFileItemErrorFiltering("occurredAt.equals=" + DEFAULT_OCCURRED_AT, "occurredAt.equals=" + UPDATED_OCCURRED_AT);
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByOccurredAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where occurredAt in
        defaultImportFileItemErrorFiltering(
            "occurredAt.in=" + DEFAULT_OCCURRED_AT + "," + UPDATED_OCCURRED_AT,
            "occurredAt.in=" + UPDATED_OCCURRED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByOccurredAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        // Get all the importFileItemErrorList where occurredAt is not null
        defaultImportFileItemErrorFiltering("occurredAt.specified=true", "occurredAt.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemErrorsByImportFileItemIsEqualToSomething() throws Exception {
        ImportFileItem importFileItem;
        if (TestUtil.findAll(em, ImportFileItem.class).isEmpty()) {
            importFileItemErrorRepository.saveAndFlush(importFileItemError);
            importFileItem = ImportFileItemResourceIT.createEntity(em);
        } else {
            importFileItem = TestUtil.findAll(em, ImportFileItem.class).get(0);
        }
        em.persist(importFileItem);
        em.flush();
        importFileItemError.setImportFileItem(importFileItem);
        importFileItemErrorRepository.saveAndFlush(importFileItemError);
        Long importFileItemId = importFileItem.getId();
        // Get all the importFileItemErrorList where importFileItem equals to importFileItemId
        defaultImportFileItemErrorShouldBeFound("importFileItemId.equals=" + importFileItemId);

        // Get all the importFileItemErrorList where importFileItem equals to (importFileItemId + 1)
        defaultImportFileItemErrorShouldNotBeFound("importFileItemId.equals=" + (importFileItemId + 1));
    }

    private void defaultImportFileItemErrorFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultImportFileItemErrorShouldBeFound(shouldBeFound);
        defaultImportFileItemErrorShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultImportFileItemErrorShouldBeFound(String filter) throws Exception {
        restImportFileItemErrorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importFileItemError.getId().intValue())))
            .andExpect(jsonPath("$.[*].columnIndex").value(hasItem(DEFAULT_COLUMN_INDEX)))
            .andExpect(jsonPath("$.[*].fieldName").value(hasItem(DEFAULT_FIELD_NAME)))
            .andExpect(jsonPath("$.[*].errorCode").value(hasItem(DEFAULT_ERROR_CODE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].severity").value(hasItem(DEFAULT_SEVERITY)))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())));

        // Check, that the count call also returns 1
        restImportFileItemErrorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultImportFileItemErrorShouldNotBeFound(String filter) throws Exception {
        restImportFileItemErrorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restImportFileItemErrorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingImportFileItemError() throws Exception {
        // Get the importFileItemError
        restImportFileItemErrorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingImportFileItemError() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFileItemError
        ImportFileItemError updatedImportFileItemError = importFileItemErrorRepository.findById(importFileItemError.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedImportFileItemError are not directly saved in db
        em.detach(updatedImportFileItemError);
        updatedImportFileItemError
            .columnIndex(UPDATED_COLUMN_INDEX)
            .fieldName(UPDATED_FIELD_NAME)
            .errorCode(UPDATED_ERROR_CODE)
            .message(UPDATED_MESSAGE)
            .severity(UPDATED_SEVERITY)
            .occurredAt(UPDATED_OCCURRED_AT);
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(updatedImportFileItemError);

        restImportFileItemErrorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importFileItemErrorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileItemErrorDTO))
            )
            .andExpect(status().isOk());

        // Validate the ImportFileItemError in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedImportFileItemErrorToMatchAllProperties(updatedImportFileItemError);
    }

    @Test
    @Transactional
    void putNonExistingImportFileItemError() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItemError.setId(longCount.incrementAndGet());

        // Create the ImportFileItemError
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportFileItemErrorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importFileItemErrorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileItemErrorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItemError in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchImportFileItemError() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItemError.setId(longCount.incrementAndGet());

        // Create the ImportFileItemError
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileItemErrorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileItemErrorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItemError in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamImportFileItemError() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItemError.setId(longCount.incrementAndGet());

        // Create the ImportFileItemError
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileItemErrorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemErrorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportFileItemError in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateImportFileItemErrorWithPatch() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFileItemError using partial update
        ImportFileItemError partialUpdatedImportFileItemError = new ImportFileItemError();
        partialUpdatedImportFileItemError.setId(importFileItemError.getId());

        partialUpdatedImportFileItemError
            .columnIndex(UPDATED_COLUMN_INDEX)
            .fieldName(UPDATED_FIELD_NAME)
            .severity(UPDATED_SEVERITY)
            .occurredAt(UPDATED_OCCURRED_AT);

        restImportFileItemErrorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportFileItemError.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportFileItemError))
            )
            .andExpect(status().isOk());

        // Validate the ImportFileItemError in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportFileItemErrorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedImportFileItemError, importFileItemError),
            getPersistedImportFileItemError(importFileItemError)
        );
    }

    @Test
    @Transactional
    void fullUpdateImportFileItemErrorWithPatch() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFileItemError using partial update
        ImportFileItemError partialUpdatedImportFileItemError = new ImportFileItemError();
        partialUpdatedImportFileItemError.setId(importFileItemError.getId());

        partialUpdatedImportFileItemError
            .columnIndex(UPDATED_COLUMN_INDEX)
            .fieldName(UPDATED_FIELD_NAME)
            .errorCode(UPDATED_ERROR_CODE)
            .message(UPDATED_MESSAGE)
            .severity(UPDATED_SEVERITY)
            .occurredAt(UPDATED_OCCURRED_AT);

        restImportFileItemErrorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportFileItemError.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportFileItemError))
            )
            .andExpect(status().isOk());

        // Validate the ImportFileItemError in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportFileItemErrorUpdatableFieldsEquals(
            partialUpdatedImportFileItemError,
            getPersistedImportFileItemError(partialUpdatedImportFileItemError)
        );
    }

    @Test
    @Transactional
    void patchNonExistingImportFileItemError() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItemError.setId(longCount.incrementAndGet());

        // Create the ImportFileItemError
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportFileItemErrorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, importFileItemErrorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importFileItemErrorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItemError in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchImportFileItemError() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItemError.setId(longCount.incrementAndGet());

        // Create the ImportFileItemError
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileItemErrorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importFileItemErrorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItemError in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamImportFileItemError() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItemError.setId(longCount.incrementAndGet());

        // Create the ImportFileItemError
        ImportFileItemErrorDTO importFileItemErrorDTO = importFileItemErrorMapper.toDto(importFileItemError);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileItemErrorMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(importFileItemErrorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportFileItemError in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteImportFileItemError() throws Exception {
        // Initialize the database
        insertedImportFileItemError = importFileItemErrorRepository.saveAndFlush(importFileItemError);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the importFileItemError
        restImportFileItemErrorMockMvc
            .perform(delete(ENTITY_API_URL_ID, importFileItemError.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return importFileItemErrorRepository.count();
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

    protected ImportFileItemError getPersistedImportFileItemError(ImportFileItemError importFileItemError) {
        return importFileItemErrorRepository.findById(importFileItemError.getId()).orElseThrow();
    }

    protected void assertPersistedImportFileItemErrorToMatchAllProperties(ImportFileItemError expectedImportFileItemError) {
        assertImportFileItemErrorAllPropertiesEquals(
            expectedImportFileItemError,
            getPersistedImportFileItemError(expectedImportFileItemError)
        );
    }

    protected void assertPersistedImportFileItemErrorToMatchUpdatableProperties(ImportFileItemError expectedImportFileItemError) {
        assertImportFileItemErrorAllUpdatablePropertiesEquals(
            expectedImportFileItemError,
            getPersistedImportFileItemError(expectedImportFileItemError)
        );
    }
}
