package ga.melara.stevesminipouch.mixin.prevent_throw;

import com.mojang.blaze3d.vertex.PoseStack;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.stats.PageChangedPacket;
import ga.melara.stevesminipouch.util.IHasPageButton;
import ga.melara.stevesminipouch.util.IHasSlotPage;
import ga.melara.stevesminipouch.util.IMenuChangable;
import ga.melara.stevesminipouch.util.ISlotHidable;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {

    @Shadow
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY) {}

    //dummy
    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Inject(method = "hasClickedOutside(DDIII)Z", at = @At(value = "HEAD"), cancellable = true)
    protected void onClickedOutside(double mouseX, double mouseY, int leftPos, int rightPos, int ___, CallbackInfoReturnable<Boolean> cir) {
        ((IHasPageButton) this).buttonClicked(mouseX, mouseY, leftPos, rightPos, cir);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    protected void onInit(Player pPlayer, CallbackInfo ci)
    {
        Messager.sendToServer(new PageChangedPacket(0));
        this.menu.slots.forEach(slot -> ((IHasSlotPage) slot).setPage(0));
    }
}
