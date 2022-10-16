package ga.melara.stevesminipouch.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface IMenuChangable {
    abstract void toggleInventory(Player player);

    abstract void toggleArmor(Player player);

    abstract void toggleCraft(Player player);

    abstract void toggleOffhand(Player player);

    abstract void changeStorageSize(int change, Player player);
}
