package com.empire.employeefinder.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String bucket;

    private String publicBucket;

    private String url;

    private String accessKey;

    private String secretKey;
}
