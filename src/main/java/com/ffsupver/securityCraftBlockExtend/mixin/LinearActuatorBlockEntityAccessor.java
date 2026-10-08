package com.ffsupver.securityCraftBlockExtend.mixin;

import com.simibubi.create.content.contraptions.AssemblyException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "com.simibubi.create.content.contraptions.piston.LinearActuatorBlockEntity")
public interface LinearActuatorBlockEntityAccessor {
    @Accessor(remap = false)
    AssemblyException getLastException();
    @Accessor(remap = false)
    void setLastException(AssemblyException exception);
}
