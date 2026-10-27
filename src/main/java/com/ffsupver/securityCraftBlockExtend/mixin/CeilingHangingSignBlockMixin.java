package com.ffsupver.securityCraftBlockExtend.mixin;

import com.ffsupver.securityCraftBlockExtend.compat.quark.ReinforcedWoodPostBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.level.block.CeilingHangingSignBlock")
public class CeilingHangingSignBlockMixin {
    @ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
    private boolean canSurvive(boolean prev, BlockState state, LevelReader worldIn, BlockPos pos) {
        return ReinforcedWoodPostBlock.canBelowSurvive(prev,state,worldIn,pos);
    }
}
