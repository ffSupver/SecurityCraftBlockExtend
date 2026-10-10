package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.reinforcedTrack;

import com.simibubi.create.content.trains.track.TrackBlockItem;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ReinforcedTrackBlockItem extends TrackBlockItem {

    public ReinforcedTrackBlockItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        // 每次进 useOn 先清一次槽，防止上一次异常残留
        TrackPlacementCapture.clear();

        InteractionResult result = super.useOn(ctx);

        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();

        if (level.isClientSide() || player == null)
            return result;
        if (result != InteractionResult.SUCCESS)
            return result;

        List<BlockPos> placed = TrackPlacementCapture.computeAndClear();
        for (BlockPos pos : placed) {
            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof ReinforcedTrackEntity rte))
                continue;

            Owner current = rte.getOwner();
            if (current != null && !current.isDefaultOwner())
                continue; // 已有主人，不覆盖

            MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
        }
        return result;
    }
}