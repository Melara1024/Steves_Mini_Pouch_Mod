package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.items.FunctionFoodItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{

    @Inject(method = "addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"))
    public void onAddEatEffect(ItemStack p_21064_, Level p_21065_, LivingEntity p_21066_, CallbackInfo ci)
    {
        Item item = p_21064_.getItem();
        if (item instanceof FunctionFoodItem) {
            ((FunctionFoodItem)item).onEat(p_21066_);
        }
    }
}
