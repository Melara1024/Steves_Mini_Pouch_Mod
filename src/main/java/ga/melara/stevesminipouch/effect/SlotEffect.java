package ga.melara.stevesminipouch.effect;

import ga.melara.stevesminipouch.stats.EffectSlotSyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;


public class SlotEffect extends Effect {

    public SlotEffect() {
        super(EffectType.BENEFICIAL, 5);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingentity, AttributeModifierManager attributeMap, int effectPower) {
        if(livingentity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingentity;
            ((ICustomInventory) player.inventory).changeEffectSize(0);
            if(player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                Messager.sendToPlayer(new EffectSlotSyncPacket(0), serverPlayer);
            }
        }
        super.removeAttributeModifiers(livingentity, attributeMap, effectPower);
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingentity, AttributeModifierManager attributeMap, int effectPower) {
        if(livingentity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingentity;
            ((ICustomInventory) player.inventory).changeEffectSize(effectPower);
            if(player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                Messager.sendToPlayer(new EffectSlotSyncPacket(effectPower), serverPlayer);
            }
        }
        super.addAttributeModifiers(livingentity, attributeMap, effectPower);
    }

    public static RegistryObject<Effect> buildInTo(DeferredRegister<Effect> EFFECT) {
        return EFFECT.register("slot_effect", SlotEffect::new);
    }
}
