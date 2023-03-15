package xfacthd.framedblocks.common.crafting;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.MathUtils;

import java.util.Locale;

public final class FramingSawRecipe implements Recipe<Container>
{
    public static final int CUBE_MATERIAL_VALUE = 6144; // Empirically determined value
    private static final Lazy<ItemStack> TOAST_ICON = Lazy.of(() -> new ItemStack(FBContent.blockFramingSaw.get()));

    private final ResourceLocation id;
    private final int materialAmount;
    private final boolean hasAdditive;
    private final Ingredient additive;
    private final int additiveCount;
    private final ItemStack result;
    private final IBlockType resultType;
    private final boolean disabled;

    FramingSawRecipe(ResourceLocation id, int materialAmount, @Nullable Ingredient additive, int additiveCount, ItemStack result, IBlockType resultType, boolean disabled)
    {
        this.id = id;
        this.materialAmount = materialAmount;
        this.hasAdditive = additive != null;
        this.additive = additive;
        this.additiveCount = additiveCount;
        this.result = result;
        this.resultType = resultType;
        this.disabled = disabled;
    }

    @Override
    public boolean matches(Container container, Level level)
    {
        return matchWithReason(container, level).success();
    }

    public FailReason matchWithReason(Container container, Level level)
    {
        ItemStack input = container.getItem(0);
        if (input.isEmpty())
        {
            return FailReason.MATERIAL_VALUE;
        }

        int inputValue = FramingSawRecipeCache.get(level.isClientSide()).getMaterialValue(input.getItem());
        int totalInputValue = inputValue * input.getCount();
        if (totalInputValue < materialAmount)
        {
            return FailReason.MATERIAL_VALUE;
        }

        long matLcm = MathUtils.lcm(inputValue, materialAmount);
        if (matLcm > totalInputValue)
        {
            return FailReason.MATERIAL_LCM;
        }

        ItemStack addStack = container.getItem(1);
        if (!hasAdditive && !addStack.isEmpty())
        {
            return FailReason.UNEXPECTED_ADDITIVE;
        }
        if (hasAdditive && addStack.isEmpty())
        {
            return FailReason.MISSING_ADDITIVE;
        }
        //noinspection ConstantConditions
        if (hasAdditive && !additive.test(addStack))
        {
            return FailReason.INCORRECT_ADDITIVE;
        }

        int addCount = additiveCount * (int) (matLcm / materialAmount);
        if (addStack.getCount() < addCount)
        {
            return FailReason.INSUFFICIENT_ADDITIVE;
        }
        return FailReason.NONE;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) { return result.copy(); }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    public int getMaterialAmount() { return materialAmount; }

    @Nullable
    public Ingredient getAdditive() { return additive; }

    public int getAdditiveCount() { return additive != null ? additiveCount : 0; }

    public ItemStack getResult() { return result; }

    @Override
    public ItemStack getResultItem(RegistryAccess access) { return result; }

    public int getResultSize(Container container, Level level)
    {
        ItemStack input = container.getItem(0);
        int inputValue = FramingSawRecipeCache.get(level.isClientSide()).getMaterialValue(input.getItem());
        long lcm = MathUtils.lcm(inputValue, materialAmount);
        return (int) (lcm / materialAmount) * result.getCount();
    }

    public IntIntPair getInputOutputCount(Item input, boolean client)
    {
        int inputValue = FramingSawRecipeCache.get(client).getMaterialValue(input);
        long lcm = MathUtils.lcm(inputValue, materialAmount);
        return IntIntPair.of(
                (int) (lcm / inputValue),
                (int) (lcm / materialAmount) * result.getCount()
        );
    }

    public IntIntPair getInputAndAdditiveCount(Container container, Level level)
    {
        return getInputAndAdditiveCount(container.getItem(0), level.isClientSide());
    }

    public IntIntPair getInputAndAdditiveCount(ItemStack input, boolean client)
    {
        int inputValue = FramingSawRecipeCache.get(client).getMaterialValue(input.getItem());
        long lcm = MathUtils.lcm(inputValue, materialAmount);
        return IntIntPair.of(
                (int) (lcm / inputValue),
                hasAdditive ? ((int) (lcm / materialAmount) * additiveCount) : 0
        );
    }

    public IBlockType getResultType() { return resultType; }

    public boolean isDisabled() { return disabled; }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public boolean isSpecial() { return true; }

    @Override
    public ItemStack getToastSymbol() { return TOAST_ICON.get(); }

    @Override
    public RecipeSerializer<?> getSerializer() { return FBContent.recipeSerializerFramingSawRecipe.get(); }

    @Override
    public RecipeType<?> getType() { return FBContent.recipeTypeFramingSawRecipe.get(); }



    public enum FailReason
    {
        NONE(true),
        MATERIAL_VALUE(false),
        MATERIAL_LCM(false),
        MISSING_ADDITIVE(false),
        UNEXPECTED_ADDITIVE(false),
        INCORRECT_ADDITIVE(false),
        INSUFFICIENT_ADDITIVE(false);

        private final boolean success;
        private final Component translation;

        FailReason(boolean success)
        {
            this.success = success;
            this.translation = Utils.translate(
                    "msg", "frame_crafter.fail." + toString().toLowerCase(Locale.ROOT)
            ).withStyle(success ? ChatFormatting.GREEN : ChatFormatting.RED);
        }

        public boolean success() { return success; }

        public Component translation() { return translation; }
    }
}
