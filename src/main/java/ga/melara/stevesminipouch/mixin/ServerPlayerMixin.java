package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.stats.StatsSynchronizer;
import ga.melara.stevesminipouch.util.IMenuSynchronizer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    private StatsSynchronizer statsSynchronizer;

    @Inject(method = "initMenu", at = @At(value = "RETURN"), cancellable = true)
    public void onInitMenu(AbstractContainerMenu menu, CallbackInfo ci) {

        if(menu instanceof InventoryMenu) {
            ServerPlayer player = (ServerPlayer) (Object) this;

            // When initMenu is executed, the data is not ready, so only the synchronizer is set.
            System.out.println("initmenu");
            statsSynchronizer = data -> Messager.sendToPlayer(new InventorySyncPacket(data, player.getUUID()), player);
            ((IMenuSynchronizer) menu).setStatsSynchronizer(this.statsSynchronizer);
        }
    }
}