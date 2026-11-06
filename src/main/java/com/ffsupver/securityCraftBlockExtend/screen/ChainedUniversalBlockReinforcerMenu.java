package com.ffsupver.securityCraftBlockExtend.screen;

import com.ffsupver.securityCraftBlockExtend.Config;
import com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalBlockReinforcerFilter;
import com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalBlockReinforcerItem;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEPackets;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEScreens;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public class ChainedUniversalBlockReinforcerMenu extends AbstractContainerMenu {
    private final ItemStack blockReinforcer;
    protected ChainedPositionData serverData = new ChainedPositionData(0, 0, 0, 0, 0, 0);
    private final int[] lastChainedPositionSent = new int[6];
    private BiConsumer<Integer,ChainedPositionData> onPositionChanged;
    private final Player player;
    //filters
    private final SimpleContainer filterContainer;
    private final ChainedUniversalBlockReinforcerFilter filter;
    private int filterIndex;

    protected boolean isFillingMode;
    protected static final int SET_MODE_BUTTON_ID = 16;

    public ChainedUniversalBlockReinforcerMenu(int pContainerId, Inventory inventory) {
        super(SCBEScreens.CHAINED_UNIVERSAL_BLOCK_REINFORCER.get(), pContainerId);
        blockReinforcer = inventory.getSelected().getItem() instanceof UniversalBlockReinforcerItem ? inventory.getSelected() : inventory.offhand.get(0);
        player = inventory.player;
        CompoundTag nbt = blockReinforcer.getOrCreateTag();

        if (nbt.contains(ChainedPositionData.NBT_TAG, CompoundTag.TAG_COMPOUND)){
            serverData = ChainedPositionData.fromNbt(nbt.getCompound(ChainedPositionData.NBT_TAG));
            lastChainedPositionSent[0] = serverData.x();
            lastChainedPositionSent[1] = serverData.y();
            lastChainedPositionSent[2] = serverData.z();
            lastChainedPositionSent[3] = serverData.posX();
            lastChainedPositionSent[4] = serverData.posY();
            lastChainedPositionSent[5] = serverData.posZ();
        }

        filterIndex = 0;
        filterContainer = new SimpleContainer(9);
        filter = new ChainedUniversalBlockReinforcerFilter();
        if (nbt.contains(ChainedUniversalBlockReinforcerFilter.NBT_TAG, CompoundTag.TAG_COMPOUND)){
            filter.fromNbt(nbt.getCompound(ChainedUniversalBlockReinforcerFilter.NBT_TAG));
        }
        filter.copyFilter(filterContainer,filterIndex);

        isFillingMode = ChainedUniversalBlockReinforcerItem.isFillingMode(blockReinforcer);

        //main player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, 9 + j + i * 9, 13 + j * 18, 104 + i * 18));
            }
        }

        //player hotbar
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 13 + i * 18, 162));
        }

        for (int i =0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                addSlot(new Slot(filterContainer, j + i * 3, 8 + j * 18, 25 + i * 18));
            }
        }


        // sync filter state
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                // 服务端：读 filter 的当前状态打包
                // 客户端：在 set() 更新了 filter 后，这里也会返回相同值，保证比较一致
                int packed = 0;
                for (int i = 0; i < ChainedUniversalBlockReinforcerFilter.FILTER_COUNT; i++) {
                    if (filter.isActive(i)) {
                        packed |= (1 << i);
                    }
                }
                return packed;
            }

            @Override
            public void set(int value) {
                // 仅在客户端由同步包调用：直接更新 filter 对象
                for (int i = 0; i < ChainedUniversalBlockReinforcerFilter.FILTER_COUNT; i++) {
                    filter.setActive(i, (value & (1 << i)) != 0);
                }
            }
        });
        // sync filterIndex
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return filterIndex;
            }

            @Override
            public void set(int pValue) {
                filterIndex = pValue;
            }
        });
        // sync isFillingMode
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return isFillingMode ? 1 : 0;
            }

            @Override
            public void set(int pValue) {
                isFillingMode = pValue == 1;
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack clickedItem = getSlot(pIndex).getItem().copyWithCount(1);

        int emptySlot = -1;
        for (int i = 0; i < filterContainer.getContainerSize(); i++){
            ItemStack stack = filterContainer.getItem(i);
            if (stack.isEmpty()){
                emptySlot = i;
                break;
            }
        }

        if (emptySlot != -1 && canAddItem(clickedItem)){
            filterContainer.setItem(emptySlot, clickedItem);
            filter.applyFilter(filterContainer, filterIndex);
            if (!player.level().isClientSide()){
                writeFilterNbt();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slot, int dragType, ClickType clickType, Player player) {
        if (slot < 36){
            if (!(slot >= 0 && getSlot(slot).getItem().getItem() instanceof UniversalBlockReinforcerItem)) {
                super.clicked(slot, dragType, clickType, player);
            }
            return;
        }else {
            int filterSlotId = slot - 36;
            if (clickType != ClickType.CLONE && clickType != ClickType.THROW){
                ItemStack carriedStack = getCarried().copyWithCount(1);

                if (canAddItem(carriedStack)){
                    this.filterContainer.setItem(filterSlotId, carriedStack);
                    filter.applyFilter(filterContainer, filterIndex);
                    writeFilterNbt();
                }
            }
        }
    }

    private boolean canAddItem(ItemStack itemStack){
        if (!itemStack.isEmpty() && !(itemStack.getItem() instanceof BlockItem)){
            return false;
        }

        boolean isStackRepeat = false;
        if (!itemStack.isEmpty()){
            for (ItemStack stack : filterContainer.items) {
                if (stack.getItem().equals(itemStack.getItem())) {
                    isStackRepeat = true;
                    break;
                }
            }
        }
        return !isStackRepeat;
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        // Only Call in Server Side
        // set isFillingMode
        if (pId == SET_MODE_BUTTON_ID){
            isFillingMode = !isFillingMode;
            ChainedUniversalBlockReinforcerItem.setFillingMode(blockReinforcer, isFillingMode);
        }else {
            // change filterContainer
            if (pId != filterIndex) {
                filterIndex = pId;
                filter.copyFilter(filterContainer, filterIndex);
            } else {
                boolean isActive = filter.isActive(pId);
                filter.setActive(pId, !isActive);
                writeFilterNbt();
            }
        }

        broadcastChanges();
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return !PlayerUtils.getItemStackFromAnyHand(player, blockReinforcer.getItem()).isEmpty();
    }

    public void onInputChanged(int index, int value) {
        if (!player.level().isClientSide()) return;

        if (lastChainedPositionSent[index] == value) return;
        lastChainedPositionSent[index] = value;

        ChainedPositionData newData = buildChainedPositionData();
        newData = newData.clamp(Config.chainedReinforcerMaxRange);

        if (getChainedPositionValue(newData,index) != lastChainedPositionSent[index]){
            onPositionChanged.accept(index, newData);
        }

        SCBEPackets.INSTANCE.sendToServer(new ChainedPositionData.Packet(newData));
    }

    public ChainedPositionData buildChainedPositionData(){
        return new ChainedPositionData(lastChainedPositionSent[0], lastChainedPositionSent[1], lastChainedPositionSent[2], lastChainedPositionSent[3], lastChainedPositionSent[4], lastChainedPositionSent[5]);
    }

    public void applyClientData(ChainedPositionData fromClient) {
        ChainedPositionData clamped = fromClient.clamp(Config.chainedReinforcerMaxRange);
        serverData = clamped;
        writePositionDataNbt();
    }

    private void writePositionDataNbt(){
        if (!player.level().isClientSide()){
            CompoundTag nbt = blockReinforcer.getOrCreateTag();
            nbt.put(ChainedPositionData.NBT_TAG, serverData.toNbt());
            blockReinforcer.setTag(nbt);

            broadcastChanges();
        }
    }

    private void writeFilterNbt(){
        if (!player.level().isClientSide()){
            CompoundTag nbt = blockReinforcer.getOrCreateTag();
            nbt.put(ChainedUniversalBlockReinforcerFilter.NBT_TAG, filter.toNbt());
            blockReinforcer.setTag(nbt);

            broadcastChanges();
        }
    }

    public boolean setGhostItem(int index, ItemStack stack){
        if (!canAddItem(stack)){
            return false;
        }
        filterContainer.setItem(index, stack);
        filter.applyFilter(filterContainer, filterIndex);

        if (!player.level().isClientSide()){
            writeFilterNbt();
        }
        return true;
    }

    public int getFilterIndex() {
        return filterIndex;
    }

    public ChainedUniversalBlockReinforcerFilter getFilter() {
        return filter;
    }

    public static int getChainedPositionValue(ChainedPositionData chainedPositionData,int i){
        return switch (i){
            case 0-> chainedPositionData.x();
            case 1-> chainedPositionData.y();
            case 2-> chainedPositionData.z();
            case 3-> chainedPositionData.posX();
            case 4-> chainedPositionData.posY();
            case 5-> chainedPositionData.posZ();
            default-> 0;
        };
    }

    public void setOnPositionChanged(BiConsumer<Integer, ChainedPositionData> onPositionChanged) {
        this.onPositionChanged = onPositionChanged;
    }
}
