package com.asynctide.turnbridge.web.rest;

import static com.asynctide.turnbridge.domain.StoredObjectAsserts.*;
import static com.asynctide.turnbridge.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asynctide.turnbridge.IntegrationTest;
import com.asynctide.turnbridge.domain.StoredObject;
import com.asynctide.turnbridge.domain.enumeration.StoragePurpose;
import com.asynctide.turnbridge.repository.StoredObjectRepository;
import com.asynctide.turnbridge.service.dto.StoredObjectDTO;
import com.asynctide.turnbridge.service.mapper.StoredObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StoredObjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StoredObjectResourceIT {

    private static final String DEFAULT_BUCKET = "AAAAAAAAAA";
    private static final String UPDATED_BUCKET = "BBBBBBBBBB";

    private static final String DEFAULT_OBJECT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_OBJECT_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_MEDIA_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MEDIA_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_CONTENT_LENGTH = 0L;
    private static final Long UPDATED_CONTENT_LENGTH = 1L;
    private static final Long SMALLER_CONTENT_LENGTH = 0L - 1L;

    private static final String DEFAULT_SHA_256 = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_SHA_256 = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final StoragePurpose DEFAULT_PURPOSE = StoragePurpose.UPLOAD_ORIGINAL;
    private static final StoragePurpose UPDATED_PURPOSE = StoragePurpose.RESULT_CSV;

    private static final String DEFAULT_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_STORAGE_CLASS = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_CLASS = "BBBBBBBBBB";

    private static final String DEFAULT_ENCRYPTION = "AAAAAAAAAA";
    private static final String UPDATED_ENCRYPTION = "BBBBBBBBBB";

    private static final String DEFAULT_METADATA = "AAAAAAAAAA";
    private static final String UPDATED_METADATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stored-objects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StoredObjectRepository storedObjectRepository;

    @Autowired
    private StoredObjectMapper storedObjectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStoredObjectMockMvc;

    private StoredObject storedObject;

    private StoredObject insertedStoredObject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StoredObject createEntity() {
        return new StoredObject()
            .bucket(DEFAULT_BUCKET)
            .objectKey(DEFAULT_OBJECT_KEY)
            .mediaType(DEFAULT_MEDIA_TYPE)
            .contentLength(DEFAULT_CONTENT_LENGTH)
            .sha256(DEFAULT_SHA_256)
            .purpose(DEFAULT_PURPOSE)
            .filename(DEFAULT_FILENAME)
            .storageClass(DEFAULT_STORAGE_CLASS)
            .encryption(DEFAULT_ENCRYPTION)
            .metadata(DEFAULT_METADATA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StoredObject createUpdatedEntity() {
        return new StoredObject()
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .mediaType(UPDATED_MEDIA_TYPE)
            .contentLength(UPDATED_CONTENT_LENGTH)
            .sha256(UPDATED_SHA_256)
            .purpose(UPDATED_PURPOSE)
            .filename(UPDATED_FILENAME)
            .storageClass(UPDATED_STORAGE_CLASS)
            .encryption(UPDATED_ENCRYPTION)
            .metadata(UPDATED_METADATA);
    }

    @BeforeEach
    void initTest() {
        storedObject = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStoredObject != null) {
            storedObjectRepository.delete(insertedStoredObject);
            insertedStoredObject = null;
        }
    }

    @Test
    @Transactional
    void createStoredObject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StoredObject
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);
        var returnedStoredObjectDTO = om.readValue(
            restStoredObjectMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storedObjectDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StoredObjectDTO.class
        );

        // Validate the StoredObject in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStoredObject = storedObjectMapper.toEntity(returnedStoredObjectDTO);
        assertStoredObjectUpdatableFieldsEquals(returnedStoredObject, getPersistedStoredObject(returnedStoredObject));

        insertedStoredObject = returnedStoredObject;
    }

    @Test
    @Transactional
    void createStoredObjectWithExistingId() throws Exception {
        // Create the StoredObject with an existing ID
        storedObject.setId(1L);
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStoredObjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storedObjectDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StoredObject in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBucketIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        storedObject.setBucket(null);

        // Create the StoredObject, which fails.
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        restStoredObjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storedObjectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkObjectKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        storedObject.setObjectKey(null);

        // Create the StoredObject, which fails.
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        restStoredObjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storedObjectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMediaTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        storedObject.setMediaType(null);

        // Create the StoredObject, which fails.
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        restStoredObjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storedObjectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContentLengthIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        storedObject.setContentLength(null);

        // Create the StoredObject, which fails.
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        restStoredObjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storedObjectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSha256IsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        storedObject.setSha256(null);

        // Create the StoredObject, which fails.
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        restStoredObjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storedObjectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPurposeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        storedObject.setPurpose(null);

        // Create the StoredObject, which fails.
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        restStoredObjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storedObjectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStoredObjects() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList
        restStoredObjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storedObject.getId().intValue())))
            .andExpect(jsonPath("$.[*].bucket").value(hasItem(DEFAULT_BUCKET)))
            .andExpect(jsonPath("$.[*].objectKey").value(hasItem(DEFAULT_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].mediaType").value(hasItem(DEFAULT_MEDIA_TYPE)))
            .andExpect(jsonPath("$.[*].contentLength").value(hasItem(DEFAULT_CONTENT_LENGTH.intValue())))
            .andExpect(jsonPath("$.[*].sha256").value(hasItem(DEFAULT_SHA_256)))
            .andExpect(jsonPath("$.[*].purpose").value(hasItem(DEFAULT_PURPOSE.toString())))
            .andExpect(jsonPath("$.[*].filename").value(hasItem(DEFAULT_FILENAME)))
            .andExpect(jsonPath("$.[*].storageClass").value(hasItem(DEFAULT_STORAGE_CLASS)))
            .andExpect(jsonPath("$.[*].encryption").value(hasItem(DEFAULT_ENCRYPTION)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)));
    }

    @Test
    @Transactional
    void getStoredObject() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get the storedObject
        restStoredObjectMockMvc
            .perform(get(ENTITY_API_URL_ID, storedObject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(storedObject.getId().intValue()))
            .andExpect(jsonPath("$.bucket").value(DEFAULT_BUCKET))
            .andExpect(jsonPath("$.objectKey").value(DEFAULT_OBJECT_KEY))
            .andExpect(jsonPath("$.mediaType").value(DEFAULT_MEDIA_TYPE))
            .andExpect(jsonPath("$.contentLength").value(DEFAULT_CONTENT_LENGTH.intValue()))
            .andExpect(jsonPath("$.sha256").value(DEFAULT_SHA_256))
            .andExpect(jsonPath("$.purpose").value(DEFAULT_PURPOSE.toString()))
            .andExpect(jsonPath("$.filename").value(DEFAULT_FILENAME))
            .andExpect(jsonPath("$.storageClass").value(DEFAULT_STORAGE_CLASS))
            .andExpect(jsonPath("$.encryption").value(DEFAULT_ENCRYPTION))
            .andExpect(jsonPath("$.metadata").value(DEFAULT_METADATA));
    }

    @Test
    @Transactional
    void getStoredObjectsByIdFiltering() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        Long id = storedObject.getId();

        defaultStoredObjectFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStoredObjectFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStoredObjectFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByBucketIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where bucket equals to
        defaultStoredObjectFiltering("bucket.equals=" + DEFAULT_BUCKET, "bucket.equals=" + UPDATED_BUCKET);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByBucketIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where bucket in
        defaultStoredObjectFiltering("bucket.in=" + DEFAULT_BUCKET + "," + UPDATED_BUCKET, "bucket.in=" + UPDATED_BUCKET);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByBucketIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where bucket is not null
        defaultStoredObjectFiltering("bucket.specified=true", "bucket.specified=false");
    }

    @Test
    @Transactional
    void getAllStoredObjectsByBucketContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where bucket contains
        defaultStoredObjectFiltering("bucket.contains=" + DEFAULT_BUCKET, "bucket.contains=" + UPDATED_BUCKET);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByBucketNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where bucket does not contain
        defaultStoredObjectFiltering("bucket.doesNotContain=" + UPDATED_BUCKET, "bucket.doesNotContain=" + DEFAULT_BUCKET);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByObjectKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where objectKey equals to
        defaultStoredObjectFiltering("objectKey.equals=" + DEFAULT_OBJECT_KEY, "objectKey.equals=" + UPDATED_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByObjectKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where objectKey in
        defaultStoredObjectFiltering("objectKey.in=" + DEFAULT_OBJECT_KEY + "," + UPDATED_OBJECT_KEY, "objectKey.in=" + UPDATED_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByObjectKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where objectKey is not null
        defaultStoredObjectFiltering("objectKey.specified=true", "objectKey.specified=false");
    }

    @Test
    @Transactional
    void getAllStoredObjectsByObjectKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where objectKey contains
        defaultStoredObjectFiltering("objectKey.contains=" + DEFAULT_OBJECT_KEY, "objectKey.contains=" + UPDATED_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByObjectKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where objectKey does not contain
        defaultStoredObjectFiltering("objectKey.doesNotContain=" + UPDATED_OBJECT_KEY, "objectKey.doesNotContain=" + DEFAULT_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByMediaTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where mediaType equals to
        defaultStoredObjectFiltering("mediaType.equals=" + DEFAULT_MEDIA_TYPE, "mediaType.equals=" + UPDATED_MEDIA_TYPE);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByMediaTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where mediaType in
        defaultStoredObjectFiltering("mediaType.in=" + DEFAULT_MEDIA_TYPE + "," + UPDATED_MEDIA_TYPE, "mediaType.in=" + UPDATED_MEDIA_TYPE);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByMediaTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where mediaType is not null
        defaultStoredObjectFiltering("mediaType.specified=true", "mediaType.specified=false");
    }

    @Test
    @Transactional
    void getAllStoredObjectsByMediaTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where mediaType contains
        defaultStoredObjectFiltering("mediaType.contains=" + DEFAULT_MEDIA_TYPE, "mediaType.contains=" + UPDATED_MEDIA_TYPE);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByMediaTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where mediaType does not contain
        defaultStoredObjectFiltering("mediaType.doesNotContain=" + UPDATED_MEDIA_TYPE, "mediaType.doesNotContain=" + DEFAULT_MEDIA_TYPE);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByContentLengthIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where contentLength equals to
        defaultStoredObjectFiltering("contentLength.equals=" + DEFAULT_CONTENT_LENGTH, "contentLength.equals=" + UPDATED_CONTENT_LENGTH);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByContentLengthIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where contentLength in
        defaultStoredObjectFiltering(
            "contentLength.in=" + DEFAULT_CONTENT_LENGTH + "," + UPDATED_CONTENT_LENGTH,
            "contentLength.in=" + UPDATED_CONTENT_LENGTH
        );
    }

    @Test
    @Transactional
    void getAllStoredObjectsByContentLengthIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where contentLength is not null
        defaultStoredObjectFiltering("contentLength.specified=true", "contentLength.specified=false");
    }

    @Test
    @Transactional
    void getAllStoredObjectsByContentLengthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where contentLength is greater than or equal to
        defaultStoredObjectFiltering(
            "contentLength.greaterThanOrEqual=" + DEFAULT_CONTENT_LENGTH,
            "contentLength.greaterThanOrEqual=" + UPDATED_CONTENT_LENGTH
        );
    }

    @Test
    @Transactional
    void getAllStoredObjectsByContentLengthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where contentLength is less than or equal to
        defaultStoredObjectFiltering(
            "contentLength.lessThanOrEqual=" + DEFAULT_CONTENT_LENGTH,
            "contentLength.lessThanOrEqual=" + SMALLER_CONTENT_LENGTH
        );
    }

    @Test
    @Transactional
    void getAllStoredObjectsByContentLengthIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where contentLength is less than
        defaultStoredObjectFiltering(
            "contentLength.lessThan=" + UPDATED_CONTENT_LENGTH,
            "contentLength.lessThan=" + DEFAULT_CONTENT_LENGTH
        );
    }

    @Test
    @Transactional
    void getAllStoredObjectsByContentLengthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where contentLength is greater than
        defaultStoredObjectFiltering(
            "contentLength.greaterThan=" + SMALLER_CONTENT_LENGTH,
            "contentLength.greaterThan=" + DEFAULT_CONTENT_LENGTH
        );
    }

    @Test
    @Transactional
    void getAllStoredObjectsBySha256IsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where sha256 equals to
        defaultStoredObjectFiltering("sha256.equals=" + DEFAULT_SHA_256, "sha256.equals=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    void getAllStoredObjectsBySha256IsInShouldWork() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where sha256 in
        defaultStoredObjectFiltering("sha256.in=" + DEFAULT_SHA_256 + "," + UPDATED_SHA_256, "sha256.in=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    void getAllStoredObjectsBySha256IsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where sha256 is not null
        defaultStoredObjectFiltering("sha256.specified=true", "sha256.specified=false");
    }

    @Test
    @Transactional
    void getAllStoredObjectsBySha256ContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where sha256 contains
        defaultStoredObjectFiltering("sha256.contains=" + DEFAULT_SHA_256, "sha256.contains=" + UPDATED_SHA_256);
    }

    @Test
    @Transactional
    void getAllStoredObjectsBySha256NotContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where sha256 does not contain
        defaultStoredObjectFiltering("sha256.doesNotContain=" + UPDATED_SHA_256, "sha256.doesNotContain=" + DEFAULT_SHA_256);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByPurposeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where purpose equals to
        defaultStoredObjectFiltering("purpose.equals=" + DEFAULT_PURPOSE, "purpose.equals=" + UPDATED_PURPOSE);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByPurposeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where purpose in
        defaultStoredObjectFiltering("purpose.in=" + DEFAULT_PURPOSE + "," + UPDATED_PURPOSE, "purpose.in=" + UPDATED_PURPOSE);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByPurposeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where purpose is not null
        defaultStoredObjectFiltering("purpose.specified=true", "purpose.specified=false");
    }

    @Test
    @Transactional
    void getAllStoredObjectsByFilenameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where filename equals to
        defaultStoredObjectFiltering("filename.equals=" + DEFAULT_FILENAME, "filename.equals=" + UPDATED_FILENAME);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByFilenameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where filename in
        defaultStoredObjectFiltering("filename.in=" + DEFAULT_FILENAME + "," + UPDATED_FILENAME, "filename.in=" + UPDATED_FILENAME);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByFilenameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where filename is not null
        defaultStoredObjectFiltering("filename.specified=true", "filename.specified=false");
    }

    @Test
    @Transactional
    void getAllStoredObjectsByFilenameContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where filename contains
        defaultStoredObjectFiltering("filename.contains=" + DEFAULT_FILENAME, "filename.contains=" + UPDATED_FILENAME);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByFilenameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where filename does not contain
        defaultStoredObjectFiltering("filename.doesNotContain=" + UPDATED_FILENAME, "filename.doesNotContain=" + DEFAULT_FILENAME);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByStorageClassIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where storageClass equals to
        defaultStoredObjectFiltering("storageClass.equals=" + DEFAULT_STORAGE_CLASS, "storageClass.equals=" + UPDATED_STORAGE_CLASS);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByStorageClassIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where storageClass in
        defaultStoredObjectFiltering(
            "storageClass.in=" + DEFAULT_STORAGE_CLASS + "," + UPDATED_STORAGE_CLASS,
            "storageClass.in=" + UPDATED_STORAGE_CLASS
        );
    }

    @Test
    @Transactional
    void getAllStoredObjectsByStorageClassIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where storageClass is not null
        defaultStoredObjectFiltering("storageClass.specified=true", "storageClass.specified=false");
    }

    @Test
    @Transactional
    void getAllStoredObjectsByStorageClassContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where storageClass contains
        defaultStoredObjectFiltering("storageClass.contains=" + DEFAULT_STORAGE_CLASS, "storageClass.contains=" + UPDATED_STORAGE_CLASS);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByStorageClassNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where storageClass does not contain
        defaultStoredObjectFiltering(
            "storageClass.doesNotContain=" + UPDATED_STORAGE_CLASS,
            "storageClass.doesNotContain=" + DEFAULT_STORAGE_CLASS
        );
    }

    @Test
    @Transactional
    void getAllStoredObjectsByEncryptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where encryption equals to
        defaultStoredObjectFiltering("encryption.equals=" + DEFAULT_ENCRYPTION, "encryption.equals=" + UPDATED_ENCRYPTION);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByEncryptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where encryption in
        defaultStoredObjectFiltering(
            "encryption.in=" + DEFAULT_ENCRYPTION + "," + UPDATED_ENCRYPTION,
            "encryption.in=" + UPDATED_ENCRYPTION
        );
    }

    @Test
    @Transactional
    void getAllStoredObjectsByEncryptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where encryption is not null
        defaultStoredObjectFiltering("encryption.specified=true", "encryption.specified=false");
    }

    @Test
    @Transactional
    void getAllStoredObjectsByEncryptionContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where encryption contains
        defaultStoredObjectFiltering("encryption.contains=" + DEFAULT_ENCRYPTION, "encryption.contains=" + UPDATED_ENCRYPTION);
    }

    @Test
    @Transactional
    void getAllStoredObjectsByEncryptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        // Get all the storedObjectList where encryption does not contain
        defaultStoredObjectFiltering("encryption.doesNotContain=" + UPDATED_ENCRYPTION, "encryption.doesNotContain=" + DEFAULT_ENCRYPTION);
    }

    private void defaultStoredObjectFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStoredObjectShouldBeFound(shouldBeFound);
        defaultStoredObjectShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStoredObjectShouldBeFound(String filter) throws Exception {
        restStoredObjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storedObject.getId().intValue())))
            .andExpect(jsonPath("$.[*].bucket").value(hasItem(DEFAULT_BUCKET)))
            .andExpect(jsonPath("$.[*].objectKey").value(hasItem(DEFAULT_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].mediaType").value(hasItem(DEFAULT_MEDIA_TYPE)))
            .andExpect(jsonPath("$.[*].contentLength").value(hasItem(DEFAULT_CONTENT_LENGTH.intValue())))
            .andExpect(jsonPath("$.[*].sha256").value(hasItem(DEFAULT_SHA_256)))
            .andExpect(jsonPath("$.[*].purpose").value(hasItem(DEFAULT_PURPOSE.toString())))
            .andExpect(jsonPath("$.[*].filename").value(hasItem(DEFAULT_FILENAME)))
            .andExpect(jsonPath("$.[*].storageClass").value(hasItem(DEFAULT_STORAGE_CLASS)))
            .andExpect(jsonPath("$.[*].encryption").value(hasItem(DEFAULT_ENCRYPTION)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)));

        // Check, that the count call also returns 1
        restStoredObjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStoredObjectShouldNotBeFound(String filter) throws Exception {
        restStoredObjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStoredObjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStoredObject() throws Exception {
        // Get the storedObject
        restStoredObjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStoredObject() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the storedObject
        StoredObject updatedStoredObject = storedObjectRepository.findById(storedObject.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStoredObject are not directly saved in db
        em.detach(updatedStoredObject);
        updatedStoredObject
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .mediaType(UPDATED_MEDIA_TYPE)
            .contentLength(UPDATED_CONTENT_LENGTH)
            .sha256(UPDATED_SHA_256)
            .purpose(UPDATED_PURPOSE)
            .filename(UPDATED_FILENAME)
            .storageClass(UPDATED_STORAGE_CLASS)
            .encryption(UPDATED_ENCRYPTION)
            .metadata(UPDATED_METADATA);
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(updatedStoredObject);

        restStoredObjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storedObjectDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storedObjectDTO))
            )
            .andExpect(status().isOk());

        // Validate the StoredObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStoredObjectToMatchAllProperties(updatedStoredObject);
    }

    @Test
    @Transactional
    void putNonExistingStoredObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storedObject.setId(longCount.incrementAndGet());

        // Create the StoredObject
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoredObjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storedObjectDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storedObjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoredObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStoredObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storedObject.setId(longCount.incrementAndGet());

        // Create the StoredObject
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoredObjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(storedObjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoredObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStoredObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storedObject.setId(longCount.incrementAndGet());

        // Create the StoredObject
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoredObjectMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(storedObjectDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StoredObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStoredObjectWithPatch() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the storedObject using partial update
        StoredObject partialUpdatedStoredObject = new StoredObject();
        partialUpdatedStoredObject.setId(storedObject.getId());

        partialUpdatedStoredObject
            .bucket(UPDATED_BUCKET)
            .purpose(UPDATED_PURPOSE)
            .encryption(UPDATED_ENCRYPTION)
            .metadata(UPDATED_METADATA);

        restStoredObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStoredObject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStoredObject))
            )
            .andExpect(status().isOk());

        // Validate the StoredObject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStoredObjectUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStoredObject, storedObject),
            getPersistedStoredObject(storedObject)
        );
    }

    @Test
    @Transactional
    void fullUpdateStoredObjectWithPatch() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the storedObject using partial update
        StoredObject partialUpdatedStoredObject = new StoredObject();
        partialUpdatedStoredObject.setId(storedObject.getId());

        partialUpdatedStoredObject
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .mediaType(UPDATED_MEDIA_TYPE)
            .contentLength(UPDATED_CONTENT_LENGTH)
            .sha256(UPDATED_SHA_256)
            .purpose(UPDATED_PURPOSE)
            .filename(UPDATED_FILENAME)
            .storageClass(UPDATED_STORAGE_CLASS)
            .encryption(UPDATED_ENCRYPTION)
            .metadata(UPDATED_METADATA);

        restStoredObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStoredObject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStoredObject))
            )
            .andExpect(status().isOk());

        // Validate the StoredObject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStoredObjectUpdatableFieldsEquals(partialUpdatedStoredObject, getPersistedStoredObject(partialUpdatedStoredObject));
    }

    @Test
    @Transactional
    void patchNonExistingStoredObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storedObject.setId(longCount.incrementAndGet());

        // Create the StoredObject
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoredObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, storedObjectDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(storedObjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoredObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStoredObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storedObject.setId(longCount.incrementAndGet());

        // Create the StoredObject
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoredObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(storedObjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StoredObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStoredObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        storedObject.setId(longCount.incrementAndGet());

        // Create the StoredObject
        StoredObjectDTO storedObjectDTO = storedObjectMapper.toDto(storedObject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoredObjectMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(storedObjectDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StoredObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStoredObject() throws Exception {
        // Initialize the database
        insertedStoredObject = storedObjectRepository.saveAndFlush(storedObject);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the storedObject
        restStoredObjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, storedObject.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return storedObjectRepository.count();
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

    protected StoredObject getPersistedStoredObject(StoredObject storedObject) {
        return storedObjectRepository.findById(storedObject.getId()).orElseThrow();
    }

    protected void assertPersistedStoredObjectToMatchAllProperties(StoredObject expectedStoredObject) {
        assertStoredObjectAllPropertiesEquals(expectedStoredObject, getPersistedStoredObject(expectedStoredObject));
    }

    protected void assertPersistedStoredObjectToMatchUpdatableProperties(StoredObject expectedStoredObject) {
        assertStoredObjectAllUpdatablePropertiesEquals(expectedStoredObject, getPersistedStoredObject(expectedStoredObject));
    }
}
