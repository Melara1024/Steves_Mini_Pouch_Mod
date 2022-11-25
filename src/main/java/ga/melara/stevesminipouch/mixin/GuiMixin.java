package ga.melara.stevesminipouch.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import ga.melara.stevesminipouch.StevesMiniPouch;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;

@Mixin(IngameGui.class)
public class GuiMixin extends AbstractGui {

    @Shadow
    @Final
    @Mutable
    protected static ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/widgets.png");

    private static final ResourceLocation HOTBARS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/hotbars.png");

    @Shadow
    protected int screenWidth;
    @Shadow
    protected int screenHeight;

    @Inject(method = "renderHotbar", at = @At(value = "HEAD"), cancellable = true)
    public void onRenderHotbar(float partialTick, MatrixStack poseStack, CallbackInfo ci) {
        // Replace and rendering hotbar texture

        int hotbarSize = ((ICustomInventory) Minecraft.getInstance().player.inventory).getHotbarSize();
        PlayerEntity player = Minecraft.getInstance().player;

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bind(HOTBARS_LOCATION);

        int blitOffset = this.getBlitOffset();
        this.setBlitOffset(-90);

        int w2 = this.screenWidth / 2;
        this.blit(poseStack, w2 - 91, this.screenHeight - 22, 0, (9 - hotbarSize) * 22, 182, (9 - hotbarSize) * 22 + 22);
        Minecraft.getInstance().getTextureManager().bind(WIDGETS_LOCATION);
        this.blit(poseStack, w2 - 91 - 1 + player.inventory.selected * 20, this.screenHeight - 22 - 1, 0, 22, 24, 22);

        this.setBlitOffset(blitOffset);
    }
}
