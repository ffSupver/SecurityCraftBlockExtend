package com.ffsupver.securityCraftBlockExtend.dataGen;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 一个方块需要加入哪些方块标签。
 * 使用 Supplier 包裹方块，避免在数据生成之前加载可选模组的类。
 */
public record SCBEBlockTagData(
        Supplier<? extends Block> block,
        List<TagKey<Block>> tags
) {
    public static SCBEBlockTagData mineWithPickaxe(Supplier<Block> block){
        return new SCBEBlockTagData.Builder(block).pickaxe().reinforced().build();
    }

    public static class Builder{
        private final Supplier<? extends Block> block;
        private final List<TagKey<Block>> tags;
        public Builder(Supplier<? extends Block> block){
            this.block = block;
            this.tags = new ArrayList<>();
        }

        public Builder add(TagKey<Block> tag){
            this.tags.add(tag);
            return this;
        }

        public Builder pickaxe(){
            return this.add(BlockTags.MINEABLE_WITH_PICKAXE);
        }

        public Builder ironTool(){
            return this.add(BlockTags.NEEDS_IRON_TOOL);
        }

        public Builder reinforced(){
            return this.add(BlockTags.WITHER_IMMUNE).add(BlockTags.DRAGON_IMMUNE);
        }

        public SCBEBlockTagData build(){
            return new SCBEBlockTagData(this.block,this.tags);
        }
    }
}