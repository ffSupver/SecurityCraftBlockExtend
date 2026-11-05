package com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChainedUniversalBlockReinforcerFilter {
    public static String NBT_TAG = "filter";
    public static final int FILTER_COUNT = 6;
    private final SimpleContainer[] filters = new SimpleContainer[FILTER_COUNT];
    private final boolean[] filterActive = new boolean[FILTER_COUNT];
    public ChainedUniversalBlockReinforcerFilter() {
        for (int i = 0; i < FILTER_COUNT; i++){
            filters[i] = new SimpleContainer(9);
            filterActive[i] = false;
        }
    }

    public CompoundTag toNbt(){
        CompoundTag nbt = new CompoundTag();
        for (int i = 0; i < FILTER_COUNT; i++){
            nbt.put("filter" + i, filters[i].createTag());
            nbt.putBoolean("filterActive" + i, filterActive[i]);
        }
        return nbt;
    }

    public void fromNbt(CompoundTag nbt){
        for (int i = 0; i < FILTER_COUNT; i++){
            filters[i].clearContent();
            filters[i].fromTag(nbt.getList("filter" + i, 10));
            filterActive[i] = nbt.getBoolean("filterActive" + i);
        }
    }

    public void copyFilter(SimpleContainer copyTo, int filterIndex){
        for (int i = 0; i < 9; i++) {
            copyTo.setItem(i, filters[filterIndex].getItem(i));
        }
    }

    public void applyFilter(SimpleContainer newFilter, int filterIndex) {
        int index = 0;
        filters[filterIndex].clearContent();
        for (ItemStack itemStack : newFilter.items) {
            filters[filterIndex].setItem(index, itemStack);
            index++;
        }
    }

    /**
     * 一次性收集所有启用 filter 中允许的方块（只统计可放置为方块的 BlockItem）
     */
    public Set<Block> collectAllowedBlocks() {
        Set<Block> allowed = new HashSet<>();
        for (int i = 0; i < FILTER_COUNT; i++) {
            if (!filterActive[i]) continue;
            SimpleContainer filter = filters[i];
            int size = filter.getContainerSize();
            for (int slot = 0; slot < size; slot++) {
                ItemStack stack = filter.getItem(slot);
                if (stack.isEmpty()) continue;
                if (stack.getItem() instanceof BlockItem blockItem) {
                    allowed.add(blockItem.getBlock());
                }
            }
        }
        return allowed;
    }

    public boolean isActive(int filterIndex) {
        return filterActive[filterIndex];
    }
    public boolean isEmpty(){
        for (int i = 0; i < FILTER_COUNT; i++) {
            SimpleContainer filter = filters[i];
            if (isActive(i) && !filter.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void setActive(int filterIndex, boolean active) {
        filterActive[filterIndex] = active;
    }
    @Override
    public String toString() {
        return "ChainedUniversalBlockReinforcerFilter{" +
                "filters=" + Arrays.toString(filters) +
                ", filterActive=" + Arrays.toString(filterActive) +
                '}';
    }
}
