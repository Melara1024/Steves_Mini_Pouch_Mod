package ga.melara.stevesminipouch.util;

import net.minecraft.entity.player.PlayerEntity;

public interface IMenuChangable {

    void updateInventoryHiding(PlayerEntity player);

    void updateArmorHiding(PlayerEntity player);

    void updateCraftHiding(PlayerEntity player);

    void updateOffhandHiding(PlayerEntity player);

    void judgePageReduction(int change, int maxpage, PlayerEntity player);
}
