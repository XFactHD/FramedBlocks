package xfacthd.framedblocks.common.compat.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.IPatternDetailsDecoder;
import appeng.api.stacks.AEItemKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

final class FramingSawPatternDetailsDecoder implements IPatternDetailsDecoder
{
    @Override
    public boolean isEncodedPattern(ItemStack stack)
    {
        return stack.is(AppliedEnergisticsCompat.GuardedAccess.ITEM_FRAMING_SAW_PATTERN);
    }

    @Override
    @Nullable
    public IPatternDetails decodePattern(AEItemKey what, Level level)
    {
        if (level != null && what.getItem() == AppliedEnergisticsCompat.GuardedAccess.ITEM_FRAMING_SAW_PATTERN.value())
        {
            return FramingSawPatternItem.decode(what, level);
        }
        return null;
    }

    @Override
    @Nullable
    public IPatternDetails decodePattern(ItemStack what, Level level, boolean tryRecovery)
    {
        if (level != null && what.getItem() == AppliedEnergisticsCompat.GuardedAccess.ITEM_FRAMING_SAW_PATTERN.value())
        {
            return FramingSawPatternItem.decode(what, level, tryRecovery);
        }
        return null;
    }
}
