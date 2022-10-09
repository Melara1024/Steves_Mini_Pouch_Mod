package ga.melara.stevesminipouch.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IStorageChangable {
    abstract void changeStorageSize(int change, Level level, LivingEntity entity);

    abstract int getMaxPage();

    abstract int getSize();
}
