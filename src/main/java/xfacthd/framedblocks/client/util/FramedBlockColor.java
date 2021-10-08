package xfacthd.framedblocks.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedFlowerPotBlockEntity;

import javax.annotation.Nullable;

public class FramedBlockColor implements BlockColor
{
    public static final FramedBlockColor INSTANCE = new FramedBlockColor();

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex)
    {
        if (level != null && pos != null)
        {
            BlockEntity be = level.getBlockEntity(pos);
            if (tintIndex < -1)
            {
                tintIndex = ModelUtils.decodeSecondaryTintIndex(tintIndex);

                if (be instanceof FramedDoubleBlockEntity dbe)
                {
                    BlockState camoState = dbe.getCamoStateTwo();
                    if (!camoState.isAir())
                    {
                        return Minecraft.getInstance().getBlockColors().getColor(camoState, level, pos, tintIndex);
                    }
                }
                else if (be instanceof FramedFlowerPotBlockEntity pbe)
                {
                    BlockState plantState = pbe.getFlowerBlock().defaultBlockState();
                    if (!plantState.isAir())
                    {
                        return Minecraft.getInstance().getBlockColors().getColor(plantState, level, pos, tintIndex);
                    }
                }
            }
            else if (be instanceof FramedBlockEntity fbe)
            {
                BlockState camoState = fbe.getCamoState();
                if (!camoState.isAir())
                {
                    return Minecraft.getInstance().getBlockColors().getColor(camoState, level, pos, tintIndex);
                }
            }
        }
        return -1;
    }
}