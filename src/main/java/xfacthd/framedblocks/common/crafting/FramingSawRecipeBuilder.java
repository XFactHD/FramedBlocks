package xfacthd.framedblocks.common.crafting;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;
import java.util.function.Consumer;

public final class FramingSawRecipeBuilder implements RecipeBuilder
{
    private final Item result;
    private final int count;
    private int material = 0;
    private List<FramingSawRecipeAdditive> additives = null;
    private boolean disabled = false;

    private FramingSawRecipeBuilder(ItemLike result, int count)
    {
        this.result = result.asItem();
        this.count = count;
    }

    public static <T extends ItemLike> FramingSawRecipeBuilder builder(RegistryObject<T> result)
    {
        return builder(result.get());
    }

    public static FramingSawRecipeBuilder builder(ItemLike result)
    {
        return builder(result, 1);
    }

    public static <T extends ItemLike> FramingSawRecipeBuilder builder(RegistryObject<T> result, int count)
    {
        return builder(result.get(), count);
    }

    public static FramingSawRecipeBuilder builder(ItemLike result, int count)
    {
        Preconditions.checkNotNull(result, "Result must be non-null");
        Preconditions.checkArgument(count > 0, "Result count must be greater than 0");
        return new FramingSawRecipeBuilder(result, count);
    }

    public FramingSawRecipeBuilder material(int material)
    {
        Preconditions.checkArgument(material > 0, "Material value must be greater than 0");
        this.material = material;
        return this;
    }

    public FramingSawRecipeBuilder additive(FramingSawRecipeAdditive additive)
    {
        this.additives = additive != null ? List.of(additive) : null;
        return this;
    }

    public FramingSawRecipeBuilder additives(List<FramingSawRecipeAdditive> additives)
    {
        this.additives = additives;
        return this;
    }

    public FramingSawRecipeBuilder disabled()
    {
        this.disabled = true;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger)
    {
        throw new UnsupportedOperationException("Advancements are not supported");
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName)
    {
        throw new UnsupportedOperationException("Recipe groups are not supported");
    }

    @Override
    public Item getResult()
    {
        return result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId)
    {
        Preconditions.checkState(material > 0, "Material value not set");
        Preconditions.checkState(material / count * count == material, "Material value not divisible by result size");

        recipeId = recipeId.withPrefix("framing_saw/");
        finishedRecipeConsumer.accept(new Result(recipeId, result, count, material, additives, disabled));
    }



    private record Result(
            ResourceLocation id,
            Item result,
            int count,
            int material,
            List<FramingSawRecipeAdditive> additives,
            boolean disabled
    ) implements FinishedRecipe
    {
        @Override
        public void serializeRecipeData(JsonObject json)
        {
            json.addProperty("material", material);

            if (additives != null)
            {
                JsonArray additiveArr = new JsonArray();
                additives.forEach(add ->
                {
                    JsonObject additive = new JsonObject();
                    additive.add("ingredient", add.ingredient().toJson());
                    additive.addProperty("count", add.count());
                    additiveArr.add(additive);
                });
                json.add("additives", additiveArr);
            }

            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result)).toString());
            if (count > 1)
            {
                resultObj.addProperty("count", count);
            }
            json.add("result", resultObj);

            if (disabled)
            {
                json.addProperty("disabled", true);
            }
        }

        @Override
        public JsonObject serializeAdvancement()
        {
            return null;
        }

        @Override
        public ResourceLocation getId()
        {
            return id;
        }

        @Override
        public ResourceLocation getAdvancementId()
        {
            return null;
        }

        @Override
        public RecipeSerializer<?> getType()
        {
            return FBContent.RECIPE_SERIALIZER_FRAMING_SAW_RECIPE.get();
        }
    }
}
