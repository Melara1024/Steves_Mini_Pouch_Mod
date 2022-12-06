package ga.melara.stevesminipouch.items.slotitems;

import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.items.SlotItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Add9SlotItem extends SlotItem {
    public static Item.Properties PROPERTIES = new Item.Properties()
            .tab(ModRegistry.ITEM_GROUP)
            .rarity(Rarity.RARE)
            .stacksTo(27)
            .food(FOOD_PROPERTIES);

    public Add9SlotItem() {
        super(PROPERTIES);
        this.incremental = 9;
    }


    @Override
    public int getRegistryNumber() {
        return 5;
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS) {
        return ITEMS.register("slot_add_lv2", Add9SlotItem::new);
    }
}
