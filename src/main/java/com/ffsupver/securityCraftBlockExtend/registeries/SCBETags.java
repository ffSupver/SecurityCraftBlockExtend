package com.ffsupver.securityCraftBlockExtend.registeries;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class SCBETags {
    public static class ItemTags{
        public static final TagKey<Item> BLOCK_REINFORCERS = itemTagKey("block_reinforcers");

        public static TagKey<Item> itemTagKey(String path){
            return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), SecurityCraftBlockExtend.asResource(path));
        }
    }
}
