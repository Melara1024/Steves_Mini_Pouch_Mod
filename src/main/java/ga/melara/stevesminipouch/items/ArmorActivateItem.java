package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.util.InventoryEffect;
import ga.melara.stevesminipouch.util.MobEffectInstanceWithFunction;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class ArmorActivateItem extends Item {

    public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(2)
            .effect(()-> new MobEffectInstanceWithFunction(InventoryEffect.ACTIVATE_ARMOR), 1)
            .build();
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties()
            .tab(CreativeModeTab.TAB_FOOD)
            .rarity(Rarity.EPIC)
            .stacksTo(1)
            .food(FOOD_PROPERTIES);


    public ArmorActivateItem() {
        super(ITEM_PROPERTIES);
    }

    public static Supplier<? extends Item> build()
    {
        return ArmorActivateItem::new;
    }
}
