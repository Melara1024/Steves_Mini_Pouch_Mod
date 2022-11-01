package ga.melara.stevesminipouch.effect;

import ga.melara.stevesminipouch.items.slotitems.Add1SlotItem;
import net.minecraft.world.effect.AbsoptionMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SlotEffect extends MobEffect {

    protected SlotEffect() {
        super(MobEffectCategory.BENEFICIAL, 5);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity p_19417_, AttributeMap p_19418_, int p_19419_) {
        //Todo スロットを減らす
        //Todo エフェクトスロット数を何処かに保存しておく？
        //Todo PlayerかInventoryに保存しておくべき値
        p_19417_.setAbsorptionAmount(p_19417_.getAbsorptionAmount() - (float)(4 * (p_19419_ + 1)));
        super.removeAttributeModifiers(p_19417_, p_19418_, p_19419_);
    }

    @Override
    public void addAttributeModifiers(LivingEntity p_19421_, AttributeMap p_19422_, int p_19423_) {
        //Todo スロットを増やす
        p_19421_.setAbsorptionAmount(p_19421_.getAbsorptionAmount() + (float)(4 * (p_19423_ + 1)));
        super.addAttributeModifiers(p_19421_, p_19422_, p_19423_);
    }

    public static RegistryObject<MobEffect> buildInTo(DeferredRegister<MobEffect> MOB_EFFECT)
    {
        return MOB_EFFECT.register("slot_effect", SlotEffect::new);
    }
}
