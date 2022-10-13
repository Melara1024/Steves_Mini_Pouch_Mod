package ga.melara.stevesminipouch.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MobEffectInstanceWithFunction extends MobEffectInstance {
    public MobEffectInstanceWithFunction(MobEffect p_19513_) {
        super(p_19513_);
    }

    public MobEffectInstanceWithFunction(InventoryEffect e)
    {
        this(e, 1);
    }

    public MobEffectInstanceWithFunction(InventoryEffect e, int dec)
    {
        super(MobEffects.BLINDNESS,20, 5);
    }

    @Override
    public boolean tick(LivingEntity livingEntity, Runnable runnable) {
        System.out.println("tick called");
        return super.tick(livingEntity, runnable);
    }

    @Override
    public void applyEffect(LivingEntity livingEntity) {
        System.out.println("apply called");
        super.applyEffect(livingEntity);

    }

}
