package com.ffsupver.securityCraftBlockExtend.mixin;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.level.block.BasePressurePlateBlock")
public interface BasePressurePlateBlockAccessor {
    @Accessor
    BlockSetType getType();
}
