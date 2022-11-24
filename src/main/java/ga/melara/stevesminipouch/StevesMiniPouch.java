package ga.melara.stevesminipouch;



import ga.melara.stevesminipouch.stats.*;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

import java.util.logging.Logger;


@Mod(StevesMiniPouch.MODID)
public class StevesMiniPouch {

    public static final String MODID = "stevesminipouch";

    private static final Logger LOGGER = (Logger) LogManager.getLogger();

    public StevesMiniPouch() {
        Config.register();

        Messager.register();

        ModRegistry.registerItems();
    }
}