package com.ffsupver.securityCraftBlockExtend.compat.jei;

import com.ffsupver.securityCraftBlockExtend.registeries.SCBEPackets;
import com.ffsupver.securityCraftBlockExtend.screen.ChainedUniversalBlockReinforcerScreen;
import com.ffsupver.securityCraftBlockExtend.screen.SetChainedFilterItemGhostItemPacket;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class ChainedUniversalBlockReinforcerScreenGhostItemHandler implements IGhostIngredientHandler<ChainedUniversalBlockReinforcerScreen> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(ChainedUniversalBlockReinforcerScreen gui, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new LinkedList<>();
        for (int i = 36; i < gui.getMenu().slots.size(); i++) {
            final int index = i;
            if (gui.getMenu().slots.get(i).isActive()){
                Slot slot = gui.getMenu().slots.get(i);
                targets.add(new Target<>() {
                    @Override
                    public Rect2i getArea() {
                        return new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
                    }

                    @Override
                    public void accept(I ingredient) {
                        ItemStack itemStack = ((ItemStack) ingredient).copyWithCount(1);
                        boolean canAdd = gui.getMenu().setGhostItem(index - 36, itemStack);
                        if (canAdd){
                            SCBEPackets.INSTANCE.sendToServer(new SetChainedFilterItemGhostItemPacket(itemStack, index - 36));
                        }
                    }
                });
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {}
}
