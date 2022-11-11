package ga.melara.stevesminipouch.util;

import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public interface IHasMixinEvent {
    @SubscribeEvent
    abstract void event(PlayerSetSpawnEvent e);
}
