package ga.melara.stevesminipouch.mixin.twilight_forest_charm;

import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.data.tags.ItemTagGenerator;
import twilightforest.events.CharmEvents;
import twilightforest.init.TFItems;
import twilightforest.util.TFItemStackUtils;

import java.util.ArrayList;
import java.util.List;

//@Mixin(CharmEvents.class)
public class CharmEventMixin {

    private static final String KEEP_POUCH_TAG = "KeepMiniPouch";


    private static boolean hasCharmCurio(Item item, Player player) {
        return false;
    }


    public static CompoundTag getPlayerData(Player player) {
        return null;
    }



    static void onCharmOfKeeping(Player player, CallbackInfo ci)
    {
        boolean tier3 = TFItemStackUtils.consumeInventoryItem(player, TFItems.CHARM_OF_KEEPING_3.get()) || hasCharmCurio(TFItems.CHARM_OF_KEEPING_3.get(), player);

        //tier1と2のときは対象でないので正常にぶちまけられるか確認

        // すでにタグが存在する場合は無視
        // playerData.contains(tag_name)で存在確認
        //Todo tier3のときのみPouch内容をタグ付けで保存

        if(getPlayerData(player).contains(KEEP_POUCH_TAG))return;


        if(tier3){
            Inventory keepInventory = new Inventory(null);
            ListTag tagList = new ListTag();

            for (int i = 36; i < player.getInventory().items.size(); i++) {
                ItemStack stack = player.getInventory().items.get(i);
                    keepInventory.items.set(i, stack.copy());
                    player.getInventory().items.set(i, ItemStack.EMPTY);
            }

            if (!keepInventory.isEmpty()) {
                keepInventory.save(tagList);
                getPlayerData(player).put(KEEP_POUCH_TAG, tagList);
            }
        }
    }


    static void onReturnStoredItems(Player player, CallbackInfo ci)
    {
        // タグが存在していれば戻す
        CompoundTag playerData = getPlayerData(player);

        if(!playerData.contains(KEEP_POUCH_TAG)) return;

        if (!player.getLevel().isClientSide() && playerData.contains(KEEP_POUCH_TAG)) {
            ListTag tag = playerData.getList(KEEP_POUCH_TAG, 10);

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

            // TFItemStackUtils.loadNoClear(tagList, player.getInventory());

            if(!blockedItems.isEmpty()) blockedItems.forEach(player.getInventory()::add);

            getPlayerData(player).getList(KEEP_POUCH_TAG, 10).clear();
            getPlayerData(player).remove(KEEP_POUCH_TAG);
        }
    }
}
