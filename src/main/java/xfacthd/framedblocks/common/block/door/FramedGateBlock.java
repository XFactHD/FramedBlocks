package xfacthd.framedblocks.common.block.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

@SuppressWarnings("deprecation")
public class FramedGateBlock extends FramedBlock
{
    private static final VoxelShape SHAPE_SOUTH = box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    private static final VoxelShape SHAPE_NORTH = box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_WEST = box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_EAST = box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

    private FramedGateBlock(BlockType blockType, Properties props)
    {
        super(blockType, props);
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.OPEN, false)
                .setValue(BlockStateProperties.POWERED, false)
                .setValue(FramedProperties.SOLID, false)
                .setValue(FramedProperties.GLOWING, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(
                BlockStateProperties.HORIZONTAL_FACING,
                BlockStateProperties.DOOR_HINGE,
                BlockStateProperties.OPEN,
                BlockStateProperties.POWERED,
                FramedProperties.SOLID,
                FramedProperties.GLOWING
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean powered = level.hasNeighborSignal(pos);
        return defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection())
                .setValue(BlockStateProperties.DOOR_HINGE, getHinge(context))
                .setValue(BlockStateProperties.POWERED, powered)
                .setValue(BlockStateProperties.OPEN, powered);
    }

    private DoorHingeSide getHinge(BlockPlaceContext context)
    {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction dir = context.getHorizontalDirection();

        BlockPos posLeft = pos.relative(dir.getCounterClockWise());
        BlockState stateLeft = level.getBlockState(posLeft);
        BlockPos posRight = pos.relative(dir.getClockWise());
        BlockState stateRight = level.getBlockState(posRight);

        if (stateLeft.is(this) || stateRight.isCollisionShapeFullBlock(level, posRight))
        {
            return DoorHingeSide.RIGHT;
        }
        if (stateRight.is(this) || stateLeft.isCollisionShapeFullBlock(level, posLeft))
        {
            return DoorHingeSide.LEFT;
        }

        Vec3 hitVec = Utils.fraction(context.getClickLocation());
        double xz = Utils.isX(dir) ? hitVec.z() : hitVec.x();
        if (Utils.isPositive(dir.getCounterClockWise()))
        {
            xz = 1D - xz;
        }
        return xz > .5D ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
    }

    @Override
    public final InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result.consumesAction()) { return result; }

        if (material == FramedDoorBlock.IRON_WOOD)
        {
            return InteractionResult.PASS;
        }

        state = state.cycle(BlockStateProperties.OPEN);
        level.setBlockAndUpdate(pos, state);

        boolean open = state.getValue(BlockStateProperties.OPEN);
        level.levelEvent(player, open ? getOpenSound() : getCloseSound(), pos, 0);
        level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        boolean powered = level.hasNeighborSignal(pos);
        if (!defaultBlockState().is(block) && powered != state.getValue(BlockStateProperties.POWERED))
        {
            if (powered != state.getValue(BlockStateProperties.OPEN))
            {
                level.levelEvent(null, powered ? getOpenSound() : getCloseSound(), pos, 0);
                level.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }

            level.setBlock(
                    pos,
                    state.setValue(BlockStateProperties.POWERED, powered).setValue(BlockStateProperties.OPEN, powered),
                    Block.UPDATE_CLIENTS
            );
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        boolean open = !state.getValue(BlockStateProperties.OPEN);
        boolean rightHinge = state.getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.RIGHT;
        return switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING))
        {
            case NORTH -> open ? SHAPE_NORTH : (rightHinge ? SHAPE_WEST  : SHAPE_EAST);
            case EAST ->  open ? SHAPE_EAST  : (rightHinge ? SHAPE_NORTH : SHAPE_SOUTH);
            case SOUTH -> open ? SHAPE_SOUTH : (rightHinge ? SHAPE_EAST  : SHAPE_WEST);
            case WEST ->  open ? SHAPE_WEST  : (rightHinge ? SHAPE_SOUTH : SHAPE_NORTH);
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos) { return false; }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) { return PushReaction.DESTROY; }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING))).cycle(BlockStateProperties.DOOR_HINGE);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        return switch (type)
        {
            case LAND, AIR -> state.getValue(BlockStateProperties.OPEN);
            default -> false;
        };
    }

    private int getCloseSound()
    {
        return material == FramedDoorBlock.IRON_WOOD ? LevelEvent.SOUND_CLOSE_IRON_DOOR : LevelEvent.SOUND_CLOSE_WOODEN_DOOR;
    }

    private int getOpenSound()
    {
        return material == FramedDoorBlock.IRON_WOOD ? LevelEvent.SOUND_OPEN_IRON_DOOR : LevelEvent.SOUND_OPEN_WOODEN_DOOR;
    }



    public static FramedGateBlock wood()
    {
        return new FramedGateBlock(
                BlockType.FRAMED_GATE_DOOR,
                IFramedBlock.createProperties(BlockType.FRAMED_GATE_DOOR)
        );
    }

    public static FramedGateBlock iron()
    {
        return new FramedGateBlock(
                BlockType.FRAMED_IRON_GATE_DOOR,
                IFramedBlock.createProperties(BlockType.FRAMED_IRON_GATE_DOOR, FramedDoorBlock.IRON_WOOD)
                        .requiresCorrectToolForDrops()
        );
    }
}
