package io.github.xfacthd.framedblocks.common.compat.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import com.google.common.primitives.Ints;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.PoweredFramingSawBlockEntity;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

final class FramingSawCraftingMachine implements ICraftingMachine
{
    private static final Lazy<PatternContainerGroup> GROUP = Lazy.of(() -> new PatternContainerGroup(
            AEItemKey.of(FBContent.BLOCK_POWERED_FRAMING_SAW.value()),
            FBContent.BLOCK_POWERED_FRAMING_SAW.value().asItem().getDefaultInstance().getItemName(),
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

            ResourceHandler<ItemResource> inv = blockEntity.getItemHandler();
            for (int i = 0; i < FramingSawMenu.SLOT_RESULT; i++)
            {
                ItemResource resource = inv.getResource(i);
                if (i >= inputs.length)
                {
                    if (!resource.isEmpty())
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
                if (!resource.isEmpty() && (!itemKey.matches(resource.toStack()) || inv.getAmountAsInt(i) + entry.getLongValue() > resource.getMaxStackSize()))
                {
                    return false;
                }
            }
            blockEntity.selectRecipe(sawPattern.getRecipe());
            try (Transaction tx = Transaction.open(null))
            {
                for (int i = 0; i < inputs.length; i++)
                {
                    var entry = inputs[i].getFirstEntry();
                    if (entry == null)
                    {
                        continue;
                    }

                    int count = Ints.saturatedCast(entry.getLongValue());
                    ItemStack stack = ((AEItemKey) entry.getKey()).toStack(count);
                    inv.insert(i, ItemResource.of(stack), count, tx);
                    inputs[i].remove(entry.getKey(), count);
                }
                tx.commit();
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
