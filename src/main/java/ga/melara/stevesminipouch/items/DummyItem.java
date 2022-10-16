package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.util.InventoryEffect;
import ga.melara.stevesminipouch.util.MobEffectInstanceWithFunction;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class DummyItem extends Item{

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties()
            .stacksTo(1);

    public DummyItem() {
        super(ITEM_PROPERTIES);
    }

    public static Supplier<? extends Item> build()
    {
        return DummyItem::new;
    }
}
