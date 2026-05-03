package com.mybuilder.mod.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mybuilder.mod.core.StructureLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

public class BuildScreen extends Screen {

    private final Screen parent;
    private final StructureLoader.StructureData data;
    private int currentLayer = 0;
    private String buildMode = "LAYER"; // LAYER or BLOCK_TYPE
    private BlockPos playerPos;

    public BuildScreen(Screen parent, StructureLoader.StructureData data) {
        super(Component.literal("Строительство"));
        this.parent = parent;
        this.data = data;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 50;

        if (data != null) {
            addRenderableWidget(Button.builder(Component.literal("Постройка: " + data.name), 
                    b -> {}).bounds(centerX - 150, y, 300, 20).build());
            y += 25;
            
            addRenderableWidget(Button.builder(Component.literal("Текущий слой: " + currentLayer + "/" + (data.getHeight()-1)), 
                    b -> {}).bounds(centerX - 150, y, 300, 20).build());
            y += 25;

            addRenderableWidget(Button.builder(Component.literal("< Пред. слой"), 
                    b -> { if (currentLayer > 0) currentLayer--; })
                    .bounds(centerX - 150, y, 140, 20).build());
                    
            addRenderableWidget(Button.builder(Component.literal("След. слой >"), 
                    b -> { if (currentLayer < data.getHeight()-1) currentLayer++; })
                    .bounds(centerX + 10, y, 140, 20).build());
            y += 30;

            addRenderableWidget(Button.builder(Component.literal("Режим: " + (buildMode.equals("LAYER") ? "По слоям" : "По блокам")), 
                    b -> buildMode = buildMode.equals("LAYER") ? "BLOCK_TYPE" : "LAYER")
                    .bounds(centerX - 150, y, 300, 20).build());
            y += 30;

            addRenderableWidget(Button.builder(Component.literal("ПОСТРОИТЬ СЛОЙ"), 
                    b -> buildLayer())
                    .bounds(centerX - 150, y, 300, 30).build());
            y += 40;
            
            addRenderableWidget(Button.builder(Component.literal("ПОСТРОИТЬ ВСЁ"), 
                    b -> buildAll())
                    .bounds(centerX - 150, y, 300, 30).build());
        } else {
            addRenderableWidget(Button.builder(Component.literal("Выберите постройку в меню"), 
                    b -> {}).bounds(centerX - 150, 50, 300, 20).build());
        }

        addRenderableWidget(Button.builder(Component.literal("Назад"), 
                btn -> minecraft.setScreen(parent))
                .bounds(centerX - 100, this.height - 40, 200, 20)
                .build());
    }

    private void buildLayer() {
        if (data == null || minecraft.player == null) return;
        
        BlockPos playerBlockPos = minecraft.player.getBlockEyePosition().subtract(0, 0.5, 0).getBlockPos();
        int layerY = data.minY + currentLayer;
        
        for (StructureLoader.BlockEntry entry : data.blocks) {
            if (entry.y == layerY) {
                BlockPos targetPos = playerBlockPos.offset(entry.x - data.minX, entry.y - data.minY, entry.z - data.minZ);
                minecraft.gameMode.destroyBlock(targetPos);
                minecraft.player.sendSystemMessage(Component.literal("Building at " + targetPos));
                // В реальном моде здесь была бы установка блока с проверкой прав
            }
        }
    }

    private void buildAll() {
        if (data == null || minecraft.player == null) return;
        minecraft.player.sendSystemMessage(Component.literal("Постройка всего здания... (требуется креатив)"));
        // Реализация полной постройки
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(font, "Режим строительства", this.width / 2, 20, 0xFFFFFF);
        
        if (data != null) {
            graphics.drawString(font, "Размеры: " + data.getWidth() + "x" + data.getHeight() + "x" + data.getDepth(), 10, 10, 0xFFFFFF);
        }
    }
}
