package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class SlotItem extends FunctionFoodItem {

    protected int incremental = 0;

    public SlotItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void onEat(LivingEntity entity) {
        if(!(entity instanceof Player player)) return;
        ICustomInventory inventory = (ICustomInventory) player.getInventory();
        inventory.changeStorageSize(incremental);
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        System.out.println("item slot");
        Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData(), serverPlayer.getUUID()), serverPlayer);
    }
}
