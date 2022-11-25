package ga.melara.stevesminipouch;



import ga.melara.stevesminipouch.stats.*;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(StevesMiniPouch.MODID)
public class StevesMiniPouch {

    public static final String MODID = "stevesminipouch";

    public static final Logger LOGGER = LogManager.getLogger();

    public StevesMiniPouch() {
        Config.register();

        Messager.register();

        ModRegistry.registerItems();
    }
}