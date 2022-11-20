package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.event.PageReduceEvent;
import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import ga.melara.stevesminipouch.stats.StatsSynchronizer;
import ga.melara.stevesminipouch.event.ServerPageChangeEvent;
import ga.melara.stevesminipouch.util.*;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
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

@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMenuMixin implements IMenuChangable, IMenuSynchronizer, ContainerSynchronizer {

    @Shadow
    public NonNullList<Slot> slots;


    StatsSynchronizer statsSynchronizer;

    @Shadow
    @Final
    private List<DataSlot> dataSlots;
    @Shadow
    public NonNullList<ItemStack> remoteSlots;
    @Shadow
    @Final
    private IntList remoteDataSlots;
    @Shadow
    @Nullable
    private ContainerSynchronizer synchronizer;

    PlayerInventorySizeData data = new PlayerInventorySizeData();

    @Override
    public void setStatsSynchronizer(StatsSynchronizer synchronizer) {
        // Send information to the client via serverPlayer.
        this.statsSynchronizer = synchronizer;
        synchronizer.sendInitialData(data);
    }

    public void initMenu(PlayerInventorySizeData data) {
        this.data = data;
    }

    @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
    public void onConstruct(MenuType p_38851_, int p_38852_, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPageChange(ServerPageChangeEvent e) {

        for(Slot s : this.slots) {
            ((IHasSlotPage) s).setPage(e.getPage());
        }

        // Synchronize all information except "carried" at page change with the client.
        // If you synchronize "carried" in here, it could disappear when players put on and take off the armor with slot enchantment.
        if(this.synchronizer != null) {
            int i = 0;
            for(int j = this.slots.size(); i < j; ++i) {
                this.remoteSlots.set(i, this.slots.get(i).getItem().copy());
                this.synchronizer.sendSlotChange((AbstractContainerMenu) (Object) this, i, this.slots.get(i).getItem().copy());
            }
            i = 0;
            for(int k = this.dataSlots.size(); i < k; ++i) {
                this.remoteDataSlots.set(i, this.dataSlots.get(i).get());
                this.synchronizer.sendDataChange((AbstractContainerMenu) (Object) this, i, this.dataSlots.get(i).get());
            }
        }
    }


    @Override
    public void toggleInventory(Player player) {
        if(!((IStorageChangable) player.getInventory()).isActiveInventory()) {
            judgeArmorHiding(player);
            judgeCraftHiding(player);
        }
    }


    @Override
    public void judgeArmorHiding(Player player) {
        for(Slot slot : this.slots) {
            if(((IHasSlotType) slot).getType() == SlotType.ARMOR) {
                if(!((IStorageChangable) player.getInventory()).isActiveArmor()) ((ISlotHidable) slot).hide();
                else ((ISlotHidable) slot).show();
            }
        }
    }

    @Override
    public void judgeCraftHiding(Player player) {
        for(Slot slot : this.slots) {
            if(((IHasSlotType) slot).getType() == SlotType.CRAFT || ((IHasSlotType) slot).getType() == SlotType.RESULT) {
                if(!((IStorageChangable) player.getInventory()).isActiveCraft()) ((ISlotHidable) slot).hide();
                else ((ISlotHidable) slot).show();
            }
        }
    }

    @Override
    public void judgeOffhandHiding(Player player) {
        for(Slot slot : this.slots) {
            if(((IHasSlotType) slot).getType() == SlotType.OFFHAND) {
                if(!((IStorageChangable) player.getInventory()).isActiveOffhand()) ((ISlotHidable) slot).hide();
                else ((ISlotHidable) slot).show();
            }
        }
    }

    @Override
    public void judgePageReduction(int change, int maxpage, Player player) {
        // Judge if the page the player is currently viewing is unnecessary.
        if(player.getLevel().isClientSide()) {
            for(Slot s : slots) {
                if(((IHasSlotPage) s).getPage() > maxpage) {
                    MinecraftForge.EVENT_BUS.post(new PageReduceEvent(maxpage));
                    return;
                }
            }
        }
    }

    @Inject(method = "addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", at = @At("RETURN"), cancellable = true)
    public void onAddSlot(Slot slot, CallbackInfoReturnable<Slot> cir) {
        SlotType.setType(slot);
    }
}
