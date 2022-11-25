package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;


public class SlotItem extends FunctionFoodItem {

    protected int incremental = 0;

    public SlotItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void onEat(LivingEntity entity) {
        if(!(entity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity)entity;
        ICustomInventory inventory = (ICustomInventory) player.inventory;
        inventory.changeStorageSize(incremental);
        if(!(player instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
        Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData()), serverPlayer);
    }
}
