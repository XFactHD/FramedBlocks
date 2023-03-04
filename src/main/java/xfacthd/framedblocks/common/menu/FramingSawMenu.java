package xfacthd.framedblocks.common.menu;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.List;

public class FramingSawMenu extends AbstractContainerMenu
{
    public static final int SLOT_INPUT = 0;
    private static final int SLOT_ADDITIVE = 1;
    private static final int SLOT_RESULT = 2;
    public static final int SLOT_INV_FIRST = 3;
    public static final int INV_SLOT_COUNT = 4 * 9;
    public static final int TOTAL_SLOT_COUNT = SLOT_INV_FIRST + INV_SLOT_COUNT;

    private final Level level;
    private final Slot inputSlot;
    private final Slot additiveSlot;
    private final Slot resultSlot;
    private final ContainerLevelAccess levelAccess;
    private final Container inputContainer = new FrameCrafterContainer(this);
    private final ResultContainer resultContainer = new ResultContainer();
    private final DataSlot selectedRecipeIdx = DataSlot.standalone();
    private final FramingSawRecipeCache cache;
    private final List<RecipeHolder> recipes;
    private ItemStack lastInput = ItemStack.EMPTY;
    private ItemStack lastAdditive = ItemStack.EMPTY;
    private FramingSawRecipe selectedRecipe = null;
    private boolean recipeChanged = false;

    public FramingSawMenu(int containerId, Inventory inv, ContainerLevelAccess levelAccess)
    {
        super(FBContent.menuTypeFramingSaw.get(), containerId);

        this.level = inv.player.level;
        this.levelAccess = levelAccess;
        this.inputSlot = addSlot(new Slot(inputContainer, SLOT_INPUT, 20, 46));
        this.additiveSlot = addSlot(new Slot(inputContainer, SLOT_ADDITIVE, 20, 82));
        this.resultSlot = addSlot(new ResultSlot(this, resultContainer, 0, 223, 64));

        FramedUtils.addPlayerInvSlots(this::addSlot, inv, 48, 151);

        addDataSlot(selectedRecipeIdx);

        this.cache = FramingSawRecipeCache.get(level.isClientSide());
        this.recipes = cache.getRecipes()
                .stream()
                .map(RecipeHolder::new)
                .toList();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            remainder = stack.copy();

            if (index == SLOT_RESULT)
            {
                stack.getItem().onCraftedBy(stack, player.level, player);
                if (!moveItemStackTo(stack, SLOT_INV_FIRST, slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, remainder);
            }
            else if (index < SLOT_INV_FIRST)
            {
                if (!moveItemStackTo(stack, SLOT_INV_FIRST, slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (cache.getMaterialValue(stack.getItem()) > 0)
            {
                if (!moveItemStackTo(stack, SLOT_INPUT))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!moveItemStackTo(stack, SLOT_ADDITIVE))
            {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            if (stack.getCount() == remainder.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
            broadcastChanges();
        }
        return remainder;
    }

    @Override
    public boolean clickMenuButton(Player player, int id)
    {
        if (isValidRecipeIndex(id))
        {
            selectedRecipeIdx.set(id);
            selectedRecipe = recipes.get(id).recipe;
            setupResultSlot();
            recipeChanged = true;
        }
        return true;
    }

    @Override
    public void slotsChanged(Container inventory)
    {
        boolean changed = false;

        ItemStack input = inputSlot.getItem();
        if (!input.is(lastInput.getItem()) || input.getCount() != lastInput.getCount())
        {
            lastInput = input.copy();
            changed = true;
        }

        ItemStack additive = additiveSlot.getItem();
        if (!additive.is(lastAdditive.getItem()) || additive.getCount() != lastAdditive.getCount())
        {
            lastAdditive = additive.copy();
            changed = true;
        }

        if (changed)
        {
            for (RecipeHolder holder : recipes)
            {
                holder.failReason = holder.recipe.matchWithReason(inputContainer, level);
            }
            setupResultSlot();
        }
    }

    private void setupResultSlot()
    {
        if (isValidRecipeIndex(selectedRecipeIdx.get()))
        {
            RecipeHolder holder = recipes.get(selectedRecipeIdx.get());
            if (holder.failReason.success())
            {
                FramingSawRecipe recipe = holder.recipe;
                ItemStack result = recipe.assemble(inputContainer);
                result.setCount(recipe.getResultSize(inputContainer, level));
                resultContainer.setRecipeUsed(recipe);
                resultSlot.set(result);
                selectedRecipe = recipe;

                broadcastChanges();
                return;
            }
        }

        resultSlot.set(ItemStack.EMPTY);
        selectedRecipe = null;
        broadcastChanges();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot)
    {
        return slot.index != SLOT_RESULT && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(this.levelAccess, player, FBContent.blockFramingSaw.get());
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        resultContainer.removeItemNoUpdate(1);
        levelAccess.execute((level, pos) -> clearContainer(player, inputContainer));
    }

    public ItemStack getInputStack()
    {
        return inputSlot.getItem();
    }

    public ItemStack getAdditiveStack()
    {
        return additiveSlot.getItem();
    }

    public List<RecipeHolder> getRecipes()
    {
        return recipes;
    }

    public int getSelectedRecipeIndex()
    {
        return selectedRecipeIdx.get();
    }

    public boolean hasRecipeChanged()
    {
        boolean changed = recipeChanged;
        recipeChanged = false;
        return changed;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean moveItemStackTo(ItemStack stack, int slot)
    {
        return moveItemStackTo(stack, slot, slot + 1, false);
    }

    public boolean isValidRecipeIndex(int idx)
    {
        return idx >= 0 && idx < recipes.size();
    }



    private static class FrameCrafterContainer extends SimpleContainer
    {
        private final FramingSawMenu menu;

        FrameCrafterContainer(FramingSawMenu menu)
        {
            super(2);
            this.menu = menu;
        }

        @Override
        public void setChanged()
        {
            super.setChanged();
            menu.slotsChanged(this);
        }
    }

    private static class ResultSlot extends Slot
    {
        private final FramingSawMenu menu;

        ResultSlot(FramingSawMenu menu, Container container, int index, int x, int y)
        {
            super(container, index, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack)
        {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack)
        {
            stack.onCraftedBy(player.level, player, stack.getCount());
            menu.resultContainer.awardUsedRecipes(player);

            IntIntPair inAddCount = menu.selectedRecipe.getInputAndAdditiveCount(menu.inputContainer, menu.level);
            int inputCount = inAddCount.leftInt();
            int additiveCount = inAddCount.rightInt();

            menu.inputSlot.remove(inputCount);
            if (additiveCount > 0)
            {
                menu.additiveSlot.remove(additiveCount);
            }

            super.onTake(player, stack);
        }
    }

    public static class RecipeHolder
    {
        private final FramingSawRecipe recipe;
        private FramingSawRecipe.FailReason failReason = FramingSawRecipe.FailReason.MATERIAL_VALUE;

        private RecipeHolder(FramingSawRecipe recipe)
        {
            this.recipe = recipe;
        }

        public FramingSawRecipe getRecipe()
        {
            return recipe;
        }

        public FramingSawRecipe.FailReason getFailReason()
        {
            return failReason;
        }
    }
}
