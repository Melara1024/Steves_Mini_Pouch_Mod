package ga.melara.stevesminipouch.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ga.melara.stevesminipouch.StevesMiniPouch;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin extends GuiComponent {

    @Shadow
    @Final
    @Mutable
    protected static ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/widgets.png");

    private static final ResourceLocation HOTBARS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/hotbars.png");

    @Shadow
    protected int screenWidth;
    @Shadow
    protected int screenHeight;

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"), cancellable = true)
    public void onRenderHotbar(float pPartialTick, PoseStack poseStack, CallbackInfo ci) {
        // Replace and rendering hotbar texture

        int hotbarSize = ((ICustomInventory) Minecraft.getInstance().player.getInventory()).getHotbarSize();
        Player player = Minecraft.getInstance().player;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, HOTBARS_LOCATION);

        //int blitOffset = this.getBlitOffset();
        //this.setBlitOffset(-90);

        int w2 = this.screenWidth / 2;
        this.blit(poseStack, w2 - 91, this.screenHeight - 22, 0, (9 - hotbarSize) * 22, 182, (9 - hotbarSize) * 22 + 22);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        this.blit(poseStack, w2 - 91 - 1 + player.getInventory().selected * 20, this.screenHeight - 22 - 1, 0, 22, 24, 22);

        //this.setBlitOffset(blitOffset);
    }
}
