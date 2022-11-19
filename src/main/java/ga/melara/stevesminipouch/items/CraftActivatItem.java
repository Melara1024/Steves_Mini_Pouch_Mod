package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CraftActivatItem extends FunctionFoodItem {

    public static final Item.Properties PROPERTIES = new Item.Properties()
            .tab(ModRegistry.ITEM_GROUP)
            .rarity(Rarity.EPIC)
            .stacksTo(1)
            .food(FOOD_PROPERTIES);

    public CraftActivatItem() {
        super(PROPERTIES);
    }

    @Override
    public void onEat(LivingEntity entity) {
        if(!(entity instanceof Player)) return;
        Player player = (Player) entity;
        ((IStorageChangable) player.getInventory()).toggleCraft(player);
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        Inventory inventory = player.getInventory();
        Messager.sendToPlayer(new InventorySyncPacket(((IStorageChangable) inventory).getAllData()), serverPlayer);
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS) {
        return ITEMS.register("activate_craft", CraftActivatItem::new);
    }
}
