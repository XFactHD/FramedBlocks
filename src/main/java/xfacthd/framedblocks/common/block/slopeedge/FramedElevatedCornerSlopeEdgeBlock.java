package xfacthd.framedblocks.common.block.slopeedge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public class FramedElevatedCornerSlopeEdgeBlock extends FramedBlock
{
    public FramedElevatedCornerSlopeEdgeBlock(BlockType blockType)
    {
        super(blockType);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.CORNER_TYPE, FramedProperties.SOLID,
                FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndCornerType()
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction side, Rotation rot)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type.isHorizontal())
        {
            return state.setValue(PropertyHolder.CORNER_TYPE, type.rotate(rot));
        }
        return rotate(state, rot);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = rot.rotate(state.getValue(FramedProperties.FACING_HOR));
        return state.setValue(FramedProperties.FACING_HOR, dir);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type.isHorizontal())
        {
            BlockState newState = Utils.mirrorFaceBlock(state, mirror);
            if (newState != state)
            {
                return newState.setValue(PropertyHolder.CORNER_TYPE, type.horizontalOpposite());
            }
            return state;
        }
        else
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
