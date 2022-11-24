package ga.melara.stevesminipouch.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class FunctionFoodItem extends Item {
    public static Food FOOD_PROPERTIES = new Food.Builder()
            .nutrition(2)
            .saturationMod(2)
            .alwaysEat()
            .effect(() -> new EffectInstance(Effects.BLINDNESS, 20, 1), 1.0F)
            .build();

    public static Item.Properties ITEM_PROPERTIES = new Item.Properties()
            .tab(ItemGroup.TAB_FOOD)
            .rarity(Rarity.EPIC)
            .stacksTo(64)
            .food(FOOD_PROPERTIES);

    public FunctionFoodItem(Item.Properties properties) {
        super(properties);
    }

    public FunctionFoodItem() {
        super(ITEM_PROPERTIES);
    }

    public void onEat(LivingEntity entity) {
    }

    public int getRegistryNumber() {
        return 0;
    }
}
