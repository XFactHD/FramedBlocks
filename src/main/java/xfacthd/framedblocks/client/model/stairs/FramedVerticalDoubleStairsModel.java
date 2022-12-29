package xfacthd.framedblocks.client.model.stairs;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedVerticalDoubleStairsBlockEntity;

public class FramedVerticalDoubleStairsModel extends FramedDoubleBlockModel
{
    private final Direction facing;

    public FramedVerticalDoubleStairsModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedVerticalDoubleStairsBlockEntity.getBlockPair(facing);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedVerticalDoubleStairs.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}