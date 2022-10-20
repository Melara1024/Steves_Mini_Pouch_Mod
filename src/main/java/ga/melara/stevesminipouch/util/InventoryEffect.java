package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.data.InventoryChangedPacket;
import ga.melara.stevesminipouch.data.Messager;
import ga.melara.stevesminipouch.event.InventoryChangeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber
public enum InventoryEffect {
    ACTIVATE_INVENTORY{
        @Override
        public void apply (Player player)
        {
            //インベントリ状態を保持するのはinventoryなのでmenuを先に持ってくる
            ((IMenuChangable)player.inventoryMenu).toggleInventory(player);
            ((IStorageChangable)player.getInventory()).toggleInventory(player);

            //menu側でslot更新

            syncToRemote(player, ACTIVATE_INVENTORY);
            player.sendSystemMessage(Component.literal("inventory activated!"));
        }
    },
    ACTIVATE_OFFHAND {
        @Override
        public void apply(Player player) {
            ((IMenuChangable)player.inventoryMenu).toggleOffhand(player);
            ((IStorageChangable)player.getInventory()).toggleOffhand(player);

            //menu側でslot更新

            syncToRemote(player, ACTIVATE_OFFHAND);
        }
    },
    ACTIVATE_CRAFT {
        @Override
        public void apply(Player player) {
            ((IMenuChangable)player.inventoryMenu).toggleCraft(player);
            ((IStorageChangable)player.getInventory()).toggleCraft(player);
            ((ICraftingContainerChangable)player.inventoryMenu.getCraftSlots()).toggleCraft(player);
            //menu側でslot更新

            syncToRemote(player, ACTIVATE_CRAFT);
        }
    },
    ACTIVATE_ARMOR {
        @Override
        public void apply(Player player) {
            ((IMenuChangable)player.inventoryMenu).toggleArmor(player);
            ((IStorageChangable)player.getInventory()).toggleArmor(player);

            //menu側でslot更新

            syncToRemote(player, ACTIVATE_ARMOR);
        }
    },
    ADD_SLOT {

        //Todo スロット追加数を指定可能なようにapplyのオーバーロードを追加する
        @Override
        public void apply(Player player) {
            apply(player, 1);
        }

        @Override
        public void apply(Player player, int change)
        {
            ((IMenuChangable)player.inventoryMenu).changeStorageSize(change, player);
            ((IStorageChangable)player.getInventory()).changeStorageSize(change, player);

            syncToRemote(player, ADD_SLOT, change);
        }
    };



    public abstract void apply(Player player);
    public void apply(Player player, int change){};

    public void syncToRemote(Player player, InventoryEffect inventoryEffect)
    {
        if(!player.getLevel().isClientSide())
            Messager.sendToPlayer(new InventoryChangedPacket(inventoryEffect, player.getUUID()), (ServerPlayer) player);
    }

    public void syncToRemote(Player player, InventoryEffect inventoryEffect, int change)
    {
        if(!player.getLevel().isClientSide())
            Messager.sendToPlayer(new InventoryChangedPacket(inventoryEffect, change, player.getUUID()), (ServerPlayer) player);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void callThisFromClient(InventoryChangeEvent e)
    {
        if(InventoryEffect.getById(e.getInventoryEffect()) == InventoryEffect.ADD_SLOT)
        {
            InventoryEffect.getById(e.getInventoryEffect()).apply(Minecraft.getInstance().player, e.getSlotChange());
        }
        else
        {
            InventoryEffect.getById(e.getInventoryEffect()).apply(Minecraft.getInstance().player);
        }
    }

    public static InventoryEffect getById(int id)
    {
        return switch(id) {
            case 0 -> InventoryEffect.ACTIVATE_INVENTORY;
            case 1 -> InventoryEffect.ACTIVATE_CRAFT;
            case 2 -> InventoryEffect.ACTIVATE_OFFHAND;
            case 3 -> InventoryEffect.ACTIVATE_ARMOR;
            case 4 -> InventoryEffect.ADD_SLOT;
            default -> ADD_SLOT;
        };
    }

    public static int getByType(InventoryEffect effect)
    {
        return switch(effect) {
            case ACTIVATE_INVENTORY -> 0;
            case ACTIVATE_CRAFT -> 1;
            case ACTIVATE_OFFHAND -> 2;
            case ACTIVATE_ARMOR -> 3;
            case ADD_SLOT -> 4;
        };
    }
}
