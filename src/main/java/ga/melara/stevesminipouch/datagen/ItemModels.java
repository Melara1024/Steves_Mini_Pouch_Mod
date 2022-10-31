package ga.melara.stevesminipouch.datagen;

import ga.melara.stevesminipouch.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
        withExistingParent(ModRegistry.SLOT_ADD1_ITEM.getId().getPath(), modLoc("item/sa1"));
        withExistingParent(ModRegistry.SLOT_ADD9_ITEM.getId().getPath(), modLoc("item/sa9"));
        withExistingParent(ModRegistry.SLOT_ADD27_ITEM.getId().getPath(), modLoc("item/sa27"));

        withExistingParent(ModRegistry.SLOT_SHRINK1_ITEM.getId().getPath(), modLoc("item/ss1"));
        withExistingParent(ModRegistry.SLOT_SHRINK9_ITEM.getId().getPath(), modLoc("item/ss9"));
        withExistingParent(ModRegistry.SLOT_SHRINK27_ITEM.getId().getPath(), modLoc("item/ss27"));

        withExistingParent(ModRegistry.INVENTORY_ACTIVATE_ITEM.getId().getPath(), modLoc("item/inv"));
        withExistingParent(ModRegistry.ARMOR_ACTIVATE_ITEM.getId().getPath(), modLoc("item/arm"));
        withExistingParent(ModRegistry.OFFHAND_ACTIVATE_ITEM.getId().getPath(), modLoc("item/off"));
        withExistingParent(ModRegistry.CRAFT_ACTIVATE_ITEM.getId().getPath(), modLoc("item/cft"));
    }

}
