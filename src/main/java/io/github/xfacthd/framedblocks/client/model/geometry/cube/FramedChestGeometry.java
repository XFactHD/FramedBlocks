package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.ChestState;
import io.github.xfacthd.framedblocks.common.data.property.LatchType;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jspecify.annotations.Nullable;

public class FramedChestGeometry extends Geometry {
    private final BlockState state;
    private final BlockStateModel baseModel;
    private final Direction facing;
    private final ChestType type;
    private final boolean closed;
    private final LatchType latch;

    public FramedChestGeometry(GeometryFactory.Context ctx) {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(BlockStateProperties.CHEST_TYPE);
        this.closed = ctx.state().getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSED;
        this.latch = ctx.state().getValue(PropertyHolder.LATCH_TYPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getAxis(), 15F/16F))
                    .applyIf(Modifiers.cut(facing.getClockWise(), 15F/16F), type != ChestType.LEFT)
                    .applyIf(Modifiers.cut(facing.getCounterClockWise(), 15F/16F), type != ChestType.RIGHT)
                    .applyIf(Modifiers.setPosition(closed ? 14F/16F : 10F/16F), quadDir == Direction.UP)
                    .export(quadMap, quadDir == Direction.UP ? null : quadDir);
        } else if (quadDir.getAxis() == facing.getAxis()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, closed ? 14F/16F : 10F/16F))
                    .applyIf(Modifiers.cut(facing.getClockWise(), 15F/16F), type != ChestType.LEFT)
                    .applyIf(Modifiers.cut(facing.getCounterClockWise(), 15F/16F), type != ChestType.RIGHT)
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap, null);
        } else {
            boolean offset = (type != ChestType.RIGHT || quadDir != facing.getCounterClockWise()) && (type != ChestType.LEFT || quadDir != facing.getClockWise());
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, closed ? 14F/16F : 10F/16F))
                    .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 15F/16F))
                    .applyIf(Modifiers.setPosition(15F/16F), offset)
                    .export(quadMap, offset ? null : quadDir);
        }

        if (latch == LatchType.CAMO && closed) {
            makeChestLatch(quadMap, quad, facing, type);
        }
    }

    public static void makeChestLatch(QuadMapBuilder quadMap, BakedQuad quad, Direction facing, ChestType type) {
        Direction face = quad.direction();
        float length = type == ChestType.SINGLE ? 9F/16F : 1F/16F;

        if (face == facing || face == facing.getOpposite()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.DOWN, 9F/16F))
                    .apply(Modifiers.cut(Direction.UP, 11F/16F))
                    .applyIf(Modifiers.cut(facing.getClockWise(), length), type != ChestType.LEFT)
                    .applyIf(Modifiers.cut(facing.getCounterClockWise(), length), type != ChestType.RIGHT)
                    .applyIf(Modifiers.setPosition(1F/16F), face != facing)
                    .export(quadMap, face == facing ? facing : null);
        } else if (DirUtils.isY(face)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), 1F/16F))
                    .applyIf(Modifiers.cut(facing.getClockWise(), length), type != ChestType.LEFT)
                    .applyIf(Modifiers.cut(facing.getCounterClockWise(), length), type != ChestType.RIGHT)
                    .apply(Modifiers.setPosition(face == Direction.UP ? 11F/16F : 9F/16F))
                    .export(quadMap, null);
        } else {
            boolean offset = (type != ChestType.RIGHT || face != facing.getCounterClockWise()) && (type != ChestType.LEFT || face != facing.getClockWise());
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(0, 7F/16F, 1, 11F/16F))
                    .apply(Modifiers.cut(facing.getOpposite(), 1F/16F))
                    .applyIf(Modifiers.setPosition(length), offset)
                    .export(quadMap, offset ? null : face);
        }
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        if (closed && latch == LatchType.DEFAULT) {
            consumer.acceptAll(baseModel, level, pos, random, state, true, false, false, null, null);
        }
    }
}
