package com.ffsupver.securityCraftBlockExtend.mixin;

import com.ffsupver.securityCraftBlockExtend.compat.quark.ReinforcedWoodPostBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.content.building.block.WoodPostBlock;

@Mixin(WoodPostBlock.PostSideType.class)
public class WoodPostMixin {
    @Inject(method = "get",at = @At(value = "HEAD"), cancellable = true,remap = false)
    private static void get(LevelAccessor world, BlockPos pos, Direction d, CallbackInfoReturnable<WoodPostBlock.PostSideType> cir) {
        BlockState sideState = world.getBlockState(pos.relative(d));
        if (sideState.getBlock() instanceof ReinforcedWoodPostBlock && sideState.getValue(ReinforcedWoodPostBlock.AXIS) == d.getAxis()){
            cir.setReturnValue(WoodPostBlock.PostSideType.OTHER_POST);
        }
    }
}
