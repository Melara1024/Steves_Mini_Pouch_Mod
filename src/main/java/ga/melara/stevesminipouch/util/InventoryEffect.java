package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.mixin.LivingEntityMixin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public enum InventoryEffect {
    ACTIVATE_INVENTORY{
        @Override
        public void apply (Player player)
        {
            ((IStorageChangable)player.getInventory()).changeStorageSize(1, player.getLevel(), player);
            player.sendSystemMessage(Component.literal("inventory activated!"));
        }
    },
    ACTIVATE_OFFHAND {
        @Override
        public void apply(Player player) {
            ((IStorageChangable)player.getInventory()).changeStorageSize(2, player.getLevel(), player);
            player.sendSystemMessage(Component.literal("offhand activated!"));
        }
    },
    ACTIVATE_CRAFT {
        @Override
        public void apply(Player player) {
            ((IStorageChangable)player.getInventory()).changeStorageSize(3, player.getLevel(), player);
            player.sendSystemMessage(Component.literal("craft activated!"));
        }
    },
    ACTIVATE_ARMOR {
        @Override
        public void apply(Player player) {
            ((IStorageChangable)player.getInventory()).changeStorageSize(4, player.getLevel(), player);
            player.sendSystemMessage(Component.literal("armor activated!"));
        }
    },
    ADD_SLOT {
        @Override
        public void apply(Player player) {
            ((IStorageChangable)player.getInventory()).changeStorageSize(5, player.getLevel(), player);
            player.sendSystemMessage(Component.literal("slot added!"));
        }
    };


    public abstract void apply(Player player);
}
