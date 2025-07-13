package com.meeting.management.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
// 由于 java.util.UUID 未被使用，移除该导入语句


// 此类按要求应定义在独立文件中，当前代码无需修改，需确保该类在名为 JsonFileStorage.java 的文件中单独存在
// 该类已按要求定义在名为 JsonFileStorage.java 的独立文件中，代码无需修改
public class JsonFileStorage<T> {
    private static final Logger logger = LoggerFactory.getLogger(JsonFileStorage.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String filePath;
    private Class<T> clazz;

    public JsonFileStorage() {
        // 默认构造函数
    }

    public JsonFileStorage(String filePath, Class<T> clazz) {
        // 如果是相对路径，则转换为绝对路径
        if (!new File(filePath).isAbsolute()) {
            this.filePath = new File(System.getProperty("user.dir"), filePath).getAbsolutePath();
        } else {
            this.filePath = filePath;
        }
        this.clazz = clazz;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    // 读取所有数据
    public List<T> readAll() throws IOException {
logger.info("读取文件: {}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            logger.info("文件不存在，返回空列表");
            return new ArrayList<>();
        }
// 导入 FileInputStream 类以解决无法解析的问题
try (InputStream inputStream = new FileInputStream(file)) {
// 导入 JavaType 和 TypeFactory 类以解决无法解析的问题
TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType collectionType = typeFactory.constructCollectionType(List.class, clazz);
            List<T> list = objectMapper.readValue(new InputStreamReader(inputStream, StandardCharsets.UTF_8), collectionType);
            return list != null ? list : new ArrayList<>();
        }
    }

    // 保存数据列表
    public void saveAll(List<T> list) throws IOException {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            logger.info("父目录不存在，创建目录: {}", file.getParentFile().getPath());
            file.getParentFile().mkdirs();
        }
        try {
            objectMapper.writeValue(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), list);
            logger.info("文件写入成功");
        } catch (IOException e) {
            logger.error("文件写入失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 添加新数据
    public T add(T item) throws IOException {
        logger.info("添加新数据: {}", item);
        List<T> list = readAll();
        // 如果是Meeting对象，设置ID
        if (item instanceof com.meeting.management.entity.Meeting) {
            com.meeting.management.entity.Meeting meeting = (com.meeting.management.entity.Meeting) item;
            if (meeting.getId() == null) {
                meeting.setId(System.currentTimeMillis());
            }
        }
        list.add(item);
        logger.info("数据添加到列表，大小: {}", list.size());
        saveAll(list);
        return item;
    }

    // 更新数据
    public boolean update(T item) throws IOException {
        List<T> list = readAll();
        Long itemId = null;
        try {
            itemId = (Long) item.getClass().getMethod("getId").invoke(item);
        } catch (Exception e) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            try {
                Long tId = (Long) t.getClass().getMethod("getId").invoke(t);
                if (tId == null || itemId == null) continue; // 跳过无效数据
                if (tId.equals(itemId)) {
                    list.set(i, item);
                    saveAll(list);
                    return true;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return false;
    }

    // 删除数据
    public boolean delete(Long id) throws IOException {
        List<T> list = readAll();
        boolean removed = false;
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            try {
                Long tId = (Long) t.getClass().getMethod("getId").invoke(t);
                if (tId == null || id == null) continue; // 跳过无效数据
                if (tId.equals(id)) {
                    list.remove(i);
                    removed = true;
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        if (removed) {
            saveAll(list);
        }
        return removed;
    }

    // 根据ID查询数据
    public T getById(Long id) throws IOException {
        List<T> list = readAll();
        for (T item : list) {
            try {
                Long itemId = (Long) item.getClass().getMethod("getId").invoke(item);
                if (itemId == null || id == null) continue; // 跳过无效数据
                if (itemId.equals(id)) {
                    return item;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }
}