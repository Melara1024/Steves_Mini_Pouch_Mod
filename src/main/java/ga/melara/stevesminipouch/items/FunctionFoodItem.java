package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class FunctionFoodItem extends Item
{
    public static FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(2)
            .alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS,20, 1), 1.0F)
            .build();

    public static Item.Properties ITEM_PROPERTIES = new Item.Properties()
            .tab(CreativeModeTab.TAB_FOOD)
            .rarity(Rarity.EPIC)
            .stacksTo(64)
            .food(FOOD_PROPERTIES);

    public FunctionFoodItem(Properties properties)
    {
        super(properties);
    }

    public FunctionFoodItem()
    {
        super(ITEM_PROPERTIES);
    }

    public void onEat(LivingEntity entity){

    }

    private boolean isAllowedEat = true;
    public void isAllowedEat(Player player){
        isAllowedEat = ((IStorageChangable)player.getInventory()).isActiveInventory();
    }
    @Override
    public boolean isEdible() {
        return this.isAllowedEat;
    }

}
