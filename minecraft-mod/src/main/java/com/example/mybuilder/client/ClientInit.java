package com.example.mybuilder.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import com.example.mybuilder.MyBuilder;
import net.minecraft.client.Minecraft;
import com.example.mybuilder.client.screen.BuildListScreen;

@Mod.EventBusSubscriber(modid = MyBuilder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {
    public static final KeyMapping OPEN_MENU_KEY = new KeyMapping(
        "key.mybuilder.open_menu",
        GLFW.GLFW_KEY_B,
        "category.mybuilder"
    );

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Инициализация клиентской части
        });
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU_KEY);
    }

    public static void openMenu() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) {
            mc.setScreen(new BuildListScreen());
        }
    }
}
