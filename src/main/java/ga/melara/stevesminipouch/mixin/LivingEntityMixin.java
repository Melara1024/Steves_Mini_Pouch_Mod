package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.items.*;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "addEatEffect", at = @At("HEAD"))
    public void onAddEatEffect(ItemStack itemStack, World level, LivingEntity entity, CallbackInfo ci) {
        Item item = itemStack.getItem();
        if(item instanceof FunctionFoodItem) {
            ((FunctionFoodItem) item).onEat(entity);
        }
    }

    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    public void onEat(World level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {

        if((LivingEntity) (Object) this instanceof PlayerEntity && itemStack.getItem() instanceof FunctionFoodItem) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            FunctionFoodItem food = (FunctionFoodItem) itemStack.getItem();
            if((itemStack.getItem() instanceof SlotItem || itemStack.getItem() instanceof CraftActivatItem || itemStack.getItem() instanceof ArmorActivateItem)
                    && !((ICustomInventory) player.inventory).isActiveInventory()) {
                if(player.level.isClientSide())
                    player.sendMessage(new TranslationTextComponent("message.useless"), player.getUUID());
                cir.setReturnValue(itemStack);
            }
        }
    }
}
