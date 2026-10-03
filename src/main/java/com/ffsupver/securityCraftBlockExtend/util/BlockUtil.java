package com.ffsupver.securityCraftBlockExtend.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlockUtil {
    public static void AllDirectionOf(BlockPos startPos, BiConsumer<BlockPos,Direction> f){
        AllDirectionOf(startPos,f,b->false);
    }
    public static void AllDirectionOf(BlockPos startPos, Consumer<BlockPos> f, Predicate<BlockPos> shouldBreak){
        AllDirectionOf(startPos,(b,fa)->f.accept(b),shouldBreak);
    }
    public static void AllDirectionOf(BlockPos startPos, BiConsumer<BlockPos,Direction> f, Predicate<BlockPos> shouldBreak){
        for (Direction d : Direction.values()){
            if (shouldBreak.test(startPos.relative(d))){
                break;
            }
            f.accept(startPos.relative(d),d);
        }
    }
    /**
     * 广度优先遍历。
     * 每个方块第一次被访问时，拿到的就是到 startPos 的最短距离，
     * 因此不会出现 DFS 那种“深路径先标记、短路径后到被剪”导致漏方块的问题。
     *
     * @param startPos       起点
     * @param walkedBlockPos 输出集合，会把符合条件的方块加进去（调用方传入，可为空集合）
     * @param check          (位置, 进入方向) -> 是否允许遍历该方块；起点传入的 fromFace 为 null
     * @param maxRange       最大遍历距离（按步数计）
     * @param maxSize        遍历结果的最大方块数量；达到后立即停止遍历
     */
    public static void walkAllBlocks(BlockPos startPos,
                                     Set<BlockPos> walkedBlockPos,
                                     BiPredicate<BlockPos, Direction> check,
                                     int maxRange,
                                     int maxSize) {
        // 数量上限保护
        if (maxSize <= 0) {
            return;
        }

        Queue<BlockPos> queue = new ArrayDeque<>();
        // 记录每个方块到起点的最短距离；同时也充当“已入队/已处理”的标记，避免重复
        Map<BlockPos, Integer> distanceMap = new HashMap<>();
        // 记录到达该方块时的“进入方向”，用于 check
        Map<BlockPos, Direction> fromFaceMap = new HashMap<>();

        distanceMap.put(startPos, 0);
        fromFaceMap.put(startPos, null);
        queue.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            int distance = distanceMap.get(current);
            Direction fromFace = fromFaceMap.get(current);

            // 已经遍历过则跳过（防御性判断，正常情况下不会触发）
            if (walkedBlockPos.contains(current)) {
                continue;
            }

            // 不满足条件则不遍历，也不扩展其邻居
            if (!check.test(current, fromFace)) {
                continue;
            }

            walkedBlockPos.add(current);

            // 达到数量上限，立即停止
            if (walkedBlockPos.size() >= maxSize) {
                return;
            }

            // 到达最大范围，不再向外扩展
            if (distance >= maxRange) {
                continue;
            }

            AllDirectionOf(current, (neighbor, d) -> {
                // 如果该邻居已经被以更短或相等的距离入队过，就不再入队
                if (distanceMap.containsKey(neighbor)) {
                    return;
                }

                distanceMap.put(neighbor, distance + 1);
                fromFaceMap.put(neighbor, d);
                queue.add(neighbor);
            });
        }
    }
}
