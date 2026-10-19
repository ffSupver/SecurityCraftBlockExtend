package com.ffsupver.securityCraftBlockExtend.item;

import com.ffsupver.securityCraftBlockExtend.Config;
import com.ffsupver.securityCraftBlockExtend.util.BlockUtil;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import java.util.*;

public class ChainedUniversalBlockReinforcerItem extends UniversalBlockReinforcerItem {
    public static List<ChainBlockPredicate> chainBlockPredicates = new ArrayList<>();

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
                ChainBlockPredicateResult firstWalkResult = onWalkBlockTest(pos, level, player);
                ChainBlockPredicateResult firstResult = onAddBlockTest(pos,level, player);
                boolean convertFirst = !firstWalkResult.equals(ChainBlockPredicateResult.FAIL) && !firstResult.equals(ChainBlockPredicateResult.FAIL);
                boolean firstConverted = false;
                if (convertFirst){
                   firstConverted = UniversalBlockReinforcerItem.convertBlock(state,level,stack,pos,player);
                }

                if (firstWalkResult.equals(ChainBlockPredicateResult.SUCCESS) || firstWalkResult.equals(ChainBlockPredicateResult.PASS) && firstConverted){
                    if (player.isShiftKeyDown() && stack.getItem() instanceof ChainedUniversalBlockReinforcerItem && !level.isClientSide()){
                        Set<BlockPos> chainBlocks = new HashSet<>();
                        int remain = stack.isDamageableItem() ? Math.min(Config.chainedReinforcerMaxBlocks, stack.getMaxDamage() - stack.getDamageValue()) : Config.chainedReinforcerMaxBlocks;
                        BlockUtil.walkAllBlocks(
                                pos,
                                chainBlocks,
                                (checkPos, face) -> shouldWalkBlock(level, checkPos, player, face, clickBlock,pos),
                                (checkPos,face)-> shouldAddBlock(level, checkPos, player, face,clickBlock),
                                Config.chainedReinforcerMaxRange,
                                remain
                        );

                        for (BlockPos chainBlock : chainBlocks) {
                            UniversalBlockReinforcerItem.convertBlock(level.getBlockState(chainBlock), level, stack, chainBlock, event.getEntity());
                        }
                    }
                }
                event.setCanceled(true);
            }
        }
    }

    private static boolean shouldWalkBlock(Level level, BlockPos checkPos, Player player, Direction face, Block clickBlock,BlockPos firstPos) {
        if (level.isLoaded(checkPos)){
            ChainBlockPredicateResult result = onWalkBlockTest(checkPos, level, player);
            if (result.equals(ChainBlockPredicateResult.FAIL)) {
                return false;
            } else if (result.equals(ChainBlockPredicateResult.SUCCESS)) {
                return true;
            }
            // 返回PASS时
            return level.getBlockState(checkPos).getBlock().equals(clickBlock) || checkPos.equals(firstPos);
        }else {
            return false;
        }
    }

    private static ChainBlockPredicateResult onWalkBlockTest(BlockPos checkPos, Level level, Player player){
        for (ChainBlockPredicate chainBlockPredicate : chainBlockPredicates) {
            ChainBlockPredicateResult result = chainBlockPredicate.shouldWalk(checkPos, level.getBlockState(checkPos), player);
            if (!result.equals(ChainBlockPredicateResult.PASS)) {
                return result;
            }
        }
        return ChainBlockPredicateResult.PASS;
    }

    private static boolean shouldAddBlock(Level level, BlockPos checkPos, Player player, Direction face, Block clickBlock){
        if (level.isLoaded(checkPos)) {
            ChainBlockPredicateResult result = onAddBlockTest(checkPos, level, player);
            if (result.equals(ChainBlockPredicateResult.FAIL)) {
                return false;
            } else if (result.equals(ChainBlockPredicateResult.SUCCESS)) {
                return true;
            }
            // 返回PASS时
            return level.getBlockState(checkPos).getBlock().equals(clickBlock);
        }else {
            return false;
        }
    }

    private static ChainBlockPredicateResult onAddBlockTest(BlockPos checkPos, Level level, Player player){
        for (ChainBlockPredicate chainBlockPredicate : chainBlockPredicates) {
            ChainBlockPredicateResult result = chainBlockPredicate.test(checkPos, level.getBlockState(checkPos), player);
            if (!result.equals(ChainBlockPredicateResult.PASS)) {
               return result;
            }
        }
        return ChainBlockPredicateResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.securitycraft_block_extend.chained_universal_block_reinforcer.description").setStyle(Utils.GRAY_STYLE));
    }

    public interface ChainBlockPredicate {
        ChainBlockPredicateResult test(BlockPos pos, BlockState state, Player player);
        ChainBlockPredicateResult shouldWalk(BlockPos pos, BlockState state, Player player);
    }

    public enum ChainBlockPredicateResult {
        SUCCESS, FAIL, PASS
    }
}
