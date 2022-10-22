package ga.melara.stevesminipouch.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MobEffectInstanceWithFunction extends MobEffectInstance {

    private InventoryEffect inventoryEffect;

    public MobEffectInstanceWithFunction(MobEffect p_19513_) {
        super(p_19513_);
    }

    public MobEffectInstanceWithFunction(InventoryEffect e)
    {
        super(MobEffects.BLINDNESS,20, 5);
        this.inventoryEffect = e;
    }

    public MobEffectInstanceWithFunction(InventoryEffect e, int value)
    {
        super(MobEffects.BLINDNESS,20, 5);
        this.inventoryEffect = e;
    }


    public void applyInventoryEffect(LivingEntity entity)
    {
        if(entity instanceof Player)this.inventoryEffect.apply((Player)entity);
        System.out.println("getEffect called -> " + this.inventoryEffect);
    }

    public InventoryEffect getInventoryEffect()
    {
        return this.inventoryEffect;
    }
}
