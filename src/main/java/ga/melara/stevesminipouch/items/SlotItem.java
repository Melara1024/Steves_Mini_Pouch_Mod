package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.IMenuChangable;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SlotItem extends FunctionFoodItem {

    public int changeValue = 0;

    public SlotItem(Item.Properties properties) {
        super(properties);
    }


    @Override
    public void onEat(LivingEntity entity) {
        if(!(entity instanceof Player)) return;

        Player player = (Player) entity;

        ((IStorageChangable) player.getInventory()).changeStorageSize(changeValue, player);

        if(!(player instanceof ServerPlayer serverPlayer)) return;
        Inventory inventory = player.getInventory();
        Messager.sendToPlayer(new InventorySyncPacket(((IStorageChangable) inventory).getAllData()), serverPlayer);
    }
}
