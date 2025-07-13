package com.meeting.management.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public abstract class JsonFileStorage<T> {
    private final Path filePath;
    private final TypeReference<List<T>> typeReference;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonFileStorage(Path filePath, TypeReference<List<T>> typeReference) {
        this.filePath = filePath;
        this.typeReference = typeReference;
        initializeFile();
    }

    private void initializeFile() {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
                objectMapper.writeValue(filePath.toFile(), new ArrayList<>());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize storage file: " + filePath, e);
        }
    }

    public List<T> readAll() {
        try {
            return objectMapper.readValue(filePath.toFile(), typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read from storage file: " + filePath, e);
        }
    }

    public void saveAll(List<T> items) {
        try {
            objectMapper.writeValue(filePath.toFile(), items);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write to storage file: " + filePath, e);
        }
    }

    public void add(T item) {
        List<T> items = readAll();
        items.add(item);
        saveAll(items);
    }

    public Optional<T> findById(Long id) {
        return readAll().stream()
                .filter(item -> {
                    try {
                        var field = item.getClass().getDeclaredField("id");
                        field.setAccessible(true);
                        return id.equals(field.get(item));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst();
    }

    public void update(T updatedItem) {
        List<T> items = readAll();
        try {
            var idField = updatedItem.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Long id = (Long) idField.get(updatedItem);

            for (int i = 0; i < items.size(); i++) {
                T item = items.get(i);
                var itemIdField = item.getClass().getDeclaredField("id");
                itemIdField.setAccessible(true);
                if (id.equals(itemIdField.get(item))) {
                    items.set(i, updatedItem);
                    break;
                }
            }
            saveAll(items);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update item", e);
        }
    }

    public void delete(Long id) {
        List<T> items = readAll();
        try {
            items.removeIf(item -> {
                try {
                    var field = item.getClass().getDeclaredField("id");
                    field.setAccessible(true);
                    return id.equals(field.get(item));
                } catch (Exception e) {
                    return false;
                }
            });
            saveAll(items);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete item", e);
        }
    }
}