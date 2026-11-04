package com.ffsupver.securityCraftBlockExtend.screen;

import com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalBlockReinforcerFilter;
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

public class ChainedUniversalBlockReinforcerMenu extends AbstractContainerMenu {
    private final ItemStack blockReinforcer;
    protected ChainedPositionData serverData = new ChainedPositionData(0, 0, 0, 0, 0, 0);
    private final int[] lastSent = new int[6];
    private final Player player;
    //filters
    private final SimpleContainer filterContainer;
    private final ChainedUniversalBlockReinforcerFilter filter;
    private int filterIndex;

    public ChainedUniversalBlockReinforcerMenu(int pContainerId, Inventory inventory) {
        super(SCBEScreens.CHAINED_UNIVERSAL_BLOCK_REINFORCER.get(), pContainerId);
        blockReinforcer = inventory.getSelected().getItem() instanceof UniversalBlockReinforcerItem ? inventory.getSelected() : inventory.offhand.get(0);
        player = inventory.player;
        CompoundTag nbt = blockReinforcer.getOrCreateTag();

        if (nbt.contains(ChainedPositionData.NBT_TAG, CompoundTag.TAG_COMPOUND)){
            serverData = ChainedPositionData.fromNbt(nbt.getCompound(ChainedPositionData.NBT_TAG));
            lastSent[0] = serverData.x();
            lastSent[1] = serverData.y();
            lastSent[2] = serverData.z();
            lastSent[3] = serverData.posX();
            lastSent[4] = serverData.posY();
            lastSent[5] = serverData.posZ();
        }

        filterIndex = 0;
        filterContainer = new SimpleContainer(9);
        filter = new ChainedUniversalBlockReinforcerFilter();
        if (nbt.contains(ChainedUniversalBlockReinforcerFilter.NBT_TAG, CompoundTag.TAG_COMPOUND)){
            filter.fromNbt(nbt.getCompound(ChainedUniversalBlockReinforcerFilter.NBT_TAG));
        }
        filter.copyFilter(filterContainer,filterIndex);

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
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
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
                if (!carriedStack.isEmpty() && !(carriedStack.getItem() instanceof BlockItem)){
                    return;
                }

                boolean isStackRepeat = false;
                if (!carriedStack.isEmpty()){
                    for (ItemStack stack : filterContainer.items) {
                        if (stack.getItem().equals(carriedStack.getItem())) {
                            isStackRepeat = true;
                            break;
                        }
                    }
                }

                if (!isStackRepeat){
                    this.filterContainer.setItem(filterSlotId, carriedStack);
                    filter.applyFilter(filterContainer, filterIndex);
                    writeFilterNbt();
                }
            }
        }
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        // Only Call in Server Side
        if (pId != filterIndex){
            filterIndex = pId;
            filter.copyFilter(filterContainer, filterIndex);
        }else {
            boolean isActive = filter.isActive(pId);
            filter.setActive(pId, !isActive);
            writeFilterNbt();
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

        if (lastSent[index] == value) return;
        lastSent[index] = value;

        SCBEPackets.INSTANCE.sendToServer(new ChainedPositionData.Packet(new ChainedPositionData(
                lastSent[0], lastSent[1], lastSent[2],
                lastSent[3], lastSent[4], lastSent[5]
        )));
    }

    public void applyClientData(ChainedPositionData fromClient) {
        serverData = fromClient;
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

    public int getFilterIndex() {
        return filterIndex;
    }

    public ChainedUniversalBlockReinforcerFilter getFilter() {
        return filter;
    }
}
