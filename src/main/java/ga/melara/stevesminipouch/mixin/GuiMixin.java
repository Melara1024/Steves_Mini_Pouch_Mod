package ga.melara.stevesminipouch.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ga.melara.stevesminipouch.StevesMiniPouch;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

@Mixin(Gui.class)
public class GuiMixin extends GuiComponent {
    //Todo インベントリ変更時，もしホットバー数が減ったら絵を置き換える
    //Todo ホットバー9スロット以上ならバニラテクスチャを参照
    //Todo ホットバー8スロット以下担った時点でmod側のwidgetsに置き換え，hotbarsとレンダリングを別にする

    @Shadow
    @Final
    @Mutable
    protected static ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/widgets.png");


    private static ResourceLocation HOTBARS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/hotbars.png");
    public boolean isVanillaHotbar;


    @Shadow
    protected int screenWidth;
    @Shadow
    protected int screenHeight;

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"), cancellable = true)
    public void onRenderHotbar(float p_93010_, PoseStack p_93011_, CallbackInfo ci) {
        int hotbarSize = ((IStorageChangable) Minecraft.getInstance().player.getInventory()).getHotbarSize();
        Player player = Minecraft.getInstance().player;
        //もしホットバーが9スロット未満だったら
        //スロットのサイズに合わせてsmp/tex/gui/hotbarsを表示する


        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, HOTBARS_LOCATION);

        int j = this.getBlitOffset();
        this.setBlitOffset(-90);

        int i = this.screenWidth / 2;

        this.blit(p_93011_, i - 91, this.screenHeight - 22, 0, (9 - hotbarSize) * 22, 182, (9 - hotbarSize) * 22 + 22);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        this.blit(p_93011_, i - 91 - 1 + player.getInventory().selected * 20, this.screenHeight - 22 - 1, 0, 22, 24, 22);

        this.setBlitOffset(j);

    }


    //インベントリは両サイドのクラスなのでこれを呼ぶのは危険，Gui側からMinecraft.getInstance().player.getInventory()としてホットバーのサイズを入手すべき


}
