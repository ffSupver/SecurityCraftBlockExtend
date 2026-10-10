package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.reinforcedTrack;

import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackPlacement;
import com.ffsupver.securityCraftBlockExtend.mixin.PlacementInfoAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TrackPlacementCapture {

    private static TrackPlacement.PlacementInfo info;
    private static BlockPos targetPos1;
    private static BlockPos targetPos2;

    private TrackPlacementCapture() {}

    /** 由 Mixin 调用 */
    public static void capture(TrackPlacement.PlacementInfo placementInfo,
                               BlockPos t1, BlockPos t2) {
        info = placementInfo;
        targetPos1 = t1;
        targetPos2 = t2;
    }

    /** 供 Item 在读之前先清一次，防止上一次遗留 */
    public static void clear() {
        info = null;
        targetPos1 = null;
        targetPos2 = null;
    }

    /**
     * 计算本次放置的所有方块位置（含端点），随后清空。
     * 返回的列表只包含"本次可能被写入"的位置，调用方仍需用 getBlockState 校验。
     */
    public static List<BlockPos> computeAndClear() {
        if (info == null) return Collections.emptyList();

        PlacementInfoAccessor acc = (PlacementInfoAccessor) info;
        BlockPos p1 = acc.create$getPos1();
        BlockPos p2 = acc.create$getPos2();
        Vec3 a1 = acc.create$getAxis1();
        Vec3 a2 = acc.create$getAxis2();
        int e1 = acc.create$getEnd1Extent();
        int e2 = acc.create$getEnd2Extent();
        BezierConnection curve = acc.create$getCurve();

        List<BlockPos> out = new ArrayList<>();
        if (p1 != null && a1 != null) collect(out, p1, a1, e1, curve != null);
        if (p2 != null && a2 != null) collect(out, p2, a2, e2, curve != null);

        // 曲线端点（HAS_BE=true）
        if (targetPos1 != null) out.add(targetPos1);
        if (targetPos2 != null) out.add(targetPos2);

        clear();
        return out;
    }

    private static void collect(List<BlockPos> out, BlockPos origin, Vec3 axis, int extent, boolean hasCurve) {
        // placeTracks 里循环条件是 i < (curve != null ? extent + 1 : extent)
        int count = hasCurve ? extent + 1 : extent;
        for (int i = 0; i < count; i++) {
            Vec3 offset = axis.scale(i);
            out.add(origin.offset(BlockPos.containing(offset)));
        }
    }

    @Nullable
    public static TrackPlacement.PlacementInfo peekInfo() {
        return info;
    }
}