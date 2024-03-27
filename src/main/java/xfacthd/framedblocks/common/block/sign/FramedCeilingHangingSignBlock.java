package xfacthd.framedblocks.common.block.sign;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedHangingSignItem;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class FramedCeilingHangingSignBlock extends AbstractFramedHangingSignBlock
{
    public FramedCeilingHangingSignBlock()
    {
        super(BlockType.FRAMED_HANGING_SIGN, IFramedBlock.createProperties(BlockType.FRAMED_HANGING_SIGN)
                .noCollission()
        );
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.ATTACHED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16, BlockStateProperties.ATTACHED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) ->
                {
                    Level level = modCtx.getLevel();
                    BlockPos adjPos = modCtx.getClickedPos().above();
                    BlockState adjState = level.getBlockState(adjPos);
                    Direction dir = Direction.fromYRot(modCtx.getRotation());
                    boolean attached = !Block.isFaceFull(adjState.getCollisionShape(level, adjPos), Direction.DOWN) || modCtx.isSecondaryUseActive();
                    if (adjState.getBlock() instanceof AbstractFramedHangingSignBlock && !modCtx.isSecondaryUseActive())
                    {
                        if (adjState.hasProperty(FramedProperties.FACING_HOR))
                        {
                            Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
                            if (adjDir.getAxis().test(dir))
                            {
                                attached = false;
                            }
                        }
                        else if (adjState.hasProperty(BlockStateProperties.ROTATION_16))
                        {
                            int adjRot = adjState.getValue(BlockStateProperties.ROTATION_16);
                            Optional<Direction> optDir = RotationSegment.convertToDirection(adjRot);
                            if (optDir.isPresent() && optDir.get().getAxis().test(dir))
                            {
                                attached = false;
                            }
                        }
                    }

                    int rotation;
                    if (attached)
                    {
                        rotation = RotationSegment.convertToSegment(modCtx.getRotation() + 180.0F);
                    }
                    else
                    {
                        rotation = RotationSegment.convertToSegment(dir.getOpposite());
                    }
                    return state.setValue(BlockStateProperties.ROTATION_16, rotation)
                            .setValue(BlockStateProperties.ATTACHED, attached);
                })
                .withWater()
                .build();
    }

    @Override
    protected boolean preventUse(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        if (level.getBlockEntity(pos) instanceof FramedSignBlockEntity sign)
        {
            boolean front = sign.isFacingFrontText(player);
            ItemStack stack = player.getItemInHand(hand);
            return !sign.canExecuteCommands(front, player) && stack.getItem() == asItem() && hit.getDirection() == Direction.DOWN;
        }
        return false;
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
        if (dir == Direction.UP && !canSurvive(state, level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, facingState, level, pos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos above = pos.above();
        return level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN, SupportType.CENTER);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getShape(state, level, pos, CollisionContext.empty());
    }

    @Override
    public float getYRotationDegrees(BlockState state)
    {
        return RotationSegment.convertToDegrees(state.getValue(BlockStateProperties.ROTATION_16));
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
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, mirror.mirror(rotation, 16));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return Utils.createBlockEntityTicker(type, FBContent.BE_TYPE_FRAMED_HANGING_SIGN.value(), FramedSignBlockEntity::tick);
    }

    @Override
    public BlockItem createBlockItem()
    {
        return new FramedHangingSignItem();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeZeroEight = box(1, 0, 7, 15, 10, 9);
        VoxelShape shapeFourTwelve = box(7, 0, 1, 9, 10, 15);
        VoxelShape fallbackShape = box(3, 0, 3, 13, 16, 13);

        for (BlockState state : states)
        {
            int rot = state.getValue(BlockStateProperties.ROTATION_16);
            builder.put(state, switch (rot)
            {
                case 0, 8 -> shapeZeroEight;
                case 4, 12 -> shapeFourTwelve;
                default -> fallbackShape;
            });
        }

        return ShapeProvider.of(builder.build());
    }
}
