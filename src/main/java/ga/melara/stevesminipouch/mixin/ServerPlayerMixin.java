package ga.melara.stevesminipouch.mixin;

import com.mojang.authlib.GameProfile;
import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.integration.curio.Curio;
import ga.melara.stevesminipouch.stats.InventoryStatsData;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.stats.StatsSynchronizer;
import ga.melara.stevesminipouch.util.ICustomInventory;
import ga.melara.stevesminipouch.util.IMenuSynchronizer;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static ga.melara.stevesminipouch.subscriber.KeepPouchEvents.*;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {

    private StatsSynchronizer statsSynchronizer;

    @Inject(method = "initMenu", at = @At(value = "HEAD"), cancellable = true)
    public void onInitMenu(CallbackInfo ci) {
        if(((ServerPlayerEntity)(Object)this).containerMenu instanceof PlayerContainer) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

            // When initMenu is executed, the data is not ready, so only the synchronizer is set.
            StatsSynchronizer statsSynchronizer = data -> Messager.sendToPlayer(new InventorySyncPacket(data), player);
            ((IMenuSynchronizer) ((ServerPlayerEntity)(Object)this).containerMenu).sendSynchronizePacket(statsSynchronizer);
        }
    }


    @Inject(method = "restoreFrom", at = @At(value = "HEAD"))
    public void onRestore(ServerPlayerEntity oldPlayer, boolean pKeepEverything, CallbackInfo ci) {

        ServerPlayerEntity newPlayer = (ServerPlayerEntity) (Object) this;

        CompoundNBT data = getPlayerData(oldPlayer);
        if (data.contains(KEEP_STATS_TAG)) {
            CompoundNBT tag = data.getCompound(KEEP_STATS_TAG);

            int inventorySize = Math.min(Config.DEFAULT_SIZE.get(), Config.MAX_SIZE.get());
            if (tag.contains("inventorysize"))
                inventorySize = Math.min(Config.MAX_SIZE.get(), tag.getInt("inventorysize"));
            if (Config.FORCE_SIZE.get()) inventorySize = Config.MAX_SIZE.get();

            boolean isActiveInventory = Config.DEFAULT_INVENTORY.get();
            if (!Config.FORCE_INVENTORY.get() && tag.contains("inventory"))
                isActiveInventory = tag.getBoolean("inventory");

            boolean isActiveArmor = Config.DEFAULT_ARMOR.get();
            if (!Config.FORCE_INVENTORY.get() && tag.contains("armor")) isActiveArmor = tag.getBoolean("armor");

            boolean isActiveOffhand = Config.DEFAULT_OFFHAND.get();
            if (!Config.FORCE_OFFHAND.get() && tag.contains("offhand")) isActiveOffhand = tag.getBoolean("offhand");

            boolean isActiveCraft = Config.DEFAULT_CRAFT.get();
            if (!Config.FORCE_CRAFT.get() && tag.contains("craft")) isActiveCraft = tag.getBoolean("craft");

            InventoryStatsData stats = new InventoryStatsData(inventorySize, 0, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft);

            ((ICustomInventory) newPlayer.inventory).initServer(stats);

            getPlayerData(newPlayer).remove(KEEP_STATS_TAG);

            if (newPlayer.getLevel().isClientSide()) return;
            ((IMenuSynchronizer) newPlayer.containerMenu).setDataToClient(stats);
            ((IMenuSynchronizer) newPlayer.inventoryMenu).setDataToClient(stats);
            Messager.sendToPlayer(new InventorySyncPacket(stats), (ServerPlayerEntity) (Object) this);
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    public void onDeath(DamageSource pCause, CallbackInfo ci) {

        PlayerEntity player = (PlayerEntity) (Object) this;
        MinecraftForge.EVENT_BUS.unregister(player.inventory);
        MinecraftForge.EVENT_BUS.unregister(player.containerMenu);
        MinecraftForge.EVENT_BUS.unregister(player.inventoryMenu);

        // if player has twilight forest's charm lv3, inventory stats and pouch will reserve.
        if (ModList.get().isLoaded("twilightforest") || !(player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))) {
            //CharmEvent priority is HIGHEST, so this event called after that.
            if(hasAnyMatching(player.inventory, (item)-> item.getItem().toString().contains("charm_of_keeping_")) ||
                    Curio.hasCharmCurio("charm_of_keeping_1", player) ||
                    Curio.hasCharmCurio("charm_of_keeping_2", player) ||
                    Curio.hasCharmCurio("charm_of_keeping_3", player)){
                if(hasAnyMatching(player.inventory, (item)-> item.getItem().toString().equals("charm_of_keeping_3")) ||
                        Curio.hasCharmCurio("charm_of_keeping_3", player)) {
                    getPlayerData(player).put(CHARM_DETECTED_TAG, new CompoundNBT());
                }
                LockableItemStackList armor = (LockableItemStackList) player.inventory.armor;
                armor.setObserver((ignore1, ignore2)->{});
                LockableItemStackList offhand = (LockableItemStackList) player.inventory.offhand;

                CompoundNBT data = getPlayerData(player);

                ListNBT tag = new ListNBT();

                for(int i = 0; i < armor.size(); ++i) {
                    if(!armor.get(i).isEmpty()) {
                        CompoundNBT compoundtag = new CompoundNBT();
                        compoundtag.putInt("Slot", i);
                        armor.get(i).save(compoundtag);
                        armor.set(i, ItemStack.EMPTY);
                        tag.add(compoundtag);
                    }
                }
                data.put(KEEP_ARM_TAG, tag);

                tag = new ListNBT();
                for(int i = 0; i < offhand.size(); ++i) {
                    if(!offhand.get(i).isEmpty()) {
                        CompoundNBT compoundtag = new CompoundNBT();
                        compoundtag.putInt("Slot", i);
                        offhand.get(i).save(compoundtag);
                        offhand.set(i, ItemStack.EMPTY);
                        tag.add(compoundtag);
                    }
                }
                data.put(KEEP_OFF_TAG, tag);
            }
        }

        LockableItemStackList items = (LockableItemStackList) player.inventory.items;

        ICustomInventory inventory = (ICustomInventory) player.inventory;

        NonNullList<ItemStack> backup = inventory.getBackUpPouch();
        for (int i = 0; i < items.size(); i++) {
            backup.set(i, items.get(i));
        }
    }

    private static boolean hasAnyMatching(PlayerInventory inventory, Predicate<ItemStack> predicate){
        for(int i=0; i<inventory.getContainerSize(); i++){
            if(predicate.test(inventory.getItem(i))) return true;
        }
        return false;
    }
    private static CompoundNBT getPlayerData(PlayerEntity player) {
        if (!player.getPersistentData().contains(PlayerEntity.PERSISTED_NBT_TAG)) {
            player.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        }
        return player.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
    }
}
