package com.example.mybuilder.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import com.example.mybuilder.util.JsonLoader.BuildData;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;

public class BuildPreviewScreen extends Screen {
    private final BuildData buildData;
    private final File sourceFile;
    private int currentLayer = 0;
    private boolean showAllLayers = true;
    private Map<String, Integer> blockCounts;
    private int totalBlocks;

    public BuildPreviewScreen(BuildData data, File file) {
        super(Component.literal("Просмотр: " + data.name));
        this.buildData = data;
        this.sourceFile = file;
        calculateBlockCounts();
    }

    private void calculateBlockCounts() {
        blockCounts = new TreeMap<>();
        totalBlocks = 0;
        for (BuildData.BlockEntry entry : buildData.blocks) {
            String name = entry.id;
            blockCounts.put(name, blockCounts.getOrDefault(name, 0) + 1);
            totalBlocks++;
        }
    }

    @Override
    protected void init() {
        super.init();

        // Кнопка "Начать строительство"
        addRenderableWidget(Button.builder(
            Component.literal("НАЧАТЬ СТРОИТЕЛЬСТВО"),
            b -> minecraft.setScreen(new BuildModeScreen(buildData, sourceFile))
        ).bounds(this.width / 2 - 100, 10, 200, 20).build());

        // Кнопка "Слои"
        addRenderableWidget(Button.builder(
            Component.literal(showAllLayers ? "Показывать все слои" : "Показывать по слоям"),
            b -> { 
                showAllLayers = !showAllLayers; 
                if (!showAllLayers) currentLayer = 0;
                b.setMessage(Component.literal(showAllLayers ? "Показывать все слои" : "Показывать по слоям"));
            }
        ).bounds(this.width / 2 - 100, 35, 200, 20).build());

        // Навигация по слоям (если включен режим послойного просмотра)
        if (!showAllLayers) {
            addRenderableWidget(Button.builder(
                Component.literal("< Слой " + (currentLayer + 1) + " >"),
                b -> {}
            ).bounds(this.width / 2 - 75, 60, 150, 20).build());

            addRenderableWidget(Button.builder(
                Component.literal("-"),
                b -> { if (currentLayer > 0) currentLayer--; }
            ).bounds(this.width / 2 - 100, 60, 40, 20).build());

            addRenderableWidget(Button.builder(
                Component.literal("+"),
                b -> { if (currentLayer < buildData.height - 1) currentLayer++; }
            ).bounds(this.width / 2 + 60, 60, 40, 20).build());
        }

        // Кнопка "Статистика блоков"
        addRenderableWidget(Button.builder(
            Component.literal("Статистика блоков"),
            b -> minecraft.setScreen(new BlockStatsScreen(buildData, sourceFile))
        ).bounds(this.width / 2 - 100, 90, 200, 20).build());

        // Кнопка "Назад"
        addRenderableWidget(Button.builder(
            Component.literal("Назад к списку"),
            b -> minecraft.setScreen(new BuildListScreen())
        ).bounds(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Заголовок
        guiGraphics.drawCenteredString(font, title, width / 2, 10, 0xFFFF00);

        // Информация о размерах
        String sizeInfo = String.format("Размеры: %d x %d x %d | Всего блоков: %d", 
            buildData.width, buildData.height, buildData.depth, totalBlocks);
        guiGraphics.drawCenteredString(font, sizeInfo, width / 2, 35, 0xFFFFFF);

        // Текущий слой
        if (!showAllLayers) {
            String layerInfo = "Просмотр слоя Y=" + (currentLayer + 1) + " из " + buildData.height;
            guiGraphics.drawCenteredString(font, layerInfo, width / 2, 80, 0xAAAAAA);
        }

        // Список блоков (первые 5)
        int yStart = 120;
        guiGraphics.drawString(font, "Основные блоки:", 20, yStart, 0xAAAAAA);
        int count = 0;
        for (Map.Entry<String, Integer> entry : blockCounts.entrySet()) {
            if (count >= 5) break;
            String line = entry.getKey() + ": " + entry.getValue();
            guiGraphics.drawString(font, line, 30, yStart + 15 + count * 12, 0xDDDDDD);
            count++;
        }
        if (blockCounts.size() > 5) {
            guiGraphics.drawString(font, "... и ещё " + (blockCounts.size() - 5) + " типов", 
                30, yStart + 15 + count * 12, 0x888888);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
