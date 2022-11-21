package ga.melara.stevesminipouch.enchant;

import ga.melara.stevesminipouch.items.slotitems.Add1SlotItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SlotEnchant extends Enchantment {
    public SlotEnchant() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.LEGS});
    }

    public static RegistryObject<Enchantment> buildInTo(DeferredRegister<Enchantment> ENCHANTMENT) {
        return ENCHANTMENT.register("slot_enchant", SlotEnchant::new);
    }

    public int getMinCost(int level) {
        return (level - 1) * 3;
    }

    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    public int getMaxLevel() {
        return 27;
    }
}
