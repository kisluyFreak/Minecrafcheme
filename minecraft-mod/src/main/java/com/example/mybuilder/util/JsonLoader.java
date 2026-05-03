package com.example.mybuilder.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonLoader {
    
    public static class BuildBlock {
        public int[] pos;
        public String state;
        
        public BuildBlock(int[] pos, String state) {
            this.pos = pos;
            this.state = state;
        }
    }
    
    public static class BuildData {
        public String name;
        public String author;
        public String description;
        public List<BuildBlock> blocks;
        public int minX, maxX, minY, maxY, minZ, maxZ;
        
        public BuildData() {
            this.blocks = new ArrayList<>();
            this.minX = 0; this.maxX = 0;
            this.minY = 0; this.maxY = 0;
            this.minZ = 0; this.maxZ = 0;
        }
        
        public void calculateBounds() {
            if (blocks.isEmpty()) return;
            
            minX = Integer.MAX_VALUE; maxX = Integer.MIN_VALUE;
            minY = Integer.MAX_VALUE; maxY = Integer.MIN_VALUE;
            minZ = Integer.MAX_VALUE; maxZ = Integer.MIN_VALUE;
            
            for (BuildBlock block : blocks) {
                if (block.pos.length >= 3) {
                    int x = block.pos[0];
                    int y = block.pos[1];
                    int z = block.pos[2];
                    
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                    if (z < minZ) minZ = z;
                    if (z > maxZ) maxZ = z;
                }
            }
        }
        
        public int getSizeX() {
            return maxX - minX + 1;
        }
        
        public int getSizeY() {
            return maxY - minY + 1;
        }
        
        public int getSizeZ() {
            return maxZ - minZ + 1;
        }
    }
    
    public static BuildData load(File file) {
        if (!file.exists() || !file.getName().endsWith(".json")) {
            return null;
        }
        
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            
            if (root == null) return null;
            
            BuildData data = new BuildData();
            
            // Читаем метаданные
            if (root.has("name")) {
                data.name = root.get("name").getAsString();
            } else {
                data.name = file.getName().replace(".json", "");
            }
            
            if (root.has("author")) {
                data.author = root.get("author").getAsString();
            }
            
            if (root.has("description")) {
                data.description = root.get("description").getAsString();
            }
            
            // Читаем блоки - поддерживаем два формата:
            // 1. {"structure": {"blocks": [...]}}
            // 2. {"blocks": [...]}
            
            JsonArray blocksArray = null;
            
            if (root.has("structure") && root.get("structure").isJsonObject()) {
                JsonObject structure = root.getAsJsonObject("structure");
                if (structure.has("blocks") && structure.get("blocks").isJsonArray()) {
                    blocksArray = structure.getAsJsonArray("blocks");
                }
                // Также читаем name из structure если есть
                if (structure.has("name") && data.name == null) {
                    data.name = structure.get("name").getAsString();
                }
            } else if (root.has("blocks") && root.get("blocks").isJsonArray()) {
                blocksArray = root.getAsJsonArray("blocks");
            }
            
            if (blocksArray == null) {
                System.err.println("[MyBuilder] Ошибка: в файле " + file.getName() + " нет массива blocks");
                return null;
            }
            
            // Парсим каждый блок - поддерживаем два формата:
            // Новый: {"pos": [x, y, z], "state": "minecraft:block"}
            // Старый: {"id": "minecraft:block", "x": 0, "y": 0, "z": 0}
            for (JsonElement element : blocksArray) {
                if (!element.isJsonObject()) continue;
                
                JsonObject blockObj = element.getAsJsonObject();
                int[] pos = null;
                String state = null;
                
                // Пробуем новый формат: pos + state
                if (blockObj.has("pos") && blockObj.get("pos").isJsonArray()) {
                    JsonArray posArray = blockObj.getAsJsonArray("pos");
                    if (posArray.size() >= 3) {
                        pos = new int[3];
                        for (int i = 0; i < 3; i++) {
                            pos[i] = posArray.get(i).getAsInt();
                        }
                    }
                    if (blockObj.has("state") && blockObj.get("state").isJsonPrimitive()) {
                        state = blockObj.get("state").getAsString();
                    }
                }
                // Пробуем старый формат: x, y, z + id
                else if (blockObj.has("x") && blockObj.has("y") && blockObj.has("z")) {
                    pos = new int[] {
                        blockObj.get("x").getAsInt(),
                        blockObj.get("y").getAsInt(),
                        blockObj.get("z").getAsInt()
                    };
                    if (blockObj.has("id") && blockObj.get("id").isJsonPrimitive()) {
                        state = blockObj.get("id").getAsString();
                    } else if (blockObj.has("state") && blockObj.get("state").isJsonPrimitive()) {
                        state = blockObj.get("state").getAsString();
                    }
                }
                
                // Пропускаем блок если не удалось распарсить
                if (pos == null || state == null) {
                    continue;
                }
                
                data.blocks.add(new BuildBlock(pos, state));
            }
            
            // Вычисляем границы
            data.calculateBounds();
            
            return data;
            
        } catch (IOException e) {
            System.err.println("[MyBuilder] Ошибка чтения файла " + file.getName() + ": " + e.getMessage());
            return null;
        }
    }
}
