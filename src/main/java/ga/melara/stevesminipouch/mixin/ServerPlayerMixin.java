package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.stats.InventoryStatsData;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.stats.StatsSynchronizer;
import ga.melara.stevesminipouch.subscriber.KeepPouchEvents;
import ga.melara.stevesminipouch.util.ICustomInventory;
import ga.melara.stevesminipouch.util.IMenuSynchronizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ga.melara.stevesminipouch.subscriber.KeepPouchEvents.KEEP_STATS_TAG;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "initMenu", at = @At(value = "HEAD"), cancellable = true)
    public void onInitMenu(AbstractContainerMenu menu, CallbackInfo ci) {
        if(menu instanceof InventoryMenu) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            // When initMenu is executed, the data is not ready, so only the synchronizer is set.
            StatsSynchronizer statsSynchronizer = data -> Messager.sendToPlayer(new InventorySyncPacket(data), player);
            ((IMenuSynchronizer) menu).setStatsSynchronizer(statsSynchronizer);
        }
    }

    @Inject(method = "restoreFrom", at = @At(value = "HEAD"))
    public void onRestore(ServerPlayer oldPlayer, boolean pKeepEverything, CallbackInfo ci) {

        ServerPlayer newPlayer = (ServerPlayer) (Object) this;

        if(newPlayer.getLevel().isClientSide()) return;

        CompoundTag data = getPlayerData(oldPlayer);

        if(data.contains(KEEP_STATS_TAG)) {

            CompoundTag tag = data.getCompound(KEEP_STATS_TAG);

            int inventorySize = Math.min(Config.DEFAULT_SIZE.get(), Config.MAX_SIZE.get());
            if(tag.contains("inventorysize"))
                inventorySize = Math.min(Config.MAX_SIZE.get(), tag.getInt("inventorysize"));
            if(Config.FORCE_SIZE.get()) inventorySize = Config.MAX_SIZE.get();

            boolean isActiveInventory = Config.DEFAULT_INVENTORY.get();
            if(!Config.FORCE_INVENTORY.get() && tag.contains("inventory"))
                isActiveInventory = tag.getBoolean("inventory");

            boolean isActiveArmor = Config.DEFAULT_ARMOR.get();
            if(!Config.FORCE_INVENTORY.get() && tag.contains("armor")) isActiveArmor = tag.getBoolean("armor");

            boolean isActiveOffhand = Config.DEFAULT_OFFHAND.get();
            if(!Config.FORCE_OFFHAND.get() && tag.contains("offhand")) isActiveOffhand = tag.getBoolean("offhand");

            boolean isActiveCraft = Config.DEFAULT_CRAFT.get();
            if(!Config.FORCE_CRAFT.get() && tag.contains("craft")) isActiveCraft = tag.getBoolean("craft");

            InventoryStatsData stats = new InventoryStatsData(inventorySize, 0, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft);
            System.out.println("keep event");
            ((ICustomInventory) newPlayer.getInventory()).initServer(stats);
            ((IMenuSynchronizer) newPlayer.containerMenu).setdataToClient(stats);
        }

    }

    private static CompoundTag getPlayerData(Player player) {
        if(!player.getPersistentData().contains(Player.PERSISTED_NBT_TAG)) {
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
    }
}