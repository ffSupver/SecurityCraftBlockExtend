package com.ffsupver.securityCraftBlockExtend.compat.create.blocks;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
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

    public static void registerDiorite() {
        PaletteBlockPattern[] patterns = AllPaletteStoneTypes.DIORITE.variantTypes;
        PalettesVariantEntry variants = AllPaletteStoneTypes.DIORITE.getVariants();
        String stoneTypesName = AllPaletteStoneTypes.DIORITE.name().toLowerCase();

        int partialIdx = 0;

        for (int i = 0; i < patterns.length; i++) {
            PaletteBlockPattern pattern = patterns[i];

            // ================= 整块方块 =================
            BlockEntry<? extends Block> blockEntry = variants.registeredBlocks.get(i);
            String name = blockEntry.getId().getPath();
            String reinforcedName = "reinforced_" + name;
            BlockEntry<BaseReinforcedBlock> reinforced =
                    registerReinforcedBlock(reinforcedName, blockEntry::get);

            SecurityCraftBlockExtend.LOGGER.info(
                    "Registered reinforced block: {} (pattern={})", reinforcedName, pattern);

            String blockModel;
            Map<String, ResourceLocation> blockTextures;
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
            } else {// CUT / BRICKS / SMALL_BRICKS / POLISHED —— 都是 cube_all
                blockModel = "block/reinforced_cube_all";
                blockTextures = Map.of(
                        "all", paletteTexture(stoneTypesName, pattern.getTexture(0))
                );
            }
            blockModelData.add(new SCBEBlockModelData(
                    reinforcedName, reinforced, blockModel, blockTextures
            ));

            blockLootData.add(new SCBEBlockLootData(reinforced, SCBEBlockLootData.BlockLootType.DROP_SELF));
            blockTagData.add(new SCBEBlockTagData(
                    reinforced,
                    List.of(BlockTags.MINEABLE_WITH_PICKAXE)
            ));

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
                    blockTagData.add(new SCBEBlockTagData(
                            reinforcedStair,
                            List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.STAIRS)
                    ));

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

                } else {
                    SecurityCraftBlockExtend.LOGGER.warn(
                            "Unhandled partial type: {} (pattern={}, class={})",
                            reinforcedPartialName, pattern, partial.getClass().getSimpleName());
                }
            }
        }
    }

    private static ResourceLocation paletteTexture(String variant, String texture) {
        String fileName = variant + (texture.equals("cut") ? "_" : "_cut_") + texture;
        return Create.asResource("block/palettes/stone_types/" + texture + "/" + fileName);
    }

    public static void register(IEventBus modEventBus){
        REGISTRATE.registerEventListeners(modEventBus);
    }

    /**
     * 该模块所有需要数据生成的方块模型信息。
     * 注意这里只返回 Supplier，不会在类加载时立即访问方块实例。
     */
    public static List<SCBEBlockModelData> getBlockModelData() {
         blockModelData.add(
                new SCBEBlockModelData(
                        "reinforced_brass_block",
                        REINFORCED_BRASS_BLOCK,
                        "block/reinforced_cube_all",
                        Map.of("all", ResourceLocation.tryBuild("create", "block/brass_block"))
                )
        );
        return blockModelData;
    }

    public static List<SCBEBlockLootData> getBlockLootData() {
        blockLootData.add(
                new SCBEBlockLootData(
                        REINFORCED_BRASS_BLOCK,
                        SCBEBlockLootData.BlockLootType.DROP_SELF
                )
        );
        return blockLootData;
    }

    public static List<SCBEBlockTagData> getBlockTagData() {
        blockTagData.add(
                new SCBEBlockTagData(
                        REINFORCED_BRASS_BLOCK,
                        List.of(
                                BlockTags.MINEABLE_WITH_PICKAXE,
                                BlockTags.NEEDS_IRON_TOOL
                        )
                )
        );
        return blockTagData;
    }
    public static BlockEntry<BaseReinforcedBlock> registerReinforcedBlock(String name, Supplier<Block> vanillaBlock) {
        return registerReinforcedBlock(name, (p) -> new BaseReinforcedBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()));
    }
    public static BlockEntry<ReinforcedStairsBlock> registerReinforcedStairBlock(String name, Supplier<Block> vanillaBlock) {
        return registerReinforcedBlock(name, (p) -> new ReinforcedStairsBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()));
    }
    public static BlockEntry<ReinforcedSlabBlock> registerReinforcedSlabBlock(String name, Supplier<Block> vanillaBlock) {
        return registerReinforcedBlock(name, (p) -> new ReinforcedSlabBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()));
    }
    public static BlockEntry<ReinforcedWallBlock> registerReinforcedWallBlock(String name, Supplier<Block> vanillaBlock) {
        return registerReinforcedBlock(name, (p) -> new ReinforcedWallBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()));
    }
    public static <B extends Block> BlockEntry<B> registerReinforcedBlock(String name, Function<BlockBehaviour.Properties,B> reinforcedBlock) {
        BlockEntry<B> blockEntry = REGISTRATE
                .block(name,
                        reinforcedBlock::apply)
                .setData(ProviderType.BLOCKSTATE, NonNullBiConsumer.noop()) // No blockstate data
                .setData(ProviderType.LANG, NonNullBiConsumer.noop()) // No language data
                .setData(ProviderType.LOOT, NonNullBiConsumer.noop()) // No loot table data
                .item(BlockItem::new)
                .setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop()) // No item model data
                .build()
                .register();
        SCBEBlocks.registerReinforcedTintBlock(blockEntry);
        creativeTabItems.add(blockEntry::get);
        return blockEntry;
    }

    public static List<Supplier<ItemLike>> getCreativeTabItems() {
        return creativeTabItems;
    }

    static {
        BlockEntry<ConnectedGlassBlock> frameGlass = AllPaletteBlocks.FRAMED_GLASS;
        registerDiorite();
    }
}
