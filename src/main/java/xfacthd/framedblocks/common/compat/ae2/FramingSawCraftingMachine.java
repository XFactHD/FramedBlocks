package xfacthd.framedblocks.common.compat.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import com.google.common.primitives.Ints;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.PoweredFramingSawBlockEntity;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

import java.util.List;

final class FramingSawCraftingMachine implements ICraftingMachine
{
    private static final Lazy<PatternContainerGroup> GROUP = Lazy.of(() -> new PatternContainerGroup(
            AEItemKey.of(FBContent.BLOCK_POWERED_FRAMING_SAW.value()),
            FBContent.BLOCK_POWERED_FRAMING_SAW.value().asItem().getDescription(),
            List.of()
    ));

    private final PoweredFramingSawBlockEntity blockEntity;

    FramingSawCraftingMachine(IAttachmentHolder blockEntity)
    {
        this.blockEntity = (PoweredFramingSawBlockEntity) blockEntity;
    }

    @Override
    public PatternContainerGroup getCraftingMachineInfo()
    {
        return GROUP.get();
    }

    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] inputs, Direction ejectDir)
    {
        if (pattern instanceof FramingSawPatternDetails sawPattern)
        {
            if (!blockEntity.isInputEmpty() && !sawPattern.getRecipe().equals(blockEntity.getSelectedRecipe()))
            {
                return false;
            }

            IItemHandler inv = blockEntity.getItemHandler();
            for (int i = 0; i < FramingSawMenu.SLOT_RESULT; i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (i >= inputs.length)
                {
                    if (!stack.isEmpty())
                    {
                        return false;
                    }
                    continue;
                }

                var entry = inputs[i].getFirstEntry();
                if (entry == null)
                {
                    continue;
                }
                if (!(entry.getKey() instanceof AEItemKey itemKey))
                {
                    return false;
                }
                if (!stack.isEmpty() && (!itemKey.matches(stack) || stack.getCount() + entry.getLongValue() > stack.getMaxStackSize()))
                {
                    return false;
                }
            }
            blockEntity.selectRecipe(sawPattern.getRecipe());
            for (int i = 0; i < inputs.length; i++)
            {
                var entry = inputs[i].getFirstEntry();
                if (entry == null)
                {
                    continue;
                }

                int count = Ints.saturatedCast(entry.getLongValue());
                inv.insertItem(i, ((AEItemKey) entry.getKey()).toStack(count), false);
                inputs[i].remove(entry.getKey(), count);
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean acceptsPlans()
    {
        return true;
    }
}
