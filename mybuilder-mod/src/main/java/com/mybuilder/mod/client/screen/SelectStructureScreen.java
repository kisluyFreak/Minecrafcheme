package com.mybuilder.mod.client.screen;

import com.mybuilder.mod.core.StructureLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.io.File;
import java.util.List;

public class SelectStructureScreen extends Screen {

    private final Screen parent;
    private final List<File> structures;
    private int selectedIndex = -1;

    public SelectStructureScreen(Screen parent, List<File> structures) {
        super(Component.literal("Выбор постройки"));
        this.parent = parent;
        this.structures = structures;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        int buttonWidth = 300;
        int buttonHeight = 20;

        for (int i = 0; i < structures.size(); i++) {
            File file = structures.get(i);
            final int index = i;
            Button btn = Button.builder(Component.literal(file.getName()), 
                    b -> {
                        selectedIndex = index;
                        try {
                            StructureLoader.StructureData data = StructureLoader.loadStructure(file);
                            minecraft.setScreen(new BuildScreen(this, data));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .bounds(centerX - buttonWidth/2, startY + i * (buttonHeight + 5), buttonWidth, buttonHeight)
                    .build();
            addRenderableWidget(btn);
        }

        if (structures.isEmpty()) {
            addRenderableWidget(new StringWidget(centerX - 150, startY, 300, 20, 
                Component.literal("Нет построек! Положите JSON в mybuilder_builds/"), font));
        }

        addRenderableWidget(Button.builder(Component.literal("Назад"), 
                btn -> minecraft.setScreen(parent))
                .bounds(centerX - buttonWidth/2, this.height - 40, buttonWidth, buttonHeight)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(font, "Доступные постройки", this.width / 2, 20, 0xFFFFFF);
    }
}
