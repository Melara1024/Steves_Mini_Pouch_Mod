package ga.melara.stevesminipouch.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static ga.melara.stevesminipouch.ModRegistry.*;
import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation parent = mcLoc("item/generated");

        ResourceLocation cube = modLoc("item/cube");
        ResourceLocation oriented_cube = modLoc("item/oriented_cube");
        ResourceLocation backpack = modLoc("item/backpack");
        ResourceLocation anvil = modLoc("item/anvil");
        ResourceLocation arm = modLoc("item/arm");

        this.singleTexture(SLOT_ADD1_ITEM.getId().getPath(), oriented_cube, "layer", modLoc("item/" + SLOT_ADD1_ITEM.getId().getPath()));
        this.singleTexture(SLOT_ADD9_ITEM.getId().getPath(), oriented_cube, "layer", modLoc("item/" + SLOT_ADD9_ITEM.getId().getPath()));
        this.singleTexture(SLOT_ADD27_ITEM.getId().getPath(), oriented_cube, "layer", modLoc("item/" + SLOT_ADD27_ITEM.getId().getPath()));

        this.singleTexture(SLOT_SUB1_ITEM.getId().getPath(), cube, "layer", modLoc("item/" + SLOT_SUB1_ITEM.getId().getPath()));
        this.singleTexture(SLOT_SUB9_ITEM.getId().getPath(), cube, "layer", modLoc("item/" + SLOT_SUB9_ITEM.getId().getPath()));
        this.singleTexture(SLOT_SUB27_ITEM.getId().getPath(), cube, "layer", modLoc("item/" + SLOT_SUB27_ITEM.getId().getPath()));

        this.singleTexture(INVENTORY_ACTIVATE_ITEM.getId().getPath(), backpack, "0", modLoc("item/backpack"));
        this.singleTexture(ARMOR_ACTIVATE_ITEM.getId().getPath(), anvil, "layer0", modLoc("item/" + ARMOR_ACTIVATE_ITEM.getId().getPath()));
        this.singleTexture(OFFHAND_ACTIVATE_ITEM.getId().getPath(), arm, "layer0", modLoc("item/" + OFFHAND_ACTIVATE_ITEM.getId().getPath()));
        this.singleTexture(CRAFT_ACTIVATE_ITEM.getId().getPath(), oriented_cube, "layer", modLoc("item/" + CRAFT_ACTIVATE_ITEM.getId().getPath()));
    }
}