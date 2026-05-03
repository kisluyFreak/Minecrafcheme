package com.example.mybuilder.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import com.example.mybuilder.util.JsonLoader.BuildData;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;

public class BlockStatsScreen extends Screen {
    private final BuildData buildData;
    private final File sourceFile;
    private List<String> statsLines = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int MAX_VISIBLE = 15;

    public BlockStatsScreen(BuildData data, File file) {
        super(Component.literal("Статистика блоков: " + data.name));
        this.buildData = data;
        this.sourceFile = file;
        generateStats();
    }

    private void generateStats() {
        statsLines.clear();
        Map<String, Integer> counts = new TreeMap<>();
        
        // Подсчёт блоков
        for (BuildData.BlockEntry entry : buildData.blocks) {
            counts.put(entry.id, counts.getOrDefault(entry.id, 0) + 1);
        }

        statsLines.add("=== ОБЩАЯ СТАТИСТИКА ===");
        statsLines.add("Всего блоков: " + buildData.blocks.size());
        statsLines.add("Размеры: " + buildData.width + " x " + buildData.height + " x " + buildData.depth);
        statsLines.add("Уникальных типов блоков: " + counts.size());
        statsLines.add("");
        statsLines.add("=== СПИСОК БЛОКОВ ===");

        // Сортировка по количеству (от большего к меньшему)
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (Map.Entry<String, Integer> entry : sorted) {
            statsLines.add(entry.getKey() + ": " + entry.getValue() + " шт.");
        }
    }

    @Override
    protected void init() {
        super.init();

        // Кнопка "Копировать в чат"
        addRenderableWidget(Button.builder(
            Component.literal("Вывести в чат"),
            b -> {
                if (minecraft.player != null) {
                    minecraft.player.displayClientMessage(Component.literal("=== Статистика постройки: " + buildData.name + " ==="), true);
                    for (String line : statsLines) {
                        minecraft.player.displayClientMessage(Component.literal(line), false);
                    }
                }
            }
        ).bounds(this.width / 2 - 100, 10, 200, 20).build());

        // Кнопка "Назад"
        addRenderableWidget(Button.builder(
            Component.literal("Назад"),
            b -> minecraft.setScreen(new BuildPreviewScreen(buildData, sourceFile))
        ).bounds(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Заголовок
        guiGraphics.drawCenteredString(font, title, width / 2, 10, 0xFFFF00);

        // Отрисовка списка с прокруткой
        int startY = 40;
        int visibleCount = Math.min(MAX_VISIBLE, statsLines.size() - scrollOffset);
        
        for (int i = 0; i < visibleCount; i++) {
            String line = statsLines.get(scrollOffset + i);
            int color = 0xDDDDDD;
            if (line.startsWith("===")) color = 0xFFFF00;
            else if (line.isEmpty()) color = 0x888888;
            
            guiGraphics.drawString(font, line, 20, startY + i * 12, color);
        }

        // Индикатор прокрутки
        if (statsLines.size() > MAX_VISIBLE) {
            String scrollInfo = (scrollOffset + 1) + " - " + Math.min(scrollOffset + MAX_VISIBLE, statsLines.size()) + 
                               " из " + statsLines.size();
            guiGraphics.drawCenteredString(font, scrollInfo, width / 2, this.height - 50, 0xAAAAAA);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (statsLines.size() > MAX_VISIBLE) {
            scrollOffset -= (int) deltaY;
            scrollOffset = Math.max(0, Math.min(scrollOffset, statsLines.size() - MAX_VISIBLE));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }
}
