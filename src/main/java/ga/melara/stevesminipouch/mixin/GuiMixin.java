package ga.melara.stevesminipouch.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ga.melara.stevesminipouch.StevesMiniPouch;
import ga.melara.stevesminipouch.util.IGuiChangable;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static net.minecraft.client.gui.components.AbstractWidget.WIDGETS_LOCATION;

@Mixin(Gui.class)
public class GuiMixin extends GuiComponent implements IGuiChangable
{
    //Todo インベントリ変更時，もしホットバー数が減ったら絵を置き換える
    //Todo ホットバー9スロット以上ならバニラテクスチャを参照
    //Todo ホットバー8スロット以下担った時点でmod側のwidgetsに置き換え，hotbarsとレンダリングを別にする

    @Shadow
    @Final
    @Mutable
    protected static ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID,"textures/gui/widgets.png");


    private static ResourceLocation HOTBARS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/hotbars.png");
    public boolean isVanillaHotbar;


    @Shadow
    protected int screenWidth;
    @Shadow
    protected int screenHeight;

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"), cancellable = true)
    public void onRenderHotbar(float p_93010_, PoseStack p_93011_, CallbackInfo ci)
    {
        int hotbarSize = ((IStorageChangable)Minecraft.getInstance().player.getInventory()).getHotbarSize();
        //もしホットバーが9スロット未満だったら
        //スロットのサイズに合わせてsmp/tex/gui/hotbarsを表示する
        if(!isVanillaHotbar && (hotbarSize == 9))
        {
            isVanillaHotbar = true;
            enableVanillaWidget();
        }
        else if(isVanillaHotbar)
        {
            isVanillaHotbar = false;
            disableVanillaWidget();
        }

        if(!isVanillaHotbar)
        {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, HOTBARS_LOCATION);

            int j = this.getBlitOffset();
            this.setBlitOffset(-90);

            int i = this.screenWidth / 2;

            this.blit(p_93011_, i - 91, this.screenHeight - 22, 0, (8 - hotbarSize)*22, 182, (8 - hotbarSize)*22 + 22);
            //this.blit(p_93011_, i - 91 - 1 + player.getInventory().selected * 20, this.screenHeight - 22 - 1, 0, 22, 24, 22);

            this.setBlitOffset(j);
        }
    }


    //インベントリは両サイドのクラスなのでこれを呼ぶのは危険，Gui側からMinecraft.getInstance().player.getInventory()としてホットバーのサイズを入手すべき

    @Override
    public void enableVanillaWidget()
    {
        WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    }

    @Override
    public void disableVanillaWidget()
    {
        WIDGETS_LOCATION = new ResourceLocation(StevesMiniPouch.MODID, "textures/gui/widgets.png");
    }
}
