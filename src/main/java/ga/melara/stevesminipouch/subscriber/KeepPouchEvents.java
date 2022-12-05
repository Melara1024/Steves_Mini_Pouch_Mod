package ga.melara.stevesminipouch.subscriber;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.stats.InventoryStatsData;
import ga.melara.stevesminipouch.util.ICustomInventory;
import ga.melara.stevesminipouch.util.IMenuSynchronizer;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static ga.melara.stevesminipouch.StevesMiniPouch.LOGGER;

@Mod.EventBusSubscriber
public class KeepPouchEvents {

    public static final String KEEP_STATS_TAG = "KeepMiniPouchStats";
    public static final String KEEP_POUCH_TAG = "KeepMiniPouchItems";

    public static final String CHARM_INV_TAG = "TFCharmInventory";


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void preserveStats(LivingDeathEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        boolean gamerule = player.getLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        boolean twilight_forest_charm = false;
        if (ModList.get().isLoaded("twilightforest")) {
            //CharmEvent priority is HIGHEST, so this event called after that.
            CompoundTag data = getPlayerData(player);
            if (data.contains(CHARM_INV_TAG)) {
                ListTag tag = data.getList(CHARM_INV_TAG, 10);
                // if listtag has more 9(hotbar) + 4(armor) + 1(offhand) tags, tier3 charm was used.
                if (tag.size() > 14) twilight_forest_charm = true;
            }
        }

        CompoundTag tag = new CompoundTag();
        if (gamerule || twilight_forest_charm) {
            ICustomInventory inv = (ICustomInventory) player.getInventory();
            tag.putInt("inventorysize", inv.getBaseSize());
            tag.putBoolean("inventory", inv.isActiveInventory());
            tag.putBoolean("armor", inv.isActiveArmor());
            tag.putBoolean("offhand", inv.isActiveOffhand());
            tag.putBoolean("craft", inv.isActiveCraft());
        }
        getPlayerData(player).put(KEEP_STATS_TAG, tag);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void preservePouch(LivingDeathEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        boolean gamerule = e.getEntity().getLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        boolean twilight_forest_charm = false;
        if (ModList.get().isLoaded("twilightforest")) {
            //CharmEvent priority is HIGHEST, so this event called after that.
            CompoundTag data = getPlayerData(player);
            if (data.contains(CHARM_INV_TAG)) {
                ListTag tag = data.getList(CHARM_INV_TAG, 10);
                // if listtag has more 9(hotbar) + 4(armor) + 1(offhand) tags, tier3 charm was used.
                if (tag.size() > 14) twilight_forest_charm = true;
            }
        }
        if (!gamerule && !twilight_forest_charm) return;

        ICustomInventory inv = (ICustomInventory) player.getInventory();
        ListTag tagList = new ListTag();
        NonNullList<ItemStack> backUpPouch = inv.getBackUpPouch();

        for (int i = 36; i < backUpPouch.size(); ++i) {
            if (!backUpPouch.get(i).isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putInt("Slot", i);
                backUpPouch.get(i).save(compoundtag);
                tagList.add(compoundtag);
                backUpPouch.set(i, ItemStack.EMPTY);
                player.getInventory().items.set(i, ItemStack.EMPTY);
            }
        }

        if (tagList.size() > 0) {
            getPlayerData(player).put(KEEP_POUCH_TAG, tagList);
        }
    }

    // The method to restore status has been moved to the server player class

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void returnPouch(PlayerEvent.PlayerRespawnEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        CompoundTag data = getPlayerData(player);
        if (data.contains(KEEP_POUCH_TAG)) {
            ListTag tag = data.getList(KEEP_POUCH_TAG, 10);

            LockableItemStackList items = (LockableItemStackList) player.getInventory().items;
            List<ItemStack> blockedItems = new ArrayList<>();

            for (int i = 0; i < tag.size(); ++i) {
                CompoundTag compoundtag = tag.getCompound(i);
                int j = compoundtag.getByte("Slot") & 255;
                ItemStack itemstack = ItemStack.of(compoundtag);
                if (!itemstack.isEmpty()) {
                    if (j < items.size()) {
                        if (items.get(j).isEmpty()) {
                            items.set(j, itemstack);
                        } else {
                            blockedItems.add(itemstack);
                        }
                    }
                }
            }
            if (!blockedItems.isEmpty()) blockedItems.forEach(player.getInventory()::add);

            getPlayerData(player).getList(KEEP_POUCH_TAG, 10).clear();
            getPlayerData(player).remove(KEEP_POUCH_TAG);
        }
    }

    private static CompoundTag getPlayerData(Player player) {
        if (!player.getPersistentData().contains(Player.PERSISTED_NBT_TAG)) {
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
    }
}
