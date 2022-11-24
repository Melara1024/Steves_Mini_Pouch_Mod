package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;


public class OffhandActivateItem extends FunctionFoodItem {

    public static final Item.Properties PROPERTIES = new Item.Properties()
            .tab(ModRegistry.ITEM_GROUP)
            .rarity(Rarity.EPIC)
            .stacksTo(1)
            .food(FOOD_PROPERTIES);

    public OffhandActivateItem() {
        super(PROPERTIES);
    }

    @Override
    public void onEat(LivingEntity entity) {
        if(!(entity instanceof Player player)) return;
        ICustomInventory inventory = (ICustomInventory) player.getInventory();
        inventory.toggleOffhand();
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData()), serverPlayer);
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS) {
        return ITEMS.register("activate_offhand", OffhandActivateItem::new);
    }

    @Override
    public int getRegistryNumber() {
        return 2;
    }
}
