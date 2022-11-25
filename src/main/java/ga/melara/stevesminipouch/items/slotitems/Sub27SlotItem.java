package ga.melara.stevesminipouch.items.slotitems;

import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.items.SlotItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;


public class Sub27SlotItem extends SlotItem {
    public static Item.Properties PROPERTIES = new Item.Properties()
            .tab(ModRegistry.ITEM_GROUP)
            .rarity(Rarity.EPIC)
            .stacksTo(27)
            .food(FOOD_PROPERTIES);

    public Sub27SlotItem() {
        super(PROPERTIES);
        this.incremental = -27;
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS) {
        return ITEMS.register("slot_sub_lv3", Sub27SlotItem::new);
    }

    @Override
    public int getRegistryNumber() {
        return 9;
    }
}
