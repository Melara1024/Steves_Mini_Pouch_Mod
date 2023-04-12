package ga.melara.stevesminipouch.datagen;

import ga.melara.stevesminipouch.command.SlotChangeCommand;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import static ga.melara.stevesminipouch.ModRegistry.*;
import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

public class Language extends LanguageProvider {
    public Language(DataGenerator generator, String locale) {
        super(generator.getPackOutput(), MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(SLOT_ADD1_ITEM.get(), "Edible Chest");
        add(SLOT_ADD9_ITEM.get(), "Edible Largechest");
        add(SLOT_ADD27_ITEM.get(), "Edible Triplechest");

        add(SLOT_SUB1_ITEM.get(), "Edible Air");
        add(SLOT_SUB9_ITEM.get(), "Edible Null");
        add(SLOT_SUB27_ITEM.get(), "Edible Void");

        add(INVENTORY_ACTIVATE_ITEM.get(), "Inventorye bread");
        add(ARMOR_ACTIVATE_ITEM.get(), "Armorange");
        add(OFFHAND_ACTIVATE_ITEM.get(), "Offhandorian");
        add(CRAFT_ACTIVATE_ITEM.get(), "Crafruits");

        add(TAB_NAME, "Steve's Mini Pouch");

        add("enchantment.stevesminipouch." + SLOT_ENCHANT.getId().getPath(), "Cramming");
        add("effect.stevesminipouch." + SLOT_EFFECT.getId().getPath(), "Cramming");

        add(SlotChangeCommand.ERROR_MESSAGE, "Pouch Command Failed.");

        add("message.useless", "You felt a great surge of power, but nothing happened.");
        add("message.simple_inventory_1", "You looked at your hand.");
        add("message.simple_inventory_2", "in main hand...");
        add("message.simple_inventory_3", "in off hand...");
    }
}
