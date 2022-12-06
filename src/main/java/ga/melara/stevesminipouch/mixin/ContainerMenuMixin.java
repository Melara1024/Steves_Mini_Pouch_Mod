package ga.melara.stevesminipouch.mixin;

import com.google.common.collect.Lists;
import ga.melara.stevesminipouch.event.InitMenuEvent;
import ga.melara.stevesminipouch.event.PageReduceEvent;
import ga.melara.stevesminipouch.stats.InventoryStatsData;
import ga.melara.stevesminipouch.stats.StatsSynchronizer;
import ga.melara.stevesminipouch.event.ServerPageChangeEvent;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Container.class)
public abstract class ContainerMenuMixin implements IMenuChangable, IMenuSynchronizer, IContainerListener {

    @Shadow
    public final List<Slot> slots = Lists.newArrayList();

    @Final
    @Shadow
    private NonNullList<ItemStack> lastSlots;

    @Shadow
    @Final
    private List<IntReferenceHolder> dataSlots;

    @Final
    @Shadow
    private List<IContainerListener> containerListeners;

    @Shadow public abstract void broadcastChanges();

    InventoryStatsData data = new InventoryStatsData();
    StatsSynchronizer statsSynchronizer;

    @Override
    public void sendSynchronizePacket(StatsSynchronizer synchronizer) {
        // Send information to the client via serverPlayer.
        this.statsSynchronizer = synchronizer;
        synchronizer.sendInitialData(data);
    }

    @Override
    public void setDataToClient(InventoryStatsData data) {
        this.data = data;
    }

    @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
    public void onConstruct(@Nullable ContainerType<?> pMenuType, int pContainerId, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.post(new InitMenuEvent((Container) (Object) this));
    }

    @SubscribeEvent
    public void onPageChange(ServerPageChangeEvent e) {

        for(Slot s : this.slots) {
            ((IHasSlotPage) s).setPage(e.getPage());
        }

        // Synchronize all information except "carried" at page change with the client.
        // If you synchronize "carried" in here, it could disappear when players put on and take off the armor with slot enchantment.
        for(int i = 0; i < this.slots.size(); ++i) {
            if(((IHasSlotType)this.slots.get(i)).getType() != SlotType.INVENTORY) continue;
            ItemStack itemstack = this.slots.get(i).getItem();
            ItemStack itemstack1 = this.lastSlots.get(i);
            if (!ItemStack.matches(itemstack1, itemstack)) {
                boolean clientStackChanged = !itemstack1.equals(itemstack, true);
                ItemStack itemstack2 = itemstack.copy();
                this.lastSlots.set(i, itemstack2);

                if (clientStackChanged)
                    for(IContainerListener icontainerlistener : this.containerListeners) {
                        icontainerlistener.slotChanged((Container)(Object)this, i, itemstack2);
                    }
            }
        }

        for(int j = 0; j < this.dataSlots.size(); ++j) {
            if(((IHasSlotType)this.slots.get(j)).getType() != SlotType.INVENTORY) continue;
            IntReferenceHolder intreferenceholder = this.dataSlots.get(j);
            if (intreferenceholder.checkAndClearUpdateFlag()) {
                for(IContainerListener icontainerlistener1 : this.containerListeners) {
                    icontainerlistener1.setContainerData((Container)(Object)this, j, intreferenceholder.get());
                }
            }
        }
    }


    @Override
    public void updateInventoryHiding(PlayerEntity player) {
        if(!((ICustomInventory) player.inventory).isActiveInventory()) {
            updateArmorHiding(player);
            updateCraftHiding(player);
        }
    }


    @Override
    public void updateArmorHiding(PlayerEntity player) {
        for(Slot slot : this.slots) {
            if(((IHasSlotType) slot).getType() == SlotType.ARMOR) {
                if(!((ICustomInventory) player.inventory).isActiveArmor()) ((ISlotHidable) slot).hide();
                else ((ISlotHidable) slot).show();
            }
        }
    }

    @Override
    public void updateCraftHiding(PlayerEntity player) {
        for(Slot slot : this.slots) {
            if(((IHasSlotType) slot).getType() == SlotType.CRAFT || ((IHasSlotType) slot).getType() == SlotType.RESULT) {
                if(!((ICustomInventory) player.inventory).isActiveCraft()) ((ISlotHidable) slot).hide();
                else ((ISlotHidable) slot).show();
            }
        }
    }

    @Override
    public void updateOffhandHiding(PlayerEntity player) {
        for(Slot slot : this.slots) {
            if(((IHasSlotType) slot).getType() == SlotType.OFFHAND) {
                if(!((ICustomInventory) player.inventory).isActiveOffhand()) ((ISlotHidable) slot).hide();
                else ((ISlotHidable) slot).show();
            }
        }
    }

    @Override
    public void judgePageReduction(int maxpage, PlayerEntity player) {
        // Judge if the page the player is currently viewing is unnecessary.
        if(player.level.isClientSide()) {
            for(Slot s : slots) {
                if(((IHasSlotType) s).getType() == SlotType.INVENTORY && ((IHasSlotPage) s).getPage() > maxpage) {
                    MinecraftForge.EVENT_BUS.post(new PageReduceEvent(maxpage));
                    return;
                }
            }
        }
    }

    @Inject(method = "addSlot", at = @At("RETURN"), cancellable = true)
    public void onAddSlot(Slot slot, CallbackInfoReturnable<Slot> cir) {
        SlotType.setType(slot);
    }
}
