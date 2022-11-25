package ga.melara.stevesminipouch.mixin;

import com.mojang.authlib.GameProfile;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.stats.StatsSynchronizer;
import ga.melara.stevesminipouch.util.IMenuSynchronizer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin extends PlayerEntity {

    private StatsSynchronizer statsSynchronizer;


    // Dummy to extend Mixin
    public ServerPlayerMixin(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
        super(p_i241920_1_, p_i241920_2_, p_i241920_3_, p_i241920_4_);
    }


    @Inject(method = "initMenu", at = @At(value = "RETURN"), cancellable = true)
    public void onInitMenu(CallbackInfo ci) {

        if(this.containerMenu instanceof PlayerContainer) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

            // When initMenu is executed, the data is not ready, so only the synchronizer is set.
            statsSynchronizer = data -> Messager.sendToPlayer(new InventorySyncPacket(data), player);
            ((IMenuSynchronizer) this.containerMenu).setStatsSynchronizer(this.statsSynchronizer);
        }
    }


    // Dummy to extend Mixin
    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }
}
