package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IAdditionalDataHandler;
import ga.melara.stevesminipouch.util.ICustomInventory;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ga.melara.stevesminipouch.StevesMiniPouch.LOGGER;
import static ga.melara.stevesminipouch.subscriber.KeepPouchEvents.*;

@Mixin(Player.class)
public class PlayerMixin {

    @Shadow
    @Final
    private
    Inventory inventory;

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"), cancellable = true)
    public void onReadStats(CompoundTag tag, CallbackInfo ci) {
        CompoundTag compoundtag = tag.getCompound("InventoryStats");
        ((IAdditionalDataHandler) this.inventory).loadStatus(compoundtag);
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    public void onReadPouch(CompoundTag tag, CallbackInfo ci) {
        // Load Method for added slots
        ListTag listtag = tag.getList("MiniPouch", 10);
        ((IAdditionalDataHandler) this.inventory).loadAdditional(listtag);
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"), cancellable = true)
    public void onSaveStats(CompoundTag tag, CallbackInfo ci) {
        tag.put("InventoryStats", ((IAdditionalDataHandler) this.inventory).saveStatus(new CompoundTag()));
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    public void onSavePouch(CompoundTag tag, CallbackInfo ci) {
        // Save Method for added slots
        tag.put("MiniPouch", ((IAdditionalDataHandler) this.inventory).saveAdditional(new ListTag()));
    }
}
