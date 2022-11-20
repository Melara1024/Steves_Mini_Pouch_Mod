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

        add(ModRegistry.INVENTORY_ACTIVATE_ITEM.get(), "Inventorye bread");
        add(ModRegistry.ARMOR_ACTIVATE_ITEM.get(), "Armorange");
        add(ModRegistry.OFFHAND_ACTIVATE_ITEM.get(), "Offhandorian");
        add(ModRegistry.CRAFT_ACTIVATE_ITEM.get(), "Crafruit");

        add("itemGroup.steves_mini_pouch", "Steve's Mini Pouch");

        add("enchantment.stevesminipouch.slot_enchant", "Cramming");
        add("effect.stevesminipouch.slot_effect", "Cramming");

        add("command.failed", "Pouch Command Failed.");

        add("message.useless", "You felt a great surge of power, but nothing happened.");
        add("message.simple_inventory_1", "You looked at your hand.");
        add("message.simple_inventory_2", "in main hand...");
        add("message.simple_inventory_3", "in off hand...");
    }
}
