package ga.melara.stevesminipouch.datagen;

import ga.melara.stevesminipouch.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;


@Mod.EventBusSubscriber
public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generator) {
        super(generator.getPackOutput());
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.SLOT_ADD1_ITEM.get())
                .define('S', Items.NETHER_STAR)
                .define('C', Items.CHEST)
                .define('X', Items.BREAD)
                .pattern("XSX")
                .pattern("XCX")
                .pattern("XXX")
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.SLOT_ADD9_ITEM.get())
                .define('X', ModRegistry.SLOT_ADD1_ITEM.get())
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .unlockedBy("has_slot_add1", has(ModRegistry.SLOT_ADD1_ITEM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.SLOT_ADD27_ITEM.get())
                .define('X', ModRegistry.SLOT_ADD9_ITEM.get())
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .unlockedBy("has_slot_add1", has(ModRegistry.SLOT_ADD1_ITEM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.SLOT_SUB1_ITEM.get())
                .define('S', Items.NETHER_STAR)
                .define('C', Items.CHEST)
                .define('X', Items.BREAD)
                .pattern("XXX")
                .pattern("XCX")
                .pattern("XSX")
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.SLOT_SUB9_ITEM.get())
                .define('X', ModRegistry.SLOT_SUB1_ITEM.get())
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .unlockedBy("has_slot_sub1", has(ModRegistry.SLOT_SUB1_ITEM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.SLOT_SUB27_ITEM.get())
                .define('X', ModRegistry.SLOT_SUB9_ITEM.get())
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .unlockedBy("has_slot_sub1", has(ModRegistry.SLOT_SUB1_ITEM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.INVENTORY_ACTIVATE_ITEM.get())
                .define('S', Items.WHEAT_SEEDS)
                .define('X', Items.OAK_LOG)
                .pattern("XXX")
                .pattern("XSX")
                .pattern("XXX")
                .unlockedBy("has_oak_log", has(Items.OAK_LOG))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.ARMOR_ACTIVATE_ITEM.get())
                .define('S', Items.DIAMOND)
                .define('X', Items.IRON_BLOCK)
                .define('G', Items.AMETHYST_SHARD)
                .pattern("XSX")
                .pattern("SGS")
                .pattern("XSX")
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.OFFHAND_ACTIVATE_ITEM.get())
                .define('S', Items.STICK)
                .define('X', Items.MOSSY_COBBLESTONE)
                .pattern("XSX")
                .pattern("XSX")
                .pattern("XSX")
                .unlockedBy("has_stick", has(Items.STICK))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModRegistry.CRAFT_ACTIVATE_ITEM.get())
                .define('E', Items.EMERALD_BLOCK)
                .define('X', Items.IRON_INGOT)
                .define('C', Items.CHEST)
                .pattern("XCX")
                .pattern("CEC")
                .pattern("XCX")
                .unlockedBy("has_emerald", has(Items.EMERALD))
                .save(writer);
    }
}
