package io.github.xfacthd.framedblocks.api.datagen.recipes.builders;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import net.minecraft.advancements.Criterion;
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

/// Builder for Framing Saw recipes.
public final class FramingSawRecipeBuilder implements RecipeBuilder {
    public static final int MAX_ADDITIVE_COUNT = 3;

    private final ItemStackTemplate result;
    private int material = 0;
    private List<Additive> additives = List.of();
    private boolean disabled = false;

    /// @param result The item to craft
    /// @param count  The amount of the item to craft
    public FramingSawRecipeBuilder(ItemLike result, int count) {
        this.result = new ItemStackTemplate(result.asItem(), count);
    }

    /// {@return a builder for a Framing Saw recipe outputting one of the given item}
    ///
    /// @param result The item to craft
    public static <T extends ItemLike> FramingSawRecipeBuilder builder(Holder<T> result) {
        return builder(result.value());
    }

    /// {@return a builder for a Framing Saw recipe outputting one of the given item}
    ///
    /// @param result The item to craft
    public static FramingSawRecipeBuilder builder(ItemLike result) {
        return builder(result, 1);
    }

    /// {@return a builder for a Framing Saw recipe outputting the given amount of the given item}
    ///
    /// @param result The item to craft
    /// @param count  The amount of the item to craft
    public static <T extends ItemLike> FramingSawRecipeBuilder builder(Holder<T> result, int count) {
        return builder(result.value(), count);
    }

    /// {@return a builder for a Framing Saw recipe outputting the given amount of the given item}
    ///
    /// @param result The item to craft
    /// @param count  The amount of the item to craft
    public static FramingSawRecipeBuilder builder(ItemLike result, int count) {
        Preconditions.checkNotNull(result, "Result must be non-null");
        Preconditions.checkArgument(count > 0, "Result count must be greater than 0");
        return new FramingSawRecipeBuilder(result, count);
    }

    /// Specify the amount of material this recipe consumes.
    ///
    /// @param material The amount of material to consume
    /// @return this builder
    public FramingSawRecipeBuilder material(int material) {
        Preconditions.checkArgument(material > 0, "Material value must be greater than 0");
        this.material = material;
        return this;
    }

    /// Specify an additive consumed by this recipe.
    ///
    /// @param additive The additive to consume
    /// @return this builder
    public FramingSawRecipeBuilder additive(Additive additive) {
        Preconditions.checkNotNull(additive, "Additive must be non-null");
        this.additives = List.of(additive);
        return this;
    }

    /// Specify additives consumed by this recipe.
    ///
    /// @param additives The additives to consume
    /// @return this builder
    public FramingSawRecipeBuilder additives(List<Additive> additives) {
        Preconditions.checkNotNull(additives, "Additives must be non-null");
        Preconditions.checkArgument(!additives.isEmpty(), "At least one additive must be provided");
        Preconditions.checkArgument(additives.size() <= MAX_ADDITIVE_COUNT, "At most 3 additives may be provided");
        this.additives = additives;
        return this;
    }

    /// Mark this recipe as disabled. The item crafted by this recipe can still be converted to other items
    /// based on the specified material value but can itself not be crafted.
    ///
    /// @return this builder
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

    /// Describes an additive to be consumed in addition to the "material".
    ///
    /// @param ingredient The ingredient to consume
    /// @param count      The amount of the ingredient to consume
    public record Additive(Ingredient ingredient, int count) {
        /// {@return an additive of one of the given item}
        public static Additive of(ItemLike item) {
            return of(item, 1);
        }

        /// {@return an additive of the given amount of the given item}
        public static Additive of(ItemLike item, int count) {
            return of(Ingredient.of(item), count);
        }

        /// {@return an additive of one of the given ingredient}
        public static Additive of(Ingredient ingredient) {
            return of(ingredient, 1);
        }

        /// {@return an additive of the given amount of the given ingredient}
        public static Additive of(Ingredient ingredient, int count) {
            return new Additive(ingredient, count);
        }
    }
}
