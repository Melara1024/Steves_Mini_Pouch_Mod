package ga.melara.stevesminipouch.util;

import net.minecraft.world.entity.LivingEntity;

public interface IMenuChangable {
    abstract void toggleInventory(LivingEntity entity);

    abstract void toggleArmor(LivingEntity entity);

    abstract void toggleCraft(LivingEntity entity);

    abstract void toggleOffhand(LivingEntity entity);

    abstract void changeStorageSize(int change, LivingEntity entity);
}
