package ga.melara.stevesminipouch.stats;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

public class Messager {
    public static SimpleChannel channel;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }


    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        channel = net;


        net.messageBuilder(PageChangedPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PageChangedPacket::new)
                .encoder(PageChangedPacket::toBytes)
                .consumer(PageChangedPacket::handle)
                .add();

        net.messageBuilder(InventorySyncPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(InventorySyncPacket::new)
                .encoder(InventorySyncPacket::toBytes)
                .consumer(InventorySyncPacket::handle)
                .add();

        net.messageBuilder(EffectSlotSyncPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(EffectSlotSyncPacket::new)
                .encoder(EffectSlotSyncPacket::toBytes)
                .consumer(EffectSlotSyncPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        channel.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayerEntity player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
