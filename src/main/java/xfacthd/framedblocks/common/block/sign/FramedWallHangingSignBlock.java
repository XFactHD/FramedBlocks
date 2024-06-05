package xfacthd.framedblocks.common.block.sign;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedWallHangingSignBlock extends AbstractFramedHangingSignBlock
{
    public FramedWallHangingSignBlock()
    {
        super(BlockType.FRAMED_WALL_HANGING_SIGN, IFramedBlock.createProperties(BlockType.FRAMED_WALL_HANGING_SIGN)
                .noCollission()
        );
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
                .withCustom((state, modCtx) ->
                {
                    Level level = modCtx.getLevel();
                    BlockPos pos = modCtx.getClickedPos();
                    Direction face = modCtx.getClickedFace();

                    for (Direction dir : modCtx.getNearestLookingDirections())
                    {
                        if (dir.getAxis().isHorizontal() && !dir.getAxis().test(face))
                        {
                            state = state.setValue(FramedProperties.FACING_HOR, dir.getOpposite());
                            if (state.canSurvive(level, pos) && canPlace(state, level, pos))
                            {
                                return state;
                            }
                        }
                    }

                    return null;
                })
                .withWater()
                .build();
    }

    @Override
    protected boolean preventUse(BlockState state, Level level, BlockPos pos, Player player, ItemStack stack, BlockHitResult hit)
    {
        if (level.getBlockEntity(pos) instanceof FramedSignBlockEntity sign)
        {
            boolean front = sign.isFacingFrontText(player);
            if (sign.cannotExecuteCommands(front, player) && stack.getItem() == asItem())
            {
                return hit.getDirection().getAxis() == state.getValue(FramedProperties.FACING_HOR).getAxis();
            }
        }
        return false;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return switch (state.getValue(FramedProperties.FACING_HOR))
        {
            case NORTH, SOUTH -> WallHangingSignBlock.SHAPE_NORTHSOUTH;
            case EAST, WEST -> WallHangingSignBlock.SHAPE_EASTWEST;
            default -> throw new IncompatibleClassChangeError();
        };
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getShape(state, level, pos, CollisionContext.empty());
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return switch (state.getValue(FramedProperties.FACING_HOR))
        {
            case NORTH, SOUTH -> WallHangingSignBlock.PLANK_NORTHSOUTH;
            case EAST, WEST -> WallHangingSignBlock.PLANK_EASTWEST;
            default -> throw new IncompatibleClassChangeError();
        };
    }

    @Override
    public float getYRotationDegrees(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR).toYRot();
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        //Not rotatable by wrench
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(state.getValue(FramedProperties.FACING_HOR)));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FramedProperties.FACING_HOR)));
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return false;
    }

    @Override
    @Nullable
    public BlockState getItemModelSource()
    {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }



    public static boolean canPlace(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction dirCw = dir.getClockWise();
        Direction dirCcw = dir.getCounterClockWise();
        return canAttachTo(level, state, pos.relative(dirCw), dirCcw) || canAttachTo(level, state, pos.relative(dirCcw), dirCw);
    }

    private static boolean canAttachTo(LevelReader level, BlockState state, BlockPos pos, Direction side)
    {
        BlockState adjState = level.getBlockState(pos);
        if (adjState.getBlock() instanceof FramedWallHangingSignBlock)
        {
            return adjState.getValue(FramedProperties.FACING_HOR).getAxis().test(state.getValue(FramedProperties.FACING_HOR));
        }
        return adjState.isFaceSturdy(level, pos, side, SupportType.FULL);
    }
}
