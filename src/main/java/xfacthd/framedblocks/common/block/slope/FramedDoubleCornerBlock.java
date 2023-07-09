package xfacthd.framedblocks.common.block.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleCornerBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedDoubleCornerBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleCornerBlock()
    {
        super(BlockType.FRAMED_DOUBLE_CORNER);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.CORNER_TYPE, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction side = context.getClickedFace();
        Direction typeSide = side;
        Vec3 hitPoint = Utils.fraction(context.getClickLocation());
        if (!Utils.isY(side))
        {
            if (hitPoint.y() < (3D / 16D))
            {
                typeSide = Direction.UP;
            }
            else if (hitPoint.y() > (13D / 16D))
            {
                typeSide = Direction.DOWN;
            }
        }

        return withCornerType(state, context, side, typeSide, hitPoint, context.getHorizontalDirection());
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
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
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
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get().defaultBlockState()
                        .setValue(PropertyHolder.CORNER_TYPE, type)
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.BLOCK_FRAMED_CORNER_SLOPE.get().defaultBlockState()
                        .setValue(PropertyHolder.CORNER_TYPE, type.verticalOpposite())
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type == CornerType.BOTTOM)
        {
            return DoubleBlockTopInteractionMode.SECOND;
        }
        else if (type.isTop())
        {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleCornerBlockEntity(pos, state);
    }



    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_DOUBLE_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}