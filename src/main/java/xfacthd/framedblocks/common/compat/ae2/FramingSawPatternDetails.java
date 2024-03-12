package xfacthd.framedblocks.common.compat.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.*;
import net.minecraft.nbt.*;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.crafting.*;

import java.util.List;
import java.util.Objects;

final class FramingSawPatternDetails implements IPatternDetails
{
    private final AEItemKey definition;
    private final RecipeHolder<FramingSawRecipe> recipe;
    private final IInput[] inputs;
    private final GenericStack[] outputs;

    FramingSawPatternDetails(AEItemKey definition, Level level)
    {
        this.definition = definition;

        CompoundTag tag = Objects.requireNonNull(definition.getTag());
        ItemStack input = FramingSawPatternEncoding.getInput(tag);
        ItemStack result = FramingSawPatternEncoding.getResult(tag);

        this.recipe = Objects.requireNonNull(FramingSawRecipeCache.get(level.isClientSide()).findRecipeFor(result));
        FramingSawRecipe recipe = this.recipe.value();
        SimpleContainer container = new SimpleContainer(input);
        FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(container, level.isClientSide());

        List<FramingSawRecipeAdditive> additives = recipe.getAdditives();
        this.inputs = new IInput[1 + additives.size()];
        this.inputs[0] = new Input(input, calc.getInputCount());
        ItemStack[] loadedAdditives = FramingSawPatternEncoding.getAdditives(tag, additives.size());
        for (int i = 0; i < additives.size(); i++)
        {
            if (!additives.get(i).ingredient().test(loadedAdditives[i]))
            {
                throw new IllegalArgumentException("Invalid additive '%s' in slot '%d' for recipe '%s'".formatted(
                        loadedAdditives[i], i, this.recipe.id()
                ));
            }
            inputs[i + 1] = new Input(loadedAdditives[i], calc.getAdditiveCount(i));
        }
        this.outputs = new GenericStack[] { new GenericStack(AEItemKey.of(result), calc.getOutputCount()) };
    }

    @Override
    public AEItemKey getDefinition()
    {
        return definition;
    }

    @Override
    public IInput[] getInputs()
    {
        return inputs;
    }

    @Override
    public GenericStack[] getOutputs()
    {
        return outputs;
    }

    @Override
    public boolean supportsPushInputsToExternalInventory()
    {
        return false;
    }

    public RecipeHolder<FramingSawRecipe> getRecipe()
    {
        return recipe;
    }

    @Override
    public int hashCode()
    {
        return definition.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj != null && obj.getClass() == getClass() && ((FramingSawPatternDetails) obj).definition.equals(definition);
    }



    private static final class Input implements IInput
    {
        private final GenericStack[] input = new GenericStack[1];
        private final long multiplier;

        public Input(ItemStack input, int multiplier)
        {
            this.input[0] = new GenericStack(AEItemKey.of(input), 1);
            this.multiplier = multiplier;
        }

        @Override
        public GenericStack[] getPossibleInputs()
        {
            return input;
        }

        @Override
        public long getMultiplier()
        {
            return multiplier;
        }

        @Override
        public boolean isValid(AEKey input, Level level)
        {
            return input.matches(this.input[0]);
        }

        @Override
        @Nullable
        public AEKey getRemainingKey(AEKey template)
        {
            return null;
        }
    }
}
