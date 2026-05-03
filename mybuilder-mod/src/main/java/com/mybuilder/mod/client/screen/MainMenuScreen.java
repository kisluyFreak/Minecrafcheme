package com.mybuilder.mod.client.screen;

import com.mybuilder.mod.core.StructureLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.io.File;
import java.util.List;

public class MainMenuScreen extends Screen {

    private Button selectButton;
    private Button statsButton;
    private Button buildButton;
    private List<File> structures;

    public MainMenuScreen(Component title) {
        super(title);
        structures = StructureLoader.getAvailableStructures();
    }

    @Override
    protected void init() {
        int width = 200;
        int height = 20;
        int centerX = this.width / 2;
        int startY = this.height / 3;

        selectButton = Button.builder(Component.literal("Выбрать постройку (" + structures.size() + ")"), 
                btn -> minecraft.setScreen(new SelectStructureScreen(this, structures)))
                .bounds(centerX - width/2, startY, width, height)
                .build();
        addRenderableWidget(selectButton);

        statsButton = Button.builder(Component.literal("Статистика блоков"), 
                btn -> minecraft.setScreen(new BlockStatsScreen(this, null)))
                .bounds(centerX - width/2, startY + 30, width, height)
                .build();
        addRenderableWidget(statsButton);

        buildButton = Button.builder(Component.literal("Режим строительства"), 
                btn -> minecraft.setScreen(new BuildScreen(this, null)))
                .bounds(centerX - width/2, startY + 60, width, height)
                .build();
        addRenderableWidget(buildButton);

        addRenderableWidget(Button.builder(Component.literal("Назад в игру"), 
                btn -> minecraft.setScreen(null))
                .bounds(centerX - width/2, startY + 100, width, height)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(font, "MyBuilder v1.0", this.width / 2, this.height / 4, 0xFFFFFF);
        graphics.drawCenteredString(font, "Нажми B чтобы открыть меню", this.width / 2, this.height / 2 + 50, 0xAAAAAA);
    }
}
