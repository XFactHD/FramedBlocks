package xfacthd.framedblocks.common.compat.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsTooltip;
import appeng.api.stacks.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.crafting.*;

import java.util.*;

final class FramingSawPatternDetails implements IPatternDetails
{
    private final AEItemKey definition;
    private final RecipeHolder<FramingSawRecipe> recipe;
    private final IInput[] inputs;
    private final List<GenericStack> outputs;

    FramingSawPatternDetails(AEItemKey definition, Level level)
    {
        this.definition = definition;

        EncodedFramingSawPattern pattern = definition.get(AppliedEnergisticsCompat.GuardedAccess.DC_TYPE_ENCODED_SAW_PATTERN.get());
        if (pattern == null)
        {
            throw new IllegalArgumentException("Given item does not encode a processing pattern: " + definition);
        }

        this.recipe = Objects.requireNonNull(FramingSawRecipeCache.get(level.isClientSide()).findRecipeFor(pattern.output()));
        FramingSawRecipe recipe = this.recipe.value();
        RecipeInput container = new SingleRecipeInput(pattern.input());
        FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(container, level.isClientSide());

        List<FramingSawRecipeAdditive> additives = recipe.getAdditives();
        this.inputs = new IInput[1 + additives.size()];
        this.inputs[0] = new Input(pattern.input(), calc.getInputCount());
        List<ItemStack> loadedAdditives = pattern.additives();
        if (additives.size() != loadedAdditives.size())
        {
            throw new IllegalArgumentException("Additive count does not match. Pattern: %d Recipe: %d".formatted(
                    loadedAdditives.size(), additives.size()
            ));
        }
        for (int i = 0; i < additives.size(); i++)
        {
            if (!additives.get(i).ingredient().test(loadedAdditives.get(i)))
            {
                throw new IllegalArgumentException("Invalid additive '%s' in slot '%d' for recipe '%s'".formatted(
                        loadedAdditives.get(i), i, this.recipe.id()
                ));
            }
            inputs[i + 1] = new Input(loadedAdditives.get(i), calc.getAdditiveCount(i));
        }
        this.outputs = List.of(new GenericStack(
                Objects.requireNonNull(AEItemKey.of(pattern.output())),
                calc.getOutputCount()
        ));
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
    public List<GenericStack> getOutputs()
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



    public static void encode(ItemStack stack, ItemStack input, ItemStack[] additives, ItemStack output)
    {
        stack.set(AppliedEnergisticsCompat.GuardedAccess.DC_TYPE_ENCODED_SAW_PATTERN, new EncodedFramingSawPattern(
                input, Arrays.stream(additives).filter(additive -> !additive.isEmpty()).toList(), output
        ));
    }

    public static PatternDetailsTooltip makeInvalidPatternTooltip(
            ItemStack stack, Level level, @SuppressWarnings("unused") @Nullable Exception cause, TooltipFlag flags
    )
    {
        PatternDetailsTooltip tooltip = new PatternDetailsTooltip(PatternDetailsTooltip.OUTPUT_TEXT_PRODUCES);
        EncodedFramingSawPattern pattern = stack.get(AppliedEnergisticsCompat.GuardedAccess.DC_TYPE_ENCODED_SAW_PATTERN);
        if (pattern != null)
        {
            tooltip.addInput(AEItemKey.of(pattern.input()), 1L);
            pattern.additives().forEach(additive ->
            {
                if (!additive.isEmpty())
                {
                    tooltip.addInput(AEItemKey.of(additive), additive.getCount());
                }
            });
            tooltip.addOutput(AEItemKey.of(pattern.output()), 1L);

            if (flags.isAdvanced() && !pattern.output().isEmpty())
            {
                RecipeHolder<FramingSawRecipe> recipe = FramingSawRecipeCache.get(level.isClientSide())
                        .findRecipeFor(pattern.output());
                if (recipe != null)
                {
                    tooltip.addProperty(Component.literal("Recipe"), Component.literal(recipe.id().toString()));
                }
            }
        }
        return tooltip;
    }



    private static final class Input implements IInput
    {
        private final GenericStack[] input = new GenericStack[1];
        private final long multiplier;

        public Input(ItemStack input, int multiplier)
        {
            this.input[0] = new GenericStack(Objects.requireNonNull(AEItemKey.of(input)), 1);
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
