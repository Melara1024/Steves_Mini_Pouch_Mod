package ga.melara.stevesminipouch.items;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

public class OffhandActivateItem extends FunctionFoodItem {

    public static final Item.Properties PROPERTIES = new Item.Properties()
            .rarity(Rarity.EPIC)
            .stacksTo(1)
            .food(FOOD_PROPERTIES);

    public OffhandActivateItem() {
        super(PROPERTIES);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level p_41422_, List<Component> tooltipComponent, TooltipFlag isAdvanced) {
        if(Config.REGISTER_OFFHAND.get()){
            tooltipComponent.add(Component.literal("§6Switching the ability to use offhand"));
        } else {
            tooltipComponent.add(Component.literal("§4It's just food."));
        }
    }

    @Override
    public void onEat(LivingEntity entity) {
        if(!Config.REGISTER_OFFHAND.get()) return;
        if(!(entity instanceof Player player)) return;
        ICustomInventory inventory = (ICustomInventory) player.getInventory();
        inventory.toggleOffhand();
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData()), serverPlayer);
    }

    public static RegistryObject<Item> buildInTo(DeferredRegister<Item> ITEMS) {
        return ITEMS.register("activate_offhand", OffhandActivateItem::new);
    }
}
