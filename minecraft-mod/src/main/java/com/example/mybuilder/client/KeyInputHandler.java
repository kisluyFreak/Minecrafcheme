package com.example.mybuilder.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.example.mybuilder.MyBuilder;
import net.minecraft.client.Minecraft;
import com.example.mybuilder.client.screen.BuildListScreen;

@Mod.EventBusSubscriber(modid = MyBuilder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (ClientInit.OPEN_MENU_KEY.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen == null) {
                mc.setScreen(new BuildListScreen());
            }
        }
    }
}
