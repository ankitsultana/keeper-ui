package com.ankitsultana.keeper.ui;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@RestController
public class KeeperUIApplication {

    private static final int DEFAULT_PORT = 12345;
    private ZookeeperFacade zookeeperFacade;

    public KeeperUIApplication() {
        try {
            this.zookeeperFacade = new ZookeeperFacade();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to initialize Zookeeper connection", e);
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(KeeperUIApplication.class);
        app.setDefaultProperties(java.util.Map.of("server.port", String.valueOf(DEFAULT_PORT)));
        app.run(args);
    }

    @GetMapping("/ls")
    public ResponseEntity<?> listPath(@RequestParam("path") String path) {
        try {
            List<String> children = zookeeperFacade.listChildren(path);
            return ResponseEntity.ok(children);
        } catch (KeeperException | InterruptedException e) {
            return ResponseEntity.badRequest().body("Error listing path: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getPath(@RequestParam("path") String path) {
        try {
            byte[] data = zookeeperFacade.getNodeData(path);
            return ResponseEntity.ok(new String(data));
        } catch (KeeperException | InterruptedException e) {
            return ResponseEntity.badRequest().body("Error getting path: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPath(@RequestParam("path") String path, @RequestBody(required = false) String data) {
        try {
            byte[] dataBytes = data != null ? data.getBytes() : new byte[0];
            String createdPath = zookeeperFacade.createNode(path, dataBytes, CreateMode.PERSISTENT);
            return ResponseEntity.ok("Created: " + createdPath);
        } catch (KeeperException | InterruptedException e) {
            return ResponseEntity.badRequest().body("Error creating path: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePath(@RequestParam("path") String path, @RequestParam(value = "version", defaultValue = "-1") int version) {
        try {
            zookeeperFacade.deleteNode(path, version);
            return ResponseEntity.ok("Deleted: " + path);
        } catch (KeeperException | InterruptedException e) {
            return ResponseEntity.badRequest().body("Error deleting path: " + e.getMessage());
        }
    }

    @PostMapping("/set")
    public ResponseEntity<?> setPath(@RequestParam("path") String path, @RequestBody String data, @RequestParam(value = "version", defaultValue = "-1") int version) {
        try {
            zookeeperFacade.setNodeData(path, data.getBytes(), version);
            return ResponseEntity.ok("Updated: " + path);
        } catch (KeeperException | InterruptedException e) {
            return ResponseEntity.badRequest().body("Error setting path: " + e.getMessage());
        }
    }
}