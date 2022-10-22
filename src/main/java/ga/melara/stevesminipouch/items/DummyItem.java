package ga.melara.stevesminipouch.items;

import net.minecraft.world.item.Item;

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
