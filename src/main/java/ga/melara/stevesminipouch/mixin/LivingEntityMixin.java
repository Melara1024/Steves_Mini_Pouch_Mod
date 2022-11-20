package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.items.*;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    public void onEat(Level level, ItemStack p_21068_, CallbackInfoReturnable<ItemStack> cir) {

        if ((LivingEntity) (Object) this instanceof Player player && p_21068_.getItem() instanceof FunctionFoodItem food)
        {
            if((p_21068_.getItem() instanceof SlotItem || p_21068_.getItem() instanceof CraftActivatItem || p_21068_.getItem() instanceof ArmorActivateItem)
            && !((IStorageChangable)player.getInventory()).isActiveInventory())
            {
                if(player.getLevel().isClientSide())
                    player.sendSystemMessage(Component.translatable("message.useless"));
                cir.setReturnValue(p_21068_);
            }
        }
    }
}
