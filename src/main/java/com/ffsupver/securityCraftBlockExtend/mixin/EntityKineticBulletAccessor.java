package com.ffsupver.securityCraftBlockExtend.mixin;

import com.tacz.guns.entity.EntityKineticBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityKineticBullet.class)
public interface EntityKineticBulletAccessor {
    @Accessor
    boolean getIgniteBlock();
    @Accessor
    boolean getExplosion();
    @Accessor
    float getExplosionDamage();
    @Accessor
    float getExplosionRadius();
    @Accessor
    boolean getExplosionKnockback();
    @Accessor
    boolean getExplosionDestroyBlock();
}
