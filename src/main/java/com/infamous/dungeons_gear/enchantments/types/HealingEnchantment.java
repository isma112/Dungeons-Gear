package com.infamous.dungeons_gear.enchantments.types;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

import net.minecraft.enchantment.Enchantment.Rarity;

public class HealingEnchantment extends DungeonsEnchantment{

    protected HealingEnchantment(Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType[] equipmentSlotTypes) {
        super(rarity, enchantmentType, equipmentSlotTypes);
    }
}
