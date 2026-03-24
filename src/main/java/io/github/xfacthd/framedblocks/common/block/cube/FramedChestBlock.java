package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import io.github.xfacthd.framedblocks.common.capability.item.CompoundStorageBlockItemResourceHandler;
import io.github.xfacthd.framedblocks.common.capability.item.IStorageBlockItemResourceHandler;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.ChestState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

public class FramedChestBlock extends FramedStorageBlock
{
    public static final DoubleBlockCombiner.Combiner<FramedChestBlockEntity, Optional<IStorageBlockItemResourceHandler>> CHEST_COMBINER = new DoubleBlockCombiner.Combiner<>()
    {
        @Override
        public Optional<IStorageBlockItemResourceHandler> acceptDouble(FramedChestBlockEntity first, FramedChestBlockEntity second)
        {
            return Optional.of(new CompoundStorageBlockItemResourceHandler(
                    first.getItemHandler(), second.getItemHandler()
            ));
        }

        @Override
        public Optional<IStorageBlockItemResourceHandler> acceptSingle(FramedChestBlockEntity single)
        {
            return Optional.of(single.getItemHandler());
        }

        @Override
        public Optional<IStorageBlockItemResourceHandler> acceptNone()
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
    public static final DoubleBlockCombiner.Combiner<FramedChestBlockEntity, Component> TITLE_COMBINER = new DoubleBlockCombiner.Combiner<>()
    {
        @Override
        public Component acceptDouble(FramedChestBlockEntity first, FramedChestBlockEntity second)
        {
            return first.hasCustomName() ? first.getName() : second.getName();
        }

        @Override
        public Component acceptSingle(FramedChestBlockEntity single)
        {
            return single.getName();
        }

        @Override
        public Component acceptNone()
        {
            return FramedChestBlockEntity.TITLE;
        }
    };

    public FramedChestBlock(Properties props)
    {
        super(BlockType.FRAMED_CHEST, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.CHEST_STATE, PropertyHolder.LATCH_TYPE,
                BlockStateProperties.CHEST_TYPE
        );
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withWater()
                .withCustom((state, modCtx) ->
                {
                    Direction dir = modCtx.getHorizontalDirection().getOpposite();
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
        return InteractionResult.SUCCESS;
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
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction side, BlockPos adjPos, BlockState adjState, RandomSource random)
    {
        BlockState newState = super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
        if (adjState.is(this) && side.getAxis().isHorizontal())
        {
            ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
            ChestType adjType = adjState.getValue(BlockStateProperties.CHEST_TYPE);
            if (type != ChestType.SINGLE || adjType == ChestType.SINGLE)
            {
                return newState;
            }

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            Direction adjFacing = adjState.getValue(FramedProperties.FACING_HOR);
            if (facing == adjFacing && getConnectionDirection(adjState) == side.getOpposite())
            {
                return newState.setValue(BlockStateProperties.CHEST_TYPE, adjType.getOpposite());
            }
        }
        else if (getConnectionDirection(state) == side)
        {
            return newState.setValue(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
        }
        return newState;
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        if (state.getValue(BlockStateProperties.CHEST_TYPE) != ChestType.SINGLE)
        {
            // Prevent rotation by players on double chests
            return state;
        }
        return super.rotate(state, direction, mode);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
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
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction side)
    {
        if (ChestBlock.isChestBlockedAt(level, pos))
        {
            return 0;
        }
        return super.getAnalogOutputSignal(state, level, pos, side);
    }

    @Override
    public FramedBlockEntity newBlockEntity(BlockPos pos, BlockState state)
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
        return BlockUtils.createBlockEntityTicker(type, FBContent.BE_TYPE_FRAMED_CHEST.value(), FramedChestBlockEntity::tick);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(PropertyHolder.LATCH_TYPE, state.getValue(PropertyHolder.LATCH_TYPE));
    }

    public static DoubleBlockCombiner.NeighborCombineResult<? extends FramedChestBlockEntity> combine(FramedChestBlockEntity be, boolean override)
    {
        return combine(be.getBlockState(), Objects.requireNonNull(be.getLevel()), be.getBlockPos(), override);
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
                override ? (_, _) -> false : ChestBlock::isChestBlockedAt
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
