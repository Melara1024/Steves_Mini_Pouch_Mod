package ga.melara.stevesminipouch.events;

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

@Mod.EventBusSubscriber
public class KeepPouchEvents {

    private static final String KEEP_STATS_TAG = "KeepMiniPouchStats";
    private static final String KEEP_POUCH_TAG = "KeepMiniPouchItems";

    private static final String CHARM_INV_TAG = "TFCharmInventory";


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void preserveStats(LivingDeathEvent e) {

        //プレイヤーじゃないエンティティの死は無視
        //メイドとかも考えたほうが良いかも
        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        //インベントリ状態を維持するかの判定
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
        CompoundTag tag = new CompoundTag();

        tag.putInt("inventorysize", inv.getBaseSize());
        tag.putInt("effectsize", inv.getEffectSize());
        tag.putBoolean("inventory", inv.isActiveInventory());
        tag.putBoolean("armor", inv.isActiveArmor());
        tag.putBoolean("offhand", inv.isActiveOffhand());
        tag.putBoolean("craft", inv.isActiveCraft());

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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void returnStats(PlayerEvent.PlayerRespawnEvent e) {

        //Todo どこかのイベントがインスタンスを無尽蔵に増やしている(or生成しまくっている？)
        //Todo どこかのパケットがインベントリサイズをいじるせいでアイテムを投げてしまう

        //todo インベントリ状態の引き継ぎもタグでやる

        //inventory内でのステータスの受け渡しをそろそろPlayerInventorySizeDataに変更する？

        //Todo インスタンス付きのイベントをすべてstatic化する

        if (!(e.getEntity() instanceof ServerPlayer player)) return;
        if (player.getLevel().isClientSide()) return;


        CompoundTag data = getPlayerData(player);
        if (data.contains(KEEP_STATS_TAG)) {

            CompoundTag tag = data.getCompound(KEEP_STATS_TAG);

            int effectSize = tag.contains("effectsize") ? tag.getInt("effectsize") : 0;

            int inventorySize;
            if (Config.FORCE_SIZE.get()) {
                inventorySize = Config.MAX_SIZE.get();
            } else if (tag.contains("inventorysize")) {
                int size = tag.getInt("inventorysize");
                if (size > Config.MAX_SIZE.get()) {
                    inventorySize = Config.MAX_SIZE.get();
                } else {
                    inventorySize = size;
                }
            } else {
                inventorySize = Math.min(Config.DEFAULT_SIZE.get(), Config.MAX_SIZE.get());
            }

            boolean isActiveInventory =
                    Config.FORCE_INVENTORY.get() ? Config.DEFAULT_INVENTORY.get() :
                            tag.contains("inventory") ? tag.getBoolean("inventory") : Config.DEFAULT_INVENTORY.get();
            boolean isActiveArmor =
                    !Config.FORCE_INVENTORY.get() && (Config.FORCE_ARMOR.get() ? Config.DEFAULT_ARMOR.get() :
                            tag.contains("armor") ? tag.getBoolean("armor") : Config.DEFAULT_ARMOR.get());
            boolean isActiveOffhand =
                    Config.FORCE_OFFHAND.get() ? Config.DEFAULT_OFFHAND.get() :
                            tag.contains("offhand") ? tag.getBoolean("offhand") : Config.DEFAULT_OFFHAND.get();
            boolean isActiveCraft =
                    !Config.FORCE_INVENTORY.get() && (Config.FORCE_CRAFT.get() ? Config.DEFAULT_CRAFT.get() :
                            tag.contains("craft") ? tag.getBoolean("craft") : Config.DEFAULT_CRAFT.get());


            System.out.println("return stats");
            ((ICustomInventory) player.getInventory()).initServer(inventorySize, effectSize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft);
            ((IMenuSynchronizer) player.containerMenu).initMenu(new InventoryStatsData(inventorySize, effectSize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void returnPouch(PlayerEvent.PlayerRespawnEvent e) {

        if (!(e.getEntity() instanceof ServerPlayer player)) return;
        if (player.getLevel().isClientSide()) return;

        CompoundTag data = getPlayerData(player);
        if (data.contains(KEEP_POUCH_TAG)) {
            ListTag tag = data.getList(KEEP_POUCH_TAG, 10);

            LockableItemStackList items = (LockableItemStackList) player.getInventory().items;
            List<ItemStack> blockedItems = new ArrayList<ItemStack>();


            for (int i = 0; i < tag.size(); ++i) {
                CompoundTag compoundtag = tag.getCompound(i);
                int j = compoundtag.getByte("Slot") & 255;
                ItemStack itemstack = ItemStack.of(compoundtag);
                if (!itemstack.isEmpty()) {
                    if (j < items.size()) {
                        if (items.get(j).isEmpty()) {
                            items.set(j, itemstack);
                            System.out.println("set items " + j + " " + itemstack);
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
