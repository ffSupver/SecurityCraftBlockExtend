package com.ffsupver.securityCraftBlockExtend.dataGen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 用于数据生成的方块模型数据。
 * 用 Supplier 包裹，避免在数据生成阶段直接引用可选模组的类。
 */
public class SCBEBlockModelData {
    public enum BlockType {
        SIMPLE,   // 普通方块 (cube_all, cube_bottom_top 等)
        STAIRS,   // 楼梯
        SLAB,     // 半砖
        WALL      // 墙
    }

    private String name;
    private Supplier<? extends Block> block;
    private String parentModel;
    private Map<String, ResourceLocation> textures;
    private BlockType type;

    public SCBEBlockModelData(
            String name,
            Supplier<? extends Block> block,
            String parentModel,
            Map<String, ResourceLocation> textures,
            BlockType type
    ){
        this.name = name;
        this.block = block;
        this.parentModel = parentModel;
        this.textures = textures;
        this.type = type;
    }
    public SCBEBlockModelData(String name, Supplier<? extends Block> block, String parentModel, Map<String, ResourceLocation> textures) {
        this(name, block, parentModel, textures, BlockType.SIMPLE);
    }

    public String name() {
        return name;
    }
    public Supplier<? extends Block> block() {
        return block;
    }
    public String parentModel() {
        return parentModel;
    }
    public Map<String, ResourceLocation> textures() {
        return textures;
    }
    public BlockType type() {
        return type;
    }
}