package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.ImportFileAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.domain.enumeration.ImportStatus;
import com.asynctide.turnbridge.domain.enumeration.ImportType;
import com.asynctide.turnbridge.repository.ImportFileRepository;
import com.asynctide.turnbridge.service.ImportFileService;
import com.asynctide.turnbridge.service.dto.ImportFileDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileMapper;
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
 * Integration tests for the {@link ImportFileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ImportFileResourceIT {

    private static final ImportType DEFAULT_IMPORT_TYPE = ImportType.INVOICE;
    private static final ImportType UPDATED_IMPORT_TYPE = ImportType.E0501;

    private static final String DEFAULT_ORIGINAL_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_SHA_256 = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_SHA_256 = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Integer DEFAULT_TOTAL_COUNT = 0;
    private static final Integer UPDATED_TOTAL_COUNT = 1;
    private static final Integer SMALLER_TOTAL_COUNT = 0 - 1;

    private static final Integer DEFAULT_SUCCESS_COUNT = 0;
    private static final Integer UPDATED_SUCCESS_COUNT = 1;
    private static final Integer SMALLER_SUCCESS_COUNT = 0 - 1;

    private static final Integer DEFAULT_ERROR_COUNT = 0;
    private static final Integer UPDATED_ERROR_COUNT = 1;
    private static final Integer SMALLER_ERROR_COUNT = 0 - 1;

    private static final ImportStatus DEFAULT_STATUS = ImportStatus.RECEIVED;
    private static final ImportStatus UPDATED_STATUS = ImportStatus.UPLOADING;

    private static final String DEFAULT_LEGACY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_LEGACY_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/import-files";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ImportFileRepository importFileRepository;

    @Mock
    private ImportFileRepository importFileRepositoryMock;

    @Autowired
    private ImportFileMapper importFileMapper;

    @Mock
    private ImportFileService importFileServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImportFileMockMvc;

    private ImportFile importFile;

    private ImportFile insertedImportFile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportFile createEntity() {
        return new ImportFile()
            .importType(DEFAULT_IMPORT_TYPE)
            .originalFilename(DEFAULT_ORIGINAL_FILENAME)
            .sha256(DEFAULT_SHA_256)
            .totalCount(DEFAULT_TOTAL_COUNT)
            .successCount(DEFAULT_SUCCESS_COUNT)
            .errorCount(DEFAULT_ERROR_COUNT)
            .status(DEFAULT_STATUS)
            .legacyType(DEFAULT_LEGACY_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportFile createUpdatedEntity() {
        return new ImportFile()
            .importType(UPDATED_IMPORT_TYPE)
            .originalFilename(UPDATED_ORIGINAL_FILENAME)
            .sha256(UPDATED_SHA_256)
            .totalCount(UPDATED_TOTAL_COUNT)
            .successCount(UPDATED_SUCCESS_COUNT)
            .errorCount(UPDATED_ERROR_COUNT)
            .status(UPDATED_STATUS)
            .legacyType(UPDATED_LEGACY_TYPE);
    }

    @BeforeEach
    void initTest() {
        importFile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedImportFile != null) {
            importFileRepository.delete(insertedImportFile);
            insertedImportFile = null;
        }
    }

    @Test
    @Transactional
    void createImportFile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ImportFile
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);
        var returnedImportFileDTO = om.readValue(
            restImportFileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ImportFileDTO.class
        );

        // Validate the ImportFile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedImportFile = importFileMapper.toEntity(returnedImportFileDTO);
        assertImportFileUpdatableFieldsEquals(returnedImportFile, getPersistedImportFile(returnedImportFile));

        insertedImportFile = returnedImportFile;
    }

    @Test
    @Transactional
    void createImportFileWithExistingId() throws Exception {
        // Create the ImportFile with an existing ID
        importFile.setId(1L);
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restImportFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ImportFile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkImportTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFile.setImportType(null);

        // Create the ImportFile, which fails.
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        restImportFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOriginalFilenameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFile.setOriginalFilename(null);

        // Create the ImportFile, which fails.
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        restImportFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSha256IsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFile.setSha256(null);

        // Create the ImportFile, which fails.
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        restImportFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFile.setTotalCount(null);

        // Create the ImportFile, which fails.
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        restImportFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFile.setStatus(null);

        // Create the ImportFile, which fails.
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        restImportFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllImportFiles() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList
        restImportFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].importType").value(hasItem(DEFAULT_IMPORT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].originalFilename").value(hasItem(DEFAULT_ORIGINAL_FILENAME)))
            .andExpect(jsonPath("$.[*].sha256").value(hasItem(DEFAULT_SHA_256)))
            .andExpect(jsonPath("$.[*].totalCount").value(hasItem(DEFAULT_TOTAL_COUNT)))
            .andExpect(jsonPath("$.[*].successCount").value(hasItem(DEFAULT_SUCCESS_COUNT)))
            .andExpect(jsonPath("$.[*].errorCount").value(hasItem(DEFAULT_ERROR_COUNT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].legacyType").value(hasItem(DEFAULT_LEGACY_TYPE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllImportFilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(importFileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restImportFileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(importFileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllImportFilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(importFileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restImportFileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(importFileRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getImportFile() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get the importFile
        restImportFileMockMvc
            .perform(get(ENTITY_API_URL_ID, importFile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(importFile.getId().intValue()))
            .andExpect(jsonPath("$.importType").value(DEFAULT_IMPORT_TYPE.toString()))
            .andExpect(jsonPath("$.originalFilename").value(DEFAULT_ORIGINAL_FILENAME))
            .andExpect(jsonPath("$.sha256").value(DEFAULT_SHA_256))
            .andExpect(jsonPath("$.totalCount").value(DEFAULT_TOTAL_COUNT))
            .andExpect(jsonPath("$.successCount").value(DEFAULT_SUCCESS_COUNT))
            .andExpect(jsonPath("$.errorCount").value(DEFAULT_ERROR_COUNT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.legacyType").value(DEFAULT_LEGACY_TYPE));
    }

    @Test
    @Transactional
    void getImportFilesByIdFiltering() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        Long id = importFile.getId();

        defaultImportFileFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultImportFileFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultImportFileFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllImportFilesByImportTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where importType equals to
        defaultImportFileFiltering("importType.equals=" + DEFAULT_IMPORT_TYPE, "importType.equals=" + UPDATED_IMPORT_TYPE);
    }

    @Test
    @Transactional
    void getAllImportFilesByImportTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where importType in
        defaultImportFileFiltering(
            "importType.in=" + DEFAULT_IMPORT_TYPE + "," + UPDATED_IMPORT_TYPE,
            "importType.in=" + UPDATED_IMPORT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByImportTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where importType is not null
        defaultImportFileFiltering("importType.specified=true", "importType.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFilesByOriginalFilenameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where originalFilename equals to
        defaultImportFileFiltering(
            "originalFilename.equals=" + DEFAULT_ORIGINAL_FILENAME,
            "originalFilename.equals=" + UPDATED_ORIGINAL_FILENAME
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByOriginalFilenameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where originalFilename in
        defaultImportFileFiltering(
            "originalFilename.in=" + DEFAULT_ORIGINAL_FILENAME + "," + UPDATED_ORIGINAL_FILENAME,
            "originalFilename.in=" + UPDATED_ORIGINAL_FILENAME
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByOriginalFilenameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where originalFilename is not null
        defaultImportFileFiltering("originalFilename.specified=true", "originalFilename.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFilesByOriginalFilenameContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where originalFilename contains
        defaultImportFileFiltering(
            "originalFilename.contains=" + DEFAULT_ORIGINAL_FILENAME,
            "originalFilename.contains=" + UPDATED_ORIGINAL_FILENAME
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByOriginalFilenameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where originalFilename does not contain
        defaultImportFileFiltering(
            "originalFilename.doesNotContain=" + UPDATED_ORIGINAL_FILENAME,
            "originalFilename.doesNotContain=" + DEFAULT_ORIGINAL_FILENAME
        );
    }

    @Test
    @Transactional
    void getAllImportFilesBySha256IsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where sha256 equals to
        defaultImportFileFiltering("sha256.equals=" + DEFAULT_SHA_256, "sha256.equals=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    void getAllImportFilesBySha256IsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where sha256 in
        defaultImportFileFiltering("sha256.in=" + DEFAULT_SHA_256 + "," + UPDATED_SHA_256, "sha256.in=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    void getAllImportFilesBySha256IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where sha256 is not null
        defaultImportFileFiltering("sha256.specified=true", "sha256.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFilesBySha256ContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where sha256 contains
        defaultImportFileFiltering("sha256.contains=" + DEFAULT_SHA_256, "sha256.contains=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    void getAllImportFilesBySha256NotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where sha256 does not contain
        defaultImportFileFiltering("sha256.doesNotContain=" + UPDATED_SHA_256, "sha256.doesNotContain=" + DEFAULT_SHA_256);
    }

    @Test
    @Transactional
    void getAllImportFilesByTotalCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where totalCount equals to
        defaultImportFileFiltering("totalCount.equals=" + DEFAULT_TOTAL_COUNT, "totalCount.equals=" + UPDATED_TOTAL_COUNT);
    }

    @Test
    @Transactional
    void getAllImportFilesByTotalCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where totalCount in
        defaultImportFileFiltering(
            "totalCount.in=" + DEFAULT_TOTAL_COUNT + "," + UPDATED_TOTAL_COUNT,
            "totalCount.in=" + UPDATED_TOTAL_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByTotalCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where totalCount is not null
        defaultImportFileFiltering("totalCount.specified=true", "totalCount.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFilesByTotalCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where totalCount is greater than or equal to
        defaultImportFileFiltering(
            "totalCount.greaterThanOrEqual=" + DEFAULT_TOTAL_COUNT,
            "totalCount.greaterThanOrEqual=" + UPDATED_TOTAL_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByTotalCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where totalCount is less than or equal to
        defaultImportFileFiltering(
            "totalCount.lessThanOrEqual=" + DEFAULT_TOTAL_COUNT,
            "totalCount.lessThanOrEqual=" + SMALLER_TOTAL_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByTotalCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where totalCount is less than
        defaultImportFileFiltering("totalCount.lessThan=" + UPDATED_TOTAL_COUNT, "totalCount.lessThan=" + DEFAULT_TOTAL_COUNT);
    }

    @Test
    @Transactional
    void getAllImportFilesByTotalCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where totalCount is greater than
        defaultImportFileFiltering("totalCount.greaterThan=" + SMALLER_TOTAL_COUNT, "totalCount.greaterThan=" + DEFAULT_TOTAL_COUNT);
    }

    @Test
    @Transactional
    void getAllImportFilesBySuccessCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where successCount equals to
        defaultImportFileFiltering("successCount.equals=" + DEFAULT_SUCCESS_COUNT, "successCount.equals=" + UPDATED_SUCCESS_COUNT);
    }

    @Test
    @Transactional
    void getAllImportFilesBySuccessCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where successCount in
        defaultImportFileFiltering(
            "successCount.in=" + DEFAULT_SUCCESS_COUNT + "," + UPDATED_SUCCESS_COUNT,
            "successCount.in=" + UPDATED_SUCCESS_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesBySuccessCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where successCount is not null
        defaultImportFileFiltering("successCount.specified=true", "successCount.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFilesBySuccessCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where successCount is greater than or equal to
        defaultImportFileFiltering(
            "successCount.greaterThanOrEqual=" + DEFAULT_SUCCESS_COUNT,
            "successCount.greaterThanOrEqual=" + UPDATED_SUCCESS_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesBySuccessCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where successCount is less than or equal to
        defaultImportFileFiltering(
            "successCount.lessThanOrEqual=" + DEFAULT_SUCCESS_COUNT,
            "successCount.lessThanOrEqual=" + SMALLER_SUCCESS_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesBySuccessCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where successCount is less than
        defaultImportFileFiltering("successCount.lessThan=" + UPDATED_SUCCESS_COUNT, "successCount.lessThan=" + DEFAULT_SUCCESS_COUNT);
    }

    @Test
    @Transactional
    void getAllImportFilesBySuccessCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where successCount is greater than
        defaultImportFileFiltering(
            "successCount.greaterThan=" + SMALLER_SUCCESS_COUNT,
            "successCount.greaterThan=" + DEFAULT_SUCCESS_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByErrorCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where errorCount equals to
        defaultImportFileFiltering("errorCount.equals=" + DEFAULT_ERROR_COUNT, "errorCount.equals=" + UPDATED_ERROR_COUNT);
    }

    @Test
    @Transactional
    void getAllImportFilesByErrorCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where errorCount in
        defaultImportFileFiltering(
            "errorCount.in=" + DEFAULT_ERROR_COUNT + "," + UPDATED_ERROR_COUNT,
            "errorCount.in=" + UPDATED_ERROR_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByErrorCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where errorCount is not null
        defaultImportFileFiltering("errorCount.specified=true", "errorCount.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFilesByErrorCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where errorCount is greater than or equal to
        defaultImportFileFiltering(
            "errorCount.greaterThanOrEqual=" + DEFAULT_ERROR_COUNT,
            "errorCount.greaterThanOrEqual=" + UPDATED_ERROR_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByErrorCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where errorCount is less than or equal to
        defaultImportFileFiltering(
            "errorCount.lessThanOrEqual=" + DEFAULT_ERROR_COUNT,
            "errorCount.lessThanOrEqual=" + SMALLER_ERROR_COUNT
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByErrorCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where errorCount is less than
        defaultImportFileFiltering("errorCount.lessThan=" + UPDATED_ERROR_COUNT, "errorCount.lessThan=" + DEFAULT_ERROR_COUNT);
    }

    @Test
    @Transactional
    void getAllImportFilesByErrorCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where errorCount is greater than
        defaultImportFileFiltering("errorCount.greaterThan=" + SMALLER_ERROR_COUNT, "errorCount.greaterThan=" + DEFAULT_ERROR_COUNT);
    }

    @Test
    @Transactional
    void getAllImportFilesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where status equals to
        defaultImportFileFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllImportFilesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where status in
        defaultImportFileFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllImportFilesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where status is not null
        defaultImportFileFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFilesByLegacyTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where legacyType equals to
        defaultImportFileFiltering("legacyType.equals=" + DEFAULT_LEGACY_TYPE, "legacyType.equals=" + UPDATED_LEGACY_TYPE);
    }

    @Test
    @Transactional
    void getAllImportFilesByLegacyTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where legacyType in
        defaultImportFileFiltering(
            "legacyType.in=" + DEFAULT_LEGACY_TYPE + "," + UPDATED_LEGACY_TYPE,
            "legacyType.in=" + UPDATED_LEGACY_TYPE
        );
    }

    @Test
    @Transactional
    void getAllImportFilesByLegacyTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where legacyType is not null
        defaultImportFileFiltering("legacyType.specified=true", "legacyType.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFilesByLegacyTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where legacyType contains
        defaultImportFileFiltering("legacyType.contains=" + DEFAULT_LEGACY_TYPE, "legacyType.contains=" + UPDATED_LEGACY_TYPE);
    }

    @Test
    @Transactional
    void getAllImportFilesByLegacyTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        // Get all the importFileList where legacyType does not contain
        defaultImportFileFiltering("legacyType.doesNotContain=" + UPDATED_LEGACY_TYPE, "legacyType.doesNotContain=" + DEFAULT_LEGACY_TYPE);
    }

    @Test
    @Transactional
    void getAllImportFilesByTenantIsEqualToSomething() throws Exception {
        Tenant tenant;
        if (TestUtil.findAll(em, Tenant.class).isEmpty()) {
            importFileRepository.saveAndFlush(importFile);
            tenant = TenantResourceIT.createEntity();
        } else {
            tenant = TestUtil.findAll(em, Tenant.class).get(0);
        }
        em.persist(tenant);
        em.flush();
        importFile.setTenant(tenant);
        importFileRepository.saveAndFlush(importFile);
        Long tenantId = tenant.getId();
        // Get all the importFileList where tenant equals to tenantId
        defaultImportFileShouldBeFound("tenantId.equals=" + tenantId);

        // Get all the importFileList where tenant equals to (tenantId + 1)
        defaultImportFileShouldNotBeFound("tenantId.equals=" + (tenantId + 1));
    }

    private void defaultImportFileFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultImportFileShouldBeFound(shouldBeFound);
        defaultImportFileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultImportFileShouldBeFound(String filter) throws Exception {
        restImportFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].importType").value(hasItem(DEFAULT_IMPORT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].originalFilename").value(hasItem(DEFAULT_ORIGINAL_FILENAME)))
            .andExpect(jsonPath("$.[*].sha256").value(hasItem(DEFAULT_SHA_256)))
            .andExpect(jsonPath("$.[*].totalCount").value(hasItem(DEFAULT_TOTAL_COUNT)))
            .andExpect(jsonPath("$.[*].successCount").value(hasItem(DEFAULT_SUCCESS_COUNT)))
            .andExpect(jsonPath("$.[*].errorCount").value(hasItem(DEFAULT_ERROR_COUNT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].legacyType").value(hasItem(DEFAULT_LEGACY_TYPE)));

        // Check, that the count call also returns 1
        restImportFileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultImportFileShouldNotBeFound(String filter) throws Exception {
        restImportFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restImportFileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingImportFile() throws Exception {
        // Get the importFile
        restImportFileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingImportFile() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFile
        ImportFile updatedImportFile = importFileRepository.findById(importFile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedImportFile are not directly saved in db
        em.detach(updatedImportFile);
        updatedImportFile
            .importType(UPDATED_IMPORT_TYPE)
            .originalFilename(UPDATED_ORIGINAL_FILENAME)
            .sha256(UPDATED_SHA_256)
            .totalCount(UPDATED_TOTAL_COUNT)
            .successCount(UPDATED_SUCCESS_COUNT)
            .errorCount(UPDATED_ERROR_COUNT)
            .status(UPDATED_STATUS)
            .legacyType(UPDATED_LEGACY_TYPE);
        ImportFileDTO importFileDTO = importFileMapper.toDto(updatedImportFile);

        restImportFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importFileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileDTO))
            )
            .andExpect(status().isOk());

        // Validate the ImportFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedImportFileToMatchAllProperties(updatedImportFile);
    }

    @Test
    @Transactional
    void putNonExistingImportFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFile.setId(longCount.incrementAndGet());

        // Create the ImportFile
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importFileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchImportFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFile.setId(longCount.incrementAndGet());

        // Create the ImportFile
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamImportFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFile.setId(longCount.incrementAndGet());

        // Create the ImportFile
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateImportFileWithPatch() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFile using partial update
        ImportFile partialUpdatedImportFile = new ImportFile();
        partialUpdatedImportFile.setId(importFile.getId());

        partialUpdatedImportFile.importType(UPDATED_IMPORT_TYPE).sha256(UPDATED_SHA_256).errorCount(UPDATED_ERROR_COUNT);

        restImportFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportFile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportFile))
            )
            .andExpect(status().isOk());

        // Validate the ImportFile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportFileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedImportFile, importFile),
            getPersistedImportFile(importFile)
        );
    }

    @Test
    @Transactional
    void fullUpdateImportFileWithPatch() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFile using partial update
        ImportFile partialUpdatedImportFile = new ImportFile();
        partialUpdatedImportFile.setId(importFile.getId());

        partialUpdatedImportFile
            .importType(UPDATED_IMPORT_TYPE)
            .originalFilename(UPDATED_ORIGINAL_FILENAME)
            .sha256(UPDATED_SHA_256)
            .totalCount(UPDATED_TOTAL_COUNT)
            .successCount(UPDATED_SUCCESS_COUNT)
            .errorCount(UPDATED_ERROR_COUNT)
            .status(UPDATED_STATUS)
            .legacyType(UPDATED_LEGACY_TYPE);

        restImportFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportFile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportFile))
            )
            .andExpect(status().isOk());

        // Validate the ImportFile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportFileUpdatableFieldsEquals(partialUpdatedImportFile, getPersistedImportFile(partialUpdatedImportFile));
    }

    @Test
    @Transactional
    void patchNonExistingImportFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFile.setId(longCount.incrementAndGet());

        // Create the ImportFile
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, importFileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchImportFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFile.setId(longCount.incrementAndGet());

        // Create the ImportFile
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamImportFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFile.setId(longCount.incrementAndGet());

        // Create the ImportFile
        ImportFileDTO importFileDTO = importFileMapper.toDto(importFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(importFileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteImportFile() throws Exception {
        // Initialize the database
        insertedImportFile = importFileRepository.saveAndFlush(importFile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the importFile
        restImportFileMockMvc
            .perform(delete(ENTITY_API_URL_ID, importFile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return importFileRepository.count();
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

    protected ImportFile getPersistedImportFile(ImportFile importFile) {
        return importFileRepository.findById(importFile.getId()).orElseThrow();
    }

    protected void assertPersistedImportFileToMatchAllProperties(ImportFile expectedImportFile) {
        assertImportFileAllPropertiesEquals(expectedImportFile, getPersistedImportFile(expectedImportFile));
    }

    protected void assertPersistedImportFileToMatchUpdatableProperties(ImportFile expectedImportFile) {
        assertImportFileAllUpdatablePropertiesEquals(expectedImportFile, getPersistedImportFile(expectedImportFile));
    }
}
