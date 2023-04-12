package ga.melara.stevesminipouch.items.slotitems;

import ga.melara.stevesminipouch.items.SlotItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Dec9SlotItem extends SlotItem {

    public static Item.Properties PROPERTIES = new Item.Properties()
            .rarity(Rarity.RARE)
            .stacksTo(27)
            .food(FOOD_PROPERTIES);

    public Dec9SlotItem() {
        super(PROPERTIES);
        this.incremental = -9;
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS) {
        return ITEMS.register("slot_sub_lv2", Dec9SlotItem::new);
    }
}
