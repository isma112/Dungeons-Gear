package com.infamous.dungeons_gear.utilties;

import com.infamous.dungeons_gear.capabilities.summoning.SummoningHelper;
import com.infamous.dungeons_gear.config.DungeonsGearConfig;
import com.infamous.dungeons_gear.goals.WildRageAttackGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static com.infamous.dungeons_gear.DungeonsGear.PROXY;

public class AbilityHelper {

    public static void stealSpeedFromTarget(LivingEntity attacker, LivingEntity target, int amplifer) {
        if (attacker == target) return;
        EffectInstance speed = new EffectInstance(Effects.MOVEMENT_SPEED, 80, amplifer);
        EffectInstance slowness = new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 80, amplifer);
        attacker.addEffect(speed);
        target.addEffect(slowness);
    }

    public static void makeNearbyPetsAttackTarget(LivingEntity target, LivingEntity owner) {
        if (isPetOfAttacker(target, owner) || isPetOfAttacker(owner, target))
            return;//don't kill your pets or master!
        List<LivingEntity> nearbyEntities = owner.getCommandSenderWorld().getLoadedEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(12), (nearbyEntity) -> {
            return canPetAttackEntity(owner, nearbyEntity);
        });
        for (LivingEntity nearbyEntity : nearbyEntities) {

            if (nearbyEntity instanceof TameableEntity) {
                TameableEntity tameableEntity = (TameableEntity) nearbyEntity;
                tameableEntity.setTarget(target);
            }
            if (nearbyEntity instanceof LlamaEntity) {
                LlamaEntity llamaEntity = (LlamaEntity) nearbyEntity;
                llamaEntity.setTarget(target);
            }
            if (nearbyEntity instanceof IronGolemEntity) {
                IronGolemEntity ironGolemEntity = (IronGolemEntity) nearbyEntity;
                ironGolemEntity.setTarget(target);
            }
        }
    }

    private static boolean canPetAttackEntity(LivingEntity owner, LivingEntity nearbyEntity) {
        return nearbyEntity != owner && isPetOfAttacker(owner, nearbyEntity) && nearbyEntity.isAlive();
    }

    public static boolean isPetOfAttacker(LivingEntity possibleOwner, LivingEntity possiblePet) {
        if (possiblePet instanceof TameableEntity) {
            TameableEntity pet = (TameableEntity) possiblePet;
            return pet.getOwner() == possibleOwner;
        } else if(possiblePet instanceof AbstractHorseEntity){
            AbstractHorseEntity horse = (AbstractHorseEntity) possiblePet;
            return horse.getOwnerUUID() == possibleOwner.getUUID();
        } else if(SummoningHelper.isEntitySummonable(possiblePet)){
                return SummoningHelper.wasSummonedBy(possiblePet, possibleOwner.getUUID());
        }
        else{
            return false;
        }
    }

    public static boolean isPetOrColleagueRelation(LivingEntity potentialPet1, LivingEntity potentialPet2) {
        LivingEntity owner = null;
        if (potentialPet1 instanceof TameableEntity)
            owner = ((TameableEntity) potentialPet1).getOwner();
        else if (potentialPet1 instanceof AbstractHorseEntity)
            owner = SummoningHelper.getOwnerForHorse((AbstractHorseEntity) potentialPet1);
        else if (SummoningHelper.isEntitySummonable(potentialPet1))
            owner = SummoningHelper.getSummoner(potentialPet1);

        LivingEntity otherOwner = null;
        if (potentialPet2 instanceof TameableEntity)
            otherOwner = ((TameableEntity) potentialPet2).getOwner();
        else if (potentialPet2 instanceof AbstractHorseEntity)
            otherOwner = SummoningHelper.getOwnerForHorse((AbstractHorseEntity) potentialPet2);
        else if (SummoningHelper.isEntitySummonable(potentialPet2))
            otherOwner = SummoningHelper.getSummoner(potentialPet2);

        if (owner == null)
            return potentialPet1 == otherOwner;
        if (otherOwner == null)
            return potentialPet2 == owner;
        return owner == otherOwner;
    }

    private static boolean isNotTheTargetOrAttacker(LivingEntity attacker, LivingEntity target, LivingEntity nearbyEntity) {
        return nearbyEntity != target
                && nearbyEntity != attacker;
    }

    private static boolean isNotAPlayerOrCanApplyToPlayers(LivingEntity nearbyEntity) {
        if (!(nearbyEntity instanceof PlayerEntity)) {
            return true;
        } else {
            return DungeonsGearConfig.ENABLE_AREA_OF_EFFECT_ON_PLAYERS.get();
        }
    }

    public static boolean canHealEntity(LivingEntity healer, LivingEntity nearbyEntity) {
        return nearbyEntity != healer
                && isAlly(healer, nearbyEntity)
                && isAliveAndCanBeSeen(nearbyEntity, healer);
    }

    public static boolean isAlly(LivingEntity healer, LivingEntity nearbyEntity) {
        return isPetOfAttacker(healer, nearbyEntity)
                || healer.isAlliedTo(nearbyEntity);
    }

    public static boolean isFacingEntity(Entity looker, Entity target, Vector3d look, int angle) {
        if (angle <= 0) return false;
        Vector3d posVec = target.position().add(0, target.getEyeHeight(), 0);
        Vector3d relativePosVec = posVec.vectorTo(looker.position().add(0, looker.getEyeHeight(), 0)).normalize();
        //relativePosVec = new Vector3d(relativePosVec.x, 0.0D, relativePosVec.z);

        double dotsq = ((relativePosVec.dot(look) * Math.abs(relativePosVec.dot(look))) / (relativePosVec.lengthSqr() * look.lengthSqr()));
        double cos = MathHelper.cos((float) ((angle / 360d) * Math.PI));
        return dotsq < -(cos * cos);
    }

    private static boolean isEntityBlacklisted(LivingEntity entity) {
        return (ForgeRegistries.ENTITIES.getKey(entity.getType()) != null && DungeonsGearConfig.ENEMY_BLACKLIST.get().contains(ForgeRegistries.ENTITIES.getKey(entity.getType()).toString()));
    }

    private static boolean isAliveAndCanBeSeen(LivingEntity nearbyEntity, LivingEntity attacker) {
        return nearbyEntity.isAlive() && attacker.canSee(nearbyEntity);
    }

    public static boolean canApplyToEnemy(LivingEntity attacker, LivingEntity target, LivingEntity nearbyEntity) {
        return isNotTheTargetOrAttacker(attacker, target, nearbyEntity)
                && isAliveAndCanBeSeen(nearbyEntity, attacker)
                && !isAlly(attacker, nearbyEntity)
                && isNotAPlayerOrCanApplyToPlayers(nearbyEntity)
                && !isEntityBlacklisted(nearbyEntity);
    }

    public static boolean canApplyToEnemy(LivingEntity attacker, LivingEntity nearbyEntity) {
        return nearbyEntity != attacker
                && isAliveAndCanBeSeen(nearbyEntity, attacker)
                && !isAlly(attacker, nearbyEntity)
                && isNotAPlayerOrCanApplyToPlayers(nearbyEntity)
                && !isEntityBlacklisted(nearbyEntity);
    }

    public static void sendIntoWildRage(MobEntity mobEntity) {
        mobEntity.targetSelector.addGoal(0, new WildRageAttackGoal(mobEntity));
        PROXY.spawnParticles(mobEntity, ParticleTypes.ANGRY_VILLAGER);
    }

}
