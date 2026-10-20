package com.ffsupver.securityCraftBlockExtend.registeries;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class SCBETags {
    public static class ItemTags{
        public static final TagKey<Item> BLOCK_REINFORCERS = itemTagKey("block_reinforcers");

        public static TagKey<Item> itemTagKey(String path){
            return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), SecurityCraftBlockExtend.asResource(path));
        }
    }
    public static class BlockTags{
        public static final TagKey<Block> REINFORCED_BLOCKS = blockTagKey(SecurityCraft.resLoc("reinforced_blocks")); // Disable mob spawn in DecreasedCraft
        public static TagKey<Block> blockTagKey(ResourceLocation tagLoc){
            return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), tagLoc);
        }
    }
}
