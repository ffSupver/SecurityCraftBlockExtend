package com.ffsupver.securityCraftBlockExtend.compat;

import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;
import java.util.function.Supplier;

public interface SCBEModCompat {
    public String getModId();
    public void init(IEventBus eventBus);

    /**
     * 供数据生成使用。返回该兼容模块需要生成模型的方块。
     * 默认返回空列表，兼容模块按需重写。
     */
    default List<SCBEBlockModelData> getBlockModelData() {
        return List.of();
    }

    default List<SCBEBlockLootData> getBlockLootData() {
        return List.of();
    }
    default List<Supplier<ItemLike>> getCreativeTabItems() {
        return List.of();
    }
    default List<SCBEBlockTagData> getBlockTagData() {
        return List.of();
    }
}
