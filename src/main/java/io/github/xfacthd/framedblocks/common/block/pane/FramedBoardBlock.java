package io.github.xfacthd.framedblocks.common.block.pane;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.item.block.FramedSpecialBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedBoardBlock extends FramedBlock {
    private static final Direction[] HOR_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final int DEFAULT_FACE = 1 << Direction.DOWN.ordinal();

    public FramedBoardBlock(Properties props) {
        super(BlockType.FRAMED_BOARD, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.FACES, DEFAULT_FACE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACES);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) -> {
                    Level level = modCtx.getLevel();
                    BlockPos pos = modCtx.getClickedPos();
                    Direction face = modCtx.getClickedFace().getOpposite();
                    BlockState prevState = level.getBlockState(pos);
                    if (prevState.is(this)) {
                        if (modCtx.replacingClickedOnBlock()) {
                            face = DirUtils.getDirByCross(face, modCtx.getClickLocation());
                        }
                        return isFacePresent(prevState, face) ? null : setFacePresent(prevState, face, true);
                    }
                    return state.setValue(PropertyHolder.FACES, 1 << face.ordinal());
                })
                .withWater()
                .build();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        if (ctx.getPlayer() != null && !ctx.getPlayer().isShiftKeyDown() && ctx.getItemInHand().is(asItem())) {
            if (!ctx.getItemInHand().getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY).isEmpty()) {
                return false;
            }

            if (ctx.replacingClickedOnBlock()) {
                Direction face = ctx.getClickedFace();
                double fraction = MathUtils.fractionInDir(ctx.getClickLocation(), face);
                return !isFacePresent(state, face) || (fraction > 0F && fraction < (3F / 32F));
            }
            return true;
        }
        return false;
    }

    @Override
    public IFramedBlockItem createBlockItem(Item.Properties props) {
        return new FramedSpecialBlockItem.Single(this, props) {
            @Override
            protected @Nullable BlockState getReplacementState(BlockPlaceContext ctx, BlockState originalState) {
                return FramedBoardBlock.this.getStateForPlacement(ctx);
            }
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        int mask = 0;
        for (Direction side : HOR_DIRECTIONS) {
            if (isFacePresent(state, side)) {
                mask |= 1 << side.get2DDataValue();
            }
        }
        mask |= mask << 4;
        mask = Integer.rotateRight(mask, 4 - rotation.ordinal());
        for (Direction side : HOR_DIRECTIONS) {
            boolean set = (mask & (1 << side.get2DDataValue())) != 0;
            state = setFacePresent(state, side, set);
        }
        return state;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }

        boolean invZ = mirror == Mirror.LEFT_RIGHT;
        Direction dirOne = invZ ? Direction.NORTH : Direction.WEST;
        Direction dirTwo = invZ ? Direction.SOUTH : Direction.EAST;
        boolean temp = isFacePresent(state, dirOne);
        state = setFacePresent(state, dirOne, isFacePresent(state, dirTwo));
        return setFacePresent(state, dirTwo, temp);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public @Nullable Direction getHorizontalOrientation(BlockState state) {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }

    public static boolean isFacePresent(BlockState state, Direction side) {
        return (state.getValue(PropertyHolder.FACES) & (1 << side.ordinal())) != 0;
    }

    public static BlockState setFacePresent(BlockState state, Direction side, boolean present) {
        int faces = state.getValue(PropertyHolder.FACES);
        int mask = 1 << side.ordinal();
        if (present) {
            faces |= mask;
        } else {
            faces &= ~mask;
        }
        return faces == 0 ? state : state.setValue(PropertyHolder.FACES, faces);
    }
}
