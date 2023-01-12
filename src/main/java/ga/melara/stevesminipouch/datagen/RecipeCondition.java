package ga.melara.stevesminipouch.datagen;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("forge", "item_exists");
    private final ResourceLocation item;

    public RecipeCondition(String location)
    {
        this(new ResourceLocation(location));
    }

    public RecipeCondition(String namespace, String path)
    {
        this(new ResourceLocation(namespace, path));
    }

    public RecipeCondition(ResourceLocation item)
    {
        this.item = item;
    }

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext context)
    {
        return ForgeRegistries.ITEMS.containsKey(item);
    }

    @Override
    public String toString()
    {
        return "item_exists(\"" + item + "\")";
    }

    public static class Serializer implements IConditionSerializer<RecipeCondition>
    {
        public static final RecipeCondition.Serializer INSTANCE = new RecipeCondition.Serializer();

        @Override
        public void write(JsonObject json, RecipeCondition value)
        {
            json.addProperty("item", value.item.toString());
        }

        @Override
        public RecipeCondition read(JsonObject json)
        {
            return new RecipeCondition(new ResourceLocation(GsonHelper.getAsString(json, "item")));
        }

        @Override
        public ResourceLocation getID()
        {
            return RecipeCondition.NAME;
        }
    }
}
