package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.ImportFileLogAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileLog;
import com.asynctide.turnbridge.repository.ImportFileLogRepository;
import com.asynctide.turnbridge.service.ImportFileLogService;
import com.asynctide.turnbridge.service.dto.ImportFileLogDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileLogMapper;
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
 * Integration tests for the {@link ImportFileLogResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ImportFileLogResourceIT {

    private static final Integer DEFAULT_LINE_INDEX = 1;
    private static final Integer UPDATED_LINE_INDEX = 2;
    private static final Integer SMALLER_LINE_INDEX = 1 - 1;

    private static final String DEFAULT_FIELD = "AAAAAAAAAA";
    private static final String UPDATED_FIELD = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_LINE = "AAAAAAAAAA";
    private static final String UPDATED_RAW_LINE = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_FAMILY = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_FAMILY = "BBBBBBBBBB";

    private static final String DEFAULT_NORMALIZED_FAMILY = "AAAAAAAAAA";
    private static final String UPDATED_NORMALIZED_FAMILY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/import-file-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ImportFileLogRepository importFileLogRepository;

    @Mock
    private ImportFileLogRepository importFileLogRepositoryMock;

    @Autowired
    private ImportFileLogMapper importFileLogMapper;

    @Mock
    private ImportFileLogService importFileLogServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImportFileLogMockMvc;

    private ImportFileLog importFileLog;

    private ImportFileLog insertedImportFileLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportFileLog createEntity(EntityManager em) {
        ImportFileLog importFileLog = new ImportFileLog()
            .lineIndex(DEFAULT_LINE_INDEX)
            .field(DEFAULT_FIELD)
            .errorCode(DEFAULT_ERROR_CODE)
            .message(DEFAULT_MESSAGE)
            .rawLine(DEFAULT_RAW_LINE)
            .sourceFamily(DEFAULT_SOURCE_FAMILY)
            .normalizedFamily(DEFAULT_NORMALIZED_FAMILY);
        // Add required entity
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            importFile = ImportFileResourceIT.createEntity();
            em.persist(importFile);
            em.flush();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        importFileLog.setImportFile(importFile);
        return importFileLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportFileLog createUpdatedEntity(EntityManager em) {
        ImportFileLog updatedImportFileLog = new ImportFileLog()
            .lineIndex(UPDATED_LINE_INDEX)
            .field(UPDATED_FIELD)
            .errorCode(UPDATED_ERROR_CODE)
            .message(UPDATED_MESSAGE)
            .rawLine(UPDATED_RAW_LINE)
            .sourceFamily(UPDATED_SOURCE_FAMILY)
            .normalizedFamily(UPDATED_NORMALIZED_FAMILY);
        // Add required entity
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            importFile = ImportFileResourceIT.createUpdatedEntity();
            em.persist(importFile);
            em.flush();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        updatedImportFileLog.setImportFile(importFile);
        return updatedImportFileLog;
    }

    @BeforeEach
    void initTest() {
        importFileLog = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedImportFileLog != null) {
            importFileLogRepository.delete(insertedImportFileLog);
            insertedImportFileLog = null;
        }
    }

    @Test
    @Transactional
    void createImportFileLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ImportFileLog
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);
        var returnedImportFileLogDTO = om.readValue(
            restImportFileLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileLogDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ImportFileLogDTO.class
        );

        // Validate the ImportFileLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedImportFileLog = importFileLogMapper.toEntity(returnedImportFileLogDTO);
        assertImportFileLogUpdatableFieldsEquals(returnedImportFileLog, getPersistedImportFileLog(returnedImportFileLog));

        insertedImportFileLog = returnedImportFileLog;
    }

    @Test
    @Transactional
    void createImportFileLogWithExistingId() throws Exception {
        // Create the ImportFileLog with an existing ID
        importFileLog.setId(1L);
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restImportFileLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ImportFileLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFileLog.setLineIndex(null);

        // Create the ImportFileLog, which fails.
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        restImportFileLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkErrorCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFileLog.setErrorCode(null);

        // Create the ImportFileLog, which fails.
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        restImportFileLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllImportFileLogs() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList
        restImportFileLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importFileLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineIndex").value(hasItem(DEFAULT_LINE_INDEX)))
            .andExpect(jsonPath("$.[*].field").value(hasItem(DEFAULT_FIELD)))
            .andExpect(jsonPath("$.[*].errorCode").value(hasItem(DEFAULT_ERROR_CODE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].rawLine").value(hasItem(DEFAULT_RAW_LINE)))
            .andExpect(jsonPath("$.[*].sourceFamily").value(hasItem(DEFAULT_SOURCE_FAMILY)))
            .andExpect(jsonPath("$.[*].normalizedFamily").value(hasItem(DEFAULT_NORMALIZED_FAMILY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllImportFileLogsWithEagerRelationshipsIsEnabled() throws Exception {
        when(importFileLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restImportFileLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(importFileLogServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllImportFileLogsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(importFileLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restImportFileLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(importFileLogRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getImportFileLog() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get the importFileLog
        restImportFileLogMockMvc
            .perform(get(ENTITY_API_URL_ID, importFileLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(importFileLog.getId().intValue()))
            .andExpect(jsonPath("$.lineIndex").value(DEFAULT_LINE_INDEX))
            .andExpect(jsonPath("$.field").value(DEFAULT_FIELD))
            .andExpect(jsonPath("$.errorCode").value(DEFAULT_ERROR_CODE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.rawLine").value(DEFAULT_RAW_LINE))
            .andExpect(jsonPath("$.sourceFamily").value(DEFAULT_SOURCE_FAMILY))
            .andExpect(jsonPath("$.normalizedFamily").value(DEFAULT_NORMALIZED_FAMILY));
    }

    @Test
    @Transactional
    void getImportFileLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        Long id = importFileLog.getId();

        defaultImportFileLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultImportFileLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultImportFileLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLineIndexIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where lineIndex equals to
        defaultImportFileLogFiltering("lineIndex.equals=" + DEFAULT_LINE_INDEX, "lineIndex.equals=" + UPDATED_LINE_INDEX);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLineIndexIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where lineIndex in
        defaultImportFileLogFiltering(
            "lineIndex.in=" + DEFAULT_LINE_INDEX + "," + UPDATED_LINE_INDEX,
            "lineIndex.in=" + UPDATED_LINE_INDEX
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLineIndexIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where lineIndex is not null
        defaultImportFileLogFiltering("lineIndex.specified=true", "lineIndex.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLineIndexIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where lineIndex is greater than or equal to
        defaultImportFileLogFiltering(
            "lineIndex.greaterThanOrEqual=" + DEFAULT_LINE_INDEX,
            "lineIndex.greaterThanOrEqual=" + UPDATED_LINE_INDEX
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLineIndexIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where lineIndex is less than or equal to
        defaultImportFileLogFiltering("lineIndex.lessThanOrEqual=" + DEFAULT_LINE_INDEX, "lineIndex.lessThanOrEqual=" + SMALLER_LINE_INDEX);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLineIndexIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where lineIndex is less than
        defaultImportFileLogFiltering("lineIndex.lessThan=" + UPDATED_LINE_INDEX, "lineIndex.lessThan=" + DEFAULT_LINE_INDEX);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLineIndexIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where lineIndex is greater than
        defaultImportFileLogFiltering("lineIndex.greaterThan=" + SMALLER_LINE_INDEX, "lineIndex.greaterThan=" + DEFAULT_LINE_INDEX);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByFieldIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where field equals to
        defaultImportFileLogFiltering("field.equals=" + DEFAULT_FIELD, "field.equals=" + UPDATED_FIELD);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByFieldIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where field in
        defaultImportFileLogFiltering("field.in=" + DEFAULT_FIELD + "," + UPDATED_FIELD, "field.in=" + UPDATED_FIELD);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByFieldIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where field is not null
        defaultImportFileLogFiltering("field.specified=true", "field.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileLogsByFieldContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where field contains
        defaultImportFileLogFiltering("field.contains=" + DEFAULT_FIELD, "field.contains=" + UPDATED_FIELD);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByFieldNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where field does not contain
        defaultImportFileLogFiltering("field.doesNotContain=" + UPDATED_FIELD, "field.doesNotContain=" + DEFAULT_FIELD);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByErrorCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where errorCode equals to
        defaultImportFileLogFiltering("errorCode.equals=" + DEFAULT_ERROR_CODE, "errorCode.equals=" + UPDATED_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByErrorCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where errorCode in
        defaultImportFileLogFiltering(
            "errorCode.in=" + DEFAULT_ERROR_CODE + "," + UPDATED_ERROR_CODE,
            "errorCode.in=" + UPDATED_ERROR_CODE
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByErrorCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where errorCode is not null
        defaultImportFileLogFiltering("errorCode.specified=true", "errorCode.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileLogsByErrorCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where errorCode contains
        defaultImportFileLogFiltering("errorCode.contains=" + DEFAULT_ERROR_CODE, "errorCode.contains=" + UPDATED_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByErrorCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where errorCode does not contain
        defaultImportFileLogFiltering("errorCode.doesNotContain=" + UPDATED_ERROR_CODE, "errorCode.doesNotContain=" + DEFAULT_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where message equals to
        defaultImportFileLogFiltering("message.equals=" + DEFAULT_MESSAGE, "message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where message in
        defaultImportFileLogFiltering("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE, "message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where message is not null
        defaultImportFileLogFiltering("message.specified=true", "message.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileLogsByMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where message contains
        defaultImportFileLogFiltering("message.contains=" + DEFAULT_MESSAGE, "message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where message does not contain
        defaultImportFileLogFiltering("message.doesNotContain=" + UPDATED_MESSAGE, "message.doesNotContain=" + DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsBySourceFamilyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where sourceFamily equals to
        defaultImportFileLogFiltering("sourceFamily.equals=" + DEFAULT_SOURCE_FAMILY, "sourceFamily.equals=" + UPDATED_SOURCE_FAMILY);
    }

    @Test
    @Transactional
    void getAllImportFileLogsBySourceFamilyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where sourceFamily in
        defaultImportFileLogFiltering(
            "sourceFamily.in=" + DEFAULT_SOURCE_FAMILY + "," + UPDATED_SOURCE_FAMILY,
            "sourceFamily.in=" + UPDATED_SOURCE_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsBySourceFamilyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where sourceFamily is not null
        defaultImportFileLogFiltering("sourceFamily.specified=true", "sourceFamily.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileLogsBySourceFamilyContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where sourceFamily contains
        defaultImportFileLogFiltering("sourceFamily.contains=" + DEFAULT_SOURCE_FAMILY, "sourceFamily.contains=" + UPDATED_SOURCE_FAMILY);
    }

    @Test
    @Transactional
    void getAllImportFileLogsBySourceFamilyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where sourceFamily does not contain
        defaultImportFileLogFiltering(
            "sourceFamily.doesNotContain=" + UPDATED_SOURCE_FAMILY,
            "sourceFamily.doesNotContain=" + DEFAULT_SOURCE_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByNormalizedFamilyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where normalizedFamily equals to
        defaultImportFileLogFiltering(
            "normalizedFamily.equals=" + DEFAULT_NORMALIZED_FAMILY,
            "normalizedFamily.equals=" + UPDATED_NORMALIZED_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByNormalizedFamilyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where normalizedFamily in
        defaultImportFileLogFiltering(
            "normalizedFamily.in=" + DEFAULT_NORMALIZED_FAMILY + "," + UPDATED_NORMALIZED_FAMILY,
            "normalizedFamily.in=" + UPDATED_NORMALIZED_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByNormalizedFamilyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where normalizedFamily is not null
        defaultImportFileLogFiltering("normalizedFamily.specified=true", "normalizedFamily.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileLogsByNormalizedFamilyContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where normalizedFamily contains
        defaultImportFileLogFiltering(
            "normalizedFamily.contains=" + DEFAULT_NORMALIZED_FAMILY,
            "normalizedFamily.contains=" + UPDATED_NORMALIZED_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByNormalizedFamilyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where normalizedFamily does not contain
        defaultImportFileLogFiltering(
            "normalizedFamily.doesNotContain=" + UPDATED_NORMALIZED_FAMILY,
            "normalizedFamily.doesNotContain=" + DEFAULT_NORMALIZED_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByImportFileIsEqualToSomething() throws Exception {
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            importFileLogRepository.saveAndFlush(importFileLog);
            importFile = ImportFileResourceIT.createEntity();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        em.persist(importFile);
        em.flush();
        importFileLog.setImportFile(importFile);
        importFileLogRepository.saveAndFlush(importFileLog);
        Long importFileId = importFile.getId();
        // Get all the importFileLogList where importFile equals to importFileId
        defaultImportFileLogShouldBeFound("importFileId.equals=" + importFileId);

        // Get all the importFileLogList where importFile equals to (importFileId + 1)
        defaultImportFileLogShouldNotBeFound("importFileId.equals=" + (importFileId + 1));
    }

    private void defaultImportFileLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultImportFileLogShouldBeFound(shouldBeFound);
        defaultImportFileLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultImportFileLogShouldBeFound(String filter) throws Exception {
        restImportFileLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importFileLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineIndex").value(hasItem(DEFAULT_LINE_INDEX)))
            .andExpect(jsonPath("$.[*].field").value(hasItem(DEFAULT_FIELD)))
            .andExpect(jsonPath("$.[*].errorCode").value(hasItem(DEFAULT_ERROR_CODE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].rawLine").value(hasItem(DEFAULT_RAW_LINE)))
            .andExpect(jsonPath("$.[*].sourceFamily").value(hasItem(DEFAULT_SOURCE_FAMILY)))
            .andExpect(jsonPath("$.[*].normalizedFamily").value(hasItem(DEFAULT_NORMALIZED_FAMILY)));

        // Check, that the count call also returns 1
        restImportFileLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultImportFileLogShouldNotBeFound(String filter) throws Exception {
        restImportFileLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restImportFileLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingImportFileLog() throws Exception {
        // Get the importFileLog
        restImportFileLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingImportFileLog() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFileLog
        ImportFileLog updatedImportFileLog = importFileLogRepository.findById(importFileLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedImportFileLog are not directly saved in db
        em.detach(updatedImportFileLog);
        updatedImportFileLog
            .lineIndex(UPDATED_LINE_INDEX)
            .field(UPDATED_FIELD)
            .errorCode(UPDATED_ERROR_CODE)
            .message(UPDATED_MESSAGE)
            .rawLine(UPDATED_RAW_LINE)
            .sourceFamily(UPDATED_SOURCE_FAMILY)
            .normalizedFamily(UPDATED_NORMALIZED_FAMILY);
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(updatedImportFileLog);

        restImportFileLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importFileLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the ImportFileLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedImportFileLogToMatchAllProperties(updatedImportFileLog);
    }

    @Test
    @Transactional
    void putNonExistingImportFileLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileLog.setId(longCount.incrementAndGet());

        // Create the ImportFileLog
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportFileLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importFileLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchImportFileLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileLog.setId(longCount.incrementAndGet());

        // Create the ImportFileLog
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamImportFileLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileLog.setId(longCount.incrementAndGet());

        // Create the ImportFileLog
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportFileLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateImportFileLogWithPatch() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFileLog using partial update
        ImportFileLog partialUpdatedImportFileLog = new ImportFileLog();
        partialUpdatedImportFileLog.setId(importFileLog.getId());

        partialUpdatedImportFileLog.field(UPDATED_FIELD).errorCode(UPDATED_ERROR_CODE).normalizedFamily(UPDATED_NORMALIZED_FAMILY);

        restImportFileLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportFileLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportFileLog))
            )
            .andExpect(status().isOk());

        // Validate the ImportFileLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportFileLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedImportFileLog, importFileLog),
            getPersistedImportFileLog(importFileLog)
        );
    }

    @Test
    @Transactional
    void fullUpdateImportFileLogWithPatch() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFileLog using partial update
        ImportFileLog partialUpdatedImportFileLog = new ImportFileLog();
        partialUpdatedImportFileLog.setId(importFileLog.getId());

        partialUpdatedImportFileLog
            .lineIndex(UPDATED_LINE_INDEX)
            .field(UPDATED_FIELD)
            .errorCode(UPDATED_ERROR_CODE)
            .message(UPDATED_MESSAGE)
            .rawLine(UPDATED_RAW_LINE)
            .sourceFamily(UPDATED_SOURCE_FAMILY)
            .normalizedFamily(UPDATED_NORMALIZED_FAMILY);

        restImportFileLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportFileLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportFileLog))
            )
            .andExpect(status().isOk());

        // Validate the ImportFileLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportFileLogUpdatableFieldsEquals(partialUpdatedImportFileLog, getPersistedImportFileLog(partialUpdatedImportFileLog));
    }

    @Test
    @Transactional
    void patchNonExistingImportFileLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileLog.setId(longCount.incrementAndGet());

        // Create the ImportFileLog
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportFileLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, importFileLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importFileLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchImportFileLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileLog.setId(longCount.incrementAndGet());

        // Create the ImportFileLog
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importFileLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamImportFileLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileLog.setId(longCount.incrementAndGet());

        // Create the ImportFileLog
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(importFileLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportFileLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteImportFileLog() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the importFileLog
        restImportFileLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, importFileLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return importFileLogRepository.count();
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

    protected ImportFileLog getPersistedImportFileLog(ImportFileLog importFileLog) {
        return importFileLogRepository.findById(importFileLog.getId()).orElseThrow();
    }

    protected void assertPersistedImportFileLogToMatchAllProperties(ImportFileLog expectedImportFileLog) {
        assertImportFileLogAllPropertiesEquals(expectedImportFileLog, getPersistedImportFileLog(expectedImportFileLog));
    }

    protected void assertPersistedImportFileLogToMatchUpdatableProperties(ImportFileLog expectedImportFileLog) {
        assertImportFileLogAllUpdatablePropertiesEquals(expectedImportFileLog, getPersistedImportFileLog(expectedImportFileLog));
    }
}
