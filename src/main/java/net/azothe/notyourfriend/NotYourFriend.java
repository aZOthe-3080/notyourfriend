package net.azothe.notyourfriend;

import net.azothe.notyourfriend.creativemodetab.ModCreativeModTabs;
import net.azothe.notyourfriend.data.PlayerData;
import net.azothe.notyourfriend.item.ModItems;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Random;

@Mod(NotYourFriend.MOD_ID)
public class NotYourFriend {
    public static final String MOD_ID = "notyourfriend";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random RANDOM = new Random();

    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(MOD_ID, path); }

    public NotYourFriend(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::commonSetup);

        ModCreativeModTabs.register(eventBus);
        ModItems.register(eventBus);
        PlayerData.register(eventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Common setup");
    }
}
