package ga.melara.stevesminipouch;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static void register() {
        registerServerConfig();
        registerClientConfig();
    }

    //server settings
    public static ForgeConfigSpec.IntValue DEFAULT_SIZE;
    public static ForgeConfigSpec.IntValue MAX_SIZE;

    public static ForgeConfigSpec.BooleanValue DEFAULT_INVENTORY;
    public static ForgeConfigSpec.BooleanValue FORCE_INVENTORY;

    public static ForgeConfigSpec.BooleanValue DEFAULT_OFFHAND;
    public static ForgeConfigSpec.BooleanValue FORCE_OFFHAND;

    public static ForgeConfigSpec.BooleanValue DEFAULT_CRAFT;
    public static ForgeConfigSpec.BooleanValue FORCE_CRAFT;

    public static ForgeConfigSpec.BooleanValue DEFAULT_ARMOR;
    public static ForgeConfigSpec.BooleanValue FORCE_ARMOR;

    public static ForgeConfigSpec.IntValue HOTBAR;

    public static ForgeConfigSpec.IntValue ADDITIONAL_INVENTORY_INDEX;

    //client settings
    public static ForgeConfigSpec.IntValue RENDER_SCALE_X;
    public static ForgeConfigSpec.IntValue RENDER_SCALE_Y;

    private static void registerServerConfig() {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();


        SERVER_BUILDER.comment("Comment Server").push("server");

        //server default slots
        DEFAULT_SIZE = SERVER_BUILDER.comment("Default Inventory Size").defineInRange("defaultsize", 80, 0, 1000);
        MAX_SIZE = SERVER_BUILDER.comment("Default Inventory Size").defineInRange("maxsize", 36, 0, 72);

        DEFAULT_INVENTORY = SERVER_BUILDER.comment("Default setting of inventory activate").define("isinventory", true);
        FORCE_INVENTORY = SERVER_BUILDER.comment("If true, inventory activate settings forced to all players.").define("forceinventory", false);

        DEFAULT_OFFHAND = SERVER_BUILDER.comment("Default setting of offhand activate").define("isoffhand", true);
        FORCE_OFFHAND = SERVER_BUILDER.comment("If true, inventory activate settings forced to all players.").define("forceoffhand", false);

        DEFAULT_CRAFT = SERVER_BUILDER.comment("Default setting of inventory crafting activate").define("iscraft", true);
        FORCE_CRAFT = SERVER_BUILDER.comment("If true, inventory activate settings forced to all players.").define("forcecraft", false);

        DEFAULT_ARMOR = SERVER_BUILDER.comment("Default setting of equipment activate").define("isarmor", true);
        FORCE_ARMOR = SERVER_BUILDER.comment("If true, inventory activate settings forced to all players.").define("forcearmor", false);


        ADDITIONAL_INVENTORY_INDEX = SERVER_BUILDER.comment("Additional Inventory Slot index. If you change this, do carefully.").defineInRange("indexstart", 300, 0, 1024);


        //server default hotbar
        HOTBAR = SERVER_BUILDER
                .comment("How much hotbar slots")
                .defineInRange("hotbar slots", 9, 0, 36);


        SERVER_BUILDER.pop();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }

    private static void registerClientConfig() {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();


        CLIENT_BUILDER.comment("Comment Client").push("client");

        //page button position
        RENDER_SCALE_X = CLIENT_BUILDER
                .comment("render position of page button")
                .defineInRange("x", 100, Integer.MIN_VALUE, Integer.MAX_VALUE);

        RENDER_SCALE_Y = CLIENT_BUILDER
                .comment("render position of page button")
                .defineInRange("y", 100, Integer.MIN_VALUE, Integer.MAX_VALUE);


        CLIENT_BUILDER.pop();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }
}
