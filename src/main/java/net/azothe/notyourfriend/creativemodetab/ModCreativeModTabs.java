package net.azothe.notyourfriend.creativemodetab;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NotYourFriend.MOD_ID);

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

    public static final Supplier<CreativeModeTab> NOT_YOUR_FRIEND_TAB = CREATIVE_MODE_TABS.register("not_your_friend_tab",
            () -> CreativeModeTab.builder()
                    .icon(ModItems.SHARD::toStack)
                    .title(Component.translatable("creativetab.notyourfriend"))
                    .withTabsBefore(
                            CreativeModeTabs.SPAWN_EGGS,
                            CreativeModeTabs.OP_BLOCKS
                    )
                    .noScrollBar()
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.SHARD);
                        output.accept(ModItems.THORN);
                        output.accept(ModItems.TWEEZERS);
                        output.accept(ModItems.KNIFE);
                    }))
                    .build());
}
