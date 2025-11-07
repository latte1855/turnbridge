package com.asynctide.turnbridge.config;

import com.asynctide.turnbridge.storage.StorageProvider;
import com.asynctide.turnbridge.storage.local.LocalFsStorageProvider;
import com.asynctide.turnbridge.storage.minio.MinioStorageProvider;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

/**
 * 儲存體自動組態。
 * <p>turnbridge.storage.type = local|minio</p>
 */
@AutoConfiguration
public class StorageAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "turnbridge.storage")
    public StorageProps storageProps() {
        return new StorageProps();
    }

    @Bean
    @ConditionalOnMissingBean
    public StorageProvider storageProvider(StorageProps props) {
        if ("minio".equalsIgnoreCase(props.getType())) {
            MinioClient client = MinioClient.builder()
                .endpoint(props.getMinio().getEndpoint())
                .credentials(props.getMinio().getAccessKey(), props.getMinio().getSecretKey())
                .build();
            return new MinioStorageProvider(client);
        }
        return new LocalFsStorageProvider(Path.of(props.getLocal().getBaseDir()));
    }
}
