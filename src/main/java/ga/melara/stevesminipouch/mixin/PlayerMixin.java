package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IAdditionalStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
    public void onReadData(CompoundTag p_36215_, CallbackInfo ci)
    {
        System.out.println("minipouch read");
        ListTag listtag = p_36215_.getList("MiniPouch", 10);
        ((IAdditionalStorage)this.inventory).loadAdditional(listtag);
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    public void onAddData(CompoundTag p_36265_, CallbackInfo ci)
    {
        System.out.println("minipouch add");
        p_36265_.put("MiniPouch", ((IAdditionalStorage)this.inventory).saveAdditional(new ListTag()));
    }
}
