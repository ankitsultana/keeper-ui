package com.ankitsultana.keeper.ui;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ZookeeperFactory {
    
    private final AppConfig appConfig;
    private final Map<String, ZookeeperFacade> instances = new ConcurrentHashMap<>();
    
    public ZookeeperFactory(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    
    public ZookeeperFacade getZookeeperFacade(String instanceName) throws IOException, InterruptedException {
        return instances.computeIfAbsent(instanceName, this::createZookeeperFacade);
    }
    
    private ZookeeperFacade createZookeeperFacade(String instanceName) {
        AppConfig.ZookeeperInstance config = appConfig.getZookeeper().get(instanceName);
        if (config == null) {
            throw new IllegalArgumentException("Unknown ZooKeeper instance: " + instanceName);
        }
        try {
            return new ZookeeperFacade(config);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to create ZooKeeper connection for instance: " + instanceName, e);
        }
    }
    
    public void closeAll() {
        instances.values().forEach(facade -> {
            try {
                facade.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        instances.clear();
    }
}