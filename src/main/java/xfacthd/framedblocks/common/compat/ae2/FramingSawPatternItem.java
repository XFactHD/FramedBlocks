package xfacthd.framedblocks.common.compat.ae2;

import appeng.api.stacks.AEItemKey;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;

final class FramingSawPatternItem extends Item
{
    private static final Holder<Item> ITEM_BLANK_PATTERN = DeferredItem.createItem(new ResourceLocation("ae2", "blank_pattern"));

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

            ItemStack newStack = ITEM_BLANK_PATTERN.value().getDefaultInstance();
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
        else
        {
            ItemStack itemStack = switch (player.getInventory().selected)
            {
                case 0 -> encode(FBContent.BLOCK_FRAMED_CUBE.value().asItem().getDefaultInstance(), new ItemStack[0], FBContent.BLOCK_FRAMED_SLOPE.value().asItem().getDefaultInstance());
                case 1 -> encode(FBContent.BLOCK_FRAMED_SLOPE.value().asItem().getDefaultInstance(), new ItemStack[0], FBContent.BLOCK_FRAMED_DOUBLE_SLOPE.value().asItem().getDefaultInstance());
                case 2 -> encode(FBContent.BLOCK_FRAMED_CUBE.value().asItem().getDefaultInstance(), new ItemStack[0], FBContent.BLOCK_FRAMED_SLAB.value().asItem().getDefaultInstance());
                case 3 -> encode(FBContent.BLOCK_FRAMED_CUBE.value().asItem().getDefaultInstance(), new ItemStack[] { Items.COAL.getDefaultInstance() }, FBContent.BLOCK_FRAMED_TORCH.value().asItem().getDefaultInstance());
                default -> null;
            };
            if (itemStack != null)
            {
                player.getInventory().add(itemStack);
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag)
    {
        // TODO: tooltip
    }



    public static ItemStack encode(ItemStack input, ItemStack[] additives, ItemStack output)
    {
        ItemStack stack = new ItemStack(AppliedEnergisticsCompat.GuardedAccess.ITEM_FRAMING_SAW_PATTERN);
        FramingSawPatternEncoding.encodeFramingSawPattern(stack.getOrCreateTag(), input, additives, output);
        return stack;
    }

    public static FramingSawPatternDetails decode(AEItemKey what, Level level)
    {
        if (what != null && what.hasTag())
        {
            try
            {
                return new FramingSawPatternDetails(what, level);
            }
            catch (Throwable t)
            {
                FramedBlocks.LOGGER.warn("Could not decode an invalid framing saw pattern {}: {}", what.getTag(), t);
            }
        }
        return null;
    }

    public static FramingSawPatternDetails decode(ItemStack stack, Level level, boolean tryRecovery)
    {
        FramingSawPatternDetails pattern = decode(AEItemKey.of(stack), level);
        if (pattern == null && tryRecovery)
        {
            CompoundTag tag = stack.getOrCreateTag();
            if (attemptRecovery(tag, level))
            {
                pattern = decode(stack, level, false);
            }
        }
        return pattern;
    }

    private static boolean attemptRecovery(CompoundTag tag, Level level)
    {
        // TODO: implement recovery
        return false;
    }
}
