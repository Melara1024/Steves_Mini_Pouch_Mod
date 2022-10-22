package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.data.InventoryChangedPacket;
import ga.melara.stevesminipouch.data.Messager;
import ga.melara.stevesminipouch.event.InventoryChangeEvent;
import ga.melara.stevesminipouch.util.IMenuChangable;
import ga.melara.stevesminipouch.util.IStorageChangable;
import ga.melara.stevesminipouch.util.InventoryEffect;
import ga.melara.stevesminipouch.util.MobEffectInstanceWithFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public class SlotItem extends FunctionFoodItem {
    
    public int changeValue = 0;

    public SlotItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void onEat(LivingEntity entity)
    {
        if(!(entity instanceof Player))return;

        Player player = (Player)entity;
        if(player.getLevel().isClientSide())System.out.println("here is client side!");
        else System.out.println("here is server side");
        ((IMenuChangable)player.inventoryMenu).changeStorageSize(changeValue, player);
        ((IStorageChangable)player.getInventory()).changeStorageSize(changeValue, player);
    }
}
