package xfacthd.framedblocks.common.menu;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.*;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.PoweredFramingSawBlockEntity;
import xfacthd.framedblocks.common.crafting.*;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.List;
import java.util.Objects;

public class PoweredFramingSawMenu extends AbstractContainerMenu implements IFramingSawMenu
{
    private final PoweredFramingSawBlockEntity blockEntity;
    private final Container inputContainer;
    private final DataSlot progressSlot;
    private final DataSlot recipeIdxSlot;
    private final DataSlot recipeStatusSlot;
    private final DataSlot energySlot;
    private final FramingSawRecipeCache cache;
    @Nullable
    private RecipeHolder<FramingSawRecipe> lastRecipe = null;

    public PoweredFramingSawMenu(int windowId, Inventory inv, FriendlyByteBuf buf)
    {
        this(windowId, inv, inv.player.level(), buf.readBlockPos());
    }

    public PoweredFramingSawMenu(int windowId, Inventory inv, Level level, BlockPos pos)
    {
        super(FBContent.MENU_TYPE_POWERED_FRAMING_SAW.value(), windowId);

        BlockEntity be = level.getBlockEntity(pos);
        Preconditions.checkState(be instanceof PoweredFramingSawBlockEntity);
        this.blockEntity = (PoweredFramingSawBlockEntity) be;

        this.cache = FramingSawRecipeCache.get(level.isClientSide());
        this.progressSlot = addDataSlot(DataSlot.standalone());
        this.recipeIdxSlot = addDataSlot(!level.isClientSide() ? DataSlot.standalone() : new RecipeIndexDataSlot());
        this.recipeStatusSlot = addDataSlot(DataSlot.standalone());
        this.energySlot = addDataSlot(DataSlot.standalone());

        IItemHandlerModifiable itemHandler = FramedUtils.makeMenuItemHandler(blockEntity.getItemHandler(), level);
        this.inputContainer = new RecipeWrapper(itemHandler);
        for (int i = 0; i <= FramingSawMenu.SLOT_RESULT; i++)
        {
            int x = switch (i)
            {
                case FramingSawMenu.SLOT_INPUT -> 34;
                case FramingSawMenu.SLOT_RESULT -> 148;
                default -> 38 + i * 18;
            };
            if (i >= FramingSawMenu.SLOT_ADDITIVE_FIRST && i < FramingSawMenu.SLOT_RESULT)
            {
                addSlot(new AdditiveSlot(itemHandler, i, x, 46));
            }
            else
            {
                addSlot(new SlotItemHandler(itemHandler, i, x, 46));
            }
        }
        FramedUtils.addPlayerInvSlots(this::addSlot, inv, 8, 100);

        recipeIdxSlot.set(-1);
    }

    @Override
    public void broadcastChanges()
    {
        progressSlot.set(blockEntity.getProgress());
        RecipeHolder<FramingSawRecipe> recipe = blockEntity.getSelectedRecipe();
        if (!Objects.equals(lastRecipe, recipe))
        {
            recipeIdxSlot.set(recipe == null ? -1 : cache.getRecipes().indexOf(recipe));
            handleRecipeChange(recipe);
        }
        FramingSawRecipeMatchResult matchResult = blockEntity.getMatchResult();
        recipeStatusSlot.set(matchResult == null ? -1 : matchResult.ordinal());
        energySlot.set(blockEntity.getEnergy());

        super.broadcastChanges();
    }

    private void handleRecipeChange(RecipeHolder<FramingSawRecipe> recipe)
    {
        lastRecipe = recipe;
        int additiveCount = recipe != null ? recipe.value().getAdditives().size() : FramingSawRecipe.MAX_ADDITIVE_COUNT;
        for (int i = 0; i < FramingSawRecipe.MAX_ADDITIVE_COUNT; i++)
        {
            AdditiveSlot slot = (AdditiveSlot) getSlot(FramingSawMenu.SLOT_ADDITIVE_FIRST + i);
            slot.active = i < additiveCount;
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id)
    {
        if (id == -1)
        {
            blockEntity.selectRecipe(null);
            return true;
        }

        List<RecipeHolder<FramingSawRecipe>> recipes = cache.getRecipes();
        if (id >= 0 && id < recipes.size())
        {
            blockEntity.selectRecipe(recipes.get(id));
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public RecipeHolder<FramingSawRecipe> getSelectedRecipe()
    {
        return lastRecipe;
    }

    public int getProgress()
    {
        return progressSlot.get();
    }

    public FramingSawRecipeMatchResult getMatchResult()
    {
        int result = recipeStatusSlot.get();
        return result == -1 ? null : FramingSawRecipeMatchResult.valueOf(result);
    }

    public int getEnergy()
    {
        return energySlot.get();
    }

    @Override
    public ItemStack getInputStack()
    {
        return getSlot(FramingSawMenu.SLOT_INPUT).getItem();
    }

    @Override
    public Container getInputContainer()
    {
        return inputContainer;
    }

    @Override
    public ItemStack getAdditiveStack(int slot)
    {
        return getSlot(FramingSawMenu.SLOT_ADDITIVE_FIRST + slot).getItem();
    }

    @Override
    public boolean isValidRecipeIndex(int idx)
    {
        return idx >= 0 && idx < cache.getRecipes().size();
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

            if (index == FramingSawMenu.SLOT_RESULT)
            {
                stack.getItem().onCraftedBy(stack, player.level(), player);
                if (!moveItemStackTo(stack, FramingSawMenu.SLOT_INV_FIRST, slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, remainder);
            }
            else if (index < FramingSawMenu.SLOT_INV_FIRST)
            {
                if (!moveItemStackTo(stack, FramingSawMenu.SLOT_INV_FIRST, slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (cache.getMaterialValue(stack.getItem()) > 0)
            {
                if (!moveItemStackTo(stack, FramingSawMenu.SLOT_INPUT, FramingSawMenu.SLOT_INPUT + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!moveItemStackTo(stack, FramingSawMenu.SLOT_ADDITIVE_FIRST, FramingSawMenu.SLOT_ADDITIVE_FIRST + FramingSawRecipe.MAX_ADDITIVE_COUNT, false))
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
        }
        return remainder;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return blockEntity.isUsableByPlayer(player);
    }



    private static final class AdditiveSlot extends SlotItemHandler
    {
        private boolean active = true;

        public AdditiveSlot(IItemHandler handler, int idx, int x, int y)
        {
            super(handler, idx, x, y);
        }

        @Override
        public boolean isActive()
        {
            return active || hasItem();
        }

        @Override
        public boolean isHighlightable()
        {
            return active || hasItem();
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack)
        {
            return active && super.mayPlace(stack);
        }
    }

    private final class RecipeIndexDataSlot extends DataSlot
    {
        private int index = -1;

        @Override
        public int get()
        {
            return index;
        }

        @Override
        public void set(int value)
        {
            index = value;
            handleRecipeChange(value == -1 ? null : cache.getRecipes().get(value));
        }
    }
}
