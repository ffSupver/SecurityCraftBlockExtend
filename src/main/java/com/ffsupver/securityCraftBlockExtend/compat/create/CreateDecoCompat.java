package com.ffsupver.securityCraftBlockExtend.compat.create;

import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.compat.SCBEModCompat;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.CreateDecoReinforcedBlocks;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;
import java.util.function.Supplier;

public class CreateDecoCompat implements SCBEModCompat {
    @Override
    public String getModId() {
        return Mods.ModIds.CREATE.ModId;
    }

    @Override
    public void init(IEventBus eventBus) {
        CreateDecoReinforcedBlocks.register();
    }

    @Override
    public List<Supplier<ItemLike>> getCreativeTabItems() {
        return CreateDecoReinforcedBlocks.getCreativeTabItems();
    }

    @Override
    public List<SCBEBlockLootData> getBlockLootData() {
        return CreateDecoReinforcedBlocks.getBlockLootData();
    }

    @Override
    public List<SCBEBlockTagData> getBlockTagData() {
        return CreateDecoReinforcedBlocks.getBlockTagData();
    }
}
