package com.ffsupver.securityCraftBlockExtend.mixin;

import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston.ReinforcedContraptionTypes;
import com.simibubi.create.AllContraptionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AllContraptionTypes.class)
public class AllContraptionTypesMixin {

    @Inject(method = "init", at = @At("RETURN"), remap = false)
    private static void scbe$registerReinforcedContraptionTypes(CallbackInfo ci) {
        ReinforcedContraptionTypes.init();
    }
}