package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.items.slotitems.Add1SlotItem;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CraftActivatItem extends FunctionFoodItem {

    public static final Item.Properties PROPERTIES = new Item.Properties()
            .tab(ModRegistry.ITEM_GROUP)
            .rarity(Rarity.EPIC)
            .stacksTo(1)
            .food(FOOD_PROPERTIES);

    public CraftActivatItem()
    {
        super(PROPERTIES);
    }

    @Override
    public void onEat(LivingEntity entity)
    {
        if(!(entity instanceof Player))return;

        Player player = (Player)entity;
        ((IMenuChangable)player.inventoryMenu).toggleCraft(player);
        ((IStorageChangable)player.getInventory()).toggleCraft(player);
        ((ICraftingContainerChangable)player.inventoryMenu.getCraftSlots()).toggleCraft(player);
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS)
    {
        System.out.println("Craft!!");
        return ITEMS.register("activate_craft", CraftActivatItem::new);
    }
}
