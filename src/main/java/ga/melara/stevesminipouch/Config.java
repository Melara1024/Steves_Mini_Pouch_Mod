package ga.melara.stevesminipouch;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static void register() {
        registerServerConfig();
        registerClientConfig();
    }


    public static ForgeConfigSpec.IntValue DEFAULT_SIZE;
    public static ForgeConfigSpec.IntValue MAX_SIZE;
    public static ForgeConfigSpec.BooleanValue FORCE_SIZE;

    public static ForgeConfigSpec.BooleanValue DEFAULT_INVENTORY;
    public static ForgeConfigSpec.BooleanValue FORCE_INVENTORY;

    public static ForgeConfigSpec.BooleanValue DEFAULT_OFFHAND;
    public static ForgeConfigSpec.BooleanValue FORCE_OFFHAND;

    public static ForgeConfigSpec.BooleanValue DEFAULT_CRAFT;
    public static ForgeConfigSpec.BooleanValue FORCE_CRAFT;

    public static ForgeConfigSpec.BooleanValue DEFAULT_ARMOR;
    public static ForgeConfigSpec.BooleanValue FORCE_ARMOR;

    public static ForgeConfigSpec.IntValue HOTBAR;

    //client settings
    public static ForgeConfigSpec.IntValue RENDER_OFFSET_X;
    public static ForgeConfigSpec.IntValue RENDER_OFFSET_Y;

    private static void registerServerConfig() {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("If you edit this Config, please do so with care. Some combinations may cause unintended behavior.").push("server");

        DEFAULT_SIZE = SERVER_BUILDER.comment("Default Inventory Size").defineInRange("defaultsize", 18, 0, Integer.MAX_VALUE);
        MAX_SIZE = SERVER_BUILDER.comment("Max Inventory Size").defineInRange("maxsize", 90, 0, Integer.MAX_VALUE);
        FORCE_SIZE = SERVER_BUILDER.comment("Force Inventory Size to Max Size").define("isforcesize", false);

        DEFAULT_INVENTORY = SERVER_BUILDER.comment("Default setting of inventory activate").define("isinventory", true);
        FORCE_INVENTORY = SERVER_BUILDER.comment("If true, inventory activate settings forced to all players.").define("forceinventory", false);

        DEFAULT_OFFHAND = SERVER_BUILDER.comment("Default setting of offhand activate").define("isoffhand", false);
        FORCE_OFFHAND = SERVER_BUILDER.comment("If true, offhand activate settings forced to all players.").define("forceoffhand", false);

        DEFAULT_CRAFT = SERVER_BUILDER.comment("Default setting of inventory crafting activate").define("iscraft", false);
        FORCE_CRAFT = SERVER_BUILDER.comment("If true, 2x2 craft activate settings forced to all players.").define("forcecraft", false);

        DEFAULT_ARMOR = SERVER_BUILDER.comment("Default setting of equipment activate").define("isarmor", false);
        FORCE_ARMOR = SERVER_BUILDER.comment("If true, armor activate settings forced to all players.").define("forcearmor", false);

        SERVER_BUILDER.pop();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }

    private static void registerClientConfig() {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.comment("If you edit this Config, please do so with care. Some combinations may cause unintended behavior.").push("client");

        RENDER_OFFSET_X = CLIENT_BUILDER
                .comment("X Offset the position of the page change button")
                .defineInRange("x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        RENDER_OFFSET_Y = CLIENT_BUILDER
                .comment("Y Offset the position of the page change button")
                .defineInRange("y", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        CLIENT_BUILDER.pop();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }
}
