package com.ffsupver.securityCraftBlockExtend.dataGen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Supplier;

import static com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockStateProvider.securitycraftLoc;

/**
 * 用于数据生成的方块模型数据。
 * 用 Supplier 包裹，避免在数据生成阶段直接引用可选模组的类。
 */
public class SCBEBlockModelData {
    public enum BlockType {
        SIMPLE,   // 普通方块 (cube_all, cube_bottom_top 等)
        STAIRS,   // 楼梯
        SLAB,     // 半砖
        WALL,      // 墙
        PILLAR,    // 轴向柱体（需要按 AXIS 旋转）
        CREATE_CONNECTED_PANE
    }

    private String name;
    private Supplier<? extends Block> block;
    private ResourceLocation parentModel;
    private Map<String, ResourceLocation> textures;
    private BlockType type;
    private final String renderType;
    private final SCBEBlockModelData itemModel;

    public SCBEBlockModelData(
            String name,
            Supplier<? extends Block> block,
            ResourceLocation parentModel,
            Map<String, ResourceLocation> textures,
            String renderType,
            BlockType type, SCBEBlockModelData itemModel
    ){
        this.name = name;
        this.block = block;
        this.parentModel = parentModel;
        this.textures = textures;
        this.renderType = renderType;
        this.type = type;
        this.itemModel = itemModel;
    }
    public SCBEBlockModelData(String name, Supplier<? extends Block> block, String parentModel, Map<String, ResourceLocation> textures,String renderType, BlockType type,SCBEBlockModelData itemModel) {
        this(name, block, securitycraftLoc(parentModel), textures,renderType, type, itemModel);
    }
    public SCBEBlockModelData(String name, Supplier<? extends Block> block, String parentModel, Map<String, ResourceLocation> textures,String renderType, BlockType type) {
        this(name, block, securitycraftLoc(parentModel), textures,renderType, type, null);
    }
    public SCBEBlockModelData(String name, Supplier<? extends Block> block, String parentModel, Map<String, ResourceLocation> textures) {
        this(name, block, securitycraftLoc(parentModel), textures,null, BlockType.SIMPLE, null);
    }
    public SCBEBlockModelData(String name, Supplier<? extends Block> block, String parentModel, Map<String, ResourceLocation> textures, BlockType type) {
        this(name, block, securitycraftLoc(parentModel), textures,null, type, null);
    }


    public String name() {
        return name;
    }
    public Supplier<? extends Block> block() {
        return block;
    }
    public ResourceLocation parentModel() {
        return parentModel;
    }
    public Map<String, ResourceLocation> textures() {
        return textures;
    }
    public BlockType type() {
        return type;
    }
    public String renderType() {
        return renderType;
    }
    public SCBEBlockModelData itemModel() {
        return itemModel;
    }
}