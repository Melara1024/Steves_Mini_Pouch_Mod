package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.stats.InventoryStatsData;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.stats.StatsSynchronizer;
import ga.melara.stevesminipouch.subscriber.KeepPouchEvents;
import ga.melara.stevesminipouch.util.ICustomInventory;
import ga.melara.stevesminipouch.util.IMenuSynchronizer;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

import static ga.melara.stevesminipouch.StevesMiniPouch.LOGGER;
import static ga.melara.stevesminipouch.subscriber.KeepPouchEvents.KEEP_STATS_TAG;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "initMenu", at = @At(value = "HEAD"), cancellable = true)
    public void onInitMenu(AbstractContainerMenu menu, CallbackInfo ci) {
        if (menu instanceof InventoryMenu) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            // When initMenu is executed, the data is not ready, so only the synchronizer is set.
            StatsSynchronizer statsSynchronizer = data -> Messager.sendToPlayer(new InventorySyncPacket(data), player);
            ((IMenuSynchronizer) menu).sendSynchronizePacket(statsSynchronizer);
        }
    }


    @Inject(method = "restoreFrom", at = @At(value = "HEAD"))
    public void onRestore(ServerPlayer oldPlayer, boolean pKeepEverything, CallbackInfo ci) {

        ServerPlayer newPlayer = (ServerPlayer) (Object) this;

        CompoundTag data = getPlayerData(oldPlayer);

        if (data.contains(KEEP_STATS_TAG)) {



            CompoundTag tag = data.getCompound(KEEP_STATS_TAG);


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

            LOGGER.warn("--- restore stats ---");
            LOGGER.warn("siz " + stats.getInventorySize());
            LOGGER.warn("inv " + stats.isActiveInventory());
            LOGGER.warn("arm " + stats.isActiveArmor());
            LOGGER.warn("off " + stats.isActiveOffhand());
            LOGGER.warn("cft " + stats.isActiveCraft());

            //黄昏チャームを使ったときだけステータス反映がクライアント側だけできない
            //サーバー側はdataコマンドを使った感じ異常なし
            //クライアントの初期化が阻害されている？
            //クライアントの初期化が上書きされている？
            //スクリーンがリアルタイムで誤情報を送ってしまっている？
            //他のGUIを開いてももとに戻らないか？ もとに戻らない場合パケットがおかしい
            //curiosリセット問題はひとまず解決

            ((ICustomInventory) newPlayer.getInventory()).initServer(stats);

            getPlayerData(newPlayer).remove(KEEP_STATS_TAG);

            if (newPlayer.getLevel().isClientSide()) return;
            LOGGER.warn("server side");
            ((IMenuSynchronizer) newPlayer.containerMenu).setdataToClient(stats);
            ((IMenuSynchronizer) newPlayer.inventoryMenu).setdataToClient(stats);
            Messager.sendToPlayer(new InventorySyncPacket(stats), (ServerPlayer) (Object) this);
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    public void onDeath(DamageSource pCause, CallbackInfo ci) {

        Player player = (Player) (Object) this;

        // if player has twilight forest's charm lv3, inventory stats and pouch will reserve.
        if (ModList.get().isLoaded("twilightforest") || !(player.getLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))) {
            //CharmEvent priority is HIGHEST, so this event called after that.
            if(((ServerPlayer)(Object)this).getInventory().hasAnyMatching((item)->{
                return item.getItem().toString().contains("charm_of_keeping_3");
            }) || hasCharmCurio("charm_of_keeping_3", player)) {
                getPlayerData(player).put(KeepPouchEvents.CHARM_DETECTED_TAG, new CompoundTag());
            }
        }

        LockableItemStackList items = (LockableItemStackList) player.getInventory().items;

        ICustomInventory inventory = (ICustomInventory) player.getInventory();

        NonNullList<ItemStack> backup = inventory.getBackUpPouch();
        for (int i = 0; i < items.size(); i++) {
            backup.set(i, items.get(i));
        }
    }


    private static CompoundTag getPlayerData(Player player) {
        if (!player.getPersistentData().contains(Player.PERSISTED_NBT_TAG)) {
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
    }

    private static boolean hasCharmCurio(String item, Player player) {
        if (ModList.get().isLoaded("curios")) {
            Optional<SlotResult> slot = CuriosApi.getCuriosHelper().findFirstCurio(player, itemStack -> itemStack.getItem().toString().equals(item));
            if (slot.isPresent()) {
                return true;
            }
        }
        return false;
    }
}