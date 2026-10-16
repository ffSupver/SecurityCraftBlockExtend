package com.ffsupver.securityCraftBlockExtend.dataGen;

import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import org.violetmoon.quark.content.building.block.VerticalSlabBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SCBELootTableProvider extends LootTableProvider {

    public SCBELootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(
                        SCBEBlockLoot::new,
                        LootContextParamSets.BLOCK
                )
        ));
    }

    private static class SCBEBlockLoot extends BlockLootSubProvider {

        protected SCBEBlockLoot() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            dropSelf(SCBEBlocks.REINFORCED_WET_SPONGE.get());
            add(SCBEBlocks.REINFORCED_REINFORCED_DEEPSLATE.get(),noDrop());

            // 兼容模块方块
            for (SCBEBlockLootData data : Mods.collectBlockLootData()) {
                Block block = data.block().get();
                switch (data.type()) {
                    case DROP_SELF -> dropSelf(block);
                    case NO_DROP -> add(block, noDrop());
                    case SLAB -> add(block,createSlabItemTable(block));
                    case VERTICAL_SLAB -> add(block,createVerticalSlabItemTable(block));
                }
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            List<Block> blocks = new ArrayList<>();

            // 原生方块
            SCBEBlocks.BLOCKS.getEntries().stream()
                    .map(RegistryObject::get)
                    .forEach(blocks::add);

            // 兼容模块方块
            Mods.collectBlockLootData().forEach(data -> blocks.add(data.block().get()));

            return blocks;
        }

        protected LootTable.Builder createVerticalSlabItemTable(Block pBlock) {
            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(this.applyExplosionDecay(pBlock, LootItem.lootTableItem(pBlock).apply(
                                    SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(pBlock).setProperties(
                                                    StatePropertiesPredicate.Builder.properties().hasProperty(VerticalSlabBlock.TYPE, VerticalSlabBlock.VerticalSlabType.DOUBLE)
                                            ))
                            )))
            );
        }
    }
}