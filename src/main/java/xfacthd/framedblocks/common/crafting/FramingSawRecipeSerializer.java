package xfacthd.framedblocks.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;

public final class FramingSawRecipeSerializer implements RecipeSerializer<FramingSawRecipe>
{
    private static final Codec<FramingSawRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("material").forGetter(FramingSawRecipe::getMaterialAmount),
            FramingSawRecipeAdditive.CODEC.listOf().optionalFieldOf("additives").flatXmap(
                    FramingSawRecipeSerializer::verifyAndMapAdditivesDecode,
                    FramingSawRecipeSerializer::verifyAndMapAdditivesEncode
            ).forGetter(FramingSawRecipe::getAdditives),
            CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(FramingSawRecipe::getResult),
            Codec.BOOL.optionalFieldOf("disabled").xmap(
                    opt -> opt.orElse(false), flag -> flag ? Optional.of(true) : Optional.empty()
            ).forGetter(FramingSawRecipe::isDisabled)
    ).apply(inst, FramingSawRecipe::new));

    @Override
    public Codec<FramingSawRecipe> codec()
    {
        return CODEC;
    }

    @Override
    public FramingSawRecipe fromNetwork(FriendlyByteBuf buffer)
    {
        int material = buffer.readInt();

        int count = buffer.readInt();
        List<FramingSawRecipeAdditive> additives = new ArrayList<>(count);
        for (int i = 0; i < count; i++)
        {
            Ingredient additive = Ingredient.fromNetwork(buffer);
            int additiveCount = buffer.readInt();
            additives.add(new FramingSawRecipeAdditive(additive, additiveCount));
        }

        ItemStack result = buffer.readItem();
        boolean disabled = buffer.readBoolean();

        return new FramingSawRecipe(material, additives, result, disabled);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, FramingSawRecipe recipe)
    {
        buffer.writeInt(recipe.getMaterialAmount());

        List<FramingSawRecipeAdditive> additives = recipe.getAdditives();
        buffer.writeInt(additives.size());
        for (FramingSawRecipeAdditive additive : additives)
        {
            additive.ingredient().toNetwork(buffer);
            buffer.writeInt(additive.count());
        }

        buffer.writeItem(recipe.getResult());
        buffer.writeBoolean(recipe.isDisabled());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static DataResult<List<FramingSawRecipeAdditive>> verifyAndMapAdditivesDecode(
            Optional<List<FramingSawRecipeAdditive>> additives
    )
    {
        if (additives.isPresent())
        {
            List<FramingSawRecipeAdditive> list = additives.get();
            if (list.size() > FramingSawRecipe.MAX_ADDITIVE_COUNT)
            {
                int count = list.size();
                return DataResult.error(() ->
                        "More than " + FramingSawRecipe.MAX_ADDITIVE_COUNT + " additives are not supported, found " + count
                );
            }
            return DataResult.success(list);
        }
        return DataResult.success(List.of());
    }

    private static DataResult<Optional<List<FramingSawRecipeAdditive>>> verifyAndMapAdditivesEncode(
            List<FramingSawRecipeAdditive> additives
    )
    {
        if (additives.isEmpty())
        {
            return DataResult.success(Optional.empty());
        }
        if (additives.size() > FramingSawRecipe.MAX_ADDITIVE_COUNT)
        {
            int count = additives.size();
            return DataResult.error(() ->
                    "More than " + FramingSawRecipe.MAX_ADDITIVE_COUNT + " additives are not supported, found " + count
            );
        }
        return DataResult.success(Optional.of(additives));
    }
}
