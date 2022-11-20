package ga.melara.stevesminipouch.util;

import net.minecraft.world.entity.player.Player;

public interface IMenuChangable {

    void toggleInventory(Player player);

    void judgeArmorHiding(Player player);

    void judgeCraftHiding(Player player);

    void judgeOffhandHiding(Player player);

    void judgePageReduction(int change, int maxpage, Player player);
}
