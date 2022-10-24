package ga.melara.stevesminipouch.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ItemEntityMixin extends Entity
{
    public ItemEntityMixin(EntityType<?> p_19870_, Level p_19871_)
    {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "playerTouch", at = @At(value = "HEAD"), cancellable = true)
    public void onAddResource(Player p_32040_, CallbackInfo ci) {
        if (!this.level.isClientSide) {
            System.out.println("playerTouch");
            System.out.println(net.minecraftforge.event.ForgeEventFactory.onItemPickup((ItemEntity)(Object) this, p_32040_));
        }
    }

    @Shadow
    @Override
    protected void defineSynchedData()
    {

    }

    @Shadow
    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_)
    {

    }

    @Shadow
    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_)
    {

    }

    @Shadow
    @Override
    public Packet<?> getAddEntityPacket()
    {
        return null;
    }
}
