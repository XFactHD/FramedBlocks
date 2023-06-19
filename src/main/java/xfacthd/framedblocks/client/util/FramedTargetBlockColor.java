package xfacthd.framedblocks.client.util;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.client.model.cube.FramedTargetModel;
import xfacthd.framedblocks.common.blockentity.special.FramedTargetBlockEntity;

public final class FramedTargetBlockColor extends FramedBlockColor implements ItemColor
{
    public static final FramedTargetBlockColor INSTANCE = new FramedTargetBlockColor();

    private FramedTargetBlockColor() { }

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex)
    {
        if (tintIndex == FramedTargetModel.OVERLAY_TINT_IDX && level != null && pos != null)
        {
            if (level.getBlockEntity(pos) instanceof FramedTargetBlockEntity be)
            {
                return be.getOverlayColor();
            }
        }
        return super.getColor(state, level, pos, tintIndex);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        if (tintIndex == FramedTargetModel.OVERLAY_TINT_IDX)
        {
            return DyeColor.RED.getTextColor();
        }
        return -1;
    }
}
