package com.ffsupver.securityCraftBlockExtend.mixin;

import net.minecraft.world.level.block.PressurePlateBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.level.block.PressurePlateBlock")
public interface PressurePlateBlockAccessor {
    @Accessor
    PressurePlateBlock.Sensitivity getSensitivity();
}
