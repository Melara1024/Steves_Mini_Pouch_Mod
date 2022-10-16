package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorItem.class)
public class ArmorItemMixin
{
    @Inject(method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", at = @At(value = "HEAD"), cancellable = true)
    public void onUse(Level p_40395_, Player p_40396_, InteractionHand p_40397_, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir)
    {
        if(!((IStorageChangable)p_40396_.getInventory()).isActiveArmor()) cir.cancel();
    }
}
