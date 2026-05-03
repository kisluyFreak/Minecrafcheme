package com.mybuilder.mod.client.screen;

import com.mybuilder.mod.core.StructureLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.Map;

public class BlockStatsScreen extends Screen {

    private final Screen parent;
    private final StructureLoader.StructureData data;

    public BlockStatsScreen(Screen parent, StructureLoader.StructureData data) {
        super(Component.literal("Статистика блоков"));
        this.parent = parent;
        this.data = data;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        
        if (data != null) {
            int y = 50;
            addRenderableWidget(Button.builder(Component.literal("Размеры: " + data.getWidth() + "x" + data.getHeight() + "x" + data.getDepth()), 
                    b -> {}).bounds(centerX - 150, y, 300, 20).build());
            y += 30;
            
            for (Map.Entry<String, Integer> entry : data.blockCounts.entrySet()) {
                addRenderableWidget(Button.builder(Component.literal(entry.getKey() + ": " + entry.getValue()), 
                        b -> {}).bounds(centerX - 150, y, 300, 20).build());
                y += 20;
                if (y > this.height - 60) break; // Limit display
            }
        } else {
            addRenderableWidget(Button.builder(Component.literal("Сначала выберите постройку"), 
                    b -> {}).bounds(centerX - 150, 50, 300, 20).build());
        }

        addRenderableWidget(Button.builder(Component.literal("Назад"), 
                btn -> minecraft.setScreen(parent))
                .bounds(centerX - 100, this.height - 40, 200, 20)
                .build());
                
        addRenderableWidget(Button.builder(Component.literal("Копировать в чат"), 
                btn -> {
                    if (data != null) {
                        StringBuilder sb = new StringBuilder("Требуемые блоки для " + data.name + ":\n");
                        for (Map.Entry<String, Integer> entry : data.blockCounts.entrySet()) {
                            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                        }
                        minecraft.player.sendSystemMessage(Component.literal(sb.toString()));
                    }
                })
                .bounds(centerX - 100, this.height - 70, 200, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(font, "Статистика блоков", this.width / 2, 20, 0xFFFFFF);
    }
}
