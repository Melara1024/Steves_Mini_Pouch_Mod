package ga.melara.stevesminipouch.subscriber;

import ga.melara.stevesminipouch.util.ICustomInventory;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.GameRules;
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
    public static final String KEEP_STATS_TAG = "KeepMiniPouchStats";
    public static final String KEEP_POUCH_TAG = "KeepMiniPouchItems";

    public static final String KEEP_ARM_TAG = "KeepArmor";
    public static final String KEEP_OFF_TAG = "KeepOffhand";

    public static final String CHARM_DETECTED_TAG = "DetectedTFCharm";

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void preserveStats(LivingDeathEvent e) {
        if(!(e.getEntity() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();

        boolean gamerule = player.getLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        boolean twilight_forest_charm = false;
        if(ModList.get().isLoaded("twilightforest")) {
            //CharmEvent priority is HIGHEST, so this event called after that.
            CompoundNBT data = getPlayerData(player);
            if(data.contains(CHARM_DETECTED_TAG)) twilight_forest_charm = true;
        }

        CompoundNBT tag = new CompoundNBT();
        if(gamerule || twilight_forest_charm) {
            ICustomInventory inv = (ICustomInventory) player.inventory;
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
        if(!(e.getEntity() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();

        boolean gamerule = e.getEntity().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        boolean twilight_forest_charm = false;
        if(ModList.get().isLoaded("twilightforest")) {
            //CharmEvent priority is HIGHEST, so this event called after that.
            CompoundNBT data = getPlayerData(player);
            if(data.contains(CHARM_DETECTED_TAG)) {
                twilight_forest_charm = true;
                getPlayerData(player).remove(CHARM_DETECTED_TAG);
            }
        }
        if(!gamerule && !twilight_forest_charm) return;

        ICustomInventory inv = (ICustomInventory) player.inventory;
        ListNBT tagList = new ListNBT();
        NonNullList<ItemStack> backUpPouch = inv.getBackUpPouch();

        for(int i = 36; i < backUpPouch.size(); ++i) {
            if(!backUpPouch.get(i).isEmpty()) {
                CompoundNBT compoundtag = new CompoundNBT();
                compoundtag.putInt("Slot", i);
                backUpPouch.get(i).save(compoundtag);
                tagList.add(compoundtag);
                backUpPouch.set(i, ItemStack.EMPTY);
                player.inventory.items.set(i, ItemStack.EMPTY);
            }
        }

        if(tagList.size() > 0) {
            getPlayerData(player).put(KEEP_POUCH_TAG, tagList);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void returnPouch(PlayerEvent.PlayerRespawnEvent e) {
        if(!(e.getEntity() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();

        CompoundNBT data = getPlayerData(player);

        if(data.contains(KEEP_ARM_TAG)) {
            ListNBT tag = data.getList(KEEP_ARM_TAG, 10);

            LockableItemStackList armor = (LockableItemStackList) player.inventory.armor;

            for(int i = 0; i < tag.size(); ++i) {
                CompoundNBT compoundtag = tag.getCompound(i);
                int slotNumber = compoundtag.getInt("Slot");
                ItemStack itemstack = ItemStack.of(compoundtag);
                if(!itemstack.isEmpty() && slotNumber < armor.size()) {
                    armor.set(slotNumber, itemstack);
                }
            }
            getPlayerData(player).getList(KEEP_ARM_TAG, 10).clear();
            getPlayerData(player).remove(KEEP_ARM_TAG);
        }

        if(data.contains(KEEP_OFF_TAG)) {
            ListNBT tag = data.getList(KEEP_OFF_TAG, 10);

            LockableItemStackList offhand = (LockableItemStackList) player.inventory.offhand;

            for(int i = 0; i < tag.size(); ++i) {
                CompoundNBT compoundtag = tag.getCompound(i);
                int slotNumber = compoundtag.getInt("Slot");
                ItemStack itemstack = ItemStack.of(compoundtag);
                if(!itemstack.isEmpty() && slotNumber < offhand.size()) {
                    offhand.set(slotNumber, itemstack);
                }
            }
            getPlayerData(player).getList(KEEP_OFF_TAG, 10).clear();
            getPlayerData(player).remove(KEEP_OFF_TAG);
        }


        if(data.contains(KEEP_POUCH_TAG)) {
            ListNBT tag = data.getList(KEEP_POUCH_TAG, 10);

            LockableItemStackList items = (LockableItemStackList) player.inventory.items;
            List<ItemStack> blockedItems = new ArrayList<>();

            for(int i = 0; i < tag.size(); ++i) {
                CompoundNBT compoundtag = tag.getCompound(i);
                int j = compoundtag.getByte("Slot") & 255;
                ItemStack itemstack = ItemStack.of(compoundtag);
                if(!itemstack.isEmpty()) {
                    if(j < items.size()) {
                        if(items.get(j).isEmpty()) {
                            items.set(j, itemstack);
                        } else {
                            blockedItems.add(itemstack);
                        }
                    }
                }
            }
            if(!blockedItems.isEmpty()) blockedItems.forEach(player.inventory::add);

            getPlayerData(player).getList(KEEP_POUCH_TAG, 10).clear();
            getPlayerData(player).remove(KEEP_POUCH_TAG);
        }
    }

    private static CompoundNBT getPlayerData(PlayerEntity player) {
        if(!player.getPersistentData().contains(PlayerEntity.PERSISTED_NBT_TAG)) {
            player.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        }
        return player.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
    }
}
