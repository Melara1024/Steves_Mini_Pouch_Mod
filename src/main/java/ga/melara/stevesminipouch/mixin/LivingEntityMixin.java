package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.items.*;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
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
public class LivingEntityMixin {

    @Inject(method = "addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"))
    public void onAddEatEffect(ItemStack itemStack, Level level, LivingEntity entity, CallbackInfo ci) {
        Item item = itemStack.getItem();
        if(item instanceof FunctionFoodItem) {
            ((FunctionFoodItem) item).onEat(entity);
        }
    }

    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    public void onEat(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {

        if((LivingEntity) (Object) this instanceof Player player && itemStack.getItem() instanceof FunctionFoodItem food) {
            if((itemStack.getItem() instanceof SlotItem || itemStack.getItem() instanceof CraftActivatItem || itemStack.getItem() instanceof ArmorActivateItem)
                    && !((ICustomInventory) player.getInventory()).isActiveInventory()) {
                if(player.getLevel().isClientSide())
                    player.sendMessage(new TranslatableComponent("message.useless"), player.getUUID());
                cir.setReturnValue(itemStack);
            }
        }
    }
}
