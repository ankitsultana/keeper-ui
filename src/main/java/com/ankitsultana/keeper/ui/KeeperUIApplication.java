package com.ankitsultana.keeper.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(AppConfig.class)
public class KeeperUIApplication {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ZookeeperFactory zookeeperFactory;
    private final AppConfig appConfig;

    @Autowired
    public KeeperUIApplication(AppConfig appConfig, ZookeeperFactory zookeeperFactory) {
        this.appConfig = appConfig;
        this.zookeeperFactory = zookeeperFactory;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(KeeperUIApplication.class);
        
        if (args.length > 0) {
            app.setDefaultProperties(java.util.Map.of(
                "spring.config.location", "file:" + args[0]
            ));
        }
        
        app.run(args);
    }

    @GetMapping("/{instance}/ls")
    public ResponseEntity<?> listPath(@PathVariable("instance") String instance, @RequestParam("path") String path) {
        try {
            ZookeeperFacade zookeeperFacade = zookeeperFactory.getZookeeperFacade(instance);
            List<String> children = zookeeperFacade.listChildren(path);
            Map<String, Object> result = new HashMap<>();
            result.put("children", children);
            return ResponseEntity.ok(result);
        } catch (KeeperException | InterruptedException | IOException e) {
            return ResponseEntity.badRequest().body("Error listing path: " + e.getMessage());
        }
    }

    @GetMapping("/{instance}/get")
    public ResponseEntity<?> getPath(@PathVariable("instance") String instance, @RequestParam("path") String path) {
        try {
            ZookeeperFacade zookeeperFacade = zookeeperFactory.getZookeeperFacade(instance);
            byte[] data = zookeeperFacade.getNodeData(path);
            org.apache.zookeeper.data.Stat stat = zookeeperFacade.getNodeStat(path);
            List<String> children = zookeeperFacade.listChildren(path);
            Map<String, Object> jsonData = new HashMap<>();
            String serialized = new String(data);
            jsonData.put("data", serialized);
            Map<String, Object> statMap = getStringObjectMap(stat);
            jsonData.put("stat", statMap);
            jsonData.put("children", children);
            return ResponseEntity.ok(jsonData);
        } catch (KeeperException | InterruptedException e) {
            return ResponseEntity.badRequest().body("Error getting path: " + e.getMessage());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> getStringObjectMap(Stat stat) {
        Map<String, Object> statMap = new HashMap<>();
        statMap.put("czxid", stat.getCzxid());
        statMap.put("mzxid", stat.getMzxid());
        statMap.put("ctime", stat.getCtime());
        statMap.put("mtime", stat.getMtime());
        statMap.put("version", stat.getVersion());
        statMap.put("cversion", stat.getCversion());
        statMap.put("aversion", stat.getAversion());
        statMap.put("ephemeralOwner", stat.getEphemeralOwner());
        statMap.put("dataLength", stat.getDataLength());
        statMap.put("numChildren", stat.getNumChildren());
        statMap.put("pzxid", stat.getPzxid());
        return statMap;
    }

    @PostMapping("/{instance}/create")
    public ResponseEntity<?> createPath(@PathVariable("instance") String instance, @RequestBody(required = false) String data) {
        try {
            if (data == null) {
                throw new IllegalArgumentException("Empty data");
            }
            Map<String, Object> mp = OBJECT_MAPPER.readValue(data, new TypeReference<>() {});
            String path = (String) mp.get("path");
            String data1 = (String) mp.get("data");
            boolean isEphemeral = Boolean.parseBoolean(String.valueOf(mp.getOrDefault("isEphemeral", "false")));
            CreateMode mode = isEphemeral ? CreateMode.EPHEMERAL : CreateMode.PERSISTENT;
            ZookeeperFacade zookeeperFacade = zookeeperFactory.getZookeeperFacade(instance);
            String createdPath = zookeeperFacade.createNode(path, data1.getBytes(StandardCharsets.UTF_8), mode);
            return ResponseEntity.ok("Created: " + createdPath);
        } catch (KeeperException | InterruptedException | IOException e) {
            return ResponseEntity.badRequest().body("Error creating path: " + e.getMessage());
        }
    }

    @DeleteMapping("/{instance}/delete")
    public ResponseEntity<?> deletePath(@PathVariable("instance") String instance, @RequestParam("path") String path, @RequestParam(value = "version", defaultValue = "-1") int version) {
        try {
            ZookeeperFacade zookeeperFacade = zookeeperFactory.getZookeeperFacade(instance);
            zookeeperFacade.deleteNode(path, version);
            return ResponseEntity.ok("Deleted: " + path);
        } catch (KeeperException | InterruptedException | IOException e) {
            return ResponseEntity.badRequest().body("Error deleting path: " + e.getMessage());
        }
    }

    @PostMapping("/{instance}/set")
    public ResponseEntity<?> setPath(@PathVariable("instance") String instance, @RequestParam("path") String path, @RequestBody String data, @RequestParam(value = "version", defaultValue = "-1") int version) {
        try {
            ZookeeperFacade zookeeperFacade = zookeeperFactory.getZookeeperFacade(instance);
            zookeeperFacade.setNodeData(path, data.getBytes(), version);
            return ResponseEntity.ok("Updated: " + path);
        } catch (KeeperException | InterruptedException | IOException e) {
            return ResponseEntity.badRequest().body("Error setting path: " + e.getMessage());
        }
    }
}