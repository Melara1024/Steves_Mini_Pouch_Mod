package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IAdditionalDataHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    @Shadow
    @Final
    private
    Inventory inventory;

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    public void onReadData(CompoundTag tag, CallbackInfo ci) {

        // State of inventory functions allowed to the player
        CompoundTag compoundtag = tag.getCompound("InventoryStats");
        ((IAdditionalDataHandler) this.inventory).loadStatus(compoundtag);

        // Storage location for added slots
        ListTag listtag = tag.getList("MiniPouch", 10);
        ((IAdditionalDataHandler) this.inventory).loadAdditional(listtag);

    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    public void onAddData(CompoundTag tag, CallbackInfo ci) {

        // State of inventory functions allowed to the player
        tag.put("InventoryStats", ((IAdditionalDataHandler) this.inventory).saveStatus(new CompoundTag()));

        // Storage location for added slots
        tag.put("MiniPouch", ((IAdditionalDataHandler) this.inventory).saveAdditional(new ListTag()));

    }
}
