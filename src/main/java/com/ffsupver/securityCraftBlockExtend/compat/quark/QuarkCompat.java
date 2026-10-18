package com.ffsupver.securityCraftBlockExtend.compat.quark;

import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.compat.SCBEModCompat;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.violetmoon.quark.content.building.block.*;
import org.violetmoon.quark.content.world.block.MonsterBoxBlock;
import org.violetmoon.quark.content.world.block.MyaliteColorLogic;
import org.violetmoon.zeta.block.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class QuarkCompat implements SCBEModCompat {
    protected static final DeferredRegister<Block> BLOCKS = SCBEBlocks.BLOCKS;
    public static final Map<Block,RegistryObject<? extends Block>> QUARK_REINFORCED_BLOCKS = new HashMap<>();
    private boolean populated = false;

    private final List<Supplier<ItemLike>> creativeTabItems = new ArrayList<>();
    private final List<SCBEBlockModelData> modelDataList = new ArrayList<>();
    private final List<SCBEBlockLootData> lootTableProviders = new ArrayList<>();
    private final List<SCBEBlockTagData> blockTagDataList = new ArrayList<>();

    private static final SCBEBlocks.ColorProcessor MYALITE_PROCESSOR =
            new SCBEBlocks.ColorProcessor() {
                @Override
                public int process(BlockState state, BlockAndTintGetter level, BlockPos pos,
                                   int tintIndex, int currentTint) {
                    BlockPos colorPos = pos;
                    if (colorPos == null) {
                        Minecraft mc = Minecraft.getInstance();
                        colorPos = BlockPos.ZERO;
                        if (mc.player != null) {
                            colorPos = mc.player.blockPosition();
                            HitResult hit = mc.hitResult;
                            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                                colorPos = ((BlockHitResult) hit).getBlockPos();
                            }
                        }
                    }
                    int myaliteTint = MyaliteColorLogic.getColor(colorPos);
                    return FastColor.ARGB32.multiply(currentTint, myaliteTint);
                }

                @Override
                public int processItem(ItemStack stack, int tintIndex, int currentTint) {
                    Minecraft mc = Minecraft.getInstance();
                    BlockPos pos = BlockPos.ZERO;
                    if (mc.player != null) {
                        pos = mc.player.blockPosition();
                        HitResult hit = mc.hitResult;
                        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                            pos = ((BlockHitResult) hit).getBlockPos();
                        }
                    }
                    int myaliteTint = MyaliteColorLogic.getColor(pos);
                    return FastColor.ARGB32.multiply(currentTint, myaliteTint);
                }
            };

    @Override
    public String getModId() {
        return Mods.ModIds.QUARK.ModId;
    }

    @Override
    public void init(IEventBus eventBus) {
        eventBus.addListener(EventPriority.HIGH,this::onRegister);
//        MinecraftForge.EVENT_BUS.addListener(this::onTagsUpdated);
    }

    private void onRegister(RegisterEvent event) {
        if (populated) return;
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) return;
        populated = true;

        System.out.println("OnRegister QuarkCompat:"+ QuarkBlocksCapture.QUARK_BLOCKS.size());
        int verticalCount = 0, stairsCount = 0, slabCount = 0,wallCount = 0, unprocessCount = 0;
        for (Map.Entry<ResourceLocation, Block> bE : QuarkBlocksCapture.QUARK_BLOCKS.entrySet()){
            Block block = bE.getValue();
            ResourceLocation originalId = bE.getKey();
            String name = "reinforced_" + originalId.getPath();

            if (block instanceof QuarkVerticalSlabBlock quarkVerticalSlabBlock) {
                Block parent = quarkVerticalSlabBlock.parent.get();
                if (ForgeRegistries.BLOCKS.getKey(parent) == null) continue;

                registerQuarkReinforced(
                        name, originalId, block,
                        ReinforcedVerticalSlab::new,
                        SCBEBlockModelData.BlockType.VERTICAL_SLAB,
                        ResourceLocation.tryBuild("quark", "block/vertical_slab"), // 这个参数不会使用
                        reg -> new SCBEBlockLootData(reg, SCBEBlockLootData.BlockLootType.VERTICAL_SLAB)
                );

                verticalCount++;
            } else if (block instanceof ZetaStairsBlock) {
                registerQuarkReinforced(
                        name, originalId, block,
                        ReinforcedStairsBlock::new,
                        SCBEBlockModelData.BlockType.STAIRS,
                        ResourceLocation.tryBuild(SecurityCraft.MODID, "block/reinforced_stairs"), // 这个参数不会使用
                        SCBEBlockLootData::dropSelf
                );
                stairsCount++;
             } else if (block instanceof ZetaSlabBlock) {
                 registerQuarkReinforced(
                         name, originalId, block,
                         ReinforcedSlabBlock::new,
                         SCBEBlockModelData.BlockType.SLAB,
                         ResourceLocation.tryBuild(SecurityCraft.MODID, "block/reinforced_slab"), // 这个参数不会使用
                         reg -> new SCBEBlockLootData(reg, SCBEBlockLootData.BlockLootType.SLAB)
                 );
                 slabCount++;
            }else if (block instanceof ZetaWallBlock){
                registerQuarkReinforced(
                        name, originalId, block,
                        ReinforcedWallBlock::new,
                        SCBEBlockModelData.BlockType.WALL,
                        ResourceLocation.tryBuild(SecurityCraft.MODID, "block/reinforced_wall"), // 这个参数不会使用
                        reg -> new SCBEBlockLootData(reg, SCBEBlockLootData.BlockLootType.DROP_SELF),
                        b->b
                );
                wallCount++;
            }else if (block instanceof FlowerPotBlock || block instanceof ZetaLeavesBlock || block instanceof LeafCarpetBlock || block instanceof HollowLogBlock
                    || block instanceof ZetaSaplingBlock || block instanceof MonsterBoxBlock || block instanceof VariantChestBlock || block instanceof StoolBlock){
                System.out.println("QuarkUnprocessed: " + block+" id:"+originalId+" class:"+block.getClass());
                unprocessCount++;
            }else {
                System.out.println("QuarkLeft: " + block+" id:"+originalId+" class:"+block.getClass());
            }
        }
        System.out.println("QuarkReinforcedBlock: verticalCount=" + verticalCount + " stairsCount=" + stairsCount + " slabCount=" + slabCount +" wallCount="+ wallCount + " unprocessCount=" + unprocessCount+" total="+QUARK_REINFORCED_BLOCKS.size());

        QUARK_REINFORCED_BLOCKS.forEach((block, reg) -> {
            System.out.println(reg.getId());
        });
    }

    /**
     * Quark 兼容方块统一注册流程。
     *
     * @param blockFactory 构造 reinforced 方块，参数为 Block.Properties 与原始方块
     * @param modelType    数据生成时使用的模型类型
     * @param parentModel  模型数据的父模型（部分 BlockType 不使用，但保留以便扩展）
     * @param lootFactory  根据注册结果生成掉落表
     */
    private <T extends Block> RegistryObject<T> registerQuarkReinforced(
            String name,
            ResourceLocation originalId,
            Block originalBlock,
            BiFunction<Block.Properties, Block, T> blockFactory,
            SCBEBlockModelData.BlockType modelType,
            ResourceLocation parentModel,
            Function<RegistryObject<T>, SCBEBlockLootData> lootFactory,
            Function<T, IReinforcedBlock> castToReinforced
    ) {

        RegistryObject<T> reg = SCBEBlocks.registerBlock(
                name,
                p -> blockFactory.apply(p, originalBlock),
                () -> SCBEBlocks.reinforcedCopy(originalBlock, UnaryOperator.identity()),
                BLOCKS
        );

        // 标签
        SCBEBlockTagData.Builder tagBuilder = new SCBEBlockTagData.Builder(reg);
        List<TagKey<Block>> tags = QuarkBlockTags.BLOCK_TAGS.get(originalId.getPath());
        if (tags != null) {
            tags.forEach(tagBuilder::add);
        }

        // 模型数据
        modelDataList.add(new SCBEBlockModelData(
                name,
                reg,
                parentModel,
                Map.of("parentId", originalId),
                null,
                modelType,
                null
        ));

        // 创造栏、掉落、映射、tint
        creativeTabItems.add(reg::get);
        lootTableProviders.add(lootFactory.apply(reg));
        SCBEBlocks.registerReinforcedBlockMapping(()->castToReinforced.apply(reg.get()), reg);
        SCBEBlocks.registerReinforcedTintBlock(reg);
        if (originalId.getPath().contains("myalite")) { // 幻境石需要特殊处理
            SCBEBlocks.REINFORCED_TINT_BLOCK_PROCESSORS.put(reg, MYALITE_PROCESSOR);
        }

        blockTagDataList.add(tagBuilder.reinforced().build());
        QUARK_REINFORCED_BLOCKS.put(originalBlock, reg);
        return reg;
    }

    private <T extends BaseReinforcedBlock> RegistryObject<T> registerQuarkReinforced(
            String name,
            ResourceLocation originalId,
            Block originalBlock,
            BiFunction<Block.Properties, Block, T> blockFactory,
            SCBEBlockModelData.BlockType modelType,
            ResourceLocation parentModel,
            Function<RegistryObject<T>, SCBEBlockLootData> lootFactory
    ) {
        return registerQuarkReinforced(
                name,originalId,originalBlock,blockFactory,
                modelType,parentModel,lootFactory,b->b
        );
    }

    @Override
    public List<SCBEBlockModelData> getBlockModelData() {
        return modelDataList;
    }

    @Override
    public List<Supplier<ItemLike>> getCreativeTabItems() {
        return creativeTabItems;
    }

    @Override
    public List<SCBEBlockLootData> getBlockLootData() {
        return lootTableProviders;
    }

    @Override
    public List<SCBEBlockTagData> getBlockTagData() {
        return blockTagDataList;
    }
}
