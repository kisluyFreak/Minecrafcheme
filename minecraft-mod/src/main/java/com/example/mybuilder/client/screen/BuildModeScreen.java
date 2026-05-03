package com.example.mybuilder.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import com.example.mybuilder.util.JsonLoader.BuildData;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class BuildModeScreen extends Screen {
    private final BuildData buildData;
    private final File sourceFile;
    private BlockPos startPos;
    private int currentLayer = 0;
    private BuildMode mode = BuildMode.BY_LAYER; // Режим: по слоям или по блокам
    private String selectedBlockId = null; // Для режима по блокам
    private boolean ghostBlocksEnabled = true;
    private List<BlockPos> placedBlocks = new ArrayList<>();

    public enum BuildMode {
        BY_LAYER,      // Строить слой за слоем
        BY_BLOCK_TYPE  // Строить по типу блока
    }

    public BuildModeScreen(BuildData data, File file) {
        super(Component.literal("Строительство: " + data.name));
        this.buildData = data;
        this.sourceFile = file;
    }

    @Override
    protected void init() {
        super.init();

        // Информация о позиции
        if (minecraft.player != null) {
            startPos = minecraft.player.blockPosition();
        }

        // Кнопка переключения режима
        addRenderableWidget(Button.builder(
            Component.literal(mode == BuildMode.BY_LAYER ? "Режим: По слоям" : "Режим: По блокам"),
            b -> {
                mode = (mode == BuildMode.BY_LAYER) ? BuildMode.BY_BLOCK_TYPE : BuildMode.BY_LAYER;
                currentLayer = 0;
                selectedBlockId = null;
                b.setMessage(Component.literal(mode == BuildMode.BY_LAYER ? "Режим: По слоям" : "Режим: По блокам"));
            }
        ).bounds(this.width / 2 - 100, 10, 200, 20).build());

        // Навигация по слоям/блокам
        if (mode == BuildMode.BY_LAYER) {
            addRenderableWidget(Button.builder(
                Component.literal("Слой: " + (currentLayer + 1) + "/" + buildData.height),
                b -> {}
            ).bounds(this.width / 2 - 100, 35, 200, 20).build());

            addRenderableWidget(Button.builder(
                Component.literal("< Пред. слой"),
                b -> { if (currentLayer > 0) currentLayer--; }
            ).bounds(this.width / 2 - 150, 60, 100, 20).build());

            addRenderableWidget(Button.builder(
                Component.literal("След. слой >"),
                b -> { if (currentLayer < buildData.height - 1) currentLayer++; }
            ).bounds(this.width / 2 + 50, 60, 100, 20).build());
        } else {
            // Режим по блокам - выбор типа блока
            String blockInfo = selectedBlockId != null ? selectedBlockId : "Все блоки";
            addRenderableWidget(Button.builder(
                Component.literal("Блок: " + blockInfo),
                b -> {}
            ).bounds(this.width / 2 - 100, 35, 200, 20).build());

            addRenderableWidget(Button.builder(
                Component.literal("< Пред. тип"),
                b -> selectPrevBlock()
            ).bounds(this.width / 2 - 150, 60, 100, 20).build());

            addRenderableWidget(Button.builder(
                Component.literal("След. тип >"),
                b -> selectNextBlock()
            ).bounds(this.width / 2 + 50, 60, 100, 20).build());
        }

        // Переключатель призрачных блоков
        addRenderableWidget(Button.builder(
            Component.literal(ghostBlocksEnabled ? "Призраки: ВКЛ" : "Призраки: ВЫКЛ"),
            b -> { 
                ghostBlocksEnabled = !ghostBlocksEnabled;
                b.setMessage(Component.literal(ghostBlocksEnabled ? "Призраки: ВКЛ" : "Призраки: ВЫКЛ"));
            }
        ).bounds(this.width / 2 - 100, 90, 200, 20).build());

        // Инструкция
        addRenderableWidget(Button.builder(
            Component.literal("Как строить?"),
            b -> {
                if (minecraft.player != null) {
                    minecraft.player.displayClientMessage(Component.literal("ЛКМ + Shift по воздуху - поставить призрачный блок"), false);
                    minecraft.player.displayClientMessage(Component.literal("ПКМ + Shift по призраку - установить реальный блок"), false);
                }
            }
        ).bounds(this.width / 2 - 100, 120, 200, 20).build());

        // Кнопка "Назад"
        addRenderableWidget(Button.builder(
            Component.literal("Назад к просмотру"),
            b -> minecraft.setScreen(new BuildPreviewScreen(buildData, sourceFile))
        ).bounds(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    private void selectNextBlock() {
        // Упрощённая логика выбора блока
        if (buildData.blocks.isEmpty()) return;
        
        if (selectedBlockId == null) {
            selectedBlockId = buildData.blocks.get(0).id;
        } else {
            int idx = -1;
            for (int i = 0; i < buildData.blocks.size(); i++) {
                if (buildData.blocks.get(i).id.equals(selectedBlockId)) {
                    idx = i;
                    break;
                }
            }
            if (idx >= 0 && idx < buildData.blocks.size() - 1) {
                selectedBlockId = buildData.blocks.get(idx + 1).id;
            } else {
                selectedBlockId = null; // Сброс
            }
        }
    }

    private void selectPrevBlock() {
        if (buildData.blocks.isEmpty()) return;
        
        if (selectedBlockId == null) {
            selectedBlockId = buildData.blocks.get(buildData.blocks.size() - 1).id;
        } else {
            int idx = -1;
            for (int i = 0; i < buildData.blocks.size(); i++) {
                if (buildData.blocks.get(i).id.equals(selectedBlockId)) {
                    idx = i;
                    break;
                }
            }
            if (idx > 0) {
                selectedBlockId = buildData.blocks.get(idx - 1).id;
            } else {
                selectedBlockId = null;
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Заголовок
        guiGraphics.drawCenteredString(font, title, width / 2, 10, 0x00FF00);

        // Информация
        String info = "Позиция старта: " + (startPos != null ? startPos.toString() : "Не определена");
        guiGraphics.drawCenteredString(font, info, width / 2, 145, 0xAAAAAA);

        String progress = "Установлено блоков: " + placedBlocks.size() + " из " + buildData.blocks.size();
        guiGraphics.drawCenteredString(font, progress, width / 2, 160, 0xDDDDDD);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // Здесь будет логика отрисовки призрачных блоков в мире (требуется рендерер)
    // Для простоты пока оставляем только UI
}
