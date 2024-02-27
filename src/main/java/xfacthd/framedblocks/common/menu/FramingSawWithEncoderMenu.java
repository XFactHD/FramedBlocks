package xfacthd.framedblocks.common.menu;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.compat.ae2.AppliedEnergisticsCompat;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.Arrays;

public class FramingSawWithEncoderMenu extends FramingSawMenu
{
    public static final int SLOT_PATTERN_INPUT = SLOT_INV_FIRST + INV_SLOT_COUNT;
    public static final int SLOT_PATTERN_OUTPUT = SLOT_PATTERN_INPUT + 1;
    public static final int MENU_BUTTON_MODE_CRAFTING = -1;
    public static final int MENU_BUTTON_MODE_ENCODING = -2;

    private final Slot patternInputSlot;
    private final Slot patternOutputSlot;
    private final Container encoderContainer = new SimpleContainer(2);
    private final EncoderModeDataSlot patternEncoderMode = new EncoderModeDataSlot(this);
    private BooleanConsumer encoderModeListener;

    FramingSawWithEncoderMenu(int containerId, Inventory inv, ContainerLevelAccess levelAccess)
    {
        super(containerId, inv, levelAccess);
        Preconditions.checkState(AppliedEnergisticsCompat.isLoaded(), "FramingSawWithEncoderMenu requires AE2, how did we get here???");

        this.patternInputSlot = addSlot(new EncoderInputSlot(this, encoderContainer, 0, 223, 73));
        this.patternOutputSlot = addSlot(new EncoderOutputSlot(this, encoderContainer, 1, 223, 109));

        addDataSlot(patternEncoderMode);

        if (!level.isClientSide())
        {
            levelAccess.execute((level, pos) ->
            {
                boolean encoder = level.getBlockState(pos).getValue(PropertyHolder.SAW_ENCODER);
                patternEncoderMode.set(encoder ? 1 : 0);
            });
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        if (isInEncoderMode())
        {
            ItemStack remainder = ItemStack.EMPTY;
            Slot slot = slots.get(index);
            if (slot.hasItem())
            {
                ItemStack stack = slot.getItem();
                remainder = stack.copy();

                if (index == SLOT_PATTERN_INPUT || index == SLOT_PATTERN_OUTPUT)
                {
                    if (!moveItemStackTo(stack, SLOT_INV_FIRST, SLOT_INV_FIRST + INV_SLOT_COUNT, true))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= SLOT_INV_FIRST)
                {
                    if (AppliedEnergisticsCompat.isPattern(stack, false))
                    {
                        if (!moveItemStackTo(stack, SLOT_PATTERN_INPUT, SLOT_PATTERN_INPUT + 1, false))
                        {
                            return ItemStack.EMPTY;
                        }
                    }
                    else if (AppliedEnergisticsCompat.isPattern(stack, true))
                    {
                        if (!moveItemStackTo(stack, SLOT_PATTERN_OUTPUT, SLOT_PATTERN_OUTPUT + 1, false))
                        {
                            return ItemStack.EMPTY;
                        }
                    }
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
        return super.quickMoveStack(player, index);
    }

    @Override
    public boolean clickMenuButton(Player player, int id)
    {
        if (!level.isClientSide())
        {
            if (id == MENU_BUTTON_MODE_CRAFTING)
            {
                setEncoderMode(player, false);
            }
            else if (id == MENU_BUTTON_MODE_ENCODING)
            {
                setEncoderMode(player, true);
            }
        }
        return super.clickMenuButton(player, id);
    }

    private void setEncoderMode(Player player, boolean encoder)
    {
        if (isInEncoderMode() != encoder)
        {
            patternEncoderMode.set(encoder ? 1 : 0);

            levelAccess.execute((level, pos) ->
            {
                clearContainer(player, encoder ? inputContainer : encoderContainer);
                slotsChanged(encoder ? inputContainer : encoderContainer);

                BlockState state = level.getBlockState(pos);
                level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.SAW_ENCODER, encoder));
            });
        }
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        levelAccess.execute((level, pos) -> clearContainer(player, encoderContainer));
    }

    @Override
    protected boolean isCraftingEnabled()
    {
        return !isInEncoderMode();
    }

    public void setEncoderModeListener(BooleanConsumer listener)
    {
        this.encoderModeListener = listener;
    }

    public boolean isInEncoderMode()
    {
        return patternEncoderMode.isEncoder();
    }

    public void tryEncodePattern(FramingSawRecipe recipe, ItemStack[] inputs)
    {
        if (!patternInputSlot.hasItem() && !patternOutputSlot.hasItem()) return;

        ItemStack[] additives = Arrays.copyOfRange(inputs, 1, inputs.length);
        ItemStack pattern = AppliedEnergisticsCompat.tryEncodePattern(inputs[0], additives, recipe.getResult());

        if (pattern != null)
        {
            if (!patternOutputSlot.hasItem())
            {
                patternInputSlot.getItem().shrink(1);
            }
            patternOutputSlot.set(pattern);
            broadcastChanges();
        }
    }



    private static class EncoderSlot extends Slot
    {
        private final FramingSawWithEncoderMenu menu;

        public EncoderSlot(FramingSawWithEncoderMenu menu, Container pContainer, int pSlot, int pX, int pY)
        {
            super(pContainer, pSlot, pX, pY);
            this.menu = menu;
        }

        @Override
        public boolean isActive()
        {
            return menu.isInEncoderMode();
        }
    }

    private static class EncoderInputSlot extends EncoderSlot
    {
        public EncoderInputSlot(FramingSawWithEncoderMenu menu, Container pContainer, int pSlot, int pX, int pY)
        {
            super(menu, pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return AppliedEnergisticsCompat.isPattern(stack, false);
        }
    }

    private static class EncoderOutputSlot extends EncoderSlot
    {
        public EncoderOutputSlot(FramingSawWithEncoderMenu menu, Container pContainer, int pSlot, int pX, int pY)
        {
            super(menu, pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return AppliedEnergisticsCompat.isPattern(stack, true);
        }
    }

    private static class EncoderModeDataSlot extends DataSlot
    {
        private final FramingSawWithEncoderMenu menu;
        private boolean encoder;

        public EncoderModeDataSlot(FramingSawWithEncoderMenu menu)
        {
            this.menu = menu;
        }

        @Override
        public int get()
        {
            return encoder ? 1 : 0;
        }

        @Override
        public void set(int value)
        {
            encoder = value != 0;
            if (menu.encoderModeListener != null)
            {
                menu.encoderModeListener.accept(encoder);
            }
        }

        public boolean isEncoder()
        {
            return encoder;
        }
    }
}
