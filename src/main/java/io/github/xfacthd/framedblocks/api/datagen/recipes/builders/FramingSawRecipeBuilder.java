package io.github.xfacthd.framedblocks.api.datagen.recipes.builders;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class FramingSawRecipeBuilder implements RecipeBuilder {
    public static final int MAX_ADDITIVE_COUNT = 3;

    private final ItemStackTemplate result;
    private int material = 0;
    private List<Additive> additives = List.of();
    private boolean disabled = false;

    public FramingSawRecipeBuilder(ItemLike result, int count) {
        this.result = new ItemStackTemplate(result.asItem(), count);
    }

    public static <T extends ItemLike> FramingSawRecipeBuilder builder(Holder<T> result) {
        return builder(result.value());
    }

    public static FramingSawRecipeBuilder builder(ItemLike result) {
        return builder(result, 1);
    }

    public static <T extends ItemLike> FramingSawRecipeBuilder builder(Holder<T> result, int count) {
        return builder(result.value(), count);
    }

    public static FramingSawRecipeBuilder builder(ItemLike result, int count) {
        Preconditions.checkNotNull(result, "Result must be non-null");
        Preconditions.checkArgument(count > 0, "Result count must be greater than 0");
        return new FramingSawRecipeBuilder(result, count);
    }

    public FramingSawRecipeBuilder material(int material) {
        Preconditions.checkArgument(material > 0, "Material value must be greater than 0");
        this.material = material;
        return this;
    }

    public FramingSawRecipeBuilder additive(Additive additive) {
        Preconditions.checkNotNull(additive, "Additive must be non-null");
        this.additives = List.of(additive);
        return this;
    }

    public FramingSawRecipeBuilder additives(List<Additive> additives) {
        Preconditions.checkNotNull(additives, "Additives must be non-null");
        Preconditions.checkArgument(!additives.isEmpty(), "At least one additive must be provided");
        Preconditions.checkArgument(additives.size() <= MAX_ADDITIVE_COUNT, "At most 3 additives may be provided");
        this.additives = additives;
        return this;
    }

    public FramingSawRecipeBuilder disabled() {
        this.disabled = true;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        throw new UnsupportedOperationException("Advancements are not supported");
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        throw new UnsupportedOperationException("Recipe groups are not supported");
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(result);
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> recipeId) {
        Preconditions.checkState(material > 0, "Material value not set");
        Preconditions.checkState(material / result.count() * result.count() == material, "Material value not divisible by result size");

        recipeId = ResourceKey.create(Registries.RECIPE, recipeId.identifier().withPrefix("framing_saw/"));
        Recipe<?> recipe = InternalAPI.INSTANCE.makeFramingSawRecipe(material, additives, result, disabled);
        output.accept(recipeId, recipe, null);
    }

    public record Additive(Ingredient ingredient, int count) {
        public static Additive of(ItemLike item) {
            return of(item, 1);
        }

        public static Additive of(ItemLike item, int count) {
            return of(Ingredient.of(item), count);
        }

        public static Additive of(Ingredient ingredient) {
            return of(ingredient, 1);
        }

        public static Additive of(Ingredient ingredient, int count) {
            return new Additive(ingredient, count);
        }
    }
}
