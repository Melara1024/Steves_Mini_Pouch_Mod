package ga.melara.stevesminipouch;

import com.mojang.logging.LogUtils;
import ga.melara.stevesminipouch.command.SlotChangeCommand;
import ga.melara.stevesminipouch.datagen.DataGenerators;
import ga.melara.stevesminipouch.stats.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import org.slf4j.Logger;


@Mod(StevesMiniPouch.MODID)
public class StevesMiniPouch {

    public static final String MODID = "stevesminipouch";

    public static final Logger LOGGER = LogUtils.getLogger();

    public StevesMiniPouch() {
        Config.register();

        Messager.register();

        ModRegistry.registerItems();
    }
}