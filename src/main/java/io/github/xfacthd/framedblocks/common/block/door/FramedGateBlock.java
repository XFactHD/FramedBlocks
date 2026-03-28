package io.github.xfacthd.framedblocks.common.block.door;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FramedGateBlock extends FramedBlock {
    private final SoundEvent closeSound;
    private final SoundEvent openSound;

    private FramedGateBlock(BlockType blockType, Properties props, SoundEvent closeSound, SoundEvent openSound) {
        super(blockType, props.pushReaction(PushReaction.DESTROY));
        this.closeSound = closeSound;
        this.openSound = openSound;
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.OPEN, false)
                .setValue(BlockStateProperties.POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(
                BlockStateProperties.HORIZONTAL_FACING,
                BlockStateProperties.DOOR_HINGE,
                BlockStateProperties.OPEN,
                BlockStateProperties.POWERED
        );
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withHorizontalFacing()
                .withCustom((state, modCtx) -> {
                    boolean powered = modCtx.getLevel().hasNeighborSignal(modCtx.getClickedPos());
                    return state.setValue(BlockStateProperties.DOOR_HINGE, getHinge(modCtx))
                            .setValue(BlockStateProperties.POWERED, powered)
                            .setValue(BlockStateProperties.OPEN, powered);
                })
                .build();
    }

    private DoorHingeSide getHinge(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction dir = context.getHorizontalDirection();

        BlockPos posLeft = pos.relative(dir.getCounterClockWise());
        BlockState stateLeft = level.getBlockState(posLeft);
        BlockPos posRight = pos.relative(dir.getClockWise());
        BlockState stateRight = level.getBlockState(posRight);

        if (stateLeft.is(this) || stateRight.isCollisionShapeFullBlock(level, posRight)) {
            return DoorHingeSide.RIGHT;
        }
        if (stateRight.is(this) || stateLeft.isCollisionShapeFullBlock(level, posLeft)) {
            return DoorHingeSide.LEFT;
        }

        Vec3 hitVec = MathUtils.fraction(context.getClickLocation());
        double xz = DirUtils.isX(dir) ? hitVec.z() : hitVec.x();
        if (DirUtils.isPositive(dir.getCounterClockWise())) {
            xz = 1D - xz;
        }
        return xz > .5D ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);
        if (result.consumesAction()) {
            return result;
        }

        if (this == FBContent.BLOCK_FRAMED_IRON_GATE.value()) {
            return InteractionResult.PASS;
        }

        state = state.cycle(BlockStateProperties.OPEN);
        level.setBlockAndUpdate(pos, state);

        boolean open = state.getValue(BlockStateProperties.OPEN);
        playSound(player, level, pos, open);
        level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        boolean powered = level.hasNeighborSignal(pos);
        if (!defaultBlockState().is(block) && powered != state.getValue(BlockStateProperties.POWERED)) {
            if (powered != state.getValue(BlockStateProperties.OPEN)) {
                playSound(null, level, pos, powered);
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
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, BlockStateProperties.HORIZONTAL_FACING, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING))).cycle(BlockStateProperties.DOOR_HINGE);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return switch (type) {
            case LAND, AIR -> state.getValue(BlockStateProperties.OPEN);
            default -> false;
        };
    }

    private void playSound(@Nullable Entity entity, Level level, BlockPos pos, boolean open) {
        level.playSound(entity, pos, open ? openSound : closeSound, SoundSource.BLOCKS, 1F, level.getRandom().nextFloat() * .1F + .9F);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }

    public static FramedGateBlock wood(Properties props) {
        return new FramedGateBlock(
                BlockType.FRAMED_GATE,
                props,
                SoundEvents.WOODEN_DOOR_CLOSE,
                SoundEvents.WOODEN_DOOR_OPEN
        );
    }

    public static FramedGateBlock iron(Properties props) {
        return new FramedGateBlock(
                BlockType.FRAMED_IRON_GATE,
                props.requiresCorrectToolForDrops(),
                SoundEvents.IRON_DOOR_CLOSE,
                SoundEvents.IRON_DOOR_OPEN
        );
    }
}
