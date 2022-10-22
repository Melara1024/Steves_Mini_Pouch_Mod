package ga.melara.stevesminipouch.items.slotitems;

import ga.melara.stevesminipouch.items.SlotItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Sub1SlotItem extends SlotItem
{
    public static Item.Properties PROPERTIES = new Item.Properties()
            .tab(CreativeModeTab.TAB_FOOD)
            .rarity(Rarity.COMMON)
            .stacksTo(27)
            .food(FOOD_PROPERTIES);;

    public Sub1SlotItem()
    {
        super(PROPERTIES);
        this.changeValue = -1;
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS)
    {
        return ITEMS.register("sub1slot", Sub1SlotItem::new);
    }
}
