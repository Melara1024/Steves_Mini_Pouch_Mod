package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.util.InventoryEffect;
import ga.melara.stevesminipouch.util.MobEffectInstanceWithFunction;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class SlotItem extends Item {

    public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(2)
            .alwaysEat()
            .effect(()-> new MobEffectInstanceWithFunction(InventoryEffect.ADD_SLOT), 1)
            .build();
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties()
            .tab(CreativeModeTab.TAB_FOOD)
            .rarity(Rarity.EPIC)
            .stacksTo(27)
            .food(FOOD_PROPERTIES);



    public SlotItem(int value) {
        super(ITEM_PROPERTIES);
    }

    public static Supplier<? extends Item> build(int value)
    {
        return () -> new SlotItem(value);
    }
}
