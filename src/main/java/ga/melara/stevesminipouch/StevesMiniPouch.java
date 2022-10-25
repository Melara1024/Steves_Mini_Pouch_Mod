package ga.melara.stevesminipouch;

import com.mojang.logging.LogUtils;
import ga.melara.stevesminipouch.data.*;
import ga.melara.stevesminipouch.data.InventoryDataEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

        eventBus.addGenericListener(Entity.class, InventoryDataEvent::onAttachCapabilitiesPlayer);
        eventBus.addListener(InventoryDataEvent::onPlayerCloned);
        eventBus.addListener(InventoryDataEvent::onRegisterCapabilities);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PlayerInventorySizeData.class);

        LOGGER.info("steve's minipouch correctry registered!");
    }




    //パケット送信の例
    @SubscribeEvent
    public void a(PlayerSetSpawnEvent e) {
        LazyOptional<PlayerInventorySizeData> l = e.getEntity().getCapability(PlayerInventoryProvider.DATA);
        PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());

        System.out.println("setspawn");
        System.out.println(p.getSlot());
        System.out.println(p.isActiveInventory());
        System.out.println(p.isEquippable());
        System.out.println(p.isActiveOffhand());
        System.out.println(p.isCraftable());


        //どうやらこれは問題ないらしい
        //プレイy－のログインイベントでどうにか取得できないか？

        if(e.getEntity() instanceof ServerPlayer serverPlayer) {
            Messager.sendToPlayer(new InventorySyncPacket(p), serverPlayer);
            //System.out.println("hello client! from server.");
        }
    }

    @SubscribeEvent
    public void b(ClientChatReceivedEvent e)
    {

        //Messager.sendToPlayer(new PacketSync(p), serverPlayer);
        //System.out.println("hello client! from server.");


        System.out.println("I got data! ->" + ClientInventoryData.getSlot());
        System.out.println("I got data! ->" + ClientInventoryData.isActiveInventory());
        System.out.println("I got data! ->" + ClientInventoryData.isActiveOffhand());
        System.out.println("I got data! ->" + ClientInventoryData.isCraftable());
        System.out.println("I got data! ->" + ClientInventoryData.isEquippable());


    }
}