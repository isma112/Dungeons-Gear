package com.infamous.dungeons_gear.mixin;


import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(HurtByTargetGoal.class)
public abstract class HurtByTargetGoalMixin extends TargetGoal {

    @Shadow
    private Class<?>[] toIgnoreAlert;

    public HurtByTargetGoalMixin(MobEntity mobIn, boolean checkSight) {
        super(mobIn, checkSight);
    }

    /**
     * @author Jackiecrazy
     * @reason Prevents the NPE caused by !mobentity.isOnSameTeam(this.goalOwner.getRevengeTarget()) when getRevengeTarget() returns null
     * Also prevents ClassCastException when checking if current iterated mob, as a TameableEntity, has an owner
     */
    @Overwrite
    protected void alertOthers() {
        double d0 = this.getFollowDistance();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
        List<Class<?>> reinforcement = null;
        if (toIgnoreAlert != null)
            reinforcement = Lists.newArrayList(toIgnoreAlert);
        List<MobEntity> list = this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), axisalignedbb);
        Iterator<MobEntity> iterator = list.iterator();

        while (reinforcement != null && iterator.hasNext()) {
            MobEntity mobentity = iterator.next();
            if (this.mob != mobentity
                    && mobentity.getTarget() == null
                    && (!(this.mob instanceof TameableEntity) ||
                    (mobentity instanceof TameableEntity && ((TameableEntity) this.mob).getOwner() == ((TameableEntity) mobentity).getOwner()))
                    && (this.mob.getLastHurtByMob() != null && !mobentity.isAlliedTo(this.mob.getLastHurtByMob()))
                    && reinforcement.contains(mobentity.getClass())) {
                //if you're not me and have no target and I'm either not tamed or you're tamed to the same master and I have a revenge target and you're not on the same team as my revenge target and you're one of the reinforcement types, set attack target to the same person
                //I don't know what Mojang or Infamous is doing, but it's now O(n).
                this.setAttackTarget(mobentity, this.mob.getLastHurtByMob());
            }
        }
    }

    protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
        mobIn.setTarget(targetIn);
    }


}
