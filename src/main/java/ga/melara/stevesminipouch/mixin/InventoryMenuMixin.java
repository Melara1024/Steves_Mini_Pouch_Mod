package ga.melara.stevesminipouch.mixin;

import com.mojang.datafixers.util.Pair;
import ga.melara.stevesminipouch.IContainerAccessWidener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin {


    @Shadow @Final public boolean active;

    @Inject(method = "<init>", at = @At(value = "TAIL"), cancellable = true)
    public void constructorTail(Inventory p_39706_, boolean p_39707_, final Player p_39708_, CallbackInfo ci) {
        ((IContainerAccessWidener)(Object)this).removeSlot();
        System.out.println("(from InvMenuMixin!)");
        //ここで一度スロットのリストをリセットする
        //再度スロットを追加，今度はコンフィグに従った個数だけ作る
    }

}