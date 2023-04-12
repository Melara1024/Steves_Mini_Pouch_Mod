package ga.melara.stevesminipouch.effect;

import ga.melara.stevesminipouch.stats.EffectSlotSyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SlotEffect extends MobEffect {

    protected SlotEffect() {
        super(MobEffectCategory.BENEFICIAL, 5);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingentity, AttributeMap attributeMap, int effectPower) {
        if(livingentity instanceof ServerPlayer player) {
            ((ICustomInventory) player.getInventory()).changeEffectSize(0);
            Messager.sendToPlayer(new EffectSlotSyncPacket(0), player);
        }
        super.removeAttributeModifiers(livingentity, attributeMap, effectPower);
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingentity, AttributeMap attributeMap, int effectPower) {
        if(livingentity instanceof ServerPlayer player) {
            ((ICustomInventory) player.getInventory()).changeEffectSize(effectPower);
            Messager.sendToPlayer(new EffectSlotSyncPacket(effectPower), player);
        }
        super.addAttributeModifiers(livingentity, attributeMap, effectPower);
    }

    public static RegistryObject<MobEffect> buildInTo(DeferredRegister<MobEffect> MOB_EFFECT) {
        return MOB_EFFECT.register("slot_effect", SlotEffect::new);
    }
}
