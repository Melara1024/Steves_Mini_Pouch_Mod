package ga.melara.stevesminipouch.effect;

import ga.melara.stevesminipouch.items.slotitems.Add1SlotItem;
import ga.melara.stevesminipouch.stats.EffectSlotSyncPacket;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.AbsoptionMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SlotEffect extends MobEffect {

    protected SlotEffect() {
        super(MobEffectCategory.BENEFICIAL, 5);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingentity, AttributeMap p_19418_, int p_19419_) {
        //Todo スロットを減らす
        //Todo エフェクトスロット数を何処かに保存しておく？
        //Todo PlayerかInventoryに保存しておくべき値

        if(livingentity instanceof Player player)
        {
            System.out.printf("SlotEffect Removed! level -> %d%n", p_19419_);
            //Todo スロットエフェクト除去処理
            ((IStorageChangable)player.getInventory()).changeEffectSize(p_19419_);
            if (player instanceof  ServerPlayer serverPlayer)
                Messager.sendToPlayer(new EffectSlotSyncPacket(p_19419_), serverPlayer);
        }
        livingentity.setAbsorptionAmount(livingentity.getAbsorptionAmount() - (float)(4 * (p_19419_ + 1)));
        super.removeAttributeModifiers(livingentity, p_19418_, p_19419_);
    }

    @Override
    public void addAttributeModifiers(LivingEntity  livingentity, AttributeMap p_19422_, int p_19423_) {
        //Todo スロットを増やす

        if(livingentity instanceof Player player)
        {
            System.out.printf("SlotEffect Added! level -> %d%n", p_19423_);
            //Todo スロットエフェクト適用処理
            ((IStorageChangable)player.getInventory()).changeEffectSize(p_19423_);
            if (player instanceof  ServerPlayer serverPlayer)
                Messager.sendToPlayer(new EffectSlotSyncPacket(p_19423_), serverPlayer);
        }

        livingentity.setAbsorptionAmount(livingentity.getAbsorptionAmount() + (float)(4 * (p_19423_ + 1)));
        super.addAttributeModifiers(livingentity, p_19422_, p_19423_);
    }

    public static RegistryObject<MobEffect> buildInTo(DeferredRegister<MobEffect> MOB_EFFECT)
    {
        return MOB_EFFECT.register("slot_effect", SlotEffect::new);
    }
}
