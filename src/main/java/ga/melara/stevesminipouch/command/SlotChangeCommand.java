package ga.melara.stevesminipouch.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.sun.jdi.connect.Connector;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Collection;

public class SlotChangeCommand {
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.give.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.clear.everything.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.clear.specific.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> p_136954_) {

        //Todo スロットの増減を行うコマンド -> プレイヤーの選択 -> 変更スロット数
        //Todo かばん状態を変化させるコマンド -> プレイヤーの選択 -> True or False
        //Todo 状態をチェックするコマンド -> プレイヤーの選択



        p_136954_.register(Commands.literal("pouch").requires((sender) -> {
            return sender.hasPermission(2);

        }).then(Commands.argument("targets", EntityArgument.entities()).executes((command) -> {
            return setInventory(command.getSource(), ImmutableList.of(command.getSource().getEntityOrException()), true);

        }).then(Commands.literal("inventory").executes((p_136982_) -> {
            return setInventory(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((p_136982_) -> {
            return setInventory(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), BoolArgumentType.getBool(p_136982_, "activate"));

        }))).then(Commands.literal("armor"    ).executes((p_136980_) -> {
            return setArmor(p_136980_.getSource(), EntityArgument.getEntities(p_136980_, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((p_136982_) -> {
            return setArmor(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), BoolArgumentType.getBool(p_136982_, "activate"));

        }))).then(Commands.literal("offhand").executes((p_136976_) -> {
            return setOffhand(p_136976_.getSource(), EntityArgument.getEntities(p_136976_, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((p_136982_) -> {
            return setOffhand(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), BoolArgumentType.getBool(p_136982_, "activate"));

        }))).then(Commands.literal("craft" ).executes((p_136974_) -> {
            return setCraft(p_136974_.getSource(), EntityArgument.getEntities(p_136974_, "targets"), true);
        }).then(Commands.argument("activate", BoolArgumentType.bool()).executes((p_136982_) -> {
            return setCraft(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), BoolArgumentType.getBool(p_136982_, "activate"));

        }))).then(Commands.literal("slot").executes((p_136956_) -> {
            return setSlot(p_136956_.getSource(), EntityArgument.getEntities(p_136956_, "targets"), 1);
        }).then(Commands.argument("size", IntegerArgumentType.integer(1, Integer.MAX_VALUE)).executes((p_136982_) -> {
            return setSlot(p_136982_.getSource(), EntityArgument.getEntities(p_136982_, "targets"), IntegerArgumentType.getInteger(p_136982_, "size"));

        })))));
    }

    //エフェクトが機能している間一定数スロットを増やす？
    //インベントリ減少アイテムで減らせないスロット
    //Todo Inventoryに一時ポーチサイズを再度追加する

    private static int setInventory(CommandSourceStack p_136960_, Collection<? extends Entity> p_136961_, boolean change) throws CommandSyntaxException
    {
        System.out.println("run setinventory command");
        int applied = 0;
        for(Entity entity : p_136961_) {
            if(entity instanceof Player player) {
                System.out.println(entity.toString());

                ((IStorageChangable) player.getInventory()).setInventory(player, change);

                if(player instanceof ServerPlayer serverPlayer) {
                    Inventory inventory = player.getInventory();
                    Messager.sendToPlayer(new InventorySyncPacket(((IStorageChangable) inventory).getAllData()), serverPlayer);
                }

                //増やす
                applied++;

            }
        }

        //メッセージ
        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            if(p_136961_.size() == 1) {
                p_136960_.sendSuccess(Component.literal(String.format("%s players inventory", change? "Activated":"Inactivated")), true);
            } else {
                p_136960_.sendSuccess(Component.literal(String.format("%s players inventory", change? "Activated":"Inactivated")), true);
            }
            return applied;
        }
    }

    private static int setArmor(CommandSourceStack p_136960_, Collection<? extends Entity> p_136961_, boolean change) throws CommandSyntaxException
    {
        System.out.println("run setarmor command");
        int applied = 0;
        for(Entity entity : p_136961_) {
            if(entity instanceof Player player) {

                System.out.println(entity.toString());

                ((IStorageChangable)player.getInventory()).setArmor(player, change);

                if(player instanceof ServerPlayer serverPlayer) {
                    Inventory inventory = player.getInventory();
                    Messager.sendToPlayer(new InventorySyncPacket(((IStorageChangable) inventory).getAllData()), serverPlayer);
                }

                //増やす
                applied++;

            }
        }

        //メッセージ
        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            if(p_136961_.size() == 1) {
                p_136960_.sendSuccess(Component.literal(String.format("%s players armor", change? "Activated":"Inactivated")), true);
            } else {
                p_136960_.sendSuccess(Component.literal(String.format("%s players armor", change? "Activated":"Inactivated")), true);
            }
            return applied;
        }
    }

    private static int setOffhand(CommandSourceStack p_136960_, Collection<? extends Entity> p_136961_, boolean change) throws CommandSyntaxException
    {
        System.out.println("run setoffhand command");
        int applied = 0;
        for(Entity entity : p_136961_) {
            if(entity instanceof Player player) {

                ((IStorageChangable) player.getInventory()).setOffhand(player, change);

                if(player instanceof ServerPlayer serverPlayer) {
                    Inventory inventory = player.getInventory();
                    Messager.sendToPlayer(new InventorySyncPacket(((IStorageChangable) inventory).getAllData()), serverPlayer);
                }

                System.out.println(entity.toString());
                //増やす
                applied++;

            }
        }

        //メッセージ
        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            if(p_136961_.size() == 1) {
                p_136960_.sendSuccess(Component.literal(String.format("%s players offhand", change? "Activated":"Inactivated")), true);
            } else {
                p_136960_.sendSuccess(Component.literal(String.format("%s players offhand", change? "Activated":"Inactivated")), true);
            }
            return applied;
        }
    }

    private static int setCraft(CommandSourceStack p_136960_, Collection<? extends Entity> p_136961_, boolean change) throws CommandSyntaxException
    {
        System.out.println("run setcraft command");
        int applied = 0;
        for(Entity entity : p_136961_) {
            if(entity instanceof Player player) {

                ((IStorageChangable) player.getInventory()).setCraft(player, change);

                if(player instanceof ServerPlayer serverPlayer) {
                    Inventory inventory = player.getInventory();
                    Messager.sendToPlayer(new InventorySyncPacket(((IStorageChangable) inventory).getAllData()), serverPlayer);
                }

                System.out.println(entity.toString());
                //増やす
                applied++;

            }
        }

        //メッセージ
        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            if(p_136961_.size() == 1) {
                p_136960_.sendSuccess(Component.literal(String.format("%s players crafting ability", change? "Activated":"Inactivated")), true);
            } else {
                p_136960_.sendSuccess(Component.literal(String.format("%s players crafting ability", change? "Activated":"Inactivated")), true);
            }
            return applied;
        }
    }

    private static int setSlot(CommandSourceStack p_136960_, Collection<? extends Entity> p_136961_, int change) throws CommandSyntaxException
    {
        System.out.println("run setslot command");
        int applied = 0;
        for(Entity entity : p_136961_) {
            if(entity instanceof Player player) {

                System.out.println(entity.toString());

                ((IStorageChangable) player.getInventory()).setStorageSize(change, player);

                if(player instanceof ServerPlayer serverPlayer) {
                    Inventory inventory = player.getInventory();
                    Messager.sendToPlayer(new InventorySyncPacket(((IStorageChangable) inventory).getAllData()), serverPlayer);
                }

                //増やす
                applied++;
            }
        }

        //メッセージ
        if(applied == 0) {
            throw ERROR_GIVE_FAILED.create();
        } else {
            if(p_136961_.size() == 1) {
                p_136960_.sendSuccess(Component.literal(String.format("%d slots %s", change, change<0?"decreased":"increased")), true);
            } else {
                p_136960_.sendSuccess(Component.literal(String.format("%d slots %s", change, change<0?"decreased":"increased")), true);
            }
            return applied;
        }
    }
}
