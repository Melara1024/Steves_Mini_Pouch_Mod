package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.data.InventorySyncPacket;
import ga.melara.stevesminipouch.data.Messager;
import ga.melara.stevesminipouch.data.PlayerInventoryProvider;
import ga.melara.stevesminipouch.data.PlayerInventorySizeData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkDirection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin
{

    @Shadow
    @Final
    public ServerGamePacketListenerImpl connection;

    @Inject(method = "readAdditionalSaveData", at = @At(value = "RETURN"), cancellable = true)
    public void onGetRemainingSpace(CompoundTag p_9131_, CallbackInfo ci) {
        LazyOptional<PlayerInventorySizeData> l = ((ServerPlayer)(Object)this).getCapability(PlayerInventoryProvider.DATA);
        PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());

        System.out.println("setspawn");
        System.out.println(p.getSlot());
        System.out.println(p.isActiveInventory());
        System.out.println(p.isEquippable());
        System.out.println(p.isActiveOffhand());
        System.out.println(p.isCraftable());



        //Messager.sendToPlayer(new InventorySyncPacket(p), (ServerPlayer)(Object)this);


    }

}
