package com.ffsupver.securityCraftBlockExtend.compat.create.blocks;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston.ReinforcedMechanicalPistonBlock;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston.ReinforcedMechanicalPistonBlockEntity;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston.ReinforcedMechanicalPistonRenderer;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.reinforcedTrack.ReinforcedTrack;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.reinforcedTrack.ReinforcedTrackBlockItem;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.reinforcedTrack.ReinforcedTrackEntity;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.decoration.palettes.*;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.trains.track.TrackRenderer;
import com.simibubi.create.content.trains.track.TrackVisual;
import com.simibubi.create.foundation.block.connected.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.blocks.reinforced.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.ffsupver.securityCraftBlockExtend.compat.create.blocks.reinforcedTrack.ReinforcedTrack.REINFORCED_TRACK_MATERIAL;
import static com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData.BlockType.*;
import static com.simibubi.create.content.decoration.palettes.PaletteBlockPattern.LAYERED;

public class CreateReinforcedBlocks {
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(SecurityCraftBlockExtend.MODID);
    public static List<SCBEBlockLootData> blockLootData = new ArrayList<>();
    public static List<SCBEBlockTagData> blockTagData = new ArrayList<>();
    public static List<SCBEBlockModelData> blockModelData = new ArrayList<>();
    public static List<Supplier<ItemLike>> creativeTabItems = new ArrayList<>();

