package com.ffsupver.securityCraftBlockExtend.compat;

import com.ffsupver.securityCraftBlockExtend.mixin.EntityKineticBulletAccessor;
import com.tacz.guns.api.event.server.AmmoHitBlockEvent;
import com.tacz.guns.config.common.AmmoConfig;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.particles.BulletHoleOption;
import com.tacz.guns.util.ExplodeUtil;
import net.geforcemods.securitycraft.SCTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public class TacZCompat implements SCBEModCompat{
    @Override
    public String getModId() {
        return Mods.ModIds.TACZ.ModId;
    }

    @Override
    public void init(IEventBus eventBus) {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,TacZCompat::onAmmoHitBlock);
    }

    private static void onAmmoHitBlock(AmmoHitBlockEvent event) {
        BlockHitResult hitResult = event.getHitResult();
        Level level = event.getLevel();
        BlockPos pos = hitResult.getBlockPos();
        EntityKineticBullet ammo = event.getAmmo();
        if (level.getBlockState(pos).is(SCTags.Blocks.REINFORCED_GLASS_PANES)){
            if (level instanceof ServerLevel serverLevel && ammo instanceof EntityKineticBulletAccessor entityKineticBulletAccessor){
                Vec3 hitVec = hitResult.getLocation();
                // 爆炸
                if (entityKineticBulletAccessor.getExplosion()){
                    ExplodeUtil.createExplosion(ammo.getOwner(), ammo, entityKineticBulletAccessor.getExplosionDamage(), entityKineticBulletAccessor.getExplosionRadius(), entityKineticBulletAccessor.getExplosionKnockback(), entityKineticBulletAccessor.getExplosionDestroyBlock(), hitVec);
                    // 爆炸直接结束不留弹孔，不处理之后的逻辑
                }else {
                    // 弹孔与点燃特效
                    BulletHoleOption bulletHoleOption = new BulletHoleOption(hitResult.getDirection(), hitResult.getBlockPos(), ammo.getAmmoId().toString(), ammo.getGunId().toString(), ammo.getGunDisplayId().toString());
                    serverLevel.sendParticles(bulletHoleOption, hitVec.x, hitVec.y, hitVec.z, 1, 0, 0, 0, 0);
                    if (entityKineticBulletAccessor.getIgniteBlock()) {
                        serverLevel.sendParticles(ParticleTypes.LAVA, hitVec.x, hitVec.y, hitVec.z, 1, 0, 0, 0, 0);

                        if (AmmoConfig.IGNITE_BLOCK.get()) {
                            BlockPos offsetPos = pos.relative(hitResult.getDirection());
                            if (BaseFireBlock.canBePlacedAt(level, offsetPos, hitResult.getDirection())) {
                                BlockState fireState = BaseFireBlock.getState(level, offsetPos);
                                level.setBlock(offsetPos, fireState, Block.UPDATE_ALL_IMMEDIATE);
                                serverLevel.sendParticles(ParticleTypes.LAVA, hitVec.x - 1.0 + ammo.getRandom().nextDouble() * 2.0, hitVec.y, hitVec.z - 1.0 + ammo.getRandom().nextDouble() * 2.0, 4, 0, 0, 0, 0);
                            }
                        }
                    }
                }
            }

            event.setCanceled(true);
            ammo.discard();
        }
    }
}
