package com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer;

import com.ffsupver.securityCraftBlockExtend.Config;
import com.ffsupver.securityCraftBlockExtend.screen.ChainedPositionData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalBlockReinforcerItem.getFillBoxStart;
import static com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalBlockReinforcerItem.getTargetBlockPos;

@OnlyIn(Dist.CLIENT)
public class ChainedUniversalBlockReinforcerItemRenderer {
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (player.isSpectator()) return;

        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof ChainedUniversalBlockReinforcerItem)) return;

        BlockPos targetPos = getTargetBlockPos(player, event.getPartialTick(), Config.chainedReinforcerInteractRange);

        if (targetPos == null) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 camPos = event.getCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(
                targetPos.getX() - camPos.x,
                targetPos.getY() - camPos.y,
                targetPos.getZ() - camPos.z
        );

        if (ChainedUniversalBlockReinforcerItem.isFillingMode(heldItem)){
            ChainedPositionData chainedPositionData = ChainedPositionData.ZERO;
            CompoundTag nbt = heldItem.getOrCreateTag();
            if (nbt.contains(ChainedPositionData.NBT_TAG, CompoundTag.TAG_COMPOUND)){
                chainedPositionData = ChainedPositionData.fromNbt(nbt.getCompound(ChainedPositionData.NBT_TAG));
            }
            renderInFillingMode(poseStack, targetPos, mc,chainedPositionData);
            poseStack.popPose();
        }else {
            renderInChainedMode(poseStack, targetPos, mc);
            poseStack.popPose();
        }
    }

    private static void renderInFillingMode(PoseStack poseStack, BlockPos targetPos, Minecraft mc,ChainedPositionData chainedPositionData) {
        AABB targetBox = mc.level.getBlockState(targetPos)
                .getShape(mc.level, targetPos)
                .bounds()
                .inflate(0.002);
        renderAABB(poseStack, mc, targetBox, 98F/255F, 1.0F, 8F/255F, 1.0F);

        // 白框：填充区域（尺寸为 0 时视为未设置，跳过）
        if (chainedPositionData.isEmpty()) {
            return;
        }
        BlockPos start = getFillBoxStart(targetPos, chainedPositionData);

        // 换算成 poseStack 局部坐标（相对 targetPos）
        int sx = start.getX() - targetPos.getX();
        int sy = start.getY() - targetPos.getY();
        int sz = start.getZ() - targetPos.getZ();

        AABB fillBox = new AABB(
                sx, sy, sz,
                sx + chainedPositionData.x(),
                sy + chainedPositionData.y(),
                sz + chainedPositionData.z()
        ).inflate(0.002);

        renderAABB(poseStack, mc, fillBox, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void renderInChainedMode(PoseStack poseStack, BlockPos targetPos, Minecraft mc){
        AABB aabb2 = mc.level.getBlockState(targetPos)
                .getShape(mc.level, targetPos)
                .bounds()
                .inflate(0.002);
        renderAABB(poseStack, mc, aabb2, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void renderAABB(PoseStack poseStack, Minecraft mc, AABB aabb2,float red,float green,float blue,float alpha){
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(poseStack, lineConsumer, aabb2, red, green, blue, alpha);

        bufferSource.endBatch(RenderType.lines());
    }
}
