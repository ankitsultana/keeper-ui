package com.ankitsultana.keeper.ui;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "")
public class AppConfig {
    
    private Server server = new Server();
    private Map<String, ZookeeperInstance> zookeeper = new HashMap<>();
    
    public Server getServer() {
        return server;
    }
    
    public void setServer(Server server) {
        this.server = server;
    }
    
    public Map<String, ZookeeperInstance> getZookeeper() {
        return zookeeper;
    }
    
    public void setZookeeper(Map<String, ZookeeperInstance> zookeeper) {
        this.zookeeper = zookeeper;
    }
    
    public static class Server {
        private int port = 12345;
        
        public int getPort() {
            return port;
        }
        
        public void setPort(int port) {
            this.port = port;
        }
    }
    
    public static class ZookeeperInstance {
        private String host = "localhost:9180";
        private int sessionTimeout = 5000;
        
        public String getHost() {
            return host;
        }
        
        public void setHost(String host) {
            this.host = host;
        }
        
        public int getSessionTimeout() {
            return sessionTimeout;
        }
        
        public void setSessionTimeout(int sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }
    }
}