package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
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
    public void onInitMenu(AbstractContainerMenu p_143400_, CallbackInfo ci) {

        if(p_143400_ instanceof InventoryMenu) {
            ServerPlayer player = (ServerPlayer) (Object) this;

            // When initMenu is executed, the data is not ready, so only the synchronizer is set.
            statsSynchronizer = data -> Messager.sendToPlayer(new InventorySyncPacket(data), player);
            ((IMenuSynchronizer) p_143400_).setStatsSynchronizer(this.statsSynchronizer);
        }
    }
}
