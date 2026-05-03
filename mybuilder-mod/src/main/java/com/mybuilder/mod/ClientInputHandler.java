package com.mybuilder.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MyBuilderMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientInputHandler {

    private boolean wasPressed = false;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {
        if (MyBuilderMod.OPEN_MENU_KEY != null && MyBuilderMod.OPEN_MENU_KEY.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen == null) {
                mc.setScreen(new com.mybuilder.mod.client.screen.MainMenuScreen(Component.literal("MyBuilder")));
            }
        }
    }
}
