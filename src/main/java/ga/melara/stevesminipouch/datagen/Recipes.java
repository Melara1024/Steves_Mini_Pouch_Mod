package ga.melara.stevesminipouch.datagen;

import ga.melara.stevesminipouch.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(ModRegistry.SLOT_ADD1_ITEM.get())
                .group(MODID)
                .define('X', Items.DIAMOND)
                .pattern("XXX")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy("has_diamond", has(Items.DIAMOND)).save(consumer);
    }
}
