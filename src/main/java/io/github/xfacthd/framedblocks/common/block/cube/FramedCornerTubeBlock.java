package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerTubeOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FramedCornerTubeBlock extends FramedBlock {
    public FramedCornerTubeBlock(Properties props) {
        super(BlockType.FRAMED_CORNER_TUBE, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.THICK, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.CORNER_TYPE_ORIENTATION, PropertyHolder.THICK);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) -> {
                    Direction primDir = modCtx.getClickedFace();

                    Direction secDir;
                    Vec3 hitVec = modCtx.getClickLocation();
                    if (DirUtils.isY(primDir)) {
                        Direction forwardDir = modCtx.getHorizontalDirection();
                        double forward = MathUtils.fractionInDir(hitVec, forwardDir) - .5;
                        double sideways = MathUtils.fractionInDir(hitVec, forwardDir.getClockWise()) - .5;
                        if (Math.max(Math.abs(forward), Math.abs(sideways)) == Math.abs(forward)) {
                            secDir = forward < 0 ? forwardDir.getOpposite() : forwardDir;
                        } else {
                            secDir = sideways < 0 ? forwardDir.getCounterClockWise() : forwardDir.getClockWise();
                        }
                    } else {
                        Direction forwardDir = primDir.getOpposite();
                        boolean right = MathUtils.fractionInDir(hitVec, forwardDir.getClockWise()) > .5;
                        secDir = right ? forwardDir.getClockWise() : forwardDir.getCounterClockWise();
                    }

                    CornerTubeOrientation orientation = CornerTubeOrientation.of(primDir, secDir);
                    return state.setValue(PropertyHolder.CORNER_TYPE_ORIENTATION, orientation);
                })
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.getMainHandItem().is(FramedConstants.Objects.FRAMED_HAMMER.value())) {
            if (!level.isClientSide()) {
                state = state.setValue(PropertyHolder.THICK, !state.getValue(PropertyHolder.THICK));
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return state.getValue(PropertyHolder.CORNER_TYPE_ORIENTATION).isVertical();
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        CornerTubeOrientation orientation = state.getValue(PropertyHolder.CORNER_TYPE_ORIENTATION);
        return state.setValue(PropertyHolder.CORNER_TYPE_ORIENTATION, orientation.rotate(rotation));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        CornerTubeOrientation orientation = state.getValue(PropertyHolder.CORNER_TYPE_ORIENTATION);
        return state.setValue(PropertyHolder.CORNER_TYPE_ORIENTATION, orientation.mirror(mirror));
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(PropertyHolder.CORNER_TYPE_ORIENTATION, CornerTubeOrientation.UP_NORTH);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        return state.getValue(PropertyHolder.CORNER_TYPE_ORIENTATION).getSecondaryDir();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }
}
