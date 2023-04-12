package ga.melara.stevesminipouch.items.slotitems;

import ga.melara.stevesminipouch.items.SlotItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Inc1SlotItem extends SlotItem {

    public static Item.Properties PROPERTIES = new Item.Properties()
            .rarity(Rarity.COMMON)
            .stacksTo(27)
            .food(FOOD_PROPERTIES);

    public Inc1SlotItem() {
        super(PROPERTIES);
        this.incremental = 1;
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS) {
        return ITEMS.register("slot_add_lv1", Add1SlotItem::new);
    }
}
