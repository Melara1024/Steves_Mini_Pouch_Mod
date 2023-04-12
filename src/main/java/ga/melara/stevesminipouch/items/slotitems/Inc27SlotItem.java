package ga.melara.stevesminipouch.items.slotitems;

import ga.melara.stevesminipouch.items.SlotItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Inc27SlotItem extends SlotItem {

    public static Item.Properties PROPERTIES = new Item.Properties()
            .rarity(Rarity.EPIC)
            .stacksTo(27)
            .food(FOOD_PROPERTIES);

    public Inc27SlotItem() {
        super(PROPERTIES);
        this.incremental = 27;
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS) {
        return ITEMS.register("slot_add_lv3", Inc27SlotItem::new);
    }
}
