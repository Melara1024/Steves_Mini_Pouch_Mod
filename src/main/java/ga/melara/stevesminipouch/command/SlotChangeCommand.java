package ga.melara.stevesminipouch.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import ga.melara.stevesminipouch.StevesMiniPouch;
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
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(Component.translatable("command.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {

        commandDispatcher.register(Commands.literal("pouch").requires((sender) -> {
            return sender.hasPermission(2);


        }).then(Commands.argument("targets", EntityArgument.entities()).executes((command) -> {
            return setInventory(command.getSource(), ImmutableList.of(command.getSource().getEntityOrException()), true);


        }).then(Commands.literal("inventory").executes((p_136982_) -> {
            return setInventory(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((p_136982_) -> {
            return setInventory(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), BoolArgumentType.getBool(p_136982_, "activate"));

        }))).then(Commands.literal("armor").executes((p_136980_) -> {
            return setArmor(p_136980_.getSource(), EntityArgument.getEntities(p_136980_, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((p_136982_) -> {
            return setArmor(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), BoolArgumentType.getBool(p_136982_, "activate"));

        }))).then(Commands.literal("offhand").executes((p_136976_) -> {
            return setOffhand(p_136976_.getSource(), EntityArgument.getEntities(p_136976_, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((p_136982_) -> {
            return setOffhand(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), BoolArgumentType.getBool(p_136982_, "activate"));

        }))).then(Commands.literal("craft").executes((p_136974_) -> {
            return setCraft(p_136974_.getSource(), EntityArgument.getEntities(p_136974_, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((p_136982_) -> {
            return setCraft(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), BoolArgumentType.getBool(p_136982_, "activate"));

        }))).then(Commands.literal("slot").executes((p_136956_) -> {
            return setSlot(p_136956_.getSource(), EntityArgument.getEntities(p_136956_, "targets"), 1);
        }).then(Commands.argument("size", IntegerArgumentType.integer(1, Integer.MAX_VALUE)).executes((p_136982_) -> {
            return setSlot(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), IntegerArgumentType.getInteger(p_136982_, "size"));

        })))));
    }

    private static int setInventory(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, boolean increment) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setInventory(player, increment);
                Inventory inventory = player.getInventory();
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            commandSourceStack.sendSuccess(Component.literal(String.format("%s players inventory", increment ? "Activated" : "Inactivated")), true);
            return applied;
        }
    }

    private static int setArmor(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, boolean increment) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setArmor(player, increment);
                Inventory inventory = player.getInventory();
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            commandSourceStack.sendSuccess(Component.literal(String.format("%s players armor", increment ? "Activated" : "Inactivated")), true);
            return applied;
        }
    }

    private static int setOffhand(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, boolean increment) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setOffhand(player, increment);
                Inventory inventory = player.getInventory();
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            commandSourceStack.sendSuccess(Component.literal(String.format("%s players offhand", increment ? "Activated" : "Inactivated")), true);
            return applied;
        }
    }

    private static int setCraft(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, boolean increment) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setCraft(player, increment);
                Inventory inventory = player.getInventory();
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            commandSourceStack.sendSuccess(Component.literal(String.format("%s players crafting ability", increment ? "Activated" : "Inactivated")), true);
            return applied;
        }
    }

    private static int setSlot(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, int increment) throws CommandSyntaxException {
        int applied = 0;
        for(Entity entity : entities) {
            if(entity instanceof ServerPlayer player) {
                ((ICustomInventory) player.getInventory()).setStorageSize(increment, player);
                Inventory inventory = player.getInventory();
                Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory) inventory).getAllData()), player);
                applied++;
            }
        }

        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            commandSourceStack.sendSuccess(Component.literal(String.format("%d slots %s", increment, increment < 0 ? "decreased" : "increased")), true);
            return applied;
        }
    }
}
