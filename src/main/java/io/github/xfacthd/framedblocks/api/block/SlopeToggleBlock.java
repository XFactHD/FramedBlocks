package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// TODO: Improve alt-slope handling across all relevant blocks
public interface SlopeToggleBlock extends IFramedBlock
{
    default SlopeOrientation getSlopeOrientation(BlockState state)
    {
        return SlopeOrientation.VERTICAL;
    }

    static boolean toggleAltSlope(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().getItem() == Utils.FRAMED_WRENCH.value())
        {
            level.setBlockAndUpdate(pos, state.setValue(FramedProperties.ALT_SLOPE, !state.getValue(FramedProperties.ALT_SLOPE)));
            return true;
        }
        return false;
    }

    enum SlopeOrientation
    {
        VERTICAL,
        HORIZONTAL
    }
}
