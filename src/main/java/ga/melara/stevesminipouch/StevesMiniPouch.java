package ga.melara.stevesminipouch;

import com.mojang.logging.LogUtils;
import ga.melara.stevesminipouch.data.*;
import ga.melara.stevesminipouch.util.PageChangeEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.slf4j.Logger;


@Mod(StevesMiniPouch.MODID)
public class StevesMiniPouch {

    public static final String MODID = "stevesminipouch";

    public static final Logger LOGGER = LogUtils.getLogger();

    public StevesMiniPouch() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Config.register();

        Messager.register();

        IEventBus eventBus = MinecraftForge.EVENT_BUS;

        eventBus.addGenericListener(Entity.class, InventoryDataEvent::onAttachCapabilitiesPlayer);
        eventBus.addListener(InventoryDataEvent::onPlayerCloned);
        eventBus.addListener(InventoryDataEvent::onRegisterCapabilities);

        MinecraftForge.EVENT_BUS.register(this);


    }

    @SubscribeEvent
    public void ooo(PageChangeEvent e)
    {
        //Messager.sendToPlayer(new PacketSync(new PlayerInventorySizeData()));
        System.out.print("from ");
        //ページの変更時，setメソッドの発火と同時にイベントを送信してサーバー側にもイベントを知らせる？
        if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
        {
            System.out.println("server");
        }
        else{
            System.out.println("client");
        }
    }


    //パケット送信の例
    @SubscribeEvent
    public void a(PlayerSetSpawnEvent e) {
        System.out.println(e.getEntity().getLevel().isClientSide());



        LazyOptional<PlayerInventorySizeData> l = e.getEntity().getCapability(PlayerInventoryProvider.DATA);
        PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());
        p.increaseSlot(1);
        System.out.println("got data is... " + p.getSlot());



        if(e.getEntity() instanceof ServerPlayer serverPlayer) {
            //Messager.sendToPlayer(new PacketSync(p), serverPlayer);
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