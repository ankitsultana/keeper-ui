package com.ankitsultana.keeper.ui;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperFacade {

    private static final String ZOOKEEPER_HOST = "localhost:9180";
    private static final int SESSION_TIMEOUT = 5000;
    
    private ZooKeeper zooKeeper;
    
    private static class NoOpWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
        }
    }

    public ZookeeperFacade() throws IOException, InterruptedException {
        CountDownLatch connectedSignal = new CountDownLatch(1);
        
        zooKeeper = new ZooKeeper(ZOOKEEPER_HOST, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                }
            }
        });
        
        connectedSignal.await();
    }

    public List<String> listChildren(String path) throws KeeperException, InterruptedException {
        return zooKeeper.getChildren(path, new NoOpWatcher());
    }

    public byte[] getNodeData(String path) throws KeeperException, InterruptedException {
        return zooKeeper.getData(path, new NoOpWatcher(), null);
    }

    public String createNode(String path, byte[] data, CreateMode createMode) throws KeeperException, InterruptedException {
        return zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    public void deleteNode(String path, int version) throws KeeperException, InterruptedException {
        zooKeeper.delete(path, version);
    }

    public Stat setNodeData(String path, byte[] data, int version) throws KeeperException, InterruptedException {
        return zooKeeper.setData(path, data, version);
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }
}