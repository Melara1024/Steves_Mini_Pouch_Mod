package ga.melara.stevesminipouch.items.slotitems;

import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.items.InventoryActivateItem;
import ga.melara.stevesminipouch.items.SlotItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class Add1SlotItem extends SlotItem
{
    public static Item.Properties PROPERTIES = new Item.Properties()
            .tab(ModRegistry.ITEM_GROUP)
            .rarity(Rarity.COMMON)
            .stacksTo(27)
            .food(FOOD_PROPERTIES);;

    public Add1SlotItem()
    {
        super(PROPERTIES);
        this.changeValue = 1;
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS)
    {
        System.out.println("Add1!!");
        return ITEMS.register("slot_add_1", Add1SlotItem::new);
    }
}
