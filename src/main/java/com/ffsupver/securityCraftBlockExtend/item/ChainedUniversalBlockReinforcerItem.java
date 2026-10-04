package com.ffsupver.securityCraftBlockExtend.item;

import com.ffsupver.securityCraftBlockExtend.Config;
import com.ffsupver.securityCraftBlockExtend.util.BlockUtil;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChainedUniversalBlockReinforcerItem extends UniversalBlockReinforcerItem {
    public ChainedUniversalBlockReinforcerItem(Properties properties) {
        super(properties);
    }

    public static void leftClickOnBlock(PlayerInteractEvent.LeftClickBlock event){
        if (ConfigHandler.SERVER.inWorldUnReinforcing.get()) {
            Player player = event.getEntity();
            ItemStack stack = player.getMainHandItem();
            Item held = stack.getItem();
            Level level = event.getLevel();
            BlockPos pos = event.getPos();

            if (held instanceof ChainedUniversalBlockReinforcerItem){
                BlockState state = level.getBlockState(pos);
                Block clickBlock = state.getBlock();
                boolean canConverted = UniversalBlockReinforcerItem.convertBlock(state,level,stack,pos,player);
                if (canConverted){
                    if (player.isShiftKeyDown() && stack.getItem() instanceof ChainedUniversalBlockReinforcerItem){
                        Set<BlockPos> chainBlocks = new HashSet<>();
                        int remain = stack.isDamageableItem() ? Math.min(Config.chainedReinforcerMaxBlocks, stack.getMaxDamage() - stack.getDamageValue()) : Config.chainedReinforcerMaxBlocks;
                        BlockUtil.walkAllBlocks(
                                pos,
                                chainBlocks,
                                (checkPos, face) -> pos.equals(checkPos) || level.isLoaded(checkPos) && level.getBlockState(checkPos).getBlock().equals(clickBlock),
                                Config.chainedReinforcerMaxRange,
                                remain
                        );

                        for (BlockPos chainBlock : chainBlocks) {
                            UniversalBlockReinforcerItem.convertBlock(level.getBlockState(chainBlock), level, stack, chainBlock, event.getEntity());
                        }
                    }

                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.securitycraft_block_extend.chained_universal_block_reinforcer.description").setStyle(Utils.GRAY_STYLE));
    }
}
