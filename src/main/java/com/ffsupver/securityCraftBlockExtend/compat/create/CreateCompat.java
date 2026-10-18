package com.ffsupver.securityCraftBlockExtend.compat.create;

import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.compat.SCBEModCompat;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.CreateReinforcedBlocks;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcerItem;
import com.simibubi.create.content.logistics.filter.FilterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.*;
import java.util.function.Supplier;

public class CreateCompat implements SCBEModCompat {
    @Override
    public String getModId() {
        return Mods.ModIds.CREATE.ModId;
    }

    @Override
    public void init(IEventBus eventBus) {
        CreateReinforcedBlocks.register(eventBus);
        eventBus.addListener(this::onCommonSetup);
        ChainedUniversalBlockReinforcerItem.chainBlockPredicates.add(new ChainedUniversalBlockReinforcerItem.ChainBlockPredicate() {
            @Override
            public ChainedUniversalBlockReinforcerItem.ChainBlockPredicateResult test(BlockPos pos, BlockState state, Player player) {
                ItemStack offHandItem = player.getOffhandItem();
                Block checkBlock = state.getBlock();
                if (offHandItem.getItem() instanceof FilterItem filterItem){
                   Set<Block> blockList = new HashSet<>();
                   Arrays.stream(filterItem.getFilterItems(offHandItem)).forEach(
                           itemStack -> {
                               if (itemStack.getItem() instanceof BlockItem blockItem){
                                   blockList.add(blockItem.getBlock());
                               }
                           }
                   );
                   if (blockList.contains(checkBlock)){
                       return ChainedUniversalBlockReinforcerItem.ChainBlockPredicateResult.SUCCESS;
                   }else{
                       return ChainedUniversalBlockReinforcerItem.ChainBlockPredicateResult.FAIL;
                   }
                }
                return ChainedUniversalBlockReinforcerItem.ChainBlockPredicateResult.PASS;
            }

            @Override
            public ChainedUniversalBlockReinforcerItem.ChainBlockPredicateResult shouldWalk(BlockPos pos, BlockState state, Player player) {
                ItemStack offHandItem = player.getOffhandItem();
                if (offHandItem.getItem() instanceof FilterItem){
                    return ChainedUniversalBlockReinforcerItem.ChainBlockPredicateResult.SUCCESS;
                }
                return ChainedUniversalBlockReinforcerItem.ChainBlockPredicateResult.PASS;
            }
        });
    }

    public void onCommonSetup(FMLCommonSetupEvent event){
        CreateReinforcedBlocks.onCommonSetup();
    }

    @Override
    public List<SCBEBlockModelData> getBlockModelData() {
        return CreateReinforcedBlocks.getBlockModelData();
    }

    @Override
    public List<SCBEBlockLootData> getBlockLootData() {
        return CreateReinforcedBlocks.getBlockLootData();
    }

    public List<Supplier<ItemLike>> getCreativeTabItems(){
       return CreateReinforcedBlocks.getCreativeTabItems();
    }

    @Override
    public List<SCBEBlockTagData> getBlockTagData() {
        return CreateReinforcedBlocks.getBlockTagData();
    }
}
