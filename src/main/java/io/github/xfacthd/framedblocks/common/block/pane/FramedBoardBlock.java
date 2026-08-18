package io.github.xfacthd.framedblocks.common.block.pane;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.block.item.placement.ValueOrders;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintBlockPlaceContext;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinter;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinters;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.item.block.FramedSpecialBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FramedBoardBlock extends FramedBlock {
    private static final Direction[] HOR_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final int DEFAULT_FACE = 1 << Direction.DOWN.ordinal();
    private static final List<Integer> SINGLE_FACE_VALUES = ValueOrders.FACING
            .stream()
            .map(dir -> 1 << dir.ordinal())
            .toList();
    private static final ValuePrinter<Integer> FACE_PRINTER = (face, defaultColor) ->
            ValuePrinters.DIRECTION.printStyled(ValueOrders.FACING.get(Integer.numberOfTrailingZeros(face)), defaultColor);

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
                            if (DirUtils.isFaceCenter(face, modCtx.getClickLocation(), 2D/16D)) {
                                face = face.getOpposite();
                            } else {
                                face = DirUtils.getDirByCross(face, modCtx.getClickLocation());
                            }
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
        if (ctx instanceof BlueprintBlockPlaceContext) {
            return false;
        }
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
        return new FramedSpecialBlockItem.Single(this, true, props) {
            @Override
            protected @Nullable BlockState getReplacementState(BlockPlaceContext ctx, BlockState originalState, @Nullable BlockState manualState) {
                if (manualState == null) {
                    return FramedBoardBlock.this.getStateForPlacement(ctx);
                }

                int originalFaces = originalState.getValue(PropertyHolder.FACES);
                int combinedFaces = originalFaces | manualState.getValue(PropertyHolder.FACES);
                if (combinedFaces != originalFaces) {
                    return originalState.setValue(PropertyHolder.FACES, combinedFaces);
                }
                return null;
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
        int faces = state.getValue(PropertyHolder.FACES);
        for (Direction side : HOR_DIRECTIONS) {
            boolean set = (mask & (1 << side.get2DDataValue())) != 0;
            int sideMask = 1 << side.ordinal();
            if (set) {
                faces |= sideMask;
            } else {
                faces &= ~sideMask;
            }
        }
        return state.setValue(PropertyHolder.FACES, faces);
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
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return TriState.DEFAULT;
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.builder(this)
                .property(PropertyHolder.FACES, builder -> builder
                        .values(SINGLE_FACE_VALUES)
                        .printer(PropertyLabels.FACING, FACE_PRINTER)
                )
                .build();
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
