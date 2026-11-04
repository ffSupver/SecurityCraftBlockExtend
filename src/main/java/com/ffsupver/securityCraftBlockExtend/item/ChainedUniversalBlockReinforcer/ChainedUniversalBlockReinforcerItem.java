package com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer;

import com.ffsupver.securityCraftBlockExtend.Config;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEPackets;
import com.ffsupver.securityCraftBlockExtend.screen.ChainedUniversalBlockReinforcerMenu;
import com.ffsupver.securityCraftBlockExtend.util.BlockUtil;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChainedUniversalBlockReinforcerItem extends UniversalBlockReinforcerItem {

    public ChainedUniversalBlockReinforcerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()){
            if (!level.isClientSide) {
                ItemStack heldItem = player.getItemInHand(hand);

                NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return heldItem.getHoverName();
                    }

                    @Override
                    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                        return new ChainedUniversalBlockReinforcerMenu(pContainerId, pPlayerInventory);
                    }
                });
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }else {
            return super.use(level, player, hand);
        }
    }

    public static boolean tryReinforcedBlock(Player player, Level level, BlockPos pos){
        ItemStack stack = player.getMainHandItem();
        Item held = stack.getItem();

        if (held instanceof ChainedUniversalBlockReinforcerItem){
            BlockState state = level.getBlockState(pos);

            Block clickBlock = state.getBlock();
            boolean firstConverted = UniversalBlockReinforcerItem.convertBlock(state,level,stack,pos,player);


            if (firstConverted){
                boolean isCooldown = player.getCooldowns().isOnCooldown(held);
                if (!isCooldown && player.isShiftKeyDown() && stack.getItem() instanceof ChainedUniversalBlockReinforcerItem){
                    if (!level.isClientSide()){
                        Set<BlockPos> chainBlocks = new HashSet<>();
                        int remain = stack.isDamageableItem() ? Math.min(Config.chainedReinforcerMaxBlocks, stack.getMaxDamage() - stack.getDamageValue()) : Config.chainedReinforcerMaxBlocks;
                        BlockUtil.walkAllBlocks(
                                pos,
                                chainBlocks,
                                (checkPos,face)-> checkPos.equals(pos) || level.getBlockState(checkPos).is(clickBlock),
                                Config.chainedReinforcerMaxRange,
                                remain
                        );


                        for (BlockPos chainBlock : chainBlocks) {
                            UniversalBlockReinforcerItem.convertBlock(level.getBlockState(chainBlock), level, stack, chainBlock, player);
                        }
                    }
                    player.getCooldowns().addCooldown(held, 5);
                }
            }
            return true;
        }
        return false;
    }

    public static void leftClickOnBlock(PlayerInteractEvent.LeftClickBlock event){
        if (ConfigHandler.SERVER.inWorldUnReinforcing.get()) {
            Player player = event.getEntity();
            Level level = event.getLevel();
            BlockPos pos = event.getPos();


            if (tryReinforcedBlock(player, level, pos)){
                event.setCanceled(true);
            }
        }
    }

    public static void leftClickOnEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (ConfigHandler.SERVER.inWorldUnReinforcing.get()) {
            Player player = event.getEntity();
            Level level = event.getLevel();
            BlockPos pos = getTargetBlockPos(player,0,Config.chainedReinforcerInteractRange);


            if (pos != null){
                if (level.isClientSide()){
                    SCBEPackets.INSTANCE.sendToServer(new ChainedUniversalReinforcePacket(pos));
                }
            }
        }
    }


    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.securitycraft_block_extend.chained_universal_block_reinforcer.description").setStyle(Utils.GRAY_STYLE));
    }


    /**
     * 获取玩家视线命中的方块位置；若手持物品不符、未命中或距离超限，则返回 null。
     */
    @Nullable
    public static BlockPos getTargetBlockPos(Player player, float partialTick,double reachDistance) {
        HitResult hitResult = player.pick(reachDistance, partialTick, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        Vec3 hitVec = blockHit.getLocation();
        if (player.getEyePosition().distanceToSqr(hitVec) > reachDistance * reachDistance) {
            return null;
        }

        return blockHit.getBlockPos();
    }

}
