package com.ankitsultana.keeper.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class KeeperUIApplication {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
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
            Map<String, Object> result = new HashMap<>();
            result.put("children", children);
            return ResponseEntity.ok(result);
        } catch (KeeperException | InterruptedException e) {
            return ResponseEntity.badRequest().body("Error listing path: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getPath(@RequestParam("path") String path) {
        try {
            byte[] data = zookeeperFacade.getNodeData(path);
            Map<String, Object> jsonData = new HashMap<>();
            String serialized = new String(data);
            jsonData.put("data", serialized);
            jsonData.put("stat", Map.of("dataLength", serialized.length()));
            jsonData.put("children", Map.of("length", 0));
            return ResponseEntity.ok(jsonData);
        } catch (KeeperException | InterruptedException e) {
            return ResponseEntity.badRequest().body("Error getting path: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPath(@RequestBody(required = false) String data) {
        try {
            if (data == null) {
                throw new IllegalArgumentException("Empty data");
            }
            Map<String, Object> mp = OBJECT_MAPPER.readValue(data, new TypeReference<>() {});
            String path = (String) mp.get("path");
            String data1 = (String) mp.get("data");
            String createdPath = zookeeperFacade.createNode(path, data1.getBytes(StandardCharsets.UTF_8), CreateMode.PERSISTENT);
            return ResponseEntity.ok("Created: " + createdPath);
        } catch (KeeperException | InterruptedException | JsonProcessingException e) {
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