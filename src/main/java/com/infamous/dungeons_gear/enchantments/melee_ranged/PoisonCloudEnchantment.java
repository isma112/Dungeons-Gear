package com.infamous.dungeons_gear.enchantments.melee_ranged;

import com.infamous.dungeons_gear.capabilities.combo.ICombo;
import com.infamous.dungeons_gear.damagesources.OffhandAttackDamageSource;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.MeleeEnchantmentList;
import com.infamous.dungeons_gear.enchantments.lists.MeleeRangedEnchantmentList;
import com.infamous.dungeons_gear.enchantments.types.DungeonsEnchantment;
import com.infamous.dungeons_gear.items.interfaces.IMeleeWeapon;
import com.infamous.dungeons_gear.utilties.AOECloudHelper;
import com.infamous.dungeons_gear.utilties.CapabilityHelper;
import com.infamous.dungeons_gear.utilties.ModEnchantmentHelper;
import com.infamous.dungeons_gear.utilties.PlayerAttackHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

import net.minecraft.enchantment.Enchantment.Rarity;

@Mod.EventBusSubscriber(modid = MODID)
public class PoisonCloudEnchantment extends DungeonsEnchantment {

    public static final String INTRINSIC_POISON_CLOUD_TAG = "IntrinsicPoisonCloud";

    public PoisonCloudEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.MELEE_RANGED, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    @SubscribeEvent
    public static void onPoisonousWeaponAttack(LivingAttackEvent event) {
        if (event.getSource().getDirectEntity() != event.getSource().getEntity()) return;
        if (event.getSource() instanceof OffhandAttackDamageSource) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity)) return;
        LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
        if (attacker.getLastHurtMobTimestamp() == attacker.tickCount) return;
        LivingEntity victim = event.getEntityLiving();
        ItemStack mainhand = attacker.getMainHandItem();
        if (hasPoisonCloudBuiltIn(mainhand)) {
            float chance = attacker.getRandom().nextFloat();
            if (chance <= 0.3F) {
                checkForPlayer(attacker);
                AOECloudHelper.spawnPoisonCloud(attacker, victim, 0);
            }
        }
        if (ModEnchantmentHelper.hasEnchantment(mainhand, MeleeRangedEnchantmentList.POISON_CLOUD)) {
            float chance = attacker.getRandom().nextFloat();
            int level = EnchantmentHelper.getItemEnchantmentLevel(MeleeRangedEnchantmentList.POISON_CLOUD, mainhand);
            if (chance <= 0.3F && !PlayerAttackHelper.isProbablyNotMeleeDamage(event.getSource())) {
                checkForPlayer(attacker);
                AOECloudHelper.spawnPoisonCloud(attacker, victim, level - 1);
            }
        }
    }

    private static boolean hasPoisonCloudBuiltIn(ItemStack mainhand) {
        return mainhand.getItem() instanceof IMeleeWeapon && ((IMeleeWeapon) mainhand.getItem()).hasPoisonCloudBuiltIn(mainhand);
    }

    @SubscribeEvent
    public static void onPoisonBowImpact(ProjectileImpactEvent.Arrow event) {
        RayTraceResult rayTraceResult = event.getRayTraceResult();
        //if(!EnchantUtils.arrowHitLivingEntity(rayTraceResult)) return;
        AbstractArrowEntity arrow = event.getArrow();
        if (!ModEnchantmentHelper.shooterIsLiving(arrow)) return;
        LivingEntity shooter = (LivingEntity) arrow.getOwner();

        int poisonLevel = ModEnchantmentHelper.enchantmentTagToLevel(arrow, MeleeRangedEnchantmentList.POISON_CLOUD);
        boolean uniqueWeaponFlag = arrow.getTags().contains(INTRINSIC_POISON_CLOUD_TAG);

        if (poisonLevel > 0) {
            if (rayTraceResult instanceof EntityRayTraceResult) {
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
                if (entityRayTraceResult.getEntity() instanceof LivingEntity) {
                    LivingEntity victim = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
                    float poisonRand = shooter.getRandom().nextFloat();
                    if (poisonRand <= 0.3F) {
                        checkForPlayer(shooter);
                        AOECloudHelper.spawnPoisonCloud(shooter, victim, poisonLevel - 1);
                    }
                }
            }
            if (rayTraceResult instanceof BlockRayTraceResult) {
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
                BlockPos blockPos = blockRayTraceResult.getBlockPos();
                float poisonRand = shooter.getRandom().nextFloat();
                if (poisonRand <= 0.3F) {
                    checkForPlayer(shooter);
                    AOECloudHelper.spawnPoisonCloudAtPos(shooter, true, blockPos, poisonLevel - 1);
                }
            }
        }
        if (uniqueWeaponFlag) {
            if (rayTraceResult instanceof EntityRayTraceResult) {
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
                if (entityRayTraceResult.getEntity() instanceof LivingEntity) {
                    LivingEntity victim = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
                    float poisonRand = shooter.getRandom().nextFloat();
                    if (poisonRand <= 0.3F) {
                        checkForPlayer(shooter);
                        AOECloudHelper.spawnPoisonCloud(shooter, victim, 0);
                    }
                }
            }
            if (rayTraceResult instanceof BlockRayTraceResult) {
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
                BlockPos blockPos = blockRayTraceResult.getBlockPos();
                float poisonRand = shooter.getRandom().nextFloat();
                if (poisonRand <= 0.3F) {
                    checkForPlayer(shooter);
                    AOECloudHelper.spawnPoisonCloudAtPos(shooter, true, blockPos, 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (player == null) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (player.isAlive()) {
            ICombo comboCap = CapabilityHelper.getComboCapability(player);
            if (comboCap == null) return;
            int poisonImmunityTimer = comboCap.getPoisonImmunityTimer();
            if (poisonImmunityTimer <= 0) {
                comboCap.setPoisonImmunityTimer(poisonImmunityTimer - 1);
            }
        }
    }

    @SubscribeEvent
    public static void onPoisonEvent(PotionEvent.PotionApplicableEvent event) {
        if (event.getPotionEffect().getEffect() == Effects.POISON) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
                ICombo comboCap = CapabilityHelper.getComboCapability(playerEntity);
                if (comboCap == null) return;
                int poisonImmunityTimer = comboCap.getPoisonImmunityTimer();
                if (poisonImmunityTimer > 0) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    private static void checkForPlayer(LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;

            ICombo comboCap = CapabilityHelper.getComboCapability(playerEntity);
            if (comboCap == null) return;
            int poisonImmunityTimer = comboCap.getPoisonImmunityTimer();
            if (poisonImmunityTimer <= 0) {
                comboCap.setPoisonImmunityTimer(60);
            }
        }
    }

    public int getMaxLevel() {
        return 3;
    }
}
