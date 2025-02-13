package com.infamous.dungeons_gear.enchantments.melee;

import com.infamous.dungeons_gear.damagesources.OffhandAttackDamageSource;
import com.infamous.dungeons_gear.effects.CustomEffects;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.types.DungeonsEnchantment;
import com.infamous.dungeons_gear.items.interfaces.IMeleeWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

import net.minecraft.enchantment.Enchantment.Rarity;

@Mod.EventBusSubscriber(modid = MODID)
public class StunningEnchantment extends DungeonsEnchantment {

    public StunningEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.MELEE, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        if(!(target instanceof LivingEntity)) return;
        float chance = user.getRandom().nextFloat();
        if(chance <=  level * 0.05){
            EffectInstance stunned = new EffectInstance(CustomEffects.STUNNED, 60);
            EffectInstance nausea = new EffectInstance(Effects.CONFUSION, 60);
            EffectInstance slowness = new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 60, 5);
            ((LivingEntity)target).addEffect(stunned);
            ((LivingEntity)target).addEffect(nausea);
            ((LivingEntity)target).addEffect(slowness);
        }
    }

    @SubscribeEvent
    public static void onHighlandAxeAttack(LivingAttackEvent event){
        if(event.getSource().getDirectEntity() instanceof AbstractArrowEntity) return;
        if(event.getSource() instanceof OffhandAttackDamageSource) return;
        if(!(event.getSource().getEntity() instanceof LivingEntity)) return;
        LivingEntity attacker = (LivingEntity)event.getSource().getEntity();
        LivingEntity victim = event.getEntityLiving();
        ItemStack mainhand = attacker.getMainHandItem();
        if(hasStunningBuiltIn(mainhand)){
            float chance = attacker.getRandom().nextFloat();
            if(chance <= 0.05) {
                EffectInstance stunned = new EffectInstance(CustomEffects.STUNNED, 60);
                EffectInstance nausea = new EffectInstance(Effects.CONFUSION, 60);
                EffectInstance slowness = new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 60, 5);
                victim.addEffect(stunned);
                victim.addEffect(nausea);
                victim.addEffect(slowness);
            }
        }
    }

    private static boolean hasStunningBuiltIn(ItemStack mainhand) {
        return mainhand.getItem() instanceof IMeleeWeapon && ((IMeleeWeapon) mainhand.getItem()).hasStunningBuiltIn(mainhand);
    }
}
