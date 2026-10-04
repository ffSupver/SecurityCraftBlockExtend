package com.ffsupver.securityCraftBlockExtend.compat.create.blocks;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.*;
import com.simibubi.create.foundation.block.connected.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.geforcemods.securitycraft.blocks.reinforced.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData.BlockType.*;
import static com.simibubi.create.content.decoration.palettes.PaletteBlockPattern.LAYERED;
import static com.simibubi.create.content.decoration.palettes.PaletteBlockPattern.PILLAR;

public class CreateReinforcedBlocks {
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(SecurityCraftBlockExtend.MODID);
    public static List<SCBEBlockLootData> blockLootData = new ArrayList<>();
    public static List<SCBEBlockTagData> blockTagData = new ArrayList<>();
    public static List<SCBEBlockModelData> blockModelData = new ArrayList<>();
    public static List<Supplier<ItemLike>> creativeTabItems = new ArrayList<>();

    public static final BlockEntry<BaseReinforcedBlock> REINFORCED_BRASS_BLOCK = registerReinforcedBlock("reinforced_brass_block", AllBlocks.BRASS_BLOCK);
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
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_BRASS_BARS =
            registerReinforcedPaneBlock(
                    "reinforced_brass_bars",
                    AllBlocks.BRASS_BARS::get,
                    b->{},
                    false
            );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_ANDESITE_BARS =
            registerReinforcedPaneBlock(
                    "reinforced_andesite_bars",
                    AllBlocks.ANDESITE_BARS::get,
                    b->{},
                    false
            );
    public static final BlockEntry<ReinforcedPaneBlock> REINFORCED_COPPER_BARS =
            registerReinforcedPaneBlock(
                    "reinforced_copper_bars",
                    AllBlocks.COPPER_BARS::get,
                    b->{},
                    false
            );

    public static void registerPaletteStoneBlocks(AllPaletteStoneTypes stoneTypes) {
        PaletteBlockPattern[] patterns = stoneTypes.variantTypes;
        PalettesVariantEntry variants = stoneTypes.getVariants();
        String stoneTypesName = stoneTypes.name().toLowerCase();

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
            }else if (pattern.equals(PILLAR)) {
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
            } else if (pattern.equals(PILLAR)) {
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
            blockTagData.add(new SCBEBlockTagData(
                    reinforced,
                    List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.WITHER_IMMUNE, BlockTags.DRAGON_IMMUNE)
            ));

            // ================= 该 pattern 下的 partials =================
            for (PaletteBlockPartial<?> partial : pattern.getPartials()) {
                BlockEntry<? extends Block> partialEntry = variants.registeredPartials.get(partialIdx++);
                String partialPath = partialEntry.getId().getPath();
                String reinforcedPartialName = "reinforced_" + partialPath;

                BlockEntry<? extends Block> reinforcedBlockEntry = null;
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
                    blockTagData.add(new SCBEBlockTagData(
                            reinforcedStair,
                            List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.STAIRS)
                    ));

                    reinforcedBlockEntry = reinforcedStair;
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
                    blockTagData.add(new SCBEBlockTagData(
                            reinforcedSlab,
                            List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.SLABS)
                    ));

                    reinforcedBlockEntry = reinforcedSlab;
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
                    blockTagData.add(new SCBEBlockTagData(
                            reinforcedWall,
                            List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.WALLS)
                    ));

                    reinforcedBlockEntry = reinforcedWall;
                } else {
                    SecurityCraftBlockExtend.LOGGER.warn(
                            "Unhandled partial type: {} (pattern={}, class={})",
                            reinforcedPartialName, pattern, partial.getClass().getSimpleName());
                }
                if (reinforcedBlockEntry != null){
                    blockTagData.add(
                            new SCBEBlockTagData(
                                    reinforcedBlockEntry,
                                    List.of(BlockTags.WITHER_IMMUNE,BlockTags.DRAGON_IMMUNE)
                            )
                    );
                }
            }
        }
    }

    private static ResourceLocation paletteTexture(String variant, String texture) {
        String fileName = variant + (texture.equals("cut") ? "_" : "_cut_") + texture;
        return Create.asResource("block/palettes/stone_types/" + texture + "/" + fileName);
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
        REGISTRATE.registerEventListeners(modEventBus);
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
                         "reinforced_framed_glass_pane",
                         REINFORCED_FRAMED_GLASS_PANE,
                         "不使用，任意占位",
                         Map.of("edge", SecurityCraftBlockExtend.asResource("block/reinforced_framed_glass_pane_top"),"pane", SecurityCraftBlockExtend.asResource("block/reinforced_framed_glass")),
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
                 )
         ));
        return blockModelData;
    }

    public static List<SCBEBlockLootData> getBlockLootData() {
        blockLootData.addAll(List.of(
                SCBEBlockLootData.dropSelf(REINFORCED_BRASS_BLOCK),
                SCBEBlockLootData.dropSelf(REINFORCED_FRAMED_GLASS),
                SCBEBlockLootData.dropSelf(REINFORCED_HORIZONTAL_FRAMED_GLASS),
                SCBEBlockLootData.dropSelf(REINFORCED_VERTICAL_FRAMED_GLASS),
                SCBEBlockLootData.dropSelf(REINFORCED_FRAMED_GLASS_PANE),
                SCBEBlockLootData.dropSelf(REINFORCED_BRASS_BARS),
                SCBEBlockLootData.dropSelf(REINFORCED_ANDESITE_BARS),
                SCBEBlockLootData.dropSelf(REINFORCED_COPPER_BARS)
                )
        );
        return blockLootData;
    }

    public static List<SCBEBlockTagData> getBlockTagData() {
        blockTagData.addAll(List.of(
                new SCBEBlockTagData.Builder(REINFORCED_BRASS_BLOCK).pickaxe().ironTool().reinforced().build(),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_FRAMED_GLASS::get),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_HORIZONTAL_FRAMED_GLASS::get),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_VERTICAL_FRAMED_GLASS::get),
                SCBEBlockTagData.mineWithPickaxe(REINFORCED_FRAMED_GLASS_PANE::get),
                new SCBEBlockTagData.Builder(REINFORCED_BRASS_BARS).pickaxe().ironTool().reinforced().build(),
                new SCBEBlockTagData.Builder(REINFORCED_ANDESITE_BARS).pickaxe().ironTool().reinforced().build(),
                new SCBEBlockTagData.Builder(REINFORCED_COPPER_BARS).pickaxe().ironTool().reinforced().build()
        ));
        return blockTagData;
    }


    public static BlockEntry<BaseReinforcedBlock> registerReinforcedBlock(String name, Supplier<Block> vanillaBlock) {
        return registerReinforcedBlock(name, vanillaBlock, (builder) -> {});
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
        registerPaletteStoneBlocks(AllPaletteStoneTypes.ASURINE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.CALCITE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.CRIMSITE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.DEEPSLATE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.DRIPSTONE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.LIMESTONE);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.OCHRUM);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.SCORCHIA);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.TUFF);
        registerPaletteStoneBlocks(AllPaletteStoneTypes.VERIDIUM);
    }
}
