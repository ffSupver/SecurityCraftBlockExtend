package com.ffsupver.securityCraftBlockExtend.mixin;

import com.ffsupver.securityCraftBlockExtend.compat.quark.QuarkBlocksCapture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.zeta.registry.ZetaRegistry;

@Mixin(ZetaRegistry.class)
public class ZetaRegistryMixin {
    @Inject(method = "registerBlock(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/resources/ResourceLocation;Z)V",at = @At("HEAD"),remap = false)
    private void onRegisterBlock(Block block, ResourceLocation id, boolean hasBlockItem, CallbackInfo ci) {
        QuarkBlocksCapture.capture(id, block);
    }
}
