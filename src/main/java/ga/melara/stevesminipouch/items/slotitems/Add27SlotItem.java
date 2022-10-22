package ga.melara.stevesminipouch.items.slotitems;

import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.items.SlotItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Add27SlotItem extends SlotItem
{
    public static Item.Properties PROPERTIES = new Item.Properties()
            .tab(ModRegistry.ITEM_GROUP)
            .rarity(Rarity.EPIC)
            .stacksTo(1)
            .food(FOOD_PROPERTIES);;

    public Add27SlotItem()
    {
        super(PROPERTIES);
        this.changeValue = 27;
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS)
    {
        System.out.println("Add27!!");
        return ITEMS.register("slot_add_3", Add27SlotItem::new);
    }
}
