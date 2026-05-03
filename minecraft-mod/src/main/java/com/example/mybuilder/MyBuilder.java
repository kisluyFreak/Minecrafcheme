package com.example.mybuilder;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.gui.GuiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;
import com.example.mybuilder.client.screen.BuildListScreen;
import java.io.File;

@Mod(MyBuilder.MOD_ID)
public class MyBuilder {
    public static final String MOD_ID = "mybuilder";
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBuilder.class);
    
    // Версия протокола сети (можно оставить пустым для клиентского мода)
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static File getConfigDir() {
        return new File("mybuilder_builds");
    }

    public MyBuilder() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Создаем папку для построек при запуске
        File buildDir = getConfigDir();
        if (!buildDir.exists()) {
            buildDir.mkdirs();
            LOGGER.info("Created builds directory: {}", buildDir.getAbsolutePath());
        } else {
            LOGGER.info("Builds directory found: {}", buildDir.getAbsolutePath());
        }
        
        LOGGER.info("MyBuilder mod initialized! Press 'B' to open the menu.");
    }
}
