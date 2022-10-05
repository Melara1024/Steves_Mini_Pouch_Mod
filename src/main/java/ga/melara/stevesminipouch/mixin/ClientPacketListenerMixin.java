package ga.melara.stevesminipouch.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleContainerSetSlot(Lnet/minecraft/network/protocol/game/ClientboundContainerSetSlotPacket;)V", at = @At("HEAD"), cancellable = true)
    public void onPacket(ClientboundContainerSetSlotPacket packet, CallbackInfo ci)
    {
//        System.out.println("-----------------------------------------");
//        System.out.println("containerid : " + packet.getContainerId());
//        System.out.println("item : " + packet.getItem());
//        System.out.println("slot : " + packet.getSlot());
//        System.out.println("stateid : " + packet.getStateId());
//        System.out.println("tostring : " + packet.toString());
    }
}
