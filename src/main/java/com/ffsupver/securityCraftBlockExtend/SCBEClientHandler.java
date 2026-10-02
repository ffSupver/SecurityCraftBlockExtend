package com.ffsupver.securityCraftBlockExtend;

import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SecurityCraftBlockExtend.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class SCBEClientHandler {

    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        for (Supplier<? extends Block> reg : SCBEBlocks.getReinforcedTintBlocks()) {
            event.register(SCBEClientHandler::reinforcedBlockColor, reg.get());
        }
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        for (Supplier<? extends Block> reg : SCBEBlocks.getReinforcedTintBlocks()) {
            event.register(SCBEClientHandler::reinforcedItemColor, reg.get());
        }
    }

    // ---------- 抽取出来的颜色计算逻辑 ----------

    private static int reinforcedBlockColor(BlockState state, BlockAndTintGetter level,
                                            BlockPos pos, int tintIndex) {
        if (tintIndex != 0) return 0xFFFFFF;

        IOwnable ownable = null;
        if (level != null && pos != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IOwnable o) ownable = o;
        }
        return ClientHandler.mixWithReinforcedTintIfEnabled(0xFFFFFF, ownable);
    }

    private static int reinforcedItemColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) return 0xFFFFFF;
        return ClientHandler.mixWithReinforcedTintIfEnabled(0xFFFFFF, null);
    }
}