package com.ffsupver.securityCraftBlockExtend.compat.simplyLight;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.compat.SCBEModCompat;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import com.flanks255.simplylight.SLBlocks;
import com.flanks255.simplylight.SimplyLight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData.BlockType.ON2STATE;
import static com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks.BLOCKS;

public class SimplyLightCompat implements SCBEModCompat {

    private static final List<SCBEBlockModelData> modelDataList = new ArrayList<>();
    private static final List<SCBEBlockLootData> lootDataList = new ArrayList<>();
    private static final List<SCBEBlockTagData> tagDataList = new ArrayList<>();
    private static final List<Supplier<ItemLike>> creativeTabItems = new ArrayList<>();
    @Override
    public String getModId() {
        return Mods.ModIds.SIMPLYLIGHT.ModId;
    }


    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK = registerReinforcedLamp(
            "illuminant_block", false, DyeColor.WHITE, SLBlocks.ILLUMINANTBLOCK::get,
            "full_block/illuminant_block", "illuminant_block_on"
    );
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_ON = registerReinforcedLamp(
            "illuminant_block_on", true, DyeColor.WHITE, SLBlocks.ILLUMINANTBLOCK_ON::get,
            "full_block/illuminant_block", "illuminant_block_on"
    );
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_BLACK = registerReinforcedLamp(false,DyeColor.BLACK,SLBlocks.ILLUMINANT_BLOCK_BLACK::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_BLACK_ON = registerReinforcedLamp(true,DyeColor.BLACK,SLBlocks.ILLUMINANT_BLOCK_BLACK_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_BLUE = registerReinforcedLamp(false,DyeColor.BLUE,SLBlocks.ILLUMINANT_BLOCK_BLUE::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_BLUE_ON = registerReinforcedLamp(true,DyeColor.BLUE,SLBlocks.ILLUMINANT_BLOCK_BLUE_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_BROWN = registerReinforcedLamp(false,DyeColor.BROWN,SLBlocks.ILLUMINANT_BLOCK_BROWN::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_BROWN_ON = registerReinforcedLamp(true,DyeColor.BROWN,SLBlocks.ILLUMINANT_BLOCK_BROWN_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_CYAN = registerReinforcedLamp(false,DyeColor.CYAN,SLBlocks.ILLUMINANT_BLOCK_CYAN::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_CYAN_ON = registerReinforcedLamp(true,DyeColor.CYAN,SLBlocks.ILLUMINANT_BLOCK_CYAN_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_GRAY = registerReinforcedLamp(false,DyeColor.GRAY,SLBlocks.ILLUMINANT_BLOCK_GRAY::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_GRAY_ON = registerReinforcedLamp(true,DyeColor.GRAY,SLBlocks.ILLUMINANT_BLOCK_GRAY_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_GREEN = registerReinforcedLamp(false,DyeColor.GREEN,SLBlocks.ILLUMINANT_BLOCK_GREEN::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_GREEN_ON = registerReinforcedLamp(true,DyeColor.GREEN,SLBlocks.ILLUMINANT_BLOCK_GREEN_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_LIGHT_BLUE = registerReinforcedLamp(false,DyeColor.LIGHT_BLUE,SLBlocks.ILLUMINANT_BLOCK_LIGHT_BLUE::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_LIGHT_BLUE_ON = registerReinforcedLamp(true,DyeColor.LIGHT_BLUE,SLBlocks.ILLUMINANT_BLOCK_LIGHT_BLUE_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_LIGHT_GRAY = registerReinforcedLamp(false,DyeColor.LIGHT_GRAY,SLBlocks.ILLUMINANT_BLOCK_LIGHT_GRAY::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_LIGHT_GRAY_ON = registerReinforcedLamp(true,DyeColor.LIGHT_GRAY,SLBlocks.ILLUMINANT_BLOCK_LIGHT_GRAY_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_LIME = registerReinforcedLamp(false,DyeColor.LIME,SLBlocks.ILLUMINANT_BLOCK_LIME::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_LIME_ON = registerReinforcedLamp(true,DyeColor.LIME,SLBlocks.ILLUMINANT_BLOCK_LIME_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_MAGENTA = registerReinforcedLamp(false,DyeColor.MAGENTA,SLBlocks.ILLUMINANT_BLOCK_MAGENTA::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_MAGENTA_ON = registerReinforcedLamp(true,DyeColor.MAGENTA,SLBlocks.ILLUMINANT_BLOCK_MAGENTA_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_ORANGE = registerReinforcedLamp(false,DyeColor.ORANGE,SLBlocks.ILLUMINANT_BLOCK_ORANGE::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_ORANGE_ON = registerReinforcedLamp(true,DyeColor.ORANGE,SLBlocks.ILLUMINANT_BLOCK_ORANGE_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_PINK = registerReinforcedLamp(false,DyeColor.PINK,SLBlocks.ILLUMINANT_BLOCK_PINK::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_PINK_ON = registerReinforcedLamp(true,DyeColor.PINK,SLBlocks.ILLUMINANT_BLOCK_PINK_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_PURPLE = registerReinforcedLamp(false,DyeColor.PURPLE,SLBlocks.ILLUMINANT_BLOCK_PURPLE::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_PURPLE_ON = registerReinforcedLamp(true,DyeColor.PURPLE,SLBlocks.ILLUMINANT_BLOCK_PURPLE_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_RED = registerReinforcedLamp(false,DyeColor.RED,SLBlocks.ILLUMINANT_BLOCK_RED::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_RED_ON = registerReinforcedLamp(true,DyeColor.RED,SLBlocks.ILLUMINANT_BLOCK_RED_ON::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_YELLOW = registerReinforcedLamp(false,DyeColor.YELLOW,SLBlocks.ILLUMINANT_BLOCK_YELLOW::get);
    public static RegistryObject<ReinforcedLampBlock> ILLUMINANT_BLOCK_YELLOW_ON = registerReinforcedLamp(true,DyeColor.YELLOW,SLBlocks.ILLUMINANT_BLOCK_YELLOW_ON::get);
    public static RegistryObject<ReinforcedSlabLampBlock> REINFORCED_ILLUMINANT_SLAB = SCBEBlocks.registerBlock(
            "reinforced_illuminant_slab",
            p->new ReinforcedSlabLampBlock(8.0,p,SLBlocks.ILLUMINANTSLAB::get),
            SCBEBlocks.reinforcedCopySupplier(SLBlocks.ILLUMINANTSLAB::get, UnaryOperator.identity()),
            BLOCKS
    );
    public static RegistryObject<ReinforcedSlabLampBlock> REINFORCED_ILLUMINANT_PANEL = SCBEBlocks.registerBlock(
            "reinforced_illuminant_panel",
            p->new ReinforcedSlabLampBlock(4.0,p,SLBlocks.ILLUMINANTPANEL::get),
            SCBEBlocks.reinforcedCopySupplier(SLBlocks.ILLUMINANTPANEL::get, UnaryOperator.identity()),
            BLOCKS
    );

    private static RegistryObject<ReinforcedLampBlock> registerReinforcedLamp(boolean defaultState,DyeColor dyeColor,Supplier<Block> vanillaBlock){
        return registerReinforcedLamp(
                "illuminant_"+dyeColor.getName()+"_block"+(defaultState?"_on":""), defaultState, dyeColor, vanillaBlock,
                "full_block/illuminant_"+dyeColor.getName()+"_block", "full_block/illuminant_"+dyeColor.getName()+"_block_on"
        );
    }

    private static RegistryObject<ReinforcedLampBlock> registerReinforcedLamp(String name,boolean defaultState,DyeColor dyeColor, Supplier<Block> vanillaBlock,String offTexture,String onTexture){
        RegistryObject<ReinforcedLampBlock> reg = SCBEBlocks.registerBlock(
                name,
                p->new ReinforcedLampBlock(defaultState, dyeColor,p, vanillaBlock),
                SCBEBlocks.reinforcedCopySupplier(vanillaBlock, UnaryOperator.identity()),
                BLOCKS
        );
        modelDataList.add(
                new SCBEBlockModelData(
                        name,
                        reg,
                        SecurityCraftBlockExtend.asResource(defaultState ? "default_on" : "default_off"),
                        Map.of(
                                "off_all", simplyLightLoc("block/"+offTexture),
                                "on_all", simplyLightLoc("block/"+onTexture)
                        ),
                        ON2STATE
                )
        );
        lootDataList.add(SCBEBlockLootData.dropSelf(reg));
        tagDataList.add(SCBEBlockTagData.mineWithPickaxe(reg::get));
        creativeTabItems.add(reg::get);
        SCBEBlocks.registerReinforcedBlockMapping(reg,reg);
        return reg;
    }

    @Override
    public void init(IEventBus eventBus) {
        SCBEBlocks.registerReinforcedBlockMapping(REINFORCED_ILLUMINANT_SLAB,REINFORCED_ILLUMINANT_SLAB);
        SCBEBlocks.registerReinforcedBlockMapping(REINFORCED_ILLUMINANT_PANEL,REINFORCED_ILLUMINANT_PANEL);
    }


    @Override
    public List<SCBEBlockModelData> getBlockModelData() {
        return modelDataList;
    }

    @Override
    public List<SCBEBlockLootData> getBlockLootData() {
        lootDataList.addAll(List.of(
                SCBEBlockLootData.dropSelf(REINFORCED_ILLUMINANT_SLAB),
                SCBEBlockLootData.dropSelf(REINFORCED_ILLUMINANT_PANEL)
        ));
        return lootDataList;
    }

    @Override
    public List<SCBEBlockTagData> getBlockTagData() {
        tagDataList.addAll(List.of(
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_ILLUMINANT_SLAB::get),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_ILLUMINANT_PANEL::get)
        ));
        return tagDataList;
    }

    @Override
    public List<Supplier<ItemLike>> getCreativeTabItems() {
        creativeTabItems.addAll(List.of(
                REINFORCED_ILLUMINANT_SLAB::get,
                REINFORCED_ILLUMINANT_PANEL::get
        ));
        return creativeTabItems;
    }

    private static ResourceLocation simplyLightLoc(String path) {
        return ResourceLocation.tryBuild(SimplyLight.MODID, path);
    }
}
