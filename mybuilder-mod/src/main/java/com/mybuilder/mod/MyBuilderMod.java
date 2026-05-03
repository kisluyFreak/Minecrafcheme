package com.mybuilder.mod;

import com.mybuilder.mod.client.screen.BuildScreen;
import com.mybuilder.mod.client.screen.MainMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterMenuScreensEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod("mybuilder")
public class MyBuilderMod {

    public static final String MOD_ID = "mybuilder";
    public static KeyMapping OPEN_MENU_KEY;

    public MyBuilderMod() {
        IEventBus modEventBus = MinecraftForge.EVENT_BUS;
        modEventBus.register(this);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Регистрация экранов будет здесь, если используем MenuType
        });
    }

    @SubscribeEvent
    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        OPEN_MENU_KEY = new KeyMapping(
            "key.mybuilder.open_menu",
            GLFW.GLFW_KEY_B,
            "category.mybuilder"
        );
        event.register(OPEN_MENU_KEY);
        
        MinecraftForge.EVENT_BUS.register(new ClientInputHandler());
    }
}
