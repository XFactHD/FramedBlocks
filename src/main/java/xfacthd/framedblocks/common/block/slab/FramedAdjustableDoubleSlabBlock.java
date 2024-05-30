package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.*;

import java.util.function.Function;

public class FramedAdjustableDoubleSlabBlock extends FramedAdjustableDoubleBlock
{
    private FramedAdjustableDoubleSlabBlock(
            BlockType type,
            Function<BlockState, Tuple<BlockState, BlockState>> statePairBuilder,
            BlockEntityType.BlockEntitySupplier<FramedAdjustableDoubleBlockEntity> beSupplier
    )
    {
        super(type, state -> Direction.UP, statePairBuilder, beSupplier);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        return switch (side)
        {
            case DOWN -> SolidityCheck.FIRST;
            case UP -> SolidityCheck.SECOND;
            case NORTH, SOUTH, WEST, EAST -> SolidityCheck.BOTH;
        };
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        return switch (side)
        {
            case DOWN -> CamoGetter.FIRST;
            case UP -> CamoGetter.SECOND;
            case NORTH, SOUTH, WEST, EAST ->
            {
                if (edge == Direction.DOWN) yield CamoGetter.FIRST;
                if (edge == Direction.UP) yield CamoGetter.SECOND;
                yield CamoGetter.NONE;
            }
        };
    }



    public static FramedAdjustableDoubleSlabBlock standard()
    {
        return new FramedAdjustableDoubleSlabBlock(
                BlockType.FRAMED_ADJ_DOUBLE_SLAB,
                FramedAdjustableDoubleBlock::makeStandardStatePair,
                FramedAdjustableDoubleBlockEntity::standard
        );
    }

    public static FramedAdjustableDoubleSlabBlock copycat()
    {
        return new FramedAdjustableDoubleSlabBlock(
                BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_SLAB,
                FramedAdjustableDoubleBlock::makeCopycatStatePair,
                FramedAdjustableDoubleBlockEntity::copycat
        );
    }
}
