package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class SlotItem extends FunctionFoodItem {

    protected int incremental = 0;

    public SlotItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void onEat(LivingEntity entity) {
        if(!Config.REGISTER_SLOT.get()) return;
        if(!(entity instanceof Player player)) return;
        ICustomInventory inventory = (ICustomInventory) player.getInventory();
        inventory.changeStorageSize(incremental);
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData()), serverPlayer);
    }

    // Todo: Add simple use mode
    @Override
    public InteractionResult useOn(UseOnContext context) {
        context.getPlayer().displayClientMessage(Component.literal("UseOn"), true);
        return InteractionResult.PASS;
    }
}
