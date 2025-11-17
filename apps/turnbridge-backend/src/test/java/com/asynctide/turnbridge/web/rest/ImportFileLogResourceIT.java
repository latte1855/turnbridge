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
 * Integration tests for the {@link ImportFileLogResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ImportFileLogResourceIT {

    private static final String DEFAULT_EVENT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LEVEL = "AAAAAAAAAA";
    private static final String UPDATED_LEVEL = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_DETAIL = "AAAAAAAAAA";
    private static final String UPDATED_DETAIL = "BBBBBBBBBB";

    private static final Instant DEFAULT_OCCURRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OCCURRED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

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
            .eventCode(DEFAULT_EVENT_CODE)
            .level(DEFAULT_LEVEL)
            .message(DEFAULT_MESSAGE)
            .detail(DEFAULT_DETAIL)
            .occurredAt(DEFAULT_OCCURRED_AT);
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
            .eventCode(UPDATED_EVENT_CODE)
            .level(UPDATED_LEVEL)
            .message(UPDATED_MESSAGE)
            .detail(UPDATED_DETAIL)
            .occurredAt(UPDATED_OCCURRED_AT);
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
    void checkEventCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFileLog.setEventCode(null);

        // Create the ImportFileLog, which fails.
        ImportFileLogDTO importFileLogDTO = importFileLogMapper.toDto(importFileLog);

        restImportFileLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFileLog.setLevel(null);

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
            .andExpect(jsonPath("$.[*].eventCode").value(hasItem(DEFAULT_EVENT_CODE)))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].detail").value(hasItem(DEFAULT_DETAIL)))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())));
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
            .andExpect(jsonPath("$.eventCode").value(DEFAULT_EVENT_CODE))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.detail").value(DEFAULT_DETAIL))
            .andExpect(jsonPath("$.occurredAt").value(DEFAULT_OCCURRED_AT.toString()));
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
    void getAllImportFileLogsByEventCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where eventCode equals to
        defaultImportFileLogFiltering("eventCode.equals=" + DEFAULT_EVENT_CODE, "eventCode.equals=" + UPDATED_EVENT_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByEventCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where eventCode in
        defaultImportFileLogFiltering(
            "eventCode.in=" + DEFAULT_EVENT_CODE + "," + UPDATED_EVENT_CODE,
            "eventCode.in=" + UPDATED_EVENT_CODE
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByEventCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where eventCode is not null
        defaultImportFileLogFiltering("eventCode.specified=true", "eventCode.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileLogsByEventCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where eventCode contains
        defaultImportFileLogFiltering("eventCode.contains=" + DEFAULT_EVENT_CODE, "eventCode.contains=" + UPDATED_EVENT_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByEventCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where eventCode does not contain
        defaultImportFileLogFiltering("eventCode.doesNotContain=" + UPDATED_EVENT_CODE, "eventCode.doesNotContain=" + DEFAULT_EVENT_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where level equals to
        defaultImportFileLogFiltering("level.equals=" + DEFAULT_LEVEL, "level.equals=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLevelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where level in
        defaultImportFileLogFiltering("level.in=" + DEFAULT_LEVEL + "," + UPDATED_LEVEL, "level.in=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where level is not null
        defaultImportFileLogFiltering("level.specified=true", "level.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLevelContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where level contains
        defaultImportFileLogFiltering("level.contains=" + DEFAULT_LEVEL, "level.contains=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByLevelNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where level does not contain
        defaultImportFileLogFiltering("level.doesNotContain=" + UPDATED_LEVEL, "level.doesNotContain=" + DEFAULT_LEVEL);
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
    void getAllImportFileLogsByOccurredAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where occurredAt equals to
        defaultImportFileLogFiltering("occurredAt.equals=" + DEFAULT_OCCURRED_AT, "occurredAt.equals=" + UPDATED_OCCURRED_AT);
    }

    @Test
    @Transactional
    void getAllImportFileLogsByOccurredAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where occurredAt in
        defaultImportFileLogFiltering(
            "occurredAt.in=" + DEFAULT_OCCURRED_AT + "," + UPDATED_OCCURRED_AT,
            "occurredAt.in=" + UPDATED_OCCURRED_AT
        );
    }

    @Test
    @Transactional
    void getAllImportFileLogsByOccurredAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileLog = importFileLogRepository.saveAndFlush(importFileLog);

        // Get all the importFileLogList where occurredAt is not null
        defaultImportFileLogFiltering("occurredAt.specified=true", "occurredAt.specified=false");
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
            .andExpect(jsonPath("$.[*].eventCode").value(hasItem(DEFAULT_EVENT_CODE)))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].detail").value(hasItem(DEFAULT_DETAIL)))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())));

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
            .eventCode(UPDATED_EVENT_CODE)
            .level(UPDATED_LEVEL)
            .message(UPDATED_MESSAGE)
            .detail(UPDATED_DETAIL)
            .occurredAt(UPDATED_OCCURRED_AT);
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

        partialUpdatedImportFileLog.level(UPDATED_LEVEL).message(UPDATED_MESSAGE).occurredAt(UPDATED_OCCURRED_AT);

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
            .eventCode(UPDATED_EVENT_CODE)
            .level(UPDATED_LEVEL)
            .message(UPDATED_MESSAGE)
            .detail(UPDATED_DETAIL)
            .occurredAt(UPDATED_OCCURRED_AT);

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
