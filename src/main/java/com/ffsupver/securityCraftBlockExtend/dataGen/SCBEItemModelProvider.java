package com.ffsupver.securityCraftBlockExtend.dataGen;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class SCBEItemModelProvider extends ItemModelProvider {

    public SCBEItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SecurityCraftBlockExtend.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        handheld(SCBEItems.CHAINED_UNIVERSAL_BLOCK_REINFORCER_LV1.get());
        handheld(SCBEItems.CHAINED_UNIVERSAL_BLOCK_REINFORCER_LV2.get());
        handheld(SCBEItems.CHAINED_UNIVERSAL_BLOCK_REINFORCER_LV3.get());
    }

    private void handheld(Item item) {
        ResourceLocation itemKey = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
        handheld(itemKey,SecurityCraftBlockExtend.asResource("item/"+itemKey.getPath()));
    }
    private void handheld(ResourceLocation itemKey, ResourceLocation texture){
        getBuilder(itemKey.toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", texture);
    }
}