package com.ankitsultana.keeper.ui;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "")
public class AppConfig {
    
    private Server server = new Server();
    private Zookeeper zookeeper = new Zookeeper();
    
    public Server getServer() {
        return server;
    }
    
    public void setServer(Server server) {
        this.server = server;
    }
    
    public Zookeeper getZookeeper() {
        return zookeeper;
    }
    
    public void setZookeeper(Zookeeper zookeeper) {
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
    
    public static class Zookeeper {
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