package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "handleKeybinds()V", at = @At(value = "RETURN"), cancellable = true)
    public void onGetSuitableHotbarSlot(CallbackInfo ci) {
        if(this.player.inventory.selected > ((ICustomInventory) this.player.inventory).getInventorySize() - 1)
            // When a key corresponding to a nonexistent hotbar slot is pressed.
            this.player.inventory.selected = ((ICustomInventory) this.player.inventory).getInventorySize() - 1;
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void onSetScreen(Screen p_91153_, CallbackInfo ci) {
        // When the inventory key is pressed while the player does not have inventory capability.
        if(p_91153_ instanceof InventoryScreen && !((ICustomInventory) player.inventory).isActiveInventory()) {
            ItemStack main = player.inventory.items.get(0);
            ItemStack offhand = player.inventory.offhand.get(0);
            String mainItem = main.getItem() == Items.AIR ? "nothing" : main.getItem().getName(main).getString();
            String offhandItem = offhand.getItem() == Items.AIR ? "nothing" : offhand.getItem().getName(offhand).getString();

            if(player.level.isClientSide()) {
                player.sendMessage(new TranslationTextComponent("message.simple_inventory_1"), player.getUUID());
                player.sendMessage(new TranslationTextComponent("message.simple_inventory_2"), player.getUUID());
                player.sendMessage(new StringTextComponent(mainItem), player.getUUID());
                player.sendMessage(new TranslationTextComponent("message.simple_inventory_3"), player.getUUID());
                player.sendMessage(new StringTextComponent(offhandItem), player.getUUID());
            }
            ci.cancel();
        }
    }
}
