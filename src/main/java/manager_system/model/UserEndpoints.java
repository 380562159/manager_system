package manager_system.model;

import com.alibaba.fastjson2.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserEndpoints {
    private static final Logger log = LoggerFactory.getLogger(UserEndpoints.class);
    private Integer userId;
    private List<String> endpoint;
    private static Properties prop;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static String filepath = "./userendpoints.properties";

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<String> getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(List<String> endpoint) {
        this.endpoint = endpoint;
    }

    public static void createFileIfNotExists(File file) {
        if (file.exists()) {
            log.info("File exists");
        } else {
            log.info("File not exists, create it ...");
            if (!file.getParentFile().exists()) {
                log.info("not exists");
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("can't create file: " + file.getAbsolutePath());
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public static void loadProperties() throws IOException {
        createFileIfNotExists(new File(filepath));
        prop = new Properties();
        prop.load(new FileInputStream(filepath));
    }

    public static List<String> getEndpointsByUserId(Long key) {
        lock.readLock().lock();
        try {
            String pointStr = prop.getProperty(key.toString());
            List<String> pointL = JSONArray.parseArray(pointStr, String.class);
            return pointL;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void updateEndpoints(UserEndpoints ue) throws IOException {
        if (ue.getUserId() == null) {
            return;
        }

        lock.writeLock().lock();
        try {
            JSONArray ja = new JSONArray();
            List<String> endpoints = ue.getEndpoint();
            if (endpoints != null) {
                for (String endpoint : endpoints) {
                    ja.add(endpoint);
                }
            }
            prop.setProperty(ue.getUserId().toString(), ja.toJSONString());

            prop.store(new FileOutputStream(filepath), null);

        } finally {
            lock.writeLock().unlock();
        }
    }
}
