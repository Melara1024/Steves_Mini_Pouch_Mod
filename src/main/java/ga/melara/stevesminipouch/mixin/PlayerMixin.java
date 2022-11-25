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

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"), cancellable = true)
    public void onReadData(CompoundNBT tag, CallbackInfo ci) {

        // State of inventory functions allowed to the player
        CompoundNBT compoundtag = tag.getCompound("InventoryStats");
        ((IAdditionalDataHandler) this.inventory).loadStatus(compoundtag);

        // Storage location for added slots
        ListNBT listtag = tag.getList("MiniPouch", 10);
        ((IAdditionalDataHandler) this.inventory).loadAdditional(listtag);

    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"), cancellable = true)
    public void onAddData(CompoundNBT tag, CallbackInfo ci) {

        // State of inventory functions allowed to the player
        tag.put("InventoryStats", ((IAdditionalDataHandler) this.inventory).saveStatus(new CompoundNBT()));

        // Storage location for added slots
        tag.put("MiniPouch", ((IAdditionalDataHandler) this.inventory).saveAdditional(new ListNBT()));

    }
}
