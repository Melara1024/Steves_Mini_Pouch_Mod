package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IAdditionalDataHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerMixin {

    @Shadow
    @Final
    private
    PlayerInventory inventory;

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"), cancellable = true)
    public void onReadStats(CompoundNBT tag, CallbackInfo ci) {
        CompoundNBT compoundtag = tag.getCompound("InventoryStats");
        ((IAdditionalDataHandler) this.inventory).loadStatus(compoundtag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"), cancellable = true)
    public void onReadPouch(CompoundNBT tag, CallbackInfo ci) {
        // Load Method for added slots
        ListNBT listtag = tag.getList("MiniPouch", 10);
        ((IAdditionalDataHandler) this.inventory).loadAdditional(listtag);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"), cancellable = true)
    public void onSaveStats(CompoundNBT tag, CallbackInfo ci) {
        tag.put("InventoryStats", ((IAdditionalDataHandler) this.inventory).saveStatus(new CompoundNBT()));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"), cancellable = true)
    public void onSavePouch(CompoundNBT tag, CallbackInfo ci) {
        // Save Method for added slots
        tag.put("MiniPouch", ((IAdditionalDataHandler) this.inventory).saveAdditional(new ListNBT()));
    }
}
