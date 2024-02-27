package xfacthd.framedblocks.common.compat.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.FramedBlocks;

import java.util.List;

final class FramingSawPatternItem extends Item
{
    public FramingSawPatternItem()
    {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        clearPattern(stack, player);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        if (context.getPlayer() != null && clearPattern(stack, context.getPlayer()))
        {
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }
        return InteractionResult.PASS;
    }

    private static boolean clearPattern(ItemStack stack, Player player)
    {
        if (player.isShiftKeyDown())
        {
            if (player.level().isClientSide())
            {
                return false;
            }

            ItemStack newStack = AppliedEnergisticsCompat.GuardedAccess.makeBlankPatternStack();
            newStack.setCount(stack.getCount());
            if (newStack.isEmpty()) return false;

            Inventory inv = player.getInventory();
            for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++)
            {
                if (inv.getItem(slot) == stack)
                {
                    inv.setItem(slot, newStack);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag)
    {
        if (!stack.hasTag()) return;

        FramingSawPatternDetails details = decode(AEItemKey.of(stack), level, false);
        if (details == null)
        {
            lines.add(Component.translatable("gui.ae2.InvalidPattern").withStyle(ChatFormatting.RED));
            return;
        }

        GenericStack[] out = details.getOutputs();
        lines.add(Component.translatable("gui.ae2.Produces")
                .append(": ")
                .append(getStackComponent(out[0]))
        );

        MutableComponent textWith = Component.translatable("gui.ae2.With").append(": ");
        MutableComponent textAnd = Component.literal(" ").append(Component.translatable("gui.ae2.And")).append(" ");
        boolean first = true;
        for (IPatternDetails.IInput input : details.getInputs())
        {
            if (input == null) continue;

            GenericStack inputTemplate = input.getPossibleInputs()[0];
            GenericStack inputStack = new GenericStack(inputTemplate.what(), inputTemplate.amount() * input.getMultiplier());

            MutableComponent prefix = first ? textWith : textAnd;
            lines.add(prefix.append(getStackComponent(inputStack)));

            first = false;
        }
    }

    private static Component getStackComponent(GenericStack stack)
    {
        String amountInfo = stack.what().formatAmount(stack.amount(), AmountFormat.FULL);
        Component displayName = stack.what().getDisplayName();
        return Component.literal(amountInfo + " x ").append(displayName);
    }



    public static ItemStack encode(ItemStack input, ItemStack[] additives, ItemStack output)
    {
        ItemStack stack = new ItemStack(AppliedEnergisticsCompat.GuardedAccess.ITEM_FRAMING_SAW_PATTERN);
        FramingSawPatternEncoding.encodeFramingSawPattern(stack.getOrCreateTag(), input, additives, output);
        return stack;
    }

    public static FramingSawPatternDetails decode(AEItemKey what, Level level, boolean logError)
    {
        if (what != null && what.hasTag())
        {
            try
            {
                return new FramingSawPatternDetails(what, level);
            }
            catch (Throwable t)
            {
                if (logError)
                {
                    FramedBlocks.LOGGER.warn("Could not decode an invalid framing saw pattern {}: {}", what.getTag(), t);
                }
            }
        }
        return null;
    }
}
