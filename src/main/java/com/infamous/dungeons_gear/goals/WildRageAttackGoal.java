package com.infamous.dungeons_gear.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.VindicatorEntity;

public class WildRageAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
    public WildRageAttackGoal(MobEntity mob) {
        super(mob, LivingEntity.class, 0, true, true, LivingEntity::attackable);
    }

    public boolean canUse() {
        return (super.canUse());
    }

    public void start() {
        super.start();
        this.mob.setNoActionTime(0);
    }
}
