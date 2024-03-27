package xfacthd.framedblocks.common.block.sign;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedSignItem;

@SuppressWarnings("deprecation")
public class FramedStandingSignBlock extends AbstractFramedSignBlock
{
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public FramedStandingSignBlock()
    {
        super(BlockType.FRAMED_SIGN, IFramedBlock.createProperties(BlockType.FRAMED_SIGN).noCollission());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) ->
                {
                    int rotation = RotationSegment.convertToSegment(modCtx.getRotation() + 180.0F);
                    return state.setValue(BlockStateProperties.ROTATION_16, rotation);
                })
                .withWater()
                .build();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return SHAPE;
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction dir,
            BlockState facingState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos facingPos
    )
    {
        if (dir == Direction.DOWN && !canSurvive(state, level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, facingState, level, pos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return level.getBlockState(pos.below()).isSolid();
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        if (rot == Rotation.COUNTERCLOCKWISE_90)
        {
            rotation += 15;
        }
        else
        {
            rotation += 1;
        }
        return state.setValue(BlockStateProperties.ROTATION_16, rotation % 16);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, rot.rotate(rotation, 16));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, mirror.mirror(rot, 16));
    }

    @Override
    public float getYRotationDegrees(BlockState state)
    {
        return RotationSegment.convertToDegrees(state.getValue(BlockStateProperties.ROTATION_16));
    }

    @Override
    public BlockItem createBlockItem()
    {
        return new FramedSignItem();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }
}