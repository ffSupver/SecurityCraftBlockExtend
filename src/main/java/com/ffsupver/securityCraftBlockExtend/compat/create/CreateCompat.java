package com.ffsupver.securityCraftBlockExtend.compat.create;

import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.compat.SCBEModCompat;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.CreateReinforcedBlocks;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;
import java.util.function.Supplier;

public class CreateCompat implements SCBEModCompat {
    @Override
    public String getModId() {
        return Mods.ModIds.CREATE.ModId;
    }

    @Override
    public void init(IEventBus eventBus) {
        CreateReinforcedBlocks.register(eventBus);
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
