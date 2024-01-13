package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.slab.FramedCheckeredCubeBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.*;

public class FramedCheckeredCubeBlock extends AbstractFramedDoubleBlock
{
    public FramedCheckeredCubeBlock()
    {
        super(BlockType.FRAMED_CHECKERED_CUBE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedCheckeredCubeBlockEntity(pos, state);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        BlockState segmentState = FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT.value().defaultBlockState();
        return new Tuple<>(segmentState, segmentState.setValue(PropertyHolder.SECOND, true));
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        return SolidityCheck.BOTH;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return FBContent.BLOCK_FRAMED_CHECKERED_CUBE.value().defaultBlockState();
    }
}
