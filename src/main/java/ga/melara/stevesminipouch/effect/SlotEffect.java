package ga.melara.stevesminipouch.effect;

import ga.melara.stevesminipouch.stats.EffectSlotSyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraftforge.registries.DeferredRegister;


public class SlotEffect extends MobEffect {

    protected SlotEffect() {
        super(MobEffectCategory.BENEFICIAL, 5);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingentity, AttributeMap attributeMap, int effectPower) {
        if(livingentity instanceof Player player) {
            ((ICustomInventory) player.getInventory()).changeEffectSize(0);
            if(player instanceof ServerPlayer serverPlayer)
                Messager.sendToPlayer(new EffectSlotSyncPacket(0), serverPlayer);
        }
        super.removeAttributeModifiers(livingentity, attributeMap, effectPower);
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingentity, AttributeMap attributeMap, int effectPower) {
        if(livingentity instanceof Player player) {
            ((ICustomInventory) player.getInventory()).changeEffectSize(effectPower);
            if(player instanceof ServerPlayer serverPlayer)
                Messager.sendToPlayer(new EffectSlotSyncPacket(effectPower), serverPlayer);
        }
        super.addAttributeModifiers(livingentity, attributeMap, effectPower);
    }

    public static RegistryObject<MobEffect> buildInTo(DeferredRegister<MobEffect> MOB_EFFECT) {
        return MOB_EFFECT.register("slot_effect", SlotEffect::new);
    }
}
