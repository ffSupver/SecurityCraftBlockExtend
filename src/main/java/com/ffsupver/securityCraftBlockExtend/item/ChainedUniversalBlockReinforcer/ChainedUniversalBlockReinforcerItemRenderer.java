package com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer;

import com.ffsupver.securityCraftBlockExtend.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.lines());

        var aabb2 = mc.level.getBlockState(targetPos)
                .getShape(mc.level, targetPos)
                .bounds()
                .inflate(0.002);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false); // 防止线条写入深度缓冲区


        LevelRenderer.renderLineBox(poseStack, lineConsumer, aabb2, 1.0F, 1.0F, 1.0F, 1.0F);

        bufferSource.endBatch(RenderType.lines());

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        poseStack.popPose();
    }
}
