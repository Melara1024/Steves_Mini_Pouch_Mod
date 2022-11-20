package ga.melara.stevesminipouch.mixin;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.util.IAdditionalStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

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
        ((IAdditionalStorage) this.inventory).loadStatus(compoundtag);

        // Storage location for added slots
        ListTag listtag = tag.getList("MiniPouch", 10);
        ((IAdditionalStorage) this.inventory).loadAdditional(listtag);

    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    public void onAddData(CompoundTag tag, CallbackInfo ci) {

        // State of inventory functions allowed to the player
        tag.put("InventoryStats", ((IAdditionalStorage) this.inventory).saveStatus(new CompoundTag()));

        // Storage location for added slots
        tag.put("MiniPouch", ((IAdditionalStorage) this.inventory).saveAdditional(new ListTag()));

    }
}
