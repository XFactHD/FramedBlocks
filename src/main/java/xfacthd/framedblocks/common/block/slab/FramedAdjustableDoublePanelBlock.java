package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.*;

import java.util.function.Function;

public class FramedAdjustableDoublePanelBlock extends FramedAdjustableDoubleBlock
{
    private FramedAdjustableDoublePanelBlock(
            BlockType type,
            Function<BlockState, Tuple<BlockState, BlockState>> statePairBuilder,
            BlockEntityType.BlockEntitySupplier<FramedAdjustableDoubleBlockEntity> beSupplier
    )
    {
        super(type, state -> state.getValue(FramedProperties.FACING_HOR), statePairBuilder, beSupplier);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
                .build();
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction facing = getFacing(state);
        if (side == facing.getOpposite())
        {
            return SolidityCheck.FIRST;
        }
        if (side == facing)
        {
            return SolidityCheck.SECOND;
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = getFacing(state);
        if (side == facing.getOpposite())
        {
            return CamoGetter.FIRST;
        }
        if (side == facing)
        {
            return CamoGetter.SECOND;
        }
        if (edge == facing.getOpposite())
        {
            return CamoGetter.FIRST;
        }
        if (edge == facing)
        {
            return CamoGetter.SECOND;
        }
        return CamoGetter.NONE;
    }



    public static FramedAdjustableDoublePanelBlock standard()
    {
        return new FramedAdjustableDoublePanelBlock(
                BlockType.FRAMED_ADJ_DOUBLE_PANEL,
                FramedAdjustableDoubleBlock::makeStandardStatePair,
                FramedAdjustableDoubleBlockEntity::standard
        );
    }

    public static FramedAdjustableDoublePanelBlock copycat()
    {
        return new FramedAdjustableDoublePanelBlock(
                BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_PANEL,
                FramedAdjustableDoubleBlock::makeCopycatStatePair,
                FramedAdjustableDoubleBlockEntity::copycat
        );
    }
}
