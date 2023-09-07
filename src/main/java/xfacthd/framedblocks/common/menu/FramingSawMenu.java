package xfacthd.framedblocks.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.*;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.Arrays;
import java.util.List;

public class FramingSawMenu extends AbstractContainerMenu implements IFramingSawMenu
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_ADDITIVE_FIRST = SLOT_INPUT + 1;
    public static final int SLOT_RESULT = SLOT_ADDITIVE_FIRST + FramingSawRecipe.MAX_ADDITIVE_COUNT;
    public static final int SLOT_INV_FIRST = SLOT_RESULT + 1;
    public static final int INV_SLOT_COUNT = 4 * 9;
    public static final int TOTAL_SLOT_COUNT = SLOT_INV_FIRST + INV_SLOT_COUNT;

    private final Level level;
    private final Slot inputSlot;
    private final Slot[] additiveSlots;
    private final Slot resultSlot;
    private final ContainerLevelAccess levelAccess;
    private final Container inputContainer = new FrameCrafterContainer(this);
    private final ResultContainer resultContainer = new ResultContainer();
    private final DataSlot selectedRecipeIdx = DataSlot.standalone();
    private final FramingSawRecipeCache cache;
    private final List<RecipeHolder> recipes;
    private final ItemStack[] lastAdditives;
    private ItemStack lastInput = ItemStack.EMPTY;
    private FramingSawRecipe selectedRecipe = null;
    private boolean recipeChanged = false;

    public FramingSawMenu(int containerId, Inventory inv, ContainerLevelAccess levelAccess)
    {
        super(FBContent.MENU_TYPE_FRAMING_SAW.get(), containerId);

        this.level = inv.player.level();
        this.levelAccess = levelAccess;
        this.inputSlot = addSlot(new Slot(inputContainer, SLOT_INPUT, 20, 28));
        this.additiveSlots = new Slot[FramingSawRecipe.MAX_ADDITIVE_COUNT];
        for (int i = 0; i < additiveSlots.length; i++)
        {
            int y = 64 + (i * 18);
            additiveSlots[i] = addSlot(new Slot(inputContainer, SLOT_ADDITIVE_FIRST + i, 20, y));
        }
        this.resultSlot = addSlot(new ResultSlot(this, resultContainer, 0, 223, 64));

        this.lastAdditives = new ItemStack[FramingSawRecipe.MAX_ADDITIVE_COUNT];
        Arrays.fill(lastAdditives, ItemStack.EMPTY);

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
                stack.getItem().onCraftedBy(stack, player.level(), player);
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
                if (!moveItemStackTo(stack, SLOT_INPUT, SLOT_INPUT + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!moveItemStackTo(stack, SLOT_ADDITIVE_FIRST, SLOT_ADDITIVE_FIRST + FramingSawRecipe.MAX_ADDITIVE_COUNT, false))
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

        for (int i = 0; i < additiveSlots.length; i++)
        {
            ItemStack additive = additiveSlots[i].getItem();
            if (!additive.is(lastAdditives[i].getItem()) || additive.getCount() != lastAdditives[i].getCount())
            {
                lastAdditives[i] = additive.copy();
                changed = true;
            }
        }

        if (changed)
        {
            for (RecipeHolder holder : recipes)
            {
                holder.matchResult = holder.recipe.matchWithResult(inputContainer, level);
            }
            setupResultSlot();
        }
    }

    private void setupResultSlot()
    {
        if (isValidRecipeIndex(selectedRecipeIdx.get()))
        {
            RecipeHolder holder = recipes.get(selectedRecipeIdx.get());
            if (holder.matchResult.success())
            {
                FramingSawRecipe recipe = holder.recipe;
                FramingSawRecipeCalculation calc = recipe.makeCraftingCalculation(
                        inputContainer, level.isClientSide()
                );

                ItemStack result = recipe.assemble(inputContainer, level.registryAccess());
                result.setCount(calc.getOutputCount());
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
        return stillValid(this.levelAccess, player, FBContent.BLOCK_FRAMING_SAW.get());
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        resultContainer.removeItemNoUpdate(1);
        levelAccess.execute((level, pos) -> clearContainer(player, inputContainer));
    }

    @Override
    public Container getInputContainer()
    {
        return inputContainer;
    }

    @Override
    public ItemStack getInputStack()
    {
        return inputSlot.getItem();
    }

    @Override
    public ItemStack getAdditiveStack(int slot)
    {
        return additiveSlots[slot].getItem();
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

    @Override
    public boolean isValidRecipeIndex(int idx)
    {
        return idx >= 0 && idx < recipes.size();
    }



    private static class FrameCrafterContainer extends SimpleContainer
    {
        private final FramingSawMenu menu;

        FrameCrafterContainer(FramingSawMenu menu)
        {
            super(FramingSawRecipe.MAX_ADDITIVE_COUNT + 1);
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
            stack.onCraftedBy(player.level(), player, stack.getCount());
            menu.resultContainer.awardUsedRecipes(player, List.of(
                    menu.inputSlot.getItem(),
                    menu.additiveSlots[0].getItem(),
                    menu.additiveSlots[1].getItem(),
                    menu.additiveSlots[2].getItem()
            ));

            FramingSawRecipeCalculation calc = menu.selectedRecipe.makeCraftingCalculation(
                    menu.inputContainer, menu.level.isClientSide()
            );
            int additiveCount = menu.selectedRecipe.getAdditives().size();

            menu.inputSlot.remove(calc.getInputCount());
            for (int i = 0; i < additiveCount; i++)
            {
                menu.additiveSlots[i].remove(calc.getAdditiveCount(i));
            }

            super.onTake(player, stack);
        }
    }

    public static class RecipeHolder
    {
        private final FramingSawRecipe recipe;
        private FramingSawRecipeMatchResult matchResult = FramingSawRecipeMatchResult.MATERIAL_VALUE;

        private RecipeHolder(FramingSawRecipe recipe)
        {
            this.recipe = recipe;
        }

        public FramingSawRecipe getRecipe()
        {
            return recipe;
        }

        public FramingSawRecipeMatchResult getMatchResult()
        {
            return matchResult;
        }
    }
}
