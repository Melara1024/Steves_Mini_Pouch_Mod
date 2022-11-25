package ga.melara.stevesminipouch.enchant;

import ga.melara.stevesminipouch.items.slotitems.Add1SlotItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class SlotEnchant extends Enchantment {
    public SlotEnchant() {
        super(Rarity.RARE, EnchantmentType.ARMOR, new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.LEGS});
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
