package ga.melara.stevesminipouch.command;

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

import java.util.Collection;

public class SlotChangeCommand {

    public static final String ERROR_MESSAGE = "command.failed";
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable(ERROR_MESSAGE));

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
                ICustomInventory inventory = (ICustomInventory) player.getInventory();
                inventory.setInventory(activate);
                Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData()), player);
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
                ICustomInventory inventory = (ICustomInventory) player.getInventory();
                inventory.setArmor(activate);
                Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData()), player);
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
                ICustomInventory inventory = (ICustomInventory) player.getInventory();
                inventory.setOffhand(activate);
                Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData()), player);
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
                ICustomInventory inventory = (ICustomInventory) player.getInventory();
                inventory.setCraft(activate);
                Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData()), player);
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
                ICustomInventory inventory = (ICustomInventory) player.getInventory();
                inventory.setStorageSize(increment);
                Messager.sendToPlayer(new InventorySyncPacket(inventory.getAllData()), player);
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
                // return arg player's stats
                if(entity instanceof ServerPlayer player) {
                    ICustomInventory inventory = (ICustomInventory)player.getInventory();
                    commandSourceStack.sendSuccess(Component.literal(String.format(
                            "§a-- %s's inventory stats --\n" +
                            "§fInventory: %s\n" +
                            "§fArmor: %s\n" +
                            "§fOffhand: %s\n" +
                            "§fCraft: %s\n" +
                            "§fBase Size: %s\n" +
                            "§fEffect Size: %s\n" +
                            "§fEnchant Size: %s",
                            player.getName().getString(),
                            (inventory.isActiveInventory()?"§e":"§7") + inventory.isActiveInventory(),
                            (inventory.isActiveArmor()?"§e":"§7") + inventory.isActiveArmor(),
                            (inventory.isActiveOffhand()?"§e":"§7") + inventory.isActiveOffhand(),
                            (inventory.isActiveCraft()?"§e":"§7") + inventory.isActiveCraft(),
                            (inventory.getBaseSize()==0?"§7":"§e") + inventory.getBaseSize(),
                            (inventory.getEffectSize()==0?"§7":"§e") + inventory.getEffectSize(),
                            (inventory.getEffectSize()==0?"§7":"§e") + inventory.getEnchantSize())), true);
                }
            }
            return applied;
        }
    }
}
