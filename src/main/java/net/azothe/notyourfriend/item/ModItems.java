package net.azothe.notyourfriend.item;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.item.itemclass.ShardItem;
import net.azothe.notyourfriend.item.itemclass.ThornItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NotYourFriend.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final DeferredItem<Item> SHARD = ITEMS.registerItem("shard",
            properties -> new ShardItem(properties
                    .useCooldown(5)));

    public static final DeferredItem<Item> THORN = ITEMS.registerItem("thorn",
            properties -> new ThornItem(properties
                    .useCooldown(5)));
}
