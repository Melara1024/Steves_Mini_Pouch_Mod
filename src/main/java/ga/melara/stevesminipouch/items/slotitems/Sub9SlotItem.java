package ga.melara.stevesminipouch.items.slotitems;

import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.items.SlotItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;


public class Sub9SlotItem extends SlotItem {
    public static Item.Properties PROPERTIES = new Item.Properties()
            .tab(ModRegistry.ITEM_GROUP)
            .rarity(Rarity.RARE)
            .stacksTo(27)
            .food(FOOD_PROPERTIES);

    public Sub9SlotItem() {
        super(PROPERTIES);
        this.incremental = -9;
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS) {
        return ITEMS.register("slot_sub_lv2", Sub9SlotItem::new);
    }

    @Override
    public int getRegistryNumber() {
        return 8;
    }
}
