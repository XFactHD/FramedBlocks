package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.LatchType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jspecify.annotations.Nullable;

public class FramedChestLidGeometry extends Geometry
{
    private final BlockState state;
    private final BlockStateModel baseModel;
    private final Direction facing;
    private final ChestType type;
    private final LatchType latch;

    public FramedChestLidGeometry(GeometryFactory.Context ctx)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(BlockStateProperties.CHEST_TYPE);
        this.latch = ctx.state().getValue(PropertyHolder.LATCH_TYPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (Utils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getAxis(), 15F/16F))
                    .applyIf(Modifiers.cut(facing.getClockWise(), 15F/16F), type != ChestType.LEFT)
                    .applyIf(Modifiers.cut(facing.getCounterClockWise(), 15F/16F), type != ChestType.RIGHT)
                    .apply(Modifiers.setPosition(quadDir == Direction.UP ? 14F/16F : 7F/16F))
                    .export(quadMap.get(quadDir == Direction.UP ? null : quadDir));
        }
        else if (quadDir.getAxis() == facing.getAxis())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, 14F/16F))
                    .apply(Modifiers.cut(Direction.DOWN, 7F/16F))
                    .applyIf(Modifiers.cut(facing.getClockWise(), 15F/16F), type != ChestType.LEFT)
                    .applyIf(Modifiers.cut(facing.getCounterClockWise(), 15F/16F), type != ChestType.RIGHT)
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap.get(null));
        }
        else
        {
            boolean offset = (type != ChestType.RIGHT || quadDir != facing.getCounterClockWise()) && (type != ChestType.LEFT || quadDir != facing.getClockWise());
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, 14F/16F))
                    .apply(Modifiers.cut(Direction.DOWN, 7F/16F))
                    .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 15F/16F))
                    .applyIf(Modifiers.setPosition(15F/16F), offset)
                    .export(quadMap.get(offset ? null : quadDir));
        }

        if (latch == LatchType.CAMO)
        {
            FramedChestGeometry.makeChestLatch(quadMap, quad, facing, type);
        }
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        if (latch == LatchType.DEFAULT)
        {
            consumer.acceptAll(baseModel, level, pos, random, state, true, false, false, false, null, null);
        }
    }
}