    public static final BlockEntry<BaseReinforcedBlock> REINFORCED_BRASS_BLOCK = registerReinforcedBlock("reinforced_brass_block", AllBlocks.BRASS_BLOCK);
    public static final BlockEntry<BaseReinforcedBlock> REINFORCED_ANDESITE_ALLOY_BLOCK = registerReinforcedBlock("reinforced_andesite_alloy_block", AllBlocks.ANDESITE_ALLOY_BLOCK);

    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_FRAMED_GLASS =
            registerReinforcedGlassBlock(
                    "reinforced_framed_glass",
                    AllPaletteBlocks.FRAMED_GLASS::get,
                    builder -> builder
                            .onRegister(CreateRegistrate.connectedTextures(
                                    () -> new SimpleCTBehaviour(AllSpriteShifts.FRAMED_GLASS)
                            ))
            );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_HORIZONTAL_FRAMED_GLASS =
            registerReinforcedGlassBlock(
                    "reinforced_horizontal_framed_glass",
                    AllPaletteBlocks.HORIZONTAL_FRAMED_GLASS::get,
                    builder -> builder
                            .onRegister(CreateRegistrate.connectedTextures(
                                    () -> new HorizontalCTBehaviour(AllSpriteShifts.HORIZONTAL_FRAMED_GLASS, AllSpriteShifts.FRAMED_GLASS)
                            ))
            );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_VERTICAL_FRAMED_GLASS =
            registerReinforcedGlassBlock(
                    "reinforced_vertical_framed_glass",
                    AllPaletteBlocks.VERTICAL_FRAMED_GLASS::get,
                    builder -> builder
                            .onRegister(CreateRegistrate.connectedTextures(
                                    () -> new HorizontalCTBehaviour(AllSpriteShifts.VERTICAL_FRAMED_GLASS)
                            ))
            );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_TILED_GLASS =
            registerReinforcedGlassBlock(
                    "reinforced_tiled_glass",
                    AllPaletteBlocks.TILED_GLASS::get,
                    b->{}
            );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_FRAMED_GLASS_PANE =
            registerReinforcedPaneBlock(
                    "reinforced_framed_glass_pane",
                    AllPaletteBlocks.FRAMED_GLASS_PANE::get,
                    builder -> builder
                            .onRegister(CreateRegistrate.connectedTextures(
                                    () -> new GlassPaneCTBehaviour(CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL, SecurityCraftBlockExtend.asResource("block/reinforced_framed_glass"),SecurityCraftBlockExtend.asResource("block/reinforced_framed_glass_connected")))
                            )),
                    false
            );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_HORIZONTAL_FRAMED_GLASS_PANE =
            registerReinforcedPaneBlock(
                    "reinforced_horizontal_framed_glass_pane",
                    AllPaletteBlocks.HORIZONTAL_FRAMED_GLASS_PANE::get,
                    builder -> builder
                            .onRegister(CreateRegistrate.connectedTextures(
                                    () -> new GlassPaneCTBehaviour(CTSpriteShifter.getCT(AllCTTypes.HORIZONTAL_KRYPPERS, SecurityCraftBlockExtend.asResource("block/reinforced_horizontal_framed_glass"),SecurityCraftBlockExtend.asResource("block/reinforced_horizontal_framed_glass_connected")))
                            )),
                    false
            );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_VERTICAL_FRAMED_GLASS_PANE =
            registerReinforcedPaneBlock(
                    "reinforced_vertical_framed_glass_pane",
                    AllPaletteBlocks.VERTICAL_FRAMED_GLASS_PANE::get,
                    builder -> builder
                            .onRegister(CreateRegistrate.connectedTextures(
                                    () -> new GlassPaneCTBehaviour(CTSpriteShifter.getCT(AllCTTypes.VERTICAL, SecurityCraftBlockExtend.asResource("block/reinforced_vertical_framed_glass"),SecurityCraftBlockExtend.asResource("block/reinforced_vertical_framed_glass_connected")))
                            )),
                    false
            );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_BRASS_BARS =
            registerReinforcedPaneBlock(
                    "reinforced_brass_bars",
                    AllBlocks.BRASS_BARS::get,
                    false
            );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_ANDESITE_BARS =
            registerReinforcedPaneBlock(
                    "reinforced_andesite_bars",
                    AllBlocks.ANDESITE_BARS::get,
                    false
            );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_COPPER_BARS =
            registerReinforcedPaneBlock(
                    "reinforced_copper_bars",
                    AllBlocks.COPPER_BARS::get,
                    false
            );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_OAK_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_oak_window_pane","oak_window",AllPaletteBlocks.OAK_WINDOW_PANE::get,ResourceLocation.parse("block/oak_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_SPRUCE_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_spruce_window_pane","spruce_window",AllPaletteBlocks.SPRUCE_WINDOW_PANE::get,ResourceLocation.parse("block/spruce_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_BIRCH_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_birch_window_pane","birch_window",AllPaletteBlocks.BIRCH_WINDOW_PANE::get,ResourceLocation.parse("block/birch_planks"),"translucent"
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_JUNGLE_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_jungle_window_pane","jungle_window",AllPaletteBlocks.JUNGLE_WINDOW_PANE::get,ResourceLocation.parse("block/jungle_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_ACACIA_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_acacia_window_pane","acacia_window",AllPaletteBlocks.ACACIA_WINDOW_PANE::get,ResourceLocation.parse("block/acacia_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_DARK_OAK_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_dark_oak_window_pane","dark_oak_window",AllPaletteBlocks.DARK_OAK_WINDOW_PANE::get,ResourceLocation.parse("block/dark_oak_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_MANGROVE_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_mangrove_window_pane","mangrove_window",AllPaletteBlocks.MANGROVE_WINDOW_PANE::get,ResourceLocation.parse("block/mangrove_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_CRIMSON_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_crimson_window_pane","crimson_window",AllPaletteBlocks.CRIMSON_WINDOW_PANE::get,ResourceLocation.parse("block/crimson_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_WARPED_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_warped_window_pane","warped_window",AllPaletteBlocks.WARPED_WINDOW_PANE::get,ResourceLocation.parse("block/warped_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_CHEERY_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_cherry_window_pane","cherry_window",AllPaletteBlocks.CHERRY_WINDOW_PANE::get,ResourceLocation.parse("block/cherry_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_BAMBOO_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_bamboo_window_pane","bamboo_window",AllPaletteBlocks.BAMBOO_WINDOW_PANE::get,ResourceLocation.parse("block/bamboo_planks")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_ORNATE_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_ornate_iron_window_pane","ornate_iron_window",AllPaletteBlocks.ORNATE_IRON_WINDOW_PANE::get,Create.asResource("block/palettes/ornate_iron_window_pane_top")
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_INDUSTRIAL_IRON_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_industrial_iron_window_pane","industrial_iron_window",
            AllPaletteBlocks.INDUSTRIAL_IRON_WINDOW_PANE::get,Create.asResource("block/palettes/industrial_iron_window_pane_top"),
            ()->AllSpriteShifts.INDUSTRIAL_IRON_WINDOW,"cutout_mipped"
    );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_WEATHERED_IRON_WINDOW_PANE = registerReinforcedWindowPane(
            "reinforced_weathered_iron_window_pane","weathered_iron_window",AllPaletteBlocks.WEATHERED_IRON_WINDOW_PANE::get,
            Create.asResource("block/palettes/weathered_iron_window_pane_top"),"translucent", WeatheredIronWindowPaneCTBehaviour::new
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_OAK_WINDOW = registerReinforcedWindow(
            "reinforced_oak_window","oak_window",AllPaletteBlocks.OAK_WINDOW::get,ResourceLocation.parse("block/oak_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_SPRUCE_WINDOW = registerReinforcedWindow(
            "reinforced_spruce_window","spruce_window",AllPaletteBlocks.SPRUCE_WINDOW::get,ResourceLocation.parse("block/spruce_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_BIRCH_WINDOW = registerReinforcedWindow(
            "reinforced_birch_window","birch_window",AllPaletteBlocks.BIRCH_WINDOW::get,ResourceLocation.parse("block/birch_planks"),"translucent"
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_JUNGLE_WINDOW = registerReinforcedWindow(
            "reinforced_jungle_window","jungle_window",AllPaletteBlocks.JUNGLE_WINDOW::get,ResourceLocation.parse("block/jungle_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_ACACIA_WINDOW = registerReinforcedWindow(
            "reinforced_acacia_window","acacia_window",AllPaletteBlocks.ACACIA_WINDOW::get,ResourceLocation.parse("block/acacia_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_DARK_OAK_WINDOW = registerReinforcedWindow(
            "reinforced_dark_oak_window","dark_oak_window",AllPaletteBlocks.DARK_OAK_WINDOW::get,ResourceLocation.parse("block/dark_oak_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_MANGROVE_WINDOW = registerReinforcedWindow(
            "reinforced_mangrove_window","mangrove_window",AllPaletteBlocks.MANGROVE_WINDOW::get,ResourceLocation.parse("block/mangrove_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_CRIMSON_WINDOW = registerReinforcedWindow(
            "reinforced_crimson_window","crimson_window",AllPaletteBlocks.CRIMSON_WINDOW::get,ResourceLocation.parse("block/crimson_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_WARPED_WINDOW = registerReinforcedWindow(
            "reinforced_warped_window","warped_window",AllPaletteBlocks.WARPED_WINDOW::get,ResourceLocation.parse("block/warped_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_CHERRY_WINDOW = registerReinforcedWindow(
            "reinforced_cherry_window","cherry_window",AllPaletteBlocks.CHERRY_WINDOW::get,ResourceLocation.parse("block/cherry_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_BAMBOO_WINDOW = registerReinforcedWindow(
            "reinforced_bamboo_window","bamboo_window",AllPaletteBlocks.BAMBOO_WINDOW::get,ResourceLocation.parse("block/bamboo_planks")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_ORNATE_IRON_WINDOW = registerReinforcedWindow(
            "reinforced_ornate_iron_window","ornate_iron_window",AllPaletteBlocks.ORNATE_IRON_WINDOW::get,Create.asResource("block/palettes/ornate_iron_window_end")
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_INDUSTRIAL_IRON_WINDOW = registerReinforcedWindow(
            "reinforced_industrial_iron_window","industrial_iron_window",AllPaletteBlocks.INDUSTRIAL_IRON_WINDOW::get,Create.asResource("block/palettes/industrial_iron_window_end"),
            ()->AllSpriteShifts.INDUSTRIAL_IRON_WINDOW,"cutout_mipped"
    );
    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_WEATHERED_IRON_WINDOW = registerReinforcedWindow(
            "reinforced_weathered_iron_window",Create.asResource("block/palettes/weathered_iron_window"),AllPaletteBlocks.WEATHERED_IRON_WINDOW::get,
            Set.of(
                    Create.asResource("block/palettes/weathered_iron_window_1_end"),
                    Create.asResource("block/palettes/weathered_iron_window_2_end"),
                    Create.asResource("block/palettes/weathered_iron_window_3_end"),
                    Create.asResource("block/palettes/weathered_iron_window_4_end")
            ),
            "translucent", WeatheredIronWindowCTBehaviour::new
    );

    public static final BlockEntry<ReinforcedMechanicalPistonBlock> REINFORCED_MECHANICAL_PISTON_BLOCK = REGISTRATE
            .block("reinforced_mechanical_piston",ReinforcedMechanicalPistonBlock::normal)
            .properties(p->SCBEBlocks.reinforcedCopy(AllBlocks.MECHANICAL_PISTON.get(),UnaryOperator.identity()).noOcclusion())
            .setData(ProviderType.BLOCKSTATE,NonNullBiConsumer.noop())
            .setData(ProviderType.LOOT,NonNullBiConsumer.noop())
            .setData(ProviderType.LANG,NonNullBiConsumer.noop())
            .item().setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop()) // No item model data
            .build()
            .register();
    public static final BlockEntry<ReinforcedMechanicalPistonBlock> REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK = REGISTRATE
            .block("reinforced_sticky_mechanical_piston",ReinforcedMechanicalPistonBlock::sticky)
            .properties(p->SCBEBlocks.reinforcedCopy(AllBlocks.STICKY_MECHANICAL_PISTON.get(),UnaryOperator.identity()).noOcclusion())
            .setData(ProviderType.BLOCKSTATE,NonNullBiConsumer.noop())
            .setData(ProviderType.LOOT,NonNullBiConsumer.noop())
            .setData(ProviderType.LANG,NonNullBiConsumer.noop())
            .item().setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop()) // No item model data
            .build()
            .register();
    public static final BlockEntityEntry<ReinforcedMechanicalPistonBlockEntity> REINFORCED_MECHANICAL_PISTON_BLOCK_ENTITY = REGISTRATE
            .blockEntity("reinforced_mechanical_piston",ReinforcedMechanicalPistonBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual::shaft, false)
            .validBlocks(REINFORCED_MECHANICAL_PISTON_BLOCK,REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK)
            .renderer(()-> ReinforcedMechanicalPistonRenderer::new)
            .register();
    public static final BlockEntry<ReinforcedTrack> REINFORCED_TRACK = REGISTRATE
            .block("reinforced_track", p->(ReinforcedTrack)REINFORCED_TRACK_MATERIAL.createBlock(SCBEBlocks.reinforcedCopySupplier(AllBlocks.TRACK::get, UnaryOperator.identity()).get()))
            .setData(ProviderType.BLOCKSTATE,NonNullBiConsumer.noop())
            .setData(ProviderType.LOOT,NonNullBiConsumer.noop())
            .setData(ProviderType.LANG,NonNullBiConsumer.noop())
            .item(ReinforcedTrackBlockItem::new).setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop()) // No item model data
            .build()
            .register();

    public static final BlockEntityEntry<ReinforcedTrackEntity> REINFORCED_TRACK_ENTITY = REGISTRATE
            .blockEntity("reinforced_track", ReinforcedTrackEntity::new)
            .visual(() -> TrackVisual::new, false)
            .validBlocks(REINFORCED_TRACK)
            .renderer(()-> TrackRenderer::new)
            .register();

    public static void registerPaletteStoneBlocks(AllPaletteStoneTypes stoneTypes) {
        registerPaletteStoneBlocks(stoneTypes,false,false);
    }
    public static void registerPaletteStoneBlocks(AllPaletteStoneTypes stoneTypes,boolean registerBaseBlock,boolean hasNaturalVariants) {
        PaletteBlockPattern[] patterns = stoneTypes.variantTypes;
        PalettesVariantEntry variants = stoneTypes.getVariants();
        String stoneTypesName = stoneTypes.name().toLowerCase();

        if (registerBaseBlock){
            Map<String,ResourceLocation> modelTextures = new HashMap<>();
            if (hasNaturalVariants){
                for (int textureIndex = 0; textureIndex < 4; textureIndex++) {
                    modelTextures.put(
                            stoneTypesName + "_" + textureIndex,
                            paletteTexture("natural/" + stoneTypesName + "_" + textureIndex)
                    );
                }
            }else {
                modelTextures.put(
                        "reinforced_" +  stoneTypesName,
                        paletteTexture(stoneTypesName)
                );
            }
            registerReinforcedStoneBlock("reinforced_" + stoneTypesName, stoneTypes.getBaseBlock(), modelTextures);
        }

        int partialIdx = 0;

        for (int i = 0; i < patterns.length; i++) {
            PaletteBlockPattern pattern = patterns[i];

            // ================= 整块方块 =================
            BlockEntry<? extends Block> blockEntry = variants.registeredBlocks.get(i);
            String name = blockEntry.getId().getPath();
            String reinforcedName = "reinforced_" + name;
            BlockEntry<? extends BaseReinforcedBlock> reinforced;
            if (pattern.equals(LAYERED)){
                final String variant = stoneTypesName;
                reinforced = registerReinforcedBlock(reinforcedName, blockEntry::get,
                    builder -> builder.onRegister(
                            CreateRegistrate.connectedTextures(() ->
                                    new HorizontalCTBehaviour(
                                            createCTShift(variant, "layered", AllCTTypes.HORIZONTAL_KRYPPERS),
                                            createCTShift(variant, "cap",     AllCTTypes.OMNIDIRECTIONAL)
                                    )
                            )
                    )
                );
            }else if (pattern.equals(PaletteBlockPattern.PILLAR)) {
                final String variant = stoneTypesName;
                reinforced = registerReinforcedPillarBlock(reinforcedName, blockEntry::get,
                        b->b.onRegister(
                                CreateRegistrate.connectedTextures(() ->
                                        new RotatedPillarCTBehaviour(
                                                createCTShift(variant, "pillar", AllCTTypes.RECTANGLE),
                                                createCTShift(variant, "cap",    AllCTTypes.OMNIDIRECTIONAL)
                                        )
                                )
                        )
                );
            } else {
                reinforced = registerReinforcedBlock(reinforcedName, blockEntry::get);
            }

            SecurityCraftBlockExtend.LOGGER.info(
                    "Registered reinforced block: {} (pattern={})", reinforcedName, pattern);

            String blockModel;
            Map<String, ResourceLocation> blockTextures;
            SCBEBlockModelData.BlockType blockType = SIMPLE;
            if (pattern.equals(LAYERED)) {
                blockModel = "block/reinforced_cube_column";
                blockTextures = Map.of(
                        "end", paletteTexture(stoneTypesName, "cap"),
                        "side", paletteTexture(stoneTypesName, "layered")
                );
            } else if (pattern.equals(PaletteBlockPattern.PILLAR)) {
                blockModel = "block/reinforced_cube_column";
                blockTextures = Map.of(
                        "end", paletteTexture(stoneTypesName, "cap"),
                        "side", paletteTexture(stoneTypesName, "pillar")
                );
                blockType = SCBEBlockModelData.BlockType.PILLAR;
            } else {// CUT / BRICKS / SMALL_BRICKS / POLISHED —— 都是 cube_all
                blockModel = "block/reinforced_cube_all";
                blockTextures = Map.of(
                        "all", paletteTexture(stoneTypesName, pattern.getTexture(0))
                );
            }
            blockModelData.add(new SCBEBlockModelData(
                    reinforcedName, reinforced, blockModel, blockTextures,blockType
            ));

            blockLootData.add(new SCBEBlockLootData(reinforced, SCBEBlockLootData.BlockLootType.DROP_SELF));
            blockTagData.add(SCBEBlockTagData.mineWithPickaxe(reinforced::get));

            // ================= 该 pattern 下的 partials =================
            for (PaletteBlockPartial<?> partial : pattern.getPartials()) {
                BlockEntry<? extends Block> partialEntry = variants.registeredPartials.get(partialIdx++);
                String partialPath = partialEntry.getId().getPath();
                String reinforcedPartialName = "reinforced_" + partialPath;

                if (partial == PaletteBlockPartial.STAIR) {
                    BlockEntry<ReinforcedStairsBlock> reinforcedStair =
                            registerReinforcedStairBlock(reinforcedPartialName, partialEntry::get);

                    SecurityCraftBlockExtend.LOGGER.info(
                            "Registered reinforced stair: {} (pattern={})", reinforcedPartialName, pattern);

                    ResourceLocation texture = paletteTexture(stoneTypesName, pattern.getTexture(0));
                    blockModelData.add(new SCBEBlockModelData(
                            reinforcedPartialName,
                            reinforcedStair,
                            "block/reinforced_stairs",
                            Map.of("bottom", texture, "top", texture, "side", texture),
                            STAIRS
                    ));

                    blockLootData.add(new SCBEBlockLootData(reinforcedStair, SCBEBlockLootData.BlockLootType.DROP_SELF));
                    blockTagData.add(new SCBEBlockTagData.Builder(reinforcedStair).pickaxe().reinforced().add(BlockTags.STAIRS).build());
                } else if (partial == PaletteBlockPartial.SLAB || partial == PaletteBlockPartial.UNIQUE_SLAB) {
                    BlockEntry<ReinforcedSlabBlock> reinforcedSlab =
                            registerReinforcedSlabBlock(reinforcedPartialName, partialEntry::get);

                    SecurityCraftBlockExtend.LOGGER.info(
                            "Registered reinforced slab: {} (pattern={})", reinforcedPartialName, pattern);

                    ResourceLocation texture = paletteTexture(stoneTypesName, pattern.getTexture(0));
                    blockModelData.add(new SCBEBlockModelData(
                            reinforcedPartialName,
                            reinforcedSlab,
                            "block/reinforced_slab",
                            Map.of("bottom", texture, "top", texture, "side", texture),
                            SLAB
                    ));

                    blockLootData.add(new SCBEBlockLootData(reinforcedSlab, SCBEBlockLootData.BlockLootType.SLAB));
                    blockTagData.add(new SCBEBlockTagData.Builder(reinforcedSlab).pickaxe().reinforced().add(BlockTags.SLABS).build());
                } else if (partial == PaletteBlockPartial.WALL) {
                    BlockEntry<ReinforcedWallBlock> reinforcedWall =
                            registerReinforcedWallBlock(reinforcedPartialName, partialEntry::get);

                    SecurityCraftBlockExtend.LOGGER.info(
                            "Registered reinforced wall: {} (pattern={})", reinforcedPartialName, pattern);

                    ResourceLocation texture = paletteTexture(stoneTypesName, pattern.getTexture(0));
                    blockModelData.add(new SCBEBlockModelData(
                            reinforcedPartialName,
                            reinforcedWall,
                            "block/reinforced_wall_inventory",
                            Map.of("wall", texture),
                            WALL
                    ));

                    blockLootData.add(new SCBEBlockLootData(reinforcedWall, SCBEBlockLootData.BlockLootType.DROP_SELF));
                    blockTagData.add(new SCBEBlockTagData.Builder(reinforcedWall).pickaxe().reinforced().add(BlockTags.WALLS).build());
                } else {
                    SecurityCraftBlockExtend.LOGGER.warn(
                            "Unhandled partial type: {} (pattern={}, class={})",
                            reinforcedPartialName, pattern, partial.getClass().getSimpleName());
                }
            }
        }
    }

    protected static BlockEntry<ReinforcedGlassBlock> registerReinforcedWindow(String id, ResourceLocation sideTexture, Supplier<Block> vanillaBlock, Set<ResourceLocation> endTextures, String renderType, Supplier<ConnectedTextureBehaviour> connectedTexture){
        BlockEntry<ReinforcedGlassBlock> reg = registerReinforcedGlassBlock(id, vanillaBlock, b-> b.onRegister(CreateRegistrate.connectedTextures(
                connectedTexture
        )));

        Iterator<ResourceLocation> iterator = endTextures.iterator();
        int index = 1;
        while (iterator.hasNext()){
            ResourceLocation endTexture = iterator.next();
            blockModelData.add(new SCBEBlockModelData(
                    id+"_"+index, reg, "block/reinforced_cube_column",
                    Map.of("end", endTexture, "side", sideTexture),
                    renderType,
                    SIMPLE
            ));
            index++;
        }

        blockLootData.add(SCBEBlockLootData.dropSelf(reg));
        blockTagData.add(new  SCBEBlockTagData.Builder(reg).reinforced().build());
        creativeTabItems.add(reg::get);
        return reg;
    }
    protected static BlockEntry<ReinforcedGlassBlock> registerReinforcedWindow(String id, ResourceLocation originId, Supplier<Block> vanillaBlock, ResourceLocation endTexture, String renderType, Supplier<ConnectedTextureBehaviour> connectedTexture) {
        return registerReinforcedWindow(id,originId,vanillaBlock,Set.of(endTexture),renderType,connectedTexture);
    }
    protected static BlockEntry<ReinforcedGlassBlock> registerReinforcedWindow(String id, String originId, Supplier<Block> vanillaBlock, ResourceLocation endTexture, String renderType, Supplier<ConnectedTextureBehaviour> connectedTexture) {
        String sideTexturePath = "block/palettes/" + originId;
        return registerReinforcedWindow(id,Create.asResource(sideTexturePath),vanillaBlock,Set.of(endTexture),renderType,connectedTexture);
    }
    protected static BlockEntry<ReinforcedGlassBlock> registerReinforcedWindow(String id, String originId, Supplier<Block> vanillaBlock, ResourceLocation endTexture, Supplier<CTSpriteShiftEntry> ctShift, String renderType){
        return registerReinforcedWindow(id,originId,vanillaBlock,endTexture,renderType,()->new HorizontalCTBehaviour(ctShift.get()));
    }
    protected static BlockEntry<ReinforcedGlassBlock> registerReinforcedWindow(String id, String originId, Supplier<Block> vanillaBlock, ResourceLocation endTexture) {
        return registerReinforcedWindow(id, originId, vanillaBlock, endTexture, "minecraft:cutout_mipped");
    }
    protected static BlockEntry<ReinforcedGlassBlock> registerReinforcedWindow(String id, String originId, Supplier<Block> vanillaBlock, ResourceLocation endTexture, String renderType) {
        String sideTexturePath = "block/palettes/" + originId;
        return registerReinforcedWindow(id,originId,vanillaBlock,endTexture,()->CTSpriteShifter.getCT(AllCTTypes.VERTICAL, Create.asResource(sideTexturePath),Create.asResource(sideTexturePath+"_connected")),renderType);
    }
    protected static BlockEntry<ReinforcedPaneBlock> registerReinforcedWindowPane(String id, ResourceLocation originId, Supplier<Block> vanillaBlock, ResourceLocation edgeTexture, String renderType, Supplier<ConnectedTextureBehaviour> connectedTexture){

        BlockEntry<ReinforcedPaneBlock> reg = registerReinforcedPaneBlock(id, vanillaBlock,b->{
            b.onRegister(CreateRegistrate.connectedTextures(
                    connectedTexture
            ));
        }, true);

        blockModelData.add(new SCBEBlockModelData(
                id,reg,"",
                Map.of("edge",edgeTexture,"pane",originId),
                renderType,
                CREATE_CONNECTED_PANE
        ));

        blockLootData.add(SCBEBlockLootData.dropSelf(reg));
        blockTagData.add(new  SCBEBlockTagData.Builder(reg).reinforced().add(SCTags.Blocks.REINFORCED_GLASS_PANES).build());
        creativeTabItems.add(reg::get);
        return reg;
    }
    protected static BlockEntry<ReinforcedPaneBlock> registerReinforcedWindowPane(String id, String originId, Supplier<Block> vanillaBlock, ResourceLocation edgeTexture, String renderType, Supplier<ConnectedTextureBehaviour> connectedTexture) {
        String paneTexturePath = "block/palettes/" + originId;
        return registerReinforcedWindowPane(id,Create.asResource(paneTexturePath),vanillaBlock,edgeTexture,renderType,connectedTexture);
    }
    protected static BlockEntry<ReinforcedPaneBlock> registerReinforcedWindowPane(String id, String originId, Supplier<Block> vanillaBlock, ResourceLocation edgeTexture, Supplier<CTSpriteShiftEntry> ctShift, String renderType){
        return registerReinforcedWindowPane(id,originId,vanillaBlock,edgeTexture,renderType,()->new GlassPaneCTBehaviour(ctShift.get()));
    }
    protected static BlockEntry<ReinforcedPaneBlock> registerReinforcedWindowPane(String id, String originId, Supplier<Block> vanillaBlock, ResourceLocation edgeTexture, String renderType) {
        String paneTexturePath = "block/palettes/" + originId;
        return registerReinforcedWindowPane(id,originId,vanillaBlock,edgeTexture,()->CTSpriteShifter.getCT(AllCTTypes.VERTICAL, Create.asResource(paneTexturePath),Create.asResource(paneTexturePath+"_connected")),renderType);
    }
    protected static BlockEntry<ReinforcedPaneBlock> registerReinforcedWindowPane(String id, String originId, Supplier<Block> vanillaBlock, ResourceLocation edgeTexture) {
        return registerReinforcedWindowPane(id, originId, vanillaBlock, edgeTexture, "minecraft:cutout_mipped");
    }

    private static ResourceLocation paletteTexture(String variant, String texture) {
        String fileName = variant + (texture.equals("cut") ? "_" : "_cut_") + texture;
        return paletteTexture(texture + "/" + fileName);
    }

    private static ResourceLocation paletteTexture(String texture) {
        return Create.asResource("block/palettes/stone_types/" + texture);
    }

    /**
     * 按 Create 的 ct(...) 语义，构造一个指向 _connected 变体的 CT shift。
     * src 就是 paletteTexture 生成的路径。
     */
    private static CTSpriteShiftEntry createCTShift(String variant, String texture, CTType type) {
        ResourceLocation src = paletteTexture(variant, texture);
        ResourceLocation target = ResourceLocation.tryBuild(src.getNamespace(), src.getPath() + "_connected");
        return CTSpriteShifter.getCT(type, src, target);
    }

    public static void register(IEventBus modEventBus){
        // extra reinforced mapping
        SCBEBlocks.registerReinforcedBlockMapping(REINFORCED_MECHANICAL_PISTON_BLOCK,REINFORCED_MECHANICAL_PISTON_BLOCK);
        SCBEBlocks.registerReinforcedBlockMapping(REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK,REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK);

        REGISTRATE.registerEventListeners(modEventBus);
    }

    public static void onCommonSetup(){
        BlockStressValues.IMPACTS.register(REINFORCED_MECHANICAL_PISTON_BLOCK.get(), () -> 4.0);
        BlockStressValues.IMPACTS.register(REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK.get(), () -> 4.0);
    }

    /**
     * 该模块所有需要数据生成的方块模型信息。
     * 注意这里只返回 Supplier，不会在类加载时立即访问方块实例。
     */
    public static List<SCBEBlockModelData> getBlockModelData() {
         blockModelData.addAll(List.of(
                new SCBEBlockModelData(
                        "reinforced_brass_block",
                        REINFORCED_BRASS_BLOCK,
                        "block/reinforced_cube_all",
                        Map.of("all", Create.asResource("block/brass_block"))
                ),
                 new SCBEBlockModelData(
                        "reinforced_andesite_alloy_block",
                        REINFORCED_ANDESITE_ALLOY_BLOCK,
                         "block/reinforced_cube_all",
                        Map.of("all", Create.asResource("block/andesite_block"))
                 ),
                 new SCBEBlockModelData(
                         "reinforced_framed_glass",
                         REINFORCED_FRAMED_GLASS,
                         "block/reinforced_cube_all",
                         Map.of("all", Create.asResource("block/palettes/framed_glass")),
                         "minecraft:cutout",
                         SIMPLE
                 ),
                 new SCBEBlockModelData(
                         "reinforced_horizontal_framed_glass",
                         REINFORCED_HORIZONTAL_FRAMED_GLASS,
                         "block/reinforced_cube_all",
                         Map.of("all", Create.asResource("block/palettes/framed_glass")),
                         "minecraft:cutout",
                         SIMPLE,
                         new SCBEBlockModelData(
                                 "reinforced_horizontal_framed_glass",
                                 REINFORCED_HORIZONTAL_FRAMED_GLASS,
                                 "block/reinforced_cube_column",
                                 Map.of("end", Create.asResource("block/palettes/framed_glass"), "side", Create.asResource("block/palettes/horizontal_framed_glass")),
                                 "minecraft:cutout",
                                 SIMPLE
                         )
                 ),
                 new SCBEBlockModelData(
                         "reinforced_vertical_framed_glass",
                         REINFORCED_VERTICAL_FRAMED_GLASS,
                         "block/reinforced_cube_all",
                         Map.of("all", Create.asResource("block/palettes/framed_glass")),
                         "minecraft:cutout",
                         SIMPLE,
                         new SCBEBlockModelData(
                                 "reinforced_vertical_framed_glass",
                                 REINFORCED_VERTICAL_FRAMED_GLASS,
                                 "block/reinforced_cube_column",
                                 Map.of("end", Create.asResource("block/palettes/framed_glass"), "side", Create.asResource("block/palettes/vertical_framed_glass")),
                                 "minecraft:cutout",
                                 SIMPLE
                         )
                 ),
                 new SCBEBlockModelData(
                         "reinforced_tiled_glass",
                         REINFORCED_TILED_GLASS,
                         "block/reinforced_cube_all",
                         Map.of("all", Create.asResource("block/palettes/tiled_glass")),
                         "minecraft:cutout",
                         SIMPLE
                 ),
                 new SCBEBlockModelData(
                         "reinforced_framed_glass_pane",
                         REINFORCED_FRAMED_GLASS_PANE,
                         "不使用，任意占位",
                         Map.of("edge", SecurityCraftBlockExtend.asResource("block/reinforced_framed_glass_pane_top"),"pane", SecurityCraftBlockExtend.asResource("block/reinforced_framed_glass")),
                         "minecraft:cutout",
                         CREATE_CONNECTED_PANE
                 ),
                 new SCBEBlockModelData(
                         "reinforced_horizontal_framed_glass_pane",
                         REINFORCED_HORIZONTAL_FRAMED_GLASS_PANE,
                         "不使用，任意占位",
                         Map.of("edge", SecurityCraftBlockExtend.asResource("block/reinforced_framed_glass_pane_top"),"pane", SecurityCraftBlockExtend.asResource("block/reinforced_horizontal_framed_glass")),
                         "minecraft:cutout",
                         CREATE_CONNECTED_PANE
                 ),
                 new SCBEBlockModelData(
                         "reinforced_vertical_framed_glass_pane",
                         REINFORCED_VERTICAL_FRAMED_GLASS_PANE,
                         "不使用，任意占位",
                         Map.of("edge", SecurityCraftBlockExtend.asResource("block/reinforced_framed_glass_pane_top"),"pane", SecurityCraftBlockExtend.asResource("block/reinforced_vertical_framed_glass")),
                         "minecraft:cutout",
                         CREATE_CONNECTED_PANE
                 ),
                 createBars(
                         "reinforced_brass_bars", REINFORCED_BRASS_BARS,
                          SecurityCraftBlockExtend.asResource("block/reinforced_brass_bars"),
                          Create.asResource("block/bars/brass_bars_edge"),
                          SecurityCraftBlockExtend.asResource("block/reinforced_brass_bars")
                 ),
                createBars(
                        "reinforced_andesite_bars",REINFORCED_ANDESITE_BARS,
                        SecurityCraftBlockExtend.asResource("block/reinforced_andesite_bars"),
                        Create.asResource("block/bars/andesite_bars_edge"),
                        SecurityCraftBlockExtend.asResource("block/reinforced_andesite_bars")
                ),
                 createBars(
                         "reinforced_copper_bars",REINFORCED_COPPER_BARS,
                         SecurityCraftBlockExtend.asResource("block/reinforced_copper_bars"),
                         Create.asResource("block/bars/copper_bars_edge"),
                         SecurityCraftBlockExtend.asResource("block/reinforced_copper_bars")
                 ),
                new SCBEBlockModelData(
                        "reinforced_track",
                        REINFORCED_TRACK,
                        "不使用，任意占位",
                        Map.of("item",Create.asResource("item/track")),
                        "minecraft:cutout_mipped",
                        CREATE_TRACK
                )
         ));
        return blockModelData;
    }

    public static List<SCBEBlockLootData> getBlockLootData() {
        blockLootData.addAll(List.of(
                SCBEBlockLootData.dropSelf(REINFORCED_BRASS_BLOCK),
                SCBEBlockLootData.dropSelf(REINFORCED_ANDESITE_ALLOY_BLOCK),
                SCBEBlockLootData.dropSelf(REINFORCED_FRAMED_GLASS),
                SCBEBlockLootData.dropSelf(REINFORCED_HORIZONTAL_FRAMED_GLASS),
                SCBEBlockLootData.dropSelf(REINFORCED_VERTICAL_FRAMED_GLASS),
                SCBEBlockLootData.dropSelf(REINFORCED_TILED_GLASS),
                SCBEBlockLootData.dropSelf(REINFORCED_FRAMED_GLASS_PANE),
                SCBEBlockLootData.dropSelf(REINFORCED_HORIZONTAL_FRAMED_GLASS_PANE),
                SCBEBlockLootData.dropSelf(REINFORCED_VERTICAL_FRAMED_GLASS_PANE),
                SCBEBlockLootData.dropSelf(REINFORCED_BRASS_BARS),
                SCBEBlockLootData.dropSelf(REINFORCED_ANDESITE_BARS),
                SCBEBlockLootData.dropSelf(REINFORCED_COPPER_BARS),
                SCBEBlockLootData.dropSelf(REINFORCED_MECHANICAL_PISTON_BLOCK),
                SCBEBlockLootData.dropSelf(REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK),
                SCBEBlockLootData.dropSelf(REINFORCED_TRACK)
                )
        );
        return blockLootData;
    }

    public static List<SCBEBlockTagData> getBlockTagData() {
        blockTagData.addAll(List.of(
                new SCBEBlockTagData.Builder(REINFORCED_BRASS_BLOCK).pickaxe().ironTool().reinforced().build(),
                new SCBEBlockTagData.Builder(REINFORCED_ANDESITE_ALLOY_BLOCK).pickaxe().add(BlockTags.NEEDS_STONE_TOOL).reinforced().build(),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_FRAMED_GLASS::get),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_HORIZONTAL_FRAMED_GLASS::get),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_VERTICAL_FRAMED_GLASS::get),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_TILED_GLASS::get),
                new SCBEBlockTagData.Builder(REINFORCED_FRAMED_GLASS_PANE).pickaxe().reinforced().add(SCTags.Blocks.REINFORCED_GLASS_PANES).build(),
                new SCBEBlockTagData.Builder(REINFORCED_HORIZONTAL_FRAMED_GLASS_PANE).pickaxe().reinforced().add(SCTags.Blocks.REINFORCED_GLASS_PANES).build(),
                new SCBEBlockTagData.Builder(REINFORCED_VERTICAL_FRAMED_GLASS_PANE).pickaxe().reinforced().add(SCTags.Blocks.REINFORCED_GLASS_PANES).build(),
                new SCBEBlockTagData.Builder(REINFORCED_BRASS_BARS).pickaxe().ironTool().add(AllTags.AllBlockTags.FAN_TRANSPARENT.tag).reinforced().build(),
                new SCBEBlockTagData.Builder(REINFORCED_ANDESITE_BARS).pickaxe().add(AllTags.AllBlockTags.FAN_TRANSPARENT.tag).reinforced().build(),
                new SCBEBlockTagData.Builder(REINFORCED_COPPER_BARS).pickaxe().add(AllTags.AllBlockTags.FAN_TRANSPARENT.tag).reinforced().build(),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_MECHANICAL_PISTON_BLOCK::get),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK::get),
                new SCBEBlockTagData.Builder(REINFORCED_TRACK).pickaxe().reinforced().add(AllTags.AllBlockTags.TRACKS.tag).build()
        ));
        return blockTagData;
    }


    public static BlockEntry<BaseReinforcedBlock> registerReinforcedBlock(String name, Supplier<Block> vanillaBlock) {
        return registerReinforcedBlock(name, vanillaBlock, (builder) -> {});
    }
    public static BlockEntry<BaseReinforcedBlock> registerReinforcedStoneBlock(String name, Supplier<Block> stone,Map<String,ResourceLocation> textures) {
        BlockEntry<BaseReinforcedBlock> blockEntry = registerReinforcedBlock(name, stone);
        for (Map.Entry<String, ResourceLocation> textureN : textures.entrySet()) {
            blockModelData.add(new SCBEBlockModelData(
                    textureN.getKey(),
                    blockEntry,
                    "block/reinforced_cube_all",
                    Map.of("all", textureN.getValue())
            ));
        }
        blockLootData.add(SCBEBlockLootData.dropSelf(blockEntry));
        blockTagData.add(SCBEBlockTagData.mineWithPickaxe(blockEntry::get));
        return blockEntry;
    }
    public static BlockEntry<BaseReinforcedBlock> registerReinforcedBlock(String name, Supplier<Block> vanillaBlock, Consumer<BlockBuilder<BaseReinforcedBlock, CreateRegistrate>> blockBuilderConsumer) {
        BlockEntry<BaseReinforcedBlock> blockEntry =  registerReinforcedBlock(name, (p) -> new BaseReinforcedBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()),blockBuilderConsumer);
        SCBEBlocks.registerReinforcedBlockMapping(blockEntry,blockEntry);
        return blockEntry;
    }
    public static BlockEntry<ReinforcedStairsBlock> registerReinforcedStairBlock(String name, Supplier<Block> vanillaBlock) {
        BlockEntry<ReinforcedStairsBlock> blockEntry =  registerReinforcedBlock(name, (p) -> new ReinforcedStairsBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()));
        SCBEBlocks.registerReinforcedBlockMapping(blockEntry,blockEntry);
        return blockEntry;
    }
    public static BlockEntry<ReinforcedSlabBlock> registerReinforcedSlabBlock(String name, Supplier<Block> vanillaBlock) {
        BlockEntry<ReinforcedSlabBlock> blockEntry =  registerReinforcedBlock(name, (p) -> new ReinforcedSlabBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()));
        SCBEBlocks.registerReinforcedBlockMapping(blockEntry,blockEntry);
        return blockEntry;
    }
    public static BlockEntry<ReinforcedWallBlock> registerReinforcedWallBlock(String name, Supplier<Block> vanillaBlock) {
        BlockEntry<ReinforcedWallBlock> blockEntry =  registerReinforcedBlock(name, (p) -> new ReinforcedWallBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()));
        SCBEBlocks.registerReinforcedBlockMapping(blockEntry,blockEntry);
        return blockEntry;
    }
    public static BlockEntry<ReinforcedConnectedPillarBlock> registerReinforcedPillarBlock(
            String name, Supplier<Block> vanillaBlock,Consumer<BlockBuilder<ReinforcedConnectedPillarBlock, CreateRegistrate>> blockBuilderConsumer) {
        BlockEntry<ReinforcedConnectedPillarBlock> blockEntry = registerReinforcedBlock(name, (p) ->
                new ReinforcedConnectedPillarBlock(
                        SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()),
                        vanillaBlock.get()),
                blockBuilderConsumer);
        SCBEBlocks.registerReinforcedBlockMapping(blockEntry,blockEntry);
        return blockEntry;
    }

    public static BlockEntry<ReinforcedGlassBlock> registerReinforcedGlassBlock(String name, Supplier<Block> vanillaBlock, Consumer<BlockBuilder<ReinforcedGlassBlock, CreateRegistrate>> blockBuilderConsumer){
        BlockEntry<ReinforcedGlassBlock> blockEntry = registerReinforcedBlock(name, (p) -> new ReinforcedGlassBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()), blockBuilderConsumer);
        SCBEBlocks.registerReinforcedBlockMapping(blockEntry,blockEntry);
        return blockEntry;
    }

    public static BlockEntry<ReinforcedPaneBlock> registerReinforcedPaneBlock(String name, Supplier<Block> vanillaBlock, Consumer<BlockBuilder<ReinforcedPaneBlock, CreateRegistrate>> blockBuilderConsumer,boolean registerReinforcedTint){
        BlockEntry<ReinforcedPaneBlock> blockEntry = registerReinforcedBlock(name, (p) -> new ReinforcedPaneBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()), blockBuilderConsumer, registerReinforcedTint);
        SCBEBlocks.registerReinforcedBlockMapping(blockEntry,blockEntry);
        return blockEntry;
    }
    public static BlockEntry<ReinforcedPaneBlock> registerReinforcedPaneBlock(String name, Supplier<Block> vanillaBlock,boolean registerReinforcedTint) {
        return registerReinforcedPaneBlock(name, vanillaBlock, (p) -> {}, registerReinforcedTint);
    }

    public static <B extends Block> BlockEntry<B> registerReinforcedBlock(String name, Function<BlockBehaviour.Properties,B> reinforcedBlock) {
        return registerReinforcedBlock(name, reinforcedBlock, (builder) -> {});
    }
    public static <B extends Block> BlockEntry<B> registerReinforcedBlock(String name, Function<BlockBehaviour.Properties,B> reinforcedBlock, Consumer<BlockBuilder<B, CreateRegistrate>> blockBuilderConsumer) {
        return registerReinforcedBlock(name, reinforcedBlock, blockBuilderConsumer, true);
    }
    public static <B extends Block> BlockEntry<B> registerReinforcedBlock(String name, Function<BlockBehaviour.Properties,B> reinforcedBlock, Consumer<BlockBuilder<B, CreateRegistrate>> blockBuilderConsumer,boolean registerReinforcedTint) {
        BlockBuilder<B, CreateRegistrate> builder = REGISTRATE
                .block(name,
                        reinforcedBlock::apply)
                .setData(ProviderType.BLOCKSTATE, NonNullBiConsumer.noop()) // No blockstate data
                .setData(ProviderType.LANG, NonNullBiConsumer.noop()) // No language data
                .setData(ProviderType.LOOT, NonNullBiConsumer.noop()); // No loot table data

        blockBuilderConsumer.accept(builder);

        BlockEntry<B> blockEntry = builder
                .item(BlockItem::new)
                .setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop()) // No item model data
                .build()
                .register();
        if (registerReinforcedTint){
            SCBEBlocks.registerReinforcedTintBlock(blockEntry);
        }
        creativeTabItems.add(blockEntry::get);
        return blockEntry;
    }

    public static List<Supplier<ItemLike>> getCreativeTabItems() {
        creativeTabItems.addAll(List.of(
                REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK::get,
                REINFORCED_MECHANICAL_PISTON_BLOCK::get,
                REINFORCED_TRACK::get
        ));
        return creativeTabItems;
    }

    public static SCBEBlockModelData createBars(String name, BlockEntry<? extends Block> block, ResourceLocation bars, ResourceLocation edge, ResourceLocation particle){
        return new SCBEBlockModelData(name,block,"该字段对 BARS 不使用，任意占位",Map.of(
                "bars",bars,"edge",edge,"particle",particle
        ),"minecraft:cutout_mipped", CREATE_BARS);
    }

    static {
        // 显式触发 AllPaletteBlocks 类初始化，保证后续 palette 变体可用
//        BlockEntry<ConnectedGlassBlock> frameGlass = AllPaletteBlocks.FRAMED_GLASS; 已经由AllPaletteBlocks.FRAMED_GLASS 引用触发
        registerPaletteStoneBlocks(AllPaletteStoneTypes.DIORITE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.ANDESITE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.GRANITE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.ASURINE,true,true);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.CALCITE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.CRIMSITE,true,true);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.DEEPSLATE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.DRIPSTONE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.LIMESTONE,true,false);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.OCHRUM,true,true);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.SCORCHIA,true,false);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.SCORIA,true,false);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.TUFF);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.VERIDIUM,true,true);
    }
}
