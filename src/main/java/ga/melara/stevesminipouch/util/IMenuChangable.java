package ga.melara.stevesminipouch.util;

import net.minecraft.world.entity.player.Player;

public interface IMenuChangable {

    void updateInventoryHiding(Player player);

    void updateArmorHiding(Player player);

    void updateCraftHiding(Player player);

    void updateOffhandHiding(Player player);

    void judgePageReduction(int change, int maxpage, Player player);
}
