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
        if(!(entity instanceof Player)) return;
        Player player = (Player) entity;
        ((ICustomInventory) player.getInventory()).changeStorageSize(incremental, player);
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        Inventory inventory = player.getInventory();
        Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData()), serverPlayer);
    }
}
