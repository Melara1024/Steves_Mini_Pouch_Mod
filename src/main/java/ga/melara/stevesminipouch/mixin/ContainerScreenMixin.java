package ga.melara.stevesminipouch.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ga.melara.stevesminipouch.data.Messager;
import ga.melara.stevesminipouch.data.PageChangedPacket;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

@Mixin(AbstractContainerScreen.class)
public class ContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {

    /*
    Todo ページ変更システム，ボタンを押してページ変数のインクリメント，デクリメント
    Todo ページ変数が変更された際にメッセージ・もしくはイベントとしてページ変数そのものを送信する
    Todo できるだけイベントでこの機能を実装する

    Todo スロットつぶしシステム，スロットがactiveを返さないように設定，closedSlotはtrueを返す(SlotMixin)
    Todo スロットが上記のような状態だったときに灰色の絵で隠せるようにrenderメソッドに対してMixinを適用
     */

    private static final ResourceLocation PATCH = new ResourceLocation(MODID,"textures/gui/patch.png");

    private int page = 0;

    @Shadow
    protected int inventoryLabelX;
    @Shadow
    protected int inventoryLabelY;

    @Shadow protected int leftPos;

    @Shadow protected int topPos;

    @Shadow protected int imageWidth;

    @Shadow T menu;


    Button upButton;
    Button downButton;
    Button pageIndicator;

    //dummy
    protected ContainerScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSlotRender(PoseStack poseStack, Slot slot, CallbackInfo ci)
    {
        //Todo そもそもactive = trueを返さないものはレンダリングが動かない
        //Todo renderメソッドそのものに表示非表示のロジックを埋め込む必要がある

        //Todo ここはページによってスロットの色を変えるお楽しみ機能をつけるのくらいにしか使わないはず
    }



    @Inject(method = "renderLabels(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", at = @At(value = "RETURN"), cancellable = true)
    public void onLabelRender(PoseStack poseStack, int unUsed1, int unUsed2, CallbackInfo ci)
    {
    }

    @Inject(method = "init()V", at = @At(value = "RETURN"), cancellable = true)
    public void oninitRender(CallbackInfo ci)
    {
        Messager.sendToServer(new PageChangedPacket(page));
        this.menu.slots.forEach(slot -> ((IHasSlotPage)slot).setPage(page));

        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;

        upButton = new Button(this.leftPos+this.inventoryLabelX+this.imageWidth-5, this.topPos+this.inventoryLabelY+18, 18, 18,
                Component.literal("▲"), (p_96337_) -> {
            previousPage();
            Messager.sendToServer(new PageChangedPacket(page));
            this.menu.slots.forEach(slot -> ((IHasSlotPage)slot).setPage(page));
        });

        downButton = new Button(this.leftPos+this.inventoryLabelX+this.imageWidth-5, this.topPos+this.inventoryLabelY+54, 18, 18,
                Component.literal("▼"), (p_96337_) -> {
            nextPage();
            Messager.sendToServer(new PageChangedPacket(page));
            this.menu.slots.forEach(slot -> ((IHasSlotPage)slot).setPage(page));
        });

        pageIndicator = new Button(this.leftPos+this.inventoryLabelX+this.imageWidth-5, this.topPos+this.inventoryLabelY+36, 18, 18,
                Component.literal(String.valueOf(page+1)), (p_96337_) -> {
        });
        pageIndicator.active = false;

        this.addRenderableWidget(upButton);

        this.addRenderableWidget(pageIndicator);

        this.addRenderableWidget(downButton);

        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    //最大値はClient側menuよりthis.menuとして入手
    private void nextPage()
    {
        if(page < (((IStorageChangable) Minecraft.getInstance().player.getInventory()).getMaxPage()))page++;
        //ここでもスロットの更新(表示，非表示の切り替え)をかける？
    }
    private void previousPage()
    {
        if(page > 0)page--;
        //ここでもスロットの更新(表示，非表示の切り替え)をかける？
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", at = @At(value = "RETURN"), cancellable = true)
    public void onRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    {
        //this.renderables.forEach(button -> button.render(poseStack, mouseX, mouseY, partialTick));
        upButton.renderButton(poseStack, mouseX, mouseY, partialTick);
        downButton.renderButton(poseStack, mouseX, mouseY, partialTick);
        pageIndicator.setMessage(Component.literal(String.valueOf(page+1)));
        pageIndicator.renderButton(poseStack, mouseX, mouseY, partialTick);
        //this.font.draw(poseStack, Component.literal(String.valueOf(page)), (float) this.leftPos+this.inventoryLabelX+this.imageWidth, this.topPos+this.inventoryLabelY+40, 0xFFFFFF);



        for(int k = 0; k < this.menu.slots.size(); ++k) {
            System.out.println("render");
            Slot slot = this.menu.slots.get(k);
            if(!((ISlotHidable)slot).isShowing())
            {
                RenderSystem.setShaderTexture(0, PATCH);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
                this.blit(poseStack, slot.x + leftPos -1, slot.y + topPos -1, this.getBlitOffset(), 18, 18, 21);
            }
        }
    }
}
