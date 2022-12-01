package ga.melara.stevesminipouch.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;

import java.util.Collection;

public class SlotChangeCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("command.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {

        commandDispatcher.register(Commands.literal("pouch")
        .then(Commands.argument("targets", EntityArgument.entities())

        .then(Commands.literal("inventory").requires((sender) -> {
            return sender.hasPermission(2);
        }).executes((command) -> {
            return setInventory(command.getSource(), EntityArgument.getEntities(command, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((command) -> {
            return setInventory(command.getSource(), EntityArgument.getEntities(command, "targets"), BoolArgumentType.getBool(command, "activate"));


        }))).then(Commands.literal("armor").requires((sender) -> {
            return sender.hasPermission(2);
        }).executes((command) -> {
            return setArmor(command.getSource(), EntityArgument.getEntities(command, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((command) -> {
            return setArmor(command.getSource(), EntityArgument.getEntities(command, "targets"), BoolArgumentType.getBool(command, "activate"));


        }))).then(Commands.literal("offhand").requires((sender) -> {
            return sender.hasPermission(2);
        }).executes((command) -> {
            return setOffhand(command.getSource(), EntityArgument.getEntities(command, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((command) -> {
            return setOffhand(command.getSource(), EntityArgument.getEntities(command, "targets"), BoolArgumentType.getBool(command, "activate"));


        }))).then(Commands.literal("craft").requires((sender) -> {
            return sender.hasPermission(2);
        }).executes((command) -> {
            return setCraft(command.getSource(), EntityArgument.getEntities(command, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((command) -> {
            return setCraft(command.getSource(), EntityArgument.getEntities(command, "targets"), BoolArgumentType.getBool(command, "activate"));


        }))).then(Commands.literal("slot").requires((sender) -> {
            return sender.hasPermission(2);
        }).executes((command) -> {
            return setSlot(command.getSource(), EntityArgument.getEntities(command, "targets"), 1);
        }).then(Commands.argument("size", IntegerArgumentType.integer(1, Integer.MAX_VALUE)).executes((command) -> {
            return setSlot(command.getSource(), EntityArgument.getEntities(command, "targets"), IntegerArgumentType.getInteger(command, "size"));


        }))).then(Commands.literal("stats").requires((sender) -> {
            return sender.hasPermission(0);
        }).executes((command) -> {
            return showStats(command.getSource(), EntityArgument.getEntities(command, "targets"));

        }))));
    }

    private static int setInventory(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, boolean activate) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setInventory(activate);
                Inventory inventory = player.getInventory();
                System.out.println("command setinventory");
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData(), player.getUUID()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_FAILED.create();
        } else {
            commandSourceStack.sendSuccess(Component.literal(String.format("%s player's inventory", activate ? "Activated" : "Inactivated")), true);
            return applied;
        }
    }

    private static int setArmor(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, boolean activate) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setArmor(activate);
                Inventory inventory = player.getInventory();
                System.out.println("command setarmor");
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData(), player.getUUID()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_FAILED.create();
        } else {
            commandSourceStack.sendSuccess(Component.literal(String.format("%s player's armor", activate ? "Activated" : "Inactivated")), true);
            return applied;
        }
    }

    private static int setOffhand(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, boolean activate) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setOffhand(activate);
                Inventory inventory = player.getInventory();
                System.out.println("command setoffhand");
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData(), player.getUUID()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_FAILED.create();
        } else {
            commandSourceStack.sendSuccess(Component.literal(String.format("%s player's offhand", activate ? "Activated" : "Inactivated")), true);
            return applied;
        }
    }

    private static int setCraft(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, boolean activate) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setCraft(activate);
                Inventory inventory = player.getInventory();
                System.out.println("command setcraft");
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData(), player.getUUID()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_FAILED.create();
        } else {
            commandSourceStack.sendSuccess(Component.literal(String.format("%s player's crafting ability", activate ? "Activated" : "Inactivated")), true);
            return applied;
        }
    }

    private static int setSlot(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, int increment) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setStorageSize(increment);
                Inventory inventory = player.getInventory();
                System.out.println("command setslot");
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData(), player.getUUID()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_FAILED.create();
        } else {
                commandSourceStack.sendSuccess(Component.literal(String.format("Set inventory size to %d slots.", increment)), true);
            return applied;
        }
    }

    private static int showStats(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer) {
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_FAILED.create();
        } else {
            for(Entity entity : entities)
            {
                // return first arg player's stats
                if(entity instanceof ServerPlayer player) {
                    ICustomInventory inventory = ((ICustomInventory)player.getInventory());

                    commandSourceStack.sendSuccess(Component.literal(String.format(
                            "-- %s's inventory stats --\n" +
                            "Inventory: %b\n" +
                            "Armor: %b\n" +
                            "Offhand: %b\n" +
                            "Craft: %b\n" +
                            "Base Size: %d\n" +
                            "Effect Size: %d\n" +
                            "Enchant Size: %d",
                            player.getName().getString(),
                            inventory.isActiveInventory(),
                            inventory.isActiveArmor(),
                            inventory.isActiveOffhand(),
                            inventory.isActiveCraft(),
                            inventory.getBaseSize(),
                            inventory.getEffectSize(),
                            inventory.getEnchantSize()))
                            , true);
                    return applied;
                }
            }
            return applied;
        }
    }
}
