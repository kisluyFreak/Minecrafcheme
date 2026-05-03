package com.mybuilder.mod.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureLoader {

    public static class StructureData {
        public String name;
        public String author;
        public List<BlockEntry> blocks = new ArrayList<>();
        public Map<String, Integer> blockCounts = new HashMap<>();
        public int minX, maxX, minY, maxY, minZ, maxZ;

        public StructureData() {
            minX = minY = minZ = Integer.MAX_VALUE;
            maxX = maxY = maxZ = Integer.MIN_VALUE;
        }

        public void addBlock(int x, int y, int z, Block block) {
            blocks.add(new BlockEntry(x, y, z, block));
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
            minZ = Math.min(minZ, z);
            maxZ = Math.max(maxZ, z);

            String blockName = String.valueOf(Blocks.BLOCK_REGISTRY.getKey(block));
            blockCounts.put(blockName, blockCounts.getOrDefault(blockName, 0) + 1);
        }

        public int getWidth() { return maxX - minX + 1; }
        public int getHeight() { return maxY - minY + 1; }
        public int getDepth() { return maxZ - minZ + 1; }
    }

    public static class BlockEntry {
        public int x, y, z;
        public Block block;

        public BlockEntry(int x, int y, int z, Block block) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.block = block;
        }
    }

    public static StructureData loadStructure(File file) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(file));
        
        StructureData data = new StructureData();
        JSONObject structure = (JSONObject) json.get("structure");
        
        if (structure != null) {
            data.name = (String) structure.getOrDefault("name", "Unknown");
            data.author = (String) structure.getOrDefault("author", "AI");
            
            JSONArray blocksArray = (JSONArray) structure.get("blocks");
            if (blocksArray != null) {
                for (Object obj : blocksArray) {
                    JSONObject blockObj = (JSONObject) obj;
                    JSONArray pos = (JSONArray) blockObj.get("pos");
                    String state = (String) blockObj.get("state");
                    
                    if (pos != null && state != null) {
                        int x = ((Number) pos.get(0)).intValue();
                        int y = ((Number) pos.get(1)).intValue();
                        int z = ((Number) pos.get(2)).intValue();
                        
                        Block block = Block.byStateId(Block.getId(Block.byName(state).defaultBlockState()));
                        if (block == Blocks.AIR) {
                            block = Blocks.BLOCK_REGISTRY.get(net.minecraft.resources.ResourceLocation.tryParse(state));
                            if (block == null) block = Blocks.STONE; // Fallback
                        }
                        
                        data.addBlock(x, y, z, block);
                    }
                }
            }
        }
        
        return data;
    }

    public static List<File> getAvailableStructures() {
        File buildsDir = new File("mybuilder_builds");
        if (!buildsDir.exists()) {
            buildsDir.mkdirs();
            return new ArrayList<>();
        }
        
        List<File> files = new ArrayList<>();
        for (File file : buildsDir.listFiles((dir, name) -> name.endsWith(".json"))) {
            files.add(file);
        }
        return files;
    }
}
