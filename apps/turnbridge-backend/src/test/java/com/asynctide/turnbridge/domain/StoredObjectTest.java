package com.asynctide.turnbridge.domain;

import static com.asynctide.turnbridge.domain.StoredObjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StoredObjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StoredObject.class);
        StoredObject storedObject1 = getStoredObjectSample1();
        StoredObject storedObject2 = new StoredObject();
        assertThat(storedObject1).isNotEqualTo(storedObject2);

        storedObject2.setId(storedObject1.getId());
        assertThat(storedObject1).isEqualTo(storedObject2);

        storedObject2 = getStoredObjectSample2();
        assertThat(storedObject1).isNotEqualTo(storedObject2);
    }
}
