package com.ffsupver.securityCraftBlockExtend.mixin;

import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.reinforcedTrack.TrackPlacementCapture;
import com.simibubi.create.content.trains.track.TrackPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrackPlacement.class)
public class TrackPlacementMixin {

    @Inject(method = "placeTracks", at = @At("HEAD"),remap = false)
    private static void create$capturePlacement(Level level,
                                                TrackPlacement.PlacementInfo info,
                                                BlockState state1,
                                                BlockState state2,
                                                BlockPos targetPos1,
                                                BlockPos targetPos2,
                                                boolean simulate,
                                                CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
        if (simulate) return;         // 只要真放
        if (level.isClientSide()) return; // 双保险
        TrackPlacementCapture.capture(info, targetPos1, targetPos2);
    }
}