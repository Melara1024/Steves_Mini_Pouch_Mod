package ga.melara.stevesminipouch.datagen;

import ga.melara.stevesminipouch.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

public class Language extends LanguageProvider {
    public Language(DataGenerator generator, String locale) {
        super(generator, MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModRegistry.SLOT_ADD1_ITEM.get(), "Edible Chest");
        add(ModRegistry.SLOT_ADD9_ITEM.get(), "Edible Largechest");
        add(ModRegistry.SLOT_ADD27_ITEM.get(), "Edible Triplechest");

        add(ModRegistry.SLOT_SHRINK1_ITEM.get(), "Edible Air");
        add(ModRegistry.SLOT_SHRINK9_ITEM.get(), "Edible Null");
        add(ModRegistry.SLOT_SHRINK27_ITEM.get(), "Edible Void");

        add(ModRegistry.INVENTORY_ACTIVATE_ITEM.get(), "Inventory Rye bread");
        add(ModRegistry.ARMOR_ACTIVATE_ITEM.get(), "Armor Orange");
        add(ModRegistry.OFFHAND_ACTIVATE_ITEM.get(), "Offhand Dorian");
        add(ModRegistry.CRAFT_ACTIVATE_ITEM.get(), "Craft Fruit");

        add("commands.pouch.failed", "Pouch Command Failed");
    }
}
