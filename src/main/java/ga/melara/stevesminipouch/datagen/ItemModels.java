package ga.melara.stevesminipouch.datagen;

import ga.melara.stevesminipouch.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;
import static ga.melara.stevesminipouch.ModRegistry.*;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation parent = mcLoc("item/generated");
        ResourceLocation box_1 = modLoc("item/" + SLOT_SUB1_ITEM.getId().getPath());
        this.singleTexture(SLOT_SUB9_ITEM.getId().getPath(), box_1, "layer", modLoc("item/" + SLOT_SUB9_ITEM.getId().getPath()));
        this.singleTexture(SLOT_SUB27_ITEM.getId().getPath(), box_1, "layer", modLoc("item/" + SLOT_SUB27_ITEM.getId().getPath()));

        this.singleTexture(INVENTORY_ACTIVATE_ITEM.getId().getPath(), parent, "layer0", modLoc("item/" + INVENTORY_ACTIVATE_ITEM.getId().getPath()));
        this.singleTexture(ARMOR_ACTIVATE_ITEM.getId().getPath(), parent, "layer0", modLoc("item/" + ARMOR_ACTIVATE_ITEM.getId().getPath()));
        this.singleTexture(OFFHAND_ACTIVATE_ITEM.getId().getPath(), parent, "layer0", modLoc("item/" + OFFHAND_ACTIVATE_ITEM.getId().getPath()));
        this.singleTexture(CRAFT_ACTIVATE_ITEM.getId().getPath(), parent, "layer0", modLoc("item/" + CRAFT_ACTIVATE_ITEM.getId().getPath()));
    }
}
