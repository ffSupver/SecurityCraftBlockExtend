package com.ffsupver.securityCraftBlockExtend.dataGen;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SCBEBlockTagsProvider extends BlockTagsProvider {

    public SCBEBlockTagsProvider(PackOutput output,
                                 CompletableFuture<HolderLookup.Provider> lookupProvider,
                                 @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SecurityCraftBlockExtend.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // ===== 原生方块标签 =====
//        tag(BlockTags.MINEABLE_WITH_PICKAXE)
//                .add(SCBEBlocks.REINFORCED_REINFORCED_DEEPSLATE.get());
//        tag(BlockTags.NEEDS_STONE_TOOL)
//                .add(SCBEBlocks.REINFORCED_REINFORCED_DEEPSLATE.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(SCBEBlocks.REINFORCED_WET_SPONGE.get());

        // ===== 兼容模块方块标签 =====
        // 先把 block -> tags 映射收集起来，按 tag 分组
        Map<TagKey<Block>, List<Block>> grouped = new HashMap<>();
        for (SCBEBlockTagData data : Mods.collectBlockTagData()) {
            Block block = data.block().get();
            for (TagKey<Block> tag : data.tags()) {
                grouped.computeIfAbsent(tag, k -> new ArrayList<>()).add(block);
            }
        }
        // 再逐个 tag 写入
        grouped.forEach((tag, blocks) -> tag(tag).add(blocks.toArray(Block[]::new)));
    }
}