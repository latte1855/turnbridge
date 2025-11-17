package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.ImportFileItemAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.ImportFile;
import com.asynctide.turnbridge.domain.ImportFileItem;
import com.asynctide.turnbridge.domain.Invoice;
import com.asynctide.turnbridge.domain.enumeration.ImportItemStatus;
import com.asynctide.turnbridge.repository.ImportFileItemRepository;
import com.asynctide.turnbridge.service.ImportFileItemService;
import com.asynctide.turnbridge.service.dto.ImportFileItemDTO;
import com.asynctide.turnbridge.service.mapper.ImportFileItemMapper;
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
 * Integration tests for the {@link ImportFileItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ImportFileItemResourceIT {

    private static final Integer DEFAULT_LINE_INDEX = 1;
    private static final Integer UPDATED_LINE_INDEX = 2;
    private static final Integer SMALLER_LINE_INDEX = 1 - 1;

    private static final String DEFAULT_RAW_DATA = "AAAAAAAAAA";
    private static final String UPDATED_RAW_DATA = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_HASH = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_RAW_HASH = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final String DEFAULT_SOURCE_FAMILY = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_FAMILY = "BBBBBBBBBB";

    private static final String DEFAULT_NORMALIZED_FAMILY = "AAAAAAAAAA";
    private static final String UPDATED_NORMALIZED_FAMILY = "BBBBBBBBBB";

    private static final String DEFAULT_NORMALIZED_JSON = "AAAAAAAAAA";
    private static final String UPDATED_NORMALIZED_JSON = "BBBBBBBBBB";

    private static final ImportItemStatus DEFAULT_STATUS = ImportItemStatus.PENDING;
    private static final ImportItemStatus UPDATED_STATUS = ImportItemStatus.NORMALIZED;

    private static final String DEFAULT_ERROR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/import-file-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ImportFileItemRepository importFileItemRepository;

    @Mock
    private ImportFileItemRepository importFileItemRepositoryMock;

    @Autowired
    private ImportFileItemMapper importFileItemMapper;

    @Mock
    private ImportFileItemService importFileItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImportFileItemMockMvc;

    private ImportFileItem importFileItem;

    private ImportFileItem insertedImportFileItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportFileItem createEntity(EntityManager em) {
        ImportFileItem importFileItem = new ImportFileItem()
            .lineIndex(DEFAULT_LINE_INDEX)
            .rawData(DEFAULT_RAW_DATA)
            .rawHash(DEFAULT_RAW_HASH)
            .sourceFamily(DEFAULT_SOURCE_FAMILY)
            .normalizedFamily(DEFAULT_NORMALIZED_FAMILY)
            .normalizedJson(DEFAULT_NORMALIZED_JSON)
            .status(DEFAULT_STATUS)
            .errorCode(DEFAULT_ERROR_CODE)
            .errorMessage(DEFAULT_ERROR_MESSAGE);
        // Add required entity
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            importFile = ImportFileResourceIT.createEntity();
            em.persist(importFile);
            em.flush();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        importFileItem.setImportFile(importFile);
        return importFileItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportFileItem createUpdatedEntity(EntityManager em) {
        ImportFileItem updatedImportFileItem = new ImportFileItem()
            .lineIndex(UPDATED_LINE_INDEX)
            .rawData(UPDATED_RAW_DATA)
            .rawHash(UPDATED_RAW_HASH)
            .sourceFamily(UPDATED_SOURCE_FAMILY)
            .normalizedFamily(UPDATED_NORMALIZED_FAMILY)
            .normalizedJson(UPDATED_NORMALIZED_JSON)
            .status(UPDATED_STATUS)
            .errorCode(UPDATED_ERROR_CODE)
            .errorMessage(UPDATED_ERROR_MESSAGE);
        // Add required entity
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            importFile = ImportFileResourceIT.createUpdatedEntity();
            em.persist(importFile);
            em.flush();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        updatedImportFileItem.setImportFile(importFile);
        return updatedImportFileItem;
    }

    @BeforeEach
    void initTest() {
        importFileItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedImportFileItem != null) {
            importFileItemRepository.delete(insertedImportFileItem);
            insertedImportFileItem = null;
        }
    }

    @Test
    @Transactional
    void createImportFileItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ImportFileItem
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);
        var returnedImportFileItemDTO = om.readValue(
            restImportFileItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ImportFileItemDTO.class
        );

        // Validate the ImportFileItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedImportFileItem = importFileItemMapper.toEntity(returnedImportFileItemDTO);
        assertImportFileItemUpdatableFieldsEquals(returnedImportFileItem, getPersistedImportFileItem(returnedImportFileItem));

        insertedImportFileItem = returnedImportFileItem;
    }

    @Test
    @Transactional
    void createImportFileItemWithExistingId() throws Exception {
        // Create the ImportFileItem with an existing ID
        importFileItem.setId(1L);
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restImportFileItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFileItem.setLineIndex(null);

        // Create the ImportFileItem, which fails.
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);

        restImportFileItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importFileItem.setStatus(null);

        // Create the ImportFileItem, which fails.
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);

        restImportFileItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllImportFileItems() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList
        restImportFileItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importFileItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineIndex").value(hasItem(DEFAULT_LINE_INDEX)))
            .andExpect(jsonPath("$.[*].rawData").value(hasItem(DEFAULT_RAW_DATA)))
            .andExpect(jsonPath("$.[*].rawHash").value(hasItem(DEFAULT_RAW_HASH)))
            .andExpect(jsonPath("$.[*].sourceFamily").value(hasItem(DEFAULT_SOURCE_FAMILY)))
            .andExpect(jsonPath("$.[*].normalizedFamily").value(hasItem(DEFAULT_NORMALIZED_FAMILY)))
            .andExpect(jsonPath("$.[*].normalizedJson").value(hasItem(DEFAULT_NORMALIZED_JSON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].errorCode").value(hasItem(DEFAULT_ERROR_CODE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllImportFileItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(importFileItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restImportFileItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(importFileItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllImportFileItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(importFileItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restImportFileItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(importFileItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getImportFileItem() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get the importFileItem
        restImportFileItemMockMvc
            .perform(get(ENTITY_API_URL_ID, importFileItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(importFileItem.getId().intValue()))
            .andExpect(jsonPath("$.lineIndex").value(DEFAULT_LINE_INDEX))
            .andExpect(jsonPath("$.rawData").value(DEFAULT_RAW_DATA))
            .andExpect(jsonPath("$.rawHash").value(DEFAULT_RAW_HASH))
            .andExpect(jsonPath("$.sourceFamily").value(DEFAULT_SOURCE_FAMILY))
            .andExpect(jsonPath("$.normalizedFamily").value(DEFAULT_NORMALIZED_FAMILY))
            .andExpect(jsonPath("$.normalizedJson").value(DEFAULT_NORMALIZED_JSON))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.errorCode").value(DEFAULT_ERROR_CODE))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    void getImportFileItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        Long id = importFileItem.getId();

        defaultImportFileItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultImportFileItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultImportFileItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByLineIndexIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where lineIndex equals to
        defaultImportFileItemFiltering("lineIndex.equals=" + DEFAULT_LINE_INDEX, "lineIndex.equals=" + UPDATED_LINE_INDEX);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByLineIndexIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where lineIndex in
        defaultImportFileItemFiltering(
            "lineIndex.in=" + DEFAULT_LINE_INDEX + "," + UPDATED_LINE_INDEX,
            "lineIndex.in=" + UPDATED_LINE_INDEX
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByLineIndexIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where lineIndex is not null
        defaultImportFileItemFiltering("lineIndex.specified=true", "lineIndex.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemsByLineIndexIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where lineIndex is greater than or equal to
        defaultImportFileItemFiltering(
            "lineIndex.greaterThanOrEqual=" + DEFAULT_LINE_INDEX,
            "lineIndex.greaterThanOrEqual=" + UPDATED_LINE_INDEX
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByLineIndexIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where lineIndex is less than or equal to
        defaultImportFileItemFiltering(
            "lineIndex.lessThanOrEqual=" + DEFAULT_LINE_INDEX,
            "lineIndex.lessThanOrEqual=" + SMALLER_LINE_INDEX
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByLineIndexIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where lineIndex is less than
        defaultImportFileItemFiltering("lineIndex.lessThan=" + UPDATED_LINE_INDEX, "lineIndex.lessThan=" + DEFAULT_LINE_INDEX);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByLineIndexIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where lineIndex is greater than
        defaultImportFileItemFiltering("lineIndex.greaterThan=" + SMALLER_LINE_INDEX, "lineIndex.greaterThan=" + DEFAULT_LINE_INDEX);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByRawHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where rawHash equals to
        defaultImportFileItemFiltering("rawHash.equals=" + DEFAULT_RAW_HASH, "rawHash.equals=" + UPDATED_RAW_HASH);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByRawHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where rawHash in
        defaultImportFileItemFiltering("rawHash.in=" + DEFAULT_RAW_HASH + "," + UPDATED_RAW_HASH, "rawHash.in=" + UPDATED_RAW_HASH);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByRawHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where rawHash is not null
        defaultImportFileItemFiltering("rawHash.specified=true", "rawHash.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemsByRawHashContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where rawHash contains
        defaultImportFileItemFiltering("rawHash.contains=" + DEFAULT_RAW_HASH, "rawHash.contains=" + UPDATED_RAW_HASH);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByRawHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where rawHash does not contain
        defaultImportFileItemFiltering("rawHash.doesNotContain=" + UPDATED_RAW_HASH, "rawHash.doesNotContain=" + DEFAULT_RAW_HASH);
    }

    @Test
    @Transactional
    void getAllImportFileItemsBySourceFamilyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where sourceFamily equals to
        defaultImportFileItemFiltering("sourceFamily.equals=" + DEFAULT_SOURCE_FAMILY, "sourceFamily.equals=" + UPDATED_SOURCE_FAMILY);
    }

    @Test
    @Transactional
    void getAllImportFileItemsBySourceFamilyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where sourceFamily in
        defaultImportFileItemFiltering(
            "sourceFamily.in=" + DEFAULT_SOURCE_FAMILY + "," + UPDATED_SOURCE_FAMILY,
            "sourceFamily.in=" + UPDATED_SOURCE_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsBySourceFamilyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where sourceFamily is not null
        defaultImportFileItemFiltering("sourceFamily.specified=true", "sourceFamily.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemsBySourceFamilyContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where sourceFamily contains
        defaultImportFileItemFiltering("sourceFamily.contains=" + DEFAULT_SOURCE_FAMILY, "sourceFamily.contains=" + UPDATED_SOURCE_FAMILY);
    }

    @Test
    @Transactional
    void getAllImportFileItemsBySourceFamilyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where sourceFamily does not contain
        defaultImportFileItemFiltering(
            "sourceFamily.doesNotContain=" + UPDATED_SOURCE_FAMILY,
            "sourceFamily.doesNotContain=" + DEFAULT_SOURCE_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByNormalizedFamilyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where normalizedFamily equals to
        defaultImportFileItemFiltering(
            "normalizedFamily.equals=" + DEFAULT_NORMALIZED_FAMILY,
            "normalizedFamily.equals=" + UPDATED_NORMALIZED_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByNormalizedFamilyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where normalizedFamily in
        defaultImportFileItemFiltering(
            "normalizedFamily.in=" + DEFAULT_NORMALIZED_FAMILY + "," + UPDATED_NORMALIZED_FAMILY,
            "normalizedFamily.in=" + UPDATED_NORMALIZED_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByNormalizedFamilyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where normalizedFamily is not null
        defaultImportFileItemFiltering("normalizedFamily.specified=true", "normalizedFamily.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemsByNormalizedFamilyContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where normalizedFamily contains
        defaultImportFileItemFiltering(
            "normalizedFamily.contains=" + DEFAULT_NORMALIZED_FAMILY,
            "normalizedFamily.contains=" + UPDATED_NORMALIZED_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByNormalizedFamilyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where normalizedFamily does not contain
        defaultImportFileItemFiltering(
            "normalizedFamily.doesNotContain=" + UPDATED_NORMALIZED_FAMILY,
            "normalizedFamily.doesNotContain=" + DEFAULT_NORMALIZED_FAMILY
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where status equals to
        defaultImportFileItemFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where status in
        defaultImportFileItemFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where status is not null
        defaultImportFileItemFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorCode equals to
        defaultImportFileItemFiltering("errorCode.equals=" + DEFAULT_ERROR_CODE, "errorCode.equals=" + UPDATED_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorCode in
        defaultImportFileItemFiltering(
            "errorCode.in=" + DEFAULT_ERROR_CODE + "," + UPDATED_ERROR_CODE,
            "errorCode.in=" + UPDATED_ERROR_CODE
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorCode is not null
        defaultImportFileItemFiltering("errorCode.specified=true", "errorCode.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorCode contains
        defaultImportFileItemFiltering("errorCode.contains=" + DEFAULT_ERROR_CODE, "errorCode.contains=" + UPDATED_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorCode does not contain
        defaultImportFileItemFiltering("errorCode.doesNotContain=" + UPDATED_ERROR_CODE, "errorCode.doesNotContain=" + DEFAULT_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorMessage equals to
        defaultImportFileItemFiltering("errorMessage.equals=" + DEFAULT_ERROR_MESSAGE, "errorMessage.equals=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorMessage in
        defaultImportFileItemFiltering(
            "errorMessage.in=" + DEFAULT_ERROR_MESSAGE + "," + UPDATED_ERROR_MESSAGE,
            "errorMessage.in=" + UPDATED_ERROR_MESSAGE
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorMessage is not null
        defaultImportFileItemFiltering("errorMessage.specified=true", "errorMessage.specified=false");
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorMessage contains
        defaultImportFileItemFiltering("errorMessage.contains=" + DEFAULT_ERROR_MESSAGE, "errorMessage.contains=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllImportFileItemsByErrorMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        // Get all the importFileItemList where errorMessage does not contain
        defaultImportFileItemFiltering(
            "errorMessage.doesNotContain=" + UPDATED_ERROR_MESSAGE,
            "errorMessage.doesNotContain=" + DEFAULT_ERROR_MESSAGE
        );
    }

    @Test
    @Transactional
    void getAllImportFileItemsByImportFileIsEqualToSomething() throws Exception {
        ImportFile importFile;
        if (TestUtil.findAll(em, ImportFile.class).isEmpty()) {
            importFileItemRepository.saveAndFlush(importFileItem);
            importFile = ImportFileResourceIT.createEntity();
        } else {
            importFile = TestUtil.findAll(em, ImportFile.class).get(0);
        }
        em.persist(importFile);
        em.flush();
        importFileItem.setImportFile(importFile);
        importFileItemRepository.saveAndFlush(importFileItem);
        Long importFileId = importFile.getId();
        // Get all the importFileItemList where importFile equals to importFileId
        defaultImportFileItemShouldBeFound("importFileId.equals=" + importFileId);

        // Get all the importFileItemList where importFile equals to (importFileId + 1)
        defaultImportFileItemShouldNotBeFound("importFileId.equals=" + (importFileId + 1));
    }

    @Test
    @Transactional
    void getAllImportFileItemsByInvoiceIsEqualToSomething() throws Exception {
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            importFileItemRepository.saveAndFlush(importFileItem);
            invoice = InvoiceResourceIT.createEntity(em);
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        em.persist(invoice);
        em.flush();
        importFileItem.setInvoice(invoice);
        importFileItemRepository.saveAndFlush(importFileItem);
        Long invoiceId = invoice.getId();
        // Get all the importFileItemList where invoice equals to invoiceId
        defaultImportFileItemShouldBeFound("invoiceId.equals=" + invoiceId);

        // Get all the importFileItemList where invoice equals to (invoiceId + 1)
        defaultImportFileItemShouldNotBeFound("invoiceId.equals=" + (invoiceId + 1));
    }

    private void defaultImportFileItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultImportFileItemShouldBeFound(shouldBeFound);
        defaultImportFileItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultImportFileItemShouldBeFound(String filter) throws Exception {
        restImportFileItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importFileItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineIndex").value(hasItem(DEFAULT_LINE_INDEX)))
            .andExpect(jsonPath("$.[*].rawData").value(hasItem(DEFAULT_RAW_DATA)))
            .andExpect(jsonPath("$.[*].rawHash").value(hasItem(DEFAULT_RAW_HASH)))
            .andExpect(jsonPath("$.[*].sourceFamily").value(hasItem(DEFAULT_SOURCE_FAMILY)))
            .andExpect(jsonPath("$.[*].normalizedFamily").value(hasItem(DEFAULT_NORMALIZED_FAMILY)))
            .andExpect(jsonPath("$.[*].normalizedJson").value(hasItem(DEFAULT_NORMALIZED_JSON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].errorCode").value(hasItem(DEFAULT_ERROR_CODE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)));

        // Check, that the count call also returns 1
        restImportFileItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultImportFileItemShouldNotBeFound(String filter) throws Exception {
        restImportFileItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restImportFileItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingImportFileItem() throws Exception {
        // Get the importFileItem
        restImportFileItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingImportFileItem() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFileItem
        ImportFileItem updatedImportFileItem = importFileItemRepository.findById(importFileItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedImportFileItem are not directly saved in db
        em.detach(updatedImportFileItem);
        updatedImportFileItem
            .lineIndex(UPDATED_LINE_INDEX)
            .rawData(UPDATED_RAW_DATA)
            .rawHash(UPDATED_RAW_HASH)
            .sourceFamily(UPDATED_SOURCE_FAMILY)
            .normalizedFamily(UPDATED_NORMALIZED_FAMILY)
            .normalizedJson(UPDATED_NORMALIZED_JSON)
            .status(UPDATED_STATUS)
            .errorCode(UPDATED_ERROR_CODE)
            .errorMessage(UPDATED_ERROR_MESSAGE);
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(updatedImportFileItem);

        restImportFileItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importFileItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the ImportFileItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedImportFileItemToMatchAllProperties(updatedImportFileItem);
    }

    @Test
    @Transactional
    void putNonExistingImportFileItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItem.setId(longCount.incrementAndGet());

        // Create the ImportFileItem
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportFileItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importFileItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchImportFileItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItem.setId(longCount.incrementAndGet());

        // Create the ImportFileItem
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importFileItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamImportFileItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItem.setId(longCount.incrementAndGet());

        // Create the ImportFileItem
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importFileItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportFileItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateImportFileItemWithPatch() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFileItem using partial update
        ImportFileItem partialUpdatedImportFileItem = new ImportFileItem();
        partialUpdatedImportFileItem.setId(importFileItem.getId());

        partialUpdatedImportFileItem
            .lineIndex(UPDATED_LINE_INDEX)
            .rawData(UPDATED_RAW_DATA)
            .rawHash(UPDATED_RAW_HASH)
            .normalizedFamily(UPDATED_NORMALIZED_FAMILY)
            .normalizedJson(UPDATED_NORMALIZED_JSON)
            .status(UPDATED_STATUS);

        restImportFileItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportFileItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportFileItem))
            )
            .andExpect(status().isOk());

        // Validate the ImportFileItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportFileItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedImportFileItem, importFileItem),
            getPersistedImportFileItem(importFileItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateImportFileItemWithPatch() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importFileItem using partial update
        ImportFileItem partialUpdatedImportFileItem = new ImportFileItem();
        partialUpdatedImportFileItem.setId(importFileItem.getId());

        partialUpdatedImportFileItem
            .lineIndex(UPDATED_LINE_INDEX)
            .rawData(UPDATED_RAW_DATA)
            .rawHash(UPDATED_RAW_HASH)
            .sourceFamily(UPDATED_SOURCE_FAMILY)
            .normalizedFamily(UPDATED_NORMALIZED_FAMILY)
            .normalizedJson(UPDATED_NORMALIZED_JSON)
            .status(UPDATED_STATUS)
            .errorCode(UPDATED_ERROR_CODE)
            .errorMessage(UPDATED_ERROR_MESSAGE);

        restImportFileItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportFileItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportFileItem))
            )
            .andExpect(status().isOk());

        // Validate the ImportFileItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportFileItemUpdatableFieldsEquals(partialUpdatedImportFileItem, getPersistedImportFileItem(partialUpdatedImportFileItem));
    }

    @Test
    @Transactional
    void patchNonExistingImportFileItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItem.setId(longCount.incrementAndGet());

        // Create the ImportFileItem
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportFileItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, importFileItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importFileItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchImportFileItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItem.setId(longCount.incrementAndGet());

        // Create the ImportFileItem
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importFileItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportFileItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamImportFileItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importFileItem.setId(longCount.incrementAndGet());

        // Create the ImportFileItem
        ImportFileItemDTO importFileItemDTO = importFileItemMapper.toDto(importFileItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportFileItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(importFileItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportFileItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteImportFileItem() throws Exception {
        // Initialize the database
        insertedImportFileItem = importFileItemRepository.saveAndFlush(importFileItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the importFileItem
        restImportFileItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, importFileItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return importFileItemRepository.count();
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

    protected ImportFileItem getPersistedImportFileItem(ImportFileItem importFileItem) {
        return importFileItemRepository.findById(importFileItem.getId()).orElseThrow();
    }

    protected void assertPersistedImportFileItemToMatchAllProperties(ImportFileItem expectedImportFileItem) {
        assertImportFileItemAllPropertiesEquals(expectedImportFileItem, getPersistedImportFileItem(expectedImportFileItem));
    }

    protected void assertPersistedImportFileItemToMatchUpdatableProperties(ImportFileItem expectedImportFileItem) {
        assertImportFileItemAllUpdatablePropertiesEquals(expectedImportFileItem, getPersistedImportFileItem(expectedImportFileItem));
    }
}
