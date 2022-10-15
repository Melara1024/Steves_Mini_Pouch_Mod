package ga.melara.stevesminipouch.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IStorageChangable {
    abstract void toggleInventory(LivingEntity entity);

    abstract void toggleArmor(LivingEntity entity);

    abstract void toggleCraft(LivingEntity entity);

    abstract void toggleOffhand(LivingEntity entity);

    abstract void changeStorageSize(int change, LivingEntity entity);

    abstract int getMaxPage();

    abstract int getSize();
}
