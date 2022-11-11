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

    //エンチャントそのものは名前だけの塊
    //プレイヤーの保持するアイテムにエンチャントがついているか調査する事が可能
    //Todo EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, p_36746_);でエンチャ保持確認
    //Todo エンチャントがついた鎧を着ているか一定時間ごとに確認して更新する部分が必要

    public SlotEnchant()
    {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.LEGS});
    }


    public static RegistryObject<Enchantment> buildInTo(DeferredRegister<Enchantment> ENCHANTMENT)
    {
        return ENCHANTMENT.register("slot_enchant", SlotEnchant::new);
    }

    public int getMinCost(int p_45083_) {
        return 5 + 20 * (p_45083_ - 1);
    }

    public int getMaxCost(int p_45085_) {
        return super.getMinCost(p_45085_) + 50;
    }

    public int getMaxLevel() {
        return 27;
    }

}
