package com.infamous.dungeons_gear.enchantments.melee;

import com.infamous.dungeons_gear.damagesources.OffhandAttackDamageSource;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.MeleeEnchantmentList;
import com.infamous.dungeons_gear.enchantments.types.DungeonsEnchantment;
import com.infamous.dungeons_gear.items.interfaces.IMeleeWeapon;
import com.infamous.dungeons_gear.utilties.AreaOfEffectHelper;
import com.infamous.dungeons_gear.utilties.ModEnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

import net.minecraft.enchantment.Enchantment.Rarity;

@Mod.EventBusSubscriber(modid = MODID)
public class WeakeningEnchantment extends DungeonsEnchantment {

    public WeakeningEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.MELEE, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        if(!(target instanceof LivingEntity)) return;
        ItemStack mainhand = user.getMainHandItem();
        boolean uniqueWeaponFlag = hasWeakeningBuiltIn(mainhand);
        if(uniqueWeaponFlag) level++;
        AreaOfEffectHelper.weakenNearbyEntities(user, (LivingEntity)target, 5, level-1);
    }

    private static boolean hasWeakeningBuiltIn(ItemStack mainhand) {
        return mainhand.getItem() instanceof IMeleeWeapon && ((IMeleeWeapon) mainhand.getItem()).hasWeakeningBuiltIn(mainhand);
    }

    @SubscribeEvent
    public static void onNamelessBladeAttack(LivingAttackEvent event){
        if(event.getSource().getDirectEntity() instanceof AbstractArrowEntity) return;
        if(event.getSource() instanceof OffhandAttackDamageSource) return;
        if(!(event.getSource().getEntity() instanceof LivingEntity)) return;
        LivingEntity attacker = (LivingEntity)event.getSource().getEntity();
        LivingEntity victim = event.getEntityLiving();
        ItemStack mainhand = attacker.getMainHandItem();
        if(hasWeakeningBuiltIn(mainhand)
                && !ModEnchantmentHelper.hasEnchantment(mainhand, MeleeEnchantmentList.WEAKENING)){
            AreaOfEffectHelper.weakenNearbyEntities(attacker, victim, 5, 0);
        }
    }
}
