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
import net.minecraftforge.common.extensions.IForgeRecipeSerializer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin extends GuiComponent {

//    @Shadow
//    @Final
//    @Mutable
//    protected static ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/widgets.png");
//
//    private static final ResourceLocation HOTBARS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/hotbars.png");

    private static final ResourceLocation PATCH = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/hotbar_patch.png");

    @Shadow
    protected int screenWidth;
    @Shadow
    protected int screenHeight;

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"), cancellable = true)
    public void onReturnRenderHotbar(float pPartialTick, PoseStack poseStack, CallbackInfo ci)
    {
        int hotbarSize = ((ICustomInventory) Minecraft.getInstance().player.getInventory()).getHotbarSize();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        int w2 = this.screenWidth / 2;

        for (int i=0; i<(9-hotbarSize); i++) {
            RenderSystem.setShaderTexture(0, PATCH);
            // blit(poseStack, drawX, drawY, ImageX, ImageY, SizeX, SizeY, ScaleX, ScaleY)
            blit(poseStack, w2 - 109 + (9-i)*20, this.screenHeight - 19, 0, 0, 18, 18, 18, 18);
        }
    }
}
