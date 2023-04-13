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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SlotItem extends FunctionFoodItem {

    protected int incremental = 0;

    public SlotItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level p_41422_, List<Component> tooltipComponent, TooltipFlag isAdvanced) {
        if(Config.REGISTER_SLOT.get() && incremental != 0){
            if(incremental > 0){
                tooltipComponent.add(Component.literal(String.format("§6Increase %d slots", incremental)));
            }
            else {
                tooltipComponent.add(Component.literal(String.format("§6Decrease %d slots", -incremental)));
            }
        } else {
            tooltipComponent.add(Component.literal("§4It's just food."));
        }
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
