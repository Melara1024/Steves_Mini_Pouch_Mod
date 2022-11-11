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
        LOGGER.info("registered configs");

        Messager.register();
        LOGGER.info("registered messager");

        ModRegistry.registerItems();
        LOGGER.info("registered items");

        IEventBus eventBus = MinecraftForge.EVENT_BUS;

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(DataGenerators.class);
        MinecraftForge.EVENT_BUS.register(PlayerInventorySizeData.class);

        LOGGER.info("steve's minipouch correctry registered!");
    }


    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        SlotChangeCommand.register(event.getDispatcher());
    }


    //パケット送信の例
    @SubscribeEvent
    public void a(PlayerSetSpawnEvent e) {


    }

    @SubscribeEvent
    public void b(ClientChatReceivedEvent e) {

        //Messager.sendToPlayer(new PacketSync(p), serverPlayer);
        //System.out.println("hello client! from server.");


        System.out.println("I got data! ->" + ClientInventoryData.getSlot());
        System.out.println("I got data! ->" + ClientInventoryData.isActiveInventory());
        System.out.println("I got data! ->" + ClientInventoryData.isActiveOffhand());
        System.out.println("I got data! ->" + ClientInventoryData.isCraftable());
        System.out.println("I got data! ->" + ClientInventoryData.isEquippable());


    }
}