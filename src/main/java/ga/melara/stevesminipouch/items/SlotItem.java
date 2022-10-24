package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.data.PlayerInventoryProvider;
import ga.melara.stevesminipouch.data.PlayerInventorySizeData;
import ga.melara.stevesminipouch.util.IMenuChangable;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.LazyOptional;

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

        //保存
        LazyOptional<PlayerInventorySizeData> l = player.getCapability(PlayerInventoryProvider.DATA);
        PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());
        p.setSlot(((IStorageChangable) player.getInventory()).getInventorySize());
    }
}
