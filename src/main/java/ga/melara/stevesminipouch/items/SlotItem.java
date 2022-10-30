package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.util.IMenuChangable;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

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

        ((IStorageChangable)player.getInventory()).changeStorageSize(changeValue, player);
        ((IMenuChangable)player.inventoryMenu).changeStorageSize(changeValue, player);
    }
}
