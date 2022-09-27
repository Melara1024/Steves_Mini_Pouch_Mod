package ga.melara.stevesminipouch;

import ga.melara.stevesminipouch.mixin.ContainerMenuMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;

public class CustomInventory extends InventoryMenu {


    public CustomInventory(Inventory p_39706_, boolean p_39707_, Player p_39708_) {
        super(p_39706_, p_39707_, p_39708_);

    }
}
