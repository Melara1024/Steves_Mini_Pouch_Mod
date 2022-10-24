package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.data.PlayerInventoryProvider;
import ga.melara.stevesminipouch.data.PlayerInventorySizeData;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ArmorActivateItem extends FunctionFoodItem {

    public static final Item.Properties PROPERTIES = new Item.Properties()
            .tab(ModRegistry.ITEM_GROUP)
            .rarity(Rarity.EPIC)
            .stacksTo(1)
            .food(FOOD_PROPERTIES);

    public ArmorActivateItem()
    {
        super(PROPERTIES);
    }

    @Override
    public void onEat(LivingEntity entity)
    {
        if(!(entity instanceof Player))return;

        Player player = (Player)entity;
        ((IMenuChangable)player.inventoryMenu).toggleArmor(player);
        ((IStorageChangable)player.getInventory()).toggleArmor(player);

        //保存
        LazyOptional<PlayerInventorySizeData> l = player.getCapability(PlayerInventoryProvider.DATA);
        PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());
        p.setEquippable(((IStorageChangable) player.getInventory()).isActiveArmor());
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS)
    {
        System.out.println("Armor!!");
        return ITEMS.register("activate_armor", ArmorActivateItem::new);
    }
}
