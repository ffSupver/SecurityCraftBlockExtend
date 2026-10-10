package com.ffsupver.securityCraftBlockExtend.mixin;

import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrackPlacement.PlacementInfo.class)
public interface PlacementInfoAccessor {
    @Accessor("pos1")   BlockPos create$getPos1();
    @Accessor("pos2")   BlockPos create$getPos2();
    @Accessor("axis1")  Vec3     create$getAxis1();
    @Accessor("axis2")  Vec3     create$getAxis2();
    @Accessor("end1Extent") int  create$getEnd1Extent();
    @Accessor("end2Extent") int  create$getEnd2Extent();
    @Accessor("curve")  BezierConnection create$getCurve();
}