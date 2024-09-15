package xfacthd.framedblocks.common.block.cube;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import xfacthd.framedblocks.common.capability.CompoundStorageBlockItemHandler;
import xfacthd.framedblocks.common.capability.IStorageBlockItemHandler;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.ChestState;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.Optional;
import java.util.OptionalLong;

public class FramedChestBlock extends FramedStorageBlock
{
    public static final DoubleBlockCombiner.Combiner<FramedChestBlockEntity, Optional<IStorageBlockItemHandler>> CHEST_COMBINER = new DoubleBlockCombiner.Combiner<>()
    {
        @Override
        public Optional<IStorageBlockItemHandler> acceptDouble(FramedChestBlockEntity first, FramedChestBlockEntity second)
        {
            return Optional.of(new CompoundStorageBlockItemHandler(
                    first.getItemHandler(), second.getItemHandler()
            ));
        }

        @Override
        public Optional<IStorageBlockItemHandler> acceptSingle(FramedChestBlockEntity single)
        {
            return Optional.of(single.getItemHandler());
        }

        @Override
        public Optional<IStorageBlockItemHandler> acceptNone()
        {
            return Optional.empty();
        }
    };
    public static final DoubleBlockCombiner.Combiner<FramedChestBlockEntity, ChestState> STATE_COMBINER = new DoubleBlockCombiner.Combiner<>()
    {
        @Override
        public ChestState acceptDouble(FramedChestBlockEntity first, FramedChestBlockEntity second)
        {
            ChestState stateOne = first.getBlockState().getValue(PropertyHolder.CHEST_STATE);
            ChestState stateTwo = second.getBlockState().getValue(PropertyHolder.CHEST_STATE);
            return stateOne != ChestState.CLOSED ? stateOne : stateTwo;
        }

        @Override
        public ChestState acceptSingle(FramedChestBlockEntity single)
        {
            return single.getBlockState().getValue(PropertyHolder.CHEST_STATE);
        }

        @Override
        public ChestState acceptNone()
        {
            return ChestState.CLOSED;
        }
    };
    public static final DoubleBlockCombiner.Combiner<FramedChestBlockEntity, OptionalLong> OPENNESS_COMBINER = new DoubleBlockCombiner.Combiner<>()
    {
        @Override
        public OptionalLong acceptDouble(FramedChestBlockEntity first, FramedChestBlockEntity second)
        {
            ChestState stateOne = first.getBlockState().getValue(PropertyHolder.CHEST_STATE);
            ChestState stateTwo = second.getBlockState().getValue(PropertyHolder.CHEST_STATE);
            return OptionalLong.of(Math.min(first.getLastChangeTime(stateOne), second.getLastChangeTime(stateTwo)));
        }

        @Override
        public OptionalLong acceptSingle(FramedChestBlockEntity single)
        {
            ChestState state = single.getBlockState().getValue(PropertyHolder.CHEST_STATE);
            return OptionalLong.of(single.getLastChangeTime(state));
        }

        @Override
        public OptionalLong acceptNone()
        {
            return OptionalLong.empty();
        }
    };

    public FramedChestBlock()
    {
        super(BlockType.FRAMED_CHEST);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.CHEST_STATE, PropertyHolder.LATCH_TYPE,
                BlockStateProperties.CHEST_TYPE, BlockStateProperties.WATERLOGGED
        );
        FramedUtils.removeProperty(builder, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withWater()
                .withCustom((state, modCtx) ->
                {
                    Direction dir = ctx.getHorizontalDirection().getOpposite();
                    ChestType type = ChestType.SINGLE;
                    boolean secondaryUse = modCtx.isSecondaryUseActive();
                    Direction face = modCtx.getClickedFace();
                    if (face.getAxis().isHorizontal() && secondaryUse)
                    {
                        Direction adjDir = getPotentialNeighborFacing(modCtx, face.getOpposite());
                        if (adjDir != null && adjDir.getAxis() != face.getAxis())
                        {
                            dir = adjDir;
                            type = adjDir.getCounterClockWise() == face.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
                        }
                    }

                    if (type == ChestType.SINGLE && !secondaryUse)
                    {
                        if (dir == getPotentialNeighborFacing(modCtx, dir.getClockWise()))
                        {
                            type = ChestType.LEFT;
                        }
                        else if (dir == getPotentialNeighborFacing(modCtx, dir.getCounterClockWise()))
                        {
                            type = ChestType.RIGHT;
                        }
                    }
                    return state.setValue(FramedProperties.FACING_HOR, dir).setValue(BlockStateProperties.CHEST_TYPE, type);
                })
                .build();
    }

    @Nullable
    private Direction getPotentialNeighborFacing(BlockPlaceContext ctx, Direction side)
    {
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(side));
        if (state.is(this) && state.getValue(BlockStateProperties.CHEST_TYPE) == ChestType.SINGLE)
        {
            return state.getValue(FramedProperties.FACING_HOR);
        }
        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (!level.isClientSide() && !ChestBlock.isChestBlockedAt(level, pos))
        {
            if (level.getBlockEntity(pos) instanceof FramedStorageBlockEntity be)
            {
                be.open((ServerPlayer) player);
                player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        ItemStack stack = player.getMainHandItem();
        if (stack.is(Utils.FRAMED_HAMMER.value()))
        {
            if (!level.isClientSide())
            {
                state = state.setValue(PropertyHolder.LATCH_TYPE, state.getValue(PropertyHolder.LATCH_TYPE).next());
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState adjState, LevelAccessor level, BlockPos pos, BlockPos adjPos)
    {
        BlockState newState = super.updateShape(state, dir, adjState, level, pos, adjPos);
        if (adjState.is(this) && dir.getAxis().isHorizontal())
        {
            ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
            ChestType adjType = adjState.getValue(BlockStateProperties.CHEST_TYPE);
            if (type != ChestType.SINGLE || adjType == ChestType.SINGLE)
            {
                return newState;
            }

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            Direction adjFacing = adjState.getValue(FramedProperties.FACING_HOR);
            if (facing == adjFacing && getConnectionDirection(adjState) == dir.getOpposite())
            {
                return newState.setValue(BlockStateProperties.CHEST_TYPE, adjType.getOpposite());
            }
        }
        else if (getConnectionDirection(state) == dir)
        {
            return newState.setValue(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
        }
        return newState;
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (state.getValue(BlockStateProperties.CHEST_TYPE) != ChestType.SINGLE)
        {
            // Prevent rotation by players on double chests
            return state;
        }
        return super.rotate(state, face, rot);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = rot.rotate(state.getValue(FramedProperties.FACING_HOR));
        return state.setValue(FramedProperties.FACING_HOR, dir);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        state = state.rotate(mirror.getRotation(state.getValue(FramedProperties.FACING_HOR)));
        if (mirror != Mirror.NONE)
        {
            ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
            state = state.setValue(BlockStateProperties.CHEST_TYPE, type.getOpposite());
        }
        return state;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        if (ChestBlock.isChestBlockedAt(level, pos))
        {
            return 0;
        }
        return super.getAnalogOutputSignal(state, level, pos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedChestBlockEntity(pos, state);
    }

    @Override
    @Nullable
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof FramedChestBlockEntity be)
        {
            return be;
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (level.isClientSide() || state.getValue(PropertyHolder.CHEST_STATE) != ChestState.CLOSING)
        {
            return null;
        }
        return Utils.createBlockEntityTicker(type, FBContent.BE_TYPE_FRAMED_CHEST.value(), FramedChestBlockEntity::tick);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(PropertyHolder.LATCH_TYPE, state.getValue(PropertyHolder.LATCH_TYPE));
    }



    public static DoubleBlockCombiner.NeighborCombineResult<? extends FramedChestBlockEntity> combine(FramedChestBlockEntity be, boolean override)
    {
        return combine(be.getBlockState(), be.getLevel(), be.getBlockPos(), override);
    }

    public static DoubleBlockCombiner.NeighborCombineResult<? extends FramedChestBlockEntity> combine(
            BlockState state, Level level, BlockPos pos, boolean override
    )
    {
        return DoubleBlockCombiner.combineWithNeigbour(
                FBContent.BE_TYPE_FRAMED_CHEST.value(),
                FramedChestBlock::getDoubleBlockType,
                FramedChestBlock::getConnectionDirection,
                FramedProperties.FACING_HOR,
                state,
                level,
                pos,
                override ? (l, p) -> false : ChestBlock::isChestBlockedAt
        );
    }

    private static DoubleBlockCombiner.BlockType getDoubleBlockType(BlockState state)
    {
        return switch (state.getValue(BlockStateProperties.CHEST_TYPE))
        {
            case SINGLE -> DoubleBlockCombiner.BlockType.SINGLE;
            case LEFT -> DoubleBlockCombiner.BlockType.SECOND;
            case RIGHT -> DoubleBlockCombiner.BlockType.FIRST;
        };
    }

    public static Direction getConnectionDirection(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return switch (state.getValue(BlockStateProperties.CHEST_TYPE))
        {
            case SINGLE -> dir;
            case LEFT -> dir.getClockWise();
            case RIGHT -> dir.getCounterClockWise();
        };
    }
}
