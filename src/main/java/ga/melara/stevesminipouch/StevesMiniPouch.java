package ga.melara.stevesminipouch;

import ga.melara.stevesminipouch.stats.Messager;
import net.minecraftforge.fml.common.Mod;

import java.util.logging.Logger;


@Mod(StevesMiniPouch.MODID)
public class StevesMiniPouch {

    public static final String MODID = "stevesminipouch";

    public static final Logger LOGGER = java.util.logging.Logger.getLogger("");

    public StevesMiniPouch() {
        Config.register();

        Messager.register();

        ModRegistry.registerItems();
    }
}