package com.example.mybuilder.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import com.example.mybuilder.MyBuilder;
import com.example.mybuilder.util.JsonLoader;
import com.example.mybuilder.util.JsonLoader.BuildData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BuildListScreen extends Screen {
    private List<File> buildFiles = new ArrayList<>();
    private List<Button> fileButtons = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int ITEM_HEIGHT = 25;
    private static final int MAX_VISIBLE = 10;

    public BuildListScreen() {
        super(Component.literal("MyBuilder - Выберите постройку"));
        refreshFileList();
    }

    private void refreshFileList() {
        buildFiles.clear();
        File dir = MyBuilder.getConfigDir();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File f : files) {
                    buildFiles.add(f);
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        fileButtons.clear();
        int startY = 30;
        
        for (int i = 0; i < Math.min(buildFiles.size(), MAX_VISIBLE); i++) {
            File f = buildFiles.get(i + scrollOffset);
            String name = f.getName().replace(".json", "");
            
            Button btn = Button.builder(
                Component.literal(name),
                b -> openBuildPreview(f)
            ).bounds(this.width / 2 - 150, startY + i * ITEM_HEIGHT, 300, 20).build();
            
            addRenderableWidget(btn);
            fileButtons.add(btn);
        }

        // Кнопка обновления
        addRenderableWidget(Button.builder(
            Component.literal("Обновить список"),
            b -> { refreshFileList(); init(); }
        ).bounds(this.width / 2 - 100, this.height - 50, 200, 20).build());

        // Кнопка закрытия
        addRenderableWidget(Button.builder(
            Component.literal("Закрыть"),
            b -> onClose()
        ).bounds(this.width / 2 - 75, this.height - 25, 150, 20).build());
    }

    private void openBuildPreview(File file) {
        BuildData data = JsonLoader.load(file);
        if (data != null) {
            minecraft.setScreen(new BuildPreviewScreen(data, file));
        } else {
            // Ошибка загрузки
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        // Заголовок
        guiGraphics.drawCenteredString(font, title, width / 2, 10, 0xFFFFFF);
        
        // Информация о папке
        String pathInfo = "Папка: mybuilder_builds/";
        guiGraphics.drawString(font, pathInfo, 10, 10, 0xAAAAAA);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (buildFiles.size() > MAX_VISIBLE) {
            scrollOffset -= (int) deltaY;
            scrollOffset = Math.max(0, Math.min(scrollOffset, buildFiles.size() - MAX_VISIBLE));
            init(); // Пересоздать кнопки
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }
}
