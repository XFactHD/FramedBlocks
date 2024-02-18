package xfacthd.framedblocks.common.datagen.builders.recipe;

import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

public interface AutoUnlockNameBuilder<T extends RecipeBuilder> extends RecipeBuilder
{
    @SuppressWarnings("unchecked")
    default T unlockedBy(RegistryObject<? extends ItemLike> triggerItem)
    {
        String name = buildCriterionName(triggerItem.getId());
        return (T) unlockedBy(name, RecipeProvider.has(triggerItem.get()));
    }

    @SuppressWarnings("unchecked")
    default T unlockedBy(TagKey<Item> triggerTag)
    {
        String name = buildCriterionName(triggerTag.location());
        return (T) unlockedBy(name, RecipeProvider.has(triggerTag));
    }

    private static String buildCriterionName(ResourceLocation triggerName)
    {
        StringBuilder name = new StringBuilder("has");
        for (String part : triggerName.getPath().split("_"))
        {
            name.append(StringUtils.capitalize(part));
        }
        return name.toString();
    }
}
