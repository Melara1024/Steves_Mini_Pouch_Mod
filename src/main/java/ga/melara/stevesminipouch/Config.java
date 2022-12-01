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

    public static ForgeConfigSpec.BooleanValue KEEP_AT_DEATH;


    //client settings
    public static ForgeConfigSpec.IntValue RENDER_OFFSET_X;
    public static ForgeConfigSpec.IntValue RENDER_OFFSET_Y;

    private static void registerServerConfig() {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("If you edit this Config, please do so with care. Some combinations may cause unintended behavior.").push("server");

        DEFAULT_SIZE = SERVER_BUILDER.comment("Default Inventory Size").defineInRange("default_size", 17, 0, Integer.MAX_VALUE);
        MAX_SIZE = SERVER_BUILDER.comment("Max Inventory Size").defineInRange("max_size", 81, 0, Integer.MAX_VALUE);
        FORCE_SIZE = SERVER_BUILDER.comment("Force Inventory Size to Max Size").define("force_size", false);

        DEFAULT_INVENTORY = SERVER_BUILDER.comment("Default setting of inventory activate").define("inventory", true);
        FORCE_INVENTORY = SERVER_BUILDER.comment("If true, The above settings are enforced on all players.").define("force_inventory", false);

        DEFAULT_OFFHAND = SERVER_BUILDER.comment("Default setting of offhand activate").define("offhand", true);
        FORCE_OFFHAND = SERVER_BUILDER.comment("If true, The above settings are enforced on all players.").define("force_offhand", false);

        DEFAULT_CRAFT = SERVER_BUILDER.comment("Default setting of 2x2 crafting activate").define("craft", true);
        FORCE_CRAFT = SERVER_BUILDER.comment("If true, The above settings are enforced on all players.").define("force_craft", false);

        DEFAULT_ARMOR = SERVER_BUILDER.comment("Default setting of armor activate").define("armor", true);
        FORCE_ARMOR = SERVER_BUILDER.comment("If true, The above settings are enforced on all players.").define("force_armor", false);

        KEEP_AT_DEATH = SERVER_BUILDER.comment("Keep player's inventory state when you die.").define("keep_at_death", false);

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
