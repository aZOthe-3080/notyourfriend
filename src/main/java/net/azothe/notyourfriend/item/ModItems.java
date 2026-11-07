package net.azothe.notyourfriend.item;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.item.items.ShardItem;
import net.azothe.notyourfriend.item.items.ThornItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NotYourFriend.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final DeferredItem<Item> TWEEZERS = ITEMS.registerSimpleItem("tweezers");
    public static final DeferredItem<Item> KNIFE = ITEMS.registerSimpleItem("knife",
            properties -> properties
                    .sword(ToolMaterial.IRON, 1f, -1.5f)
                    .durability(250));

    public static final DeferredItem<Item> SHARD = ITEMS.registerItem("shard", ShardItem::new);
    public static final DeferredItem<Item> THORN = ITEMS.registerItem("thorn", ThornItem::new);
}
