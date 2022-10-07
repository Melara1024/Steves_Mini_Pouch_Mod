package ga.melara.stevesminipouch.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ga.melara.stevesminipouch.data.Messager;
import ga.melara.stevesminipouch.data.PageChangedPacket;
import ga.melara.stevesminipouch.util.IHasSlotType;
import ga.melara.stevesminipouch.util.SlotType;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
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

    //dummy
    protected ContainerScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSlotRender(PoseStack poseStack, Slot slot, CallbackInfo ci)
    {
        //ページから該当するもののみを表示する
        //System.out.println(((IHasSlotType)(Object)slot).getType());
        //System.out.println(slot.container.toString());
        //System.out.println(slot.getSlotIndex());
        if(((IHasSlotType)(Object)slot).getType() == SlotType.INVENTORY)
        {
            //System.out.println(((IHasSlotType)(Object)slot).getPage());
            //System.out.println(this.page);
            //System.out.println(((IHasSlotType)(Object)slot).getVisiblity());
            if(((IHasSlotType)(Object)slot).getPage() == this.page)
            {
                ((IHasSlotType)(Object)slot).show();
            }
            else
            {
                ((IHasSlotType)(Object)slot).hide();
            }
        }



//        if(((IHasSlotType)slot).getType() != SlotType.INVENTORY){
//            return;
//        }
//
//        int availableSlot = 20;
//        int x = slot.x;
//        int y = slot.y;
//
//        this.setBlitOffset(100);
//        this.itemRenderer.blitOffset = 100.0F;
//
//        RenderSystem.setShaderTexture(0, PATCH);
//        RenderSystem.enableTexture();
//        RenderSystem.enableDepthTest();
//        this.blit(poseStack, x-1, y-1, this.getBlitOffset(), 18, 18, 21);
//
//
//        this.itemRenderer.blitOffset = 0.0F;
//        this.setBlitOffset(0);
    }



    @Inject(method = "renderLabels(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", at = @At(value = "RETURN"), cancellable = true)
    public void onLabelRender(PoseStack poseStack, int unUsed1, int unUsed2, CallbackInfo ci)
    {
        System.out.println(this.menu.getType().toString());
        Messager.sendToServer(new PageChangedPacket(8));
        //MinecraftForge.EVENT_BUS.post(new PageChangeEvent(8, Minecraft.getInstance().level));


        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;

        RenderSystem.setShaderTexture(0, PATCH);
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.inventoryLabelX-1, this.inventoryLabelY-1, this.getBlitOffset(), 18, 18, 21);


        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    @Inject(method = "init()V", at = @At(value = "RETURN"), cancellable = true)
    public void oninitRender(CallbackInfo ci)
    {
        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;

        this.addRenderableWidget(new Button(this.leftPos+this.inventoryLabelX+this.imageWidth-5, this.topPos+this.inventoryLabelY+18, 18, 18,
                Component.literal("▲"), (p_96337_) -> {
            previousPage();
            System.out.println("now page is" + page);
        }));

        this.addRenderableWidget(new Button(this.leftPos+this.inventoryLabelX+this.imageWidth-5, this.topPos+this.inventoryLabelY+24+18, 18, 18,
                Component.literal("▼"), (p_96337_) -> {
            nextPage();
            System.out.println("now page is" + page);
        }));

        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    private void nextPage()
    {
        if(page < 1)page++;
    }
    private void previousPage()
    {
        if(page > 0)page--;
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", at = @At(value = "RETURN"), cancellable = true)
    public void onRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    {
        this.renderables.forEach(button -> button.render(poseStack, mouseX, mouseY, partialTick));
    }
}
