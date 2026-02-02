package xfacthd.framedblocks.common.block.cube;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedSpecialBlockItem;

public class FramedLayeredCubeBlock extends FramedBlock
{
    private static final Direction[] DIRECTIONS = Direction.values();

    public FramedLayeredCubeBlock()
    {
        super(BlockType.FRAMED_LAYERED_CUBE);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, BlockStateProperties.LAYERS, FramedProperties.SOLID, BlockStateProperties.WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return PlacementStateBuilder.of(this, context)
                .withTargetFacing(true)
                .withCustom((state, modCtx) ->
                {
                    BlockState prevState = modCtx.getLevel().getBlockState(modCtx.getClickedPos());
                    if (prevState.is(this))
                    {
                        int layers = prevState.getValue(BlockStateProperties.LAYERS);
                        return prevState.setValue(BlockStateProperties.LAYERS, Math.min(8, layers + 1));
                    }
                    return state;
                })
                .withWater()
                .build();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext)
    {
        int layers = state.getValue(BlockStateProperties.LAYERS);
        if (layers >= 8 || !useContext.getItemInHand().is(this.asItem()))
        {
            return false;
        }
        if (!useContext.getItemInHand().getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY).isEmpty())
        {
            return false;
        }
        if (useContext.replacingClickedOnBlock())
        {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            return useContext.getClickedFace() == facing;
        }
        return true;
    }

    @Override
    public BlockItem createBlockItem()
    {
        return new FramedSpecialBlockItem.Single(this, new Item.Properties())
        {
            @Nullable
            @Override
            protected BlockState getReplacementState(BlockPlaceContext ctx, BlockState originalState)
            {
                return FramedLayeredCubeBlock.this.getStateForPlacement(ctx);
            }
        };
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return type == PathComputationType.LAND && state.getValue(BlockStateProperties.LAYERS) < 5;
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return state.setValue(BlockStateProperties.FACING, switch (rot)
        {
            case NONE -> facing;
            case CLOCKWISE_90 -> DIRECTIONS[(facing.ordinal() + 1) % DIRECTIONS.length];
            case CLOCKWISE_180 -> facing.getOpposite();
            case COUNTERCLOCKWISE_90 -> DIRECTIONS[Mth.positiveModulo(facing.ordinal() - 1, DIRECTIONS.length)];
        });
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (Utils.isY(facing))
        {
            return state;
        }
        return state.setValue(BlockStateProperties.FACING, rot.rotate(facing));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, BlockStateProperties.FACING, mirror);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }
}
