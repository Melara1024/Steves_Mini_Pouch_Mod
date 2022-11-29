package ga.melara.stevesminipouch.event;

import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ga.melara.stevesminipouch.StevesMiniPouch.LOGGER;

public class KeepPouchEvent {

    private static final String KEEP_POUCH_TAG = "KeepMiniPouch";
    private static final String CHARM_INV_TAG = "TFCharmInventory";

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDeath(LivingDeathEvent e) {

        //Todo プレイヤーに情報を書き込む

        if(Objects.isNull(player) || !e.getEntity().getUUID().equals(this.player.getUUID())) return;

        LOGGER.warn("death");
        LOGGER.debug(this.getAllData().toString());
        // インベントリ状態引き継ぎ用
        isOldInventory = true;

        // 死亡時にインベントリの内容をタグに保存する
        // すでに保存されていた場合は飛ばす

        if(Objects.isNull(player)) return;

        boolean gamerule = e.getEntity().getLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);

        boolean twilight_forest_charm = false;
        if(ModList.get().isLoaded("twilightforest")) {
            //CharmEvent priority is HIGHEST, so this event called after that.
            twilight_forest_charm = getPlayerData(player).contains(KEEP_POUCH_TAG);
        }

        if(!gamerule && !twilight_forest_charm) return;

        if(player.getUUID().equals(e.getEntity().getUUID())) {
            Inventory keepInventory = new Inventory(player);
            ((ICustomInventory) keepInventory).changeStorageSize(this.getInventorySize());

            // このインベントリは36スロットのまま！！
            ListTag tagList = new ListTag();

            for(int i = 36; i < backUpPouch.size(); ++i) {
                if(!backUpPouch.get(i).isEmpty()) {
                    CompoundTag compoundtag = new CompoundTag();
                    compoundtag.putInt("Slot", i);
                    backUpPouch.get(i).save(compoundtag);
                    tagList.add(compoundtag);
                    backUpPouch.set(i, ItemStack.EMPTY);
                    items.set(i, ItemStack.EMPTY);
                }
            }

            if(tagList.size() > 0) {
                getPlayerData(player).put(KEEP_POUCH_TAG, tagList);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void keepStats(PlayerEvent.PlayerRespawnEvent e) {

        //Todo どこかのイベントがインスタンスを無尽蔵に増やしている(or生成しまくっている？)
        //Todo どこかのパケットがインベントリサイズをいじるせいでアイテムを投げてしまう

        //todo インベントリ状態の引き継ぎもタグでやる

        if(Objects.isNull(player) || player.getLevel().isClientSide() || !e.getEntity().getUUID().equals(this.player.getUUID())) return;

        if(isOldInventory && Objects.nonNull(player)) {
            if(!(e.getEntity() instanceof ServerPlayer serverPlayer)) return;

            ICustomInventory inv = (ICustomInventory) serverPlayer.getInventory();
            synchronized(compartments) {

                inv.initMiniPouch(this.inventorySize, this.effectSize, this.isActiveInventory, this.isActiveArmor, this.isActiveOffhand, this.isActiveCraft);
                if(!serverPlayer.getLevel().isClientSide)
                    Messager.sendToPlayer(new InventorySyncPacket(this.getAllData()), serverPlayer);
            }
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if(Objects.isNull(player) || player.getLevel().isClientSide() || !e.getEntity().getUUID().equals(this.player.getUUID())) return;

        if(!(e.getEntity() instanceof ServerPlayer serverPlayer)) return;

        // Because both the size and the item list are being tweaked, the data will break if this is executed in parallel.
        synchronized(serverPlayer) {

            CompoundTag playerData = getPlayerData(serverPlayer);
            if(!serverPlayer.getLevel().isClientSide() && playerData.contains(KEEP_POUCH_TAG)) {
                ListTag tag = playerData.getList(KEEP_POUCH_TAG, 10);

                LockableItemStackList items = (LockableItemStackList) serverPlayer.getInventory().items;
                List<ItemStack> blockedItems = new ArrayList<ItemStack>();

                synchronized(serverPlayer.getInventory().items) {

                    for(int i = 0; i < tag.size(); ++i) {
                        CompoundTag compoundtag = tag.getCompound(i);
                        int j = compoundtag.getByte("Slot") & 255;
                        ItemStack itemstack = ItemStack.of(compoundtag);
                        if(!itemstack.isEmpty()) {
                            if(j < items.size()) {
                                if(items.get(j).isEmpty()) {
                                    items.set(j, itemstack);
                                    System.out.println("set items " + j + " " + itemstack);
                                } else {
                                    blockedItems.add(itemstack);
                                }
                            }
                        }
                    }
                    if(!blockedItems.isEmpty()) blockedItems.forEach(serverPlayer.getInventory()::add);
                }


                getPlayerData(player).getList(KEEP_POUCH_TAG, 10).clear();
                getPlayerData(player).remove(KEEP_POUCH_TAG);
            }
        }
    }

    private static CompoundTag getPlayerData(Player player) {
        if(!player.getPersistentData().contains(Player.PERSISTED_NBT_TAG)) {
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
    }

}
