package ga.melara.stevesminipouch.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(new Recipes(generator));

        generator.addProvider(new ItemModels(generator, event.getExistingFileHelper()));
        generator.addProvider(new Language(generator, "en_us"));
    }
}