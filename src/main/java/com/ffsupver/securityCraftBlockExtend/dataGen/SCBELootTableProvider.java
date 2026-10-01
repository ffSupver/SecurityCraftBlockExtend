package com.ffsupver.securityCraftBlockExtend.dataGen;

import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return SCBEBlocks.BLOCKS.getEntries().stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toList());
        }
    }
}