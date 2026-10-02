package com.ffsupver.securityCraftBlockExtend.compat.create.blocks;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class CreateReinforcedBlocks {
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(SecurityCraftBlockExtend.MODID);
    public static List<Supplier<ItemLike>> creativeTabItems = new ArrayList<>();

    public static final BlockEntry<BaseReinforcedBlock> REINFORCED_BRASS_BLOCK = registerReinforcedBlock("reinforced_brass_block", AllBlocks.BRASS_BLOCK);

    public static void register(IEventBus modEventBus){
        REGISTRATE.registerEventListeners(modEventBus);
    }

    /**
     * 该模块所有需要数据生成的方块模型信息。
     * 注意这里只返回 Supplier，不会在类加载时立即访问方块实例。
     */
    public static List<SCBEBlockModelData> getBlockModelData() {
        return List.of(
                new SCBEBlockModelData(
                        "reinforced_brass_block",
                        REINFORCED_BRASS_BLOCK,
                        "block/reinforced_cube_all",
                        Map.of("all", ResourceLocation.tryBuild("create", "block/brass_block"))
                )
        );
    }

    public static List<SCBEBlockLootData> getBlockLootData() {
        return List.of(
                new SCBEBlockLootData(
                        REINFORCED_BRASS_BLOCK,
                        SCBEBlockLootData.BlockLootType.DROP_SELF
                )
        );
    }

    public static BlockEntry<BaseReinforcedBlock> registerReinforcedBlock(String name, Supplier<Block> vanillaBlock){
        BlockEntry<BaseReinforcedBlock> blockEntry = REGISTRATE
                .block(name,
                        p->new BaseReinforcedBlock(SCBEBlocks.reinforcedCopy(vanillaBlock.get(), UnaryOperator.identity()), vanillaBlock.get()))
                .blockstate((b,b1)->{})
                .setData(ProviderType.LANG, NonNullBiConsumer.noop()) // No language data
                .setData(ProviderType.LOOT, NonNullBiConsumer.noop()) // No loot table data
                .item(BlockItem::new)
                .build()
                .register();
        SCBEBlocks.registerReinforcedTintBlock(blockEntry);
        creativeTabItems.add(blockEntry::get);
        return blockEntry;
    }

    public static List<Supplier<ItemLike>> getCreativeTabItems() {
        return creativeTabItems;
    }
}
