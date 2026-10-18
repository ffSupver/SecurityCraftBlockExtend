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
     * 广度优先遍历（遍历条件和收录条件相同）。
     */
    public static void walkAllBlocks(BlockPos startPos,
                                     Set<BlockPos> walkedBlockPos,
                                     BiPredicate<BlockPos, Direction> check,
                                     int maxRange,
                                     int maxSize) {
        walkAllBlocks(startPos, walkedBlockPos, check, check, maxRange, maxSize);
    }

    /**
     * 广度优先遍历。
     * 每个方块第一次被访问时，拿到的就是到 startPos 的最短距离。
     *
     * @param startPos        起点
     * @param walkedBlockPos  输出集合，只收录 shouldInclude 通过的方块（可以不相连）
     * @param shouldTraverse  (位置, 进入方向) -> 是否继续向邻居扩散；返回 false 表示到此为止，不再扩展
     * @param shouldInclude   (位置, 进入方向) -> 是否把该方块加入结果集
     * @param maxRange        最大遍历距离（按步数计，等于 maxRange 的方块仍可收录但不扩展）
     * @param maxSize         结果集最大数量；达到后立即停止遍历
     */
    public static void walkAllBlocks(BlockPos startPos,
                                     Set<BlockPos> walkedBlockPos,
                                     BiPredicate<BlockPos, Direction> shouldTraverse,
                                     BiPredicate<BlockPos, Direction> shouldInclude,
                                     int maxRange,
                                     int maxSize) {
        // 数量上限保护
        if (maxSize <= 0) {
            return;
        }

        Queue<BlockPos> queue = new ArrayDeque<>();
        // 记录每个方块到起点的最短距离；同时充当“已入队/已处理”标记，避免重复
        Map<BlockPos, Integer> distanceMap = new HashMap<>();
        // 记录到达该方块时的“进入方向”，用于两个谓词
        Map<BlockPos, Direction> fromFaceMap = new HashMap<>();

        distanceMap.put(startPos, 0);
        fromFaceMap.put(startPos, null);
        queue.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            int distance = distanceMap.get(current);
            Direction fromFace = fromFaceMap.get(current);

            // ① 是否收录当前方块（收录和遍历互相独立）
            if (shouldInclude.test(current, fromFace)) {
                walkedBlockPos.add(current);
                if (walkedBlockPos.size() >= maxSize) {
                    return; // 达到数量上限，立即停止
                }
            }

            // ② 到达最大范围，不再向外扩展（但仍允许被收录）
            if (distance >= maxRange) {
                continue;
            }

            // ③ 是否继续向外遍历附近方块
            if (!shouldTraverse.test(current, fromFace)) {
                continue;
            }

            AllDirectionOf(current, (neighbor, d) -> {
                // 已经以更短或相等的距离入队过，就不再入队
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
