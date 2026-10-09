package com.ffsupver.securityCraftBlockExtend.mixin;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.piston.LinearActuatorBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LinearActuatorBlockEntity.class)
public interface LinearActuatorBlockEntityAccessor {
    @Accessor(remap = false)
    AssemblyException getLastException();
    @Accessor(remap = false)
    void setLastException(AssemblyException exception);
}
