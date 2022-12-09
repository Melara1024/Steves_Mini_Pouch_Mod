package ga.melara.stevesminipouch.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.event.InitMenuEvent;
import ga.melara.stevesminipouch.event.PageReduceEvent;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.stats.PageChangedPacket;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements IHasPageButton {

    private static final ResourceLocation PATCH = new ResourceLocation(MODID, "textures/gui/patch.png");

    private int page = 0;

    @Shadow
    protected int inventoryLabelX;
    @Shadow
    protected int inventoryLabelY;

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    protected int imageWidth;

    @Shadow
    T menu;

    Button upButton;
    Button downButton;
    Button pageIndicator;

    protected ContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"), cancellable = true)
    public void onInit(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(this);

        //たぶんメニューを開いた瞬間にアイテム情報をクライアントに送れていない？
        page = 0;
        Messager.sendToServer(new PageChangedPacket(0));
        this.menu.slots.forEach(slot -> ((IHasSlotPage) slot).setPage(0));
    }

    @Inject(method = "onClose", at = @At(value = "HEAD"), cancellable = true)
    public void onClose(CallbackInfo ci) {
        page = 0;
        Messager.sendToServer(new PageChangedPacket(0));
        this.menu.slots.forEach(slot -> ((IHasSlotPage) slot).setPage(0));
    }

    @Inject(method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSlotRender(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        //Todo  Change background color on page change.
    }

    @Inject(method = "init()V", at = @At(value = "RETURN"), cancellable = true)
    public void oninitRender(CallbackInfo ci) {

        MinecraftForge.EVENT_BUS.post(new InitMenuEvent(menu));

        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;

        int buttonX = this.leftPos + this.inventoryLabelX + this.imageWidth - 9 + Config.RENDER_OFFSET_X.get();
        int buttonY = this.topPos + this.inventoryLabelY + 18 + Config.RENDER_OFFSET_Y.get();

        // Page change button settings
        upButton = new Button.Builder(Component.literal("▲"), (button) -> {
            previousPage();
            Messager.sendToServer(new PageChangedPacket(page));
            this.menu.slots.forEach(slot -> ((IHasSlotPage) slot).setPage(page));
        }).pos(buttonX, buttonY).size(18, 18).build();

        // Button only for page display
        pageIndicator = new Button.Builder(Component.literal(String.valueOf(page + 1)), (button) -> {
        }).pos(buttonX, buttonY + upButton.getHeight()).size(18, 18).build();
        pageIndicator.active = false;

        downButton = new Button.Builder(Component.literal("▼"), (button) -> {
            nextPage();
            Messager.sendToServer(new PageChangedPacket(page));
            this.menu.slots.forEach(slot -> ((IHasSlotPage) slot).setPage(page));
        }).pos(buttonX, buttonY + upButton.getHeight() + pageIndicator.getHeight()).size(18, 18).build();

        upButton.visible = false;
        downButton.visible = false;
        pageIndicator.visible = false;

        this.addRenderableWidget(upButton);
        this.addRenderableWidget(pageIndicator);
        this.addRenderableWidget(downButton);

        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    public void nextPage() {
        if(page < (((ICustomInventory) Minecraft.getInstance().player.getInventory()).getMaxPage())) page++;
    }

    public void previousPage() {
        if(page > 0) page--;
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", at = @At(value = "RETURN"), cancellable = true)
    public void onRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if((((ICustomInventory) Minecraft.getInstance().player.getInventory()).getMaxPage()) > 0) {
            // Rendering of page change button
            if(!pageIndicator.visible) {
                upButton.visible = true;
                downButton.visible = true;
                pageIndicator.visible = true;
            }

            // Shift the button position when the recipe book is opened.
            int buttonX = this.leftPos + this.inventoryLabelX + this.imageWidth - 9 + Config.RENDER_OFFSET_X.get();
            if(pageIndicator.getX() != buttonX) {
                upButton.setX(buttonX);
                downButton.setX(buttonX);
                pageIndicator.setX(buttonX);
            }

            upButton.renderButton(poseStack, mouseX, mouseY, partialTick);
            downButton.renderButton(poseStack, mouseX, mouseY, partialTick);
            pageIndicator.setMessage(Component.literal(String.valueOf(page + 1)));
            pageIndicator.renderButton(poseStack, mouseX, mouseY, partialTick);

        } else {
            // Page change button is not displayed when the slot is smaller than 36.
            if(pageIndicator.visible) {
                upButton.visible = false;
                downButton.visible = false;
                pageIndicator.visible = false;
            }
        }

        int j = this.getBlitOffset();
        this.setBlitOffset(-90);

        for(Slot slot : this.menu.slots) {
            // Update status of slots.

            ((ISlotHidable) slot).setHiding();

            if(((IHasSlotPage) slot).getPage() != page) ((IHasSlotPage) slot).setPage(page);

            if(((IHasSlotType) slot).getType() == SlotType.UNDEFINED) SlotType.setType(slot);

            // Render "x" on an unusable slot.
            if(!((ISlotHidable) slot).isShowing()) {
                patchSlot(poseStack, slot);
            }
        }
        this.setBlitOffset(j);
    }


    @Inject(method = "hasClickedOutside(DDIII)Z", at = @At(value = "HEAD"), cancellable = true)
    protected void onClickedOutside(double mouseX, double mouseY, int leftPos, int rightPos, int ___, CallbackInfoReturnable<Boolean> cir) {
        buttonClicked(mouseX, mouseY, leftPos, rightPos, cir);
    }

    @Override
    public void buttonClicked(double mouseX, double mouseY, int leftPos, int RightPos, CallbackInfoReturnable<Boolean> cir) {
        int x = upButton.getX();
        int y = upButton.getY();

        int w = upButton.getWidth();
        int h = upButton.getHeight() * 3;

        if(mouseX < x + w && mouseX > x && mouseY < y + h && mouseY > y) {
            cir.setReturnValue(false);
        }
    }

    @SubscribeEvent
    public void onPageReduce(PageReduceEvent e) {
        page = 0;
        Messager.sendToServer(new PageChangedPacket(0));
        this.menu.slots.forEach(slot -> {
            if(((IHasSlotType) slot).getType() == SlotType.INVENTORY) ((IHasSlotPage) slot).setPage(0);
        });
    }

    private void patchSlot(PoseStack poseStack, Slot slot) {
        RenderSystem.setShaderTexture(0, PATCH);
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        blit(poseStack, slot.x + leftPos - 1, slot.y + topPos - 1, 0, 0, 18, 18, 18, 18);
    }
}
