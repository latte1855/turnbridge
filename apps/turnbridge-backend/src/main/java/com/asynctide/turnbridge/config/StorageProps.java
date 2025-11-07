package com.asynctide.turnbridge.config;

/**
 * Storage 設定屬性承載。
 */
public class StorageProps {

    private String type = "local";
    private Local local = new Local();
    private Minio minio = new Minio();

    // === Getter / Setter ===
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Minio getMinio() {
        return minio;
    }

    public void setMinio(Minio minio) {
        this.minio = minio;
    }

    // === Local 內部類 ===
    public static class Local {
        private String baseDir = "/tmp/turnbridge/storage";

        public String getBaseDir() {
            return baseDir;
        }

        public void setBaseDir(String baseDir) {
            this.baseDir = baseDir;
        }
    }

    // === Minio 內部類 ===
    public static class Minio {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucketPolicy = "private";

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucketPolicy() {
            return bucketPolicy;
        }

        public void setBucketPolicy(String bucketPolicy) {
            this.bucketPolicy = bucketPolicy;
        }
    }
}
