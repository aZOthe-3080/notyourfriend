package net.azothe.notyourfriend.data.datagen;

import net.azothe.notyourfriend.NotYourFriend;
import net.azothe.notyourfriend.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput output) {
        super(output, NotYourFriend.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.SHARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.THORN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.TWEEZERS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.KNIFE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    }
}
