package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    public LocalPlayer player;

    @Inject(method = "handleKeybinds()V", at = @At(value = "RETURN"), cancellable = true)
    public void onGetSuitableHotbarSlot(CallbackInfo ci) {
        if(this.player.getInventory().selected > ((ICustomInventory) this.player.getInventory()).getInventorySize() - 1)
            // When a key corresponding to a nonexistent hotbar slot is pressed.
            this.player.getInventory().selected = ((ICustomInventory) this.player.getInventory()).getInventorySize() - 1;
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void onSetScreen(Screen screen, CallbackInfo ci) {
        // When the inventory key is pressed while the player does not have inventory capability.
        if(screen instanceof InventoryScreen && !((ICustomInventory) player.getInventory()).isActiveInventory()) {
            ItemStack main = player.getInventory().items.get(0);
            ItemStack offhand = player.getInventory().offhand.get(0);
            String mainItem = main.getItem() == Items.AIR ? "nothing" : main.getItem().getName(main).getString();
            String offhandItem = offhand.getItem() == Items.AIR ? "nothing" : offhand.getItem().getName(offhand).getString();

            if(player.level.isClientSide()) {
                player.sendMessage(new TranslatableComponent("message.simple_inventory_1"), player.getUUID());
                player.sendMessage(new TranslatableComponent("message.simple_inventory_2"), player.getUUID());
                player.sendMessage(new TextComponent(mainItem), player.getUUID());
                player.sendMessage(new TranslatableComponent("message.simple_inventory_3"), player.getUUID());
                player.sendMessage(new TextComponent(offhandItem), player.getUUID());
            }
            ci.cancel();
        }
    }
}
