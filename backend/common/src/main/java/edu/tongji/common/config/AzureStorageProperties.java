package edu.tongji.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Azure Blob Storage 配置属性
 */
@Component
@ConfigurationProperties(prefix = "azure.storage")
public class AzureStorageProperties {
    
    private String connectionString;
    private String container;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }
}

