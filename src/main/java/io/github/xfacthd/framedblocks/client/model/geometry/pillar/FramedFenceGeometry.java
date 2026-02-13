package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedFenceGeometry extends Geometry
{
    private final boolean north;
    private final boolean east;
    private final boolean south;
    private final boolean west;

    public FramedFenceGeometry(GeometryFactory.Context ctx)
    {
        this.north = ctx.state().getValue(BlockStateProperties.NORTH);
        this.east = ctx.state().getValue(BlockStateProperties.EAST);
        this.south = ctx.state().getValue(BlockStateProperties.SOUTH);
        this.west = ctx.state().getValue(BlockStateProperties.WEST);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (Utils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                    .export(quadMap.get(quadDir));
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(quadDir.getClockWise(), 10F/16F))
                    .apply(Modifiers.cut(quadDir.getCounterClockWise(), 10F/16F))
                    .apply(Modifiers.setPosition(10F/16F))
                    .export(quadMap.get(null));
        }

        createFenceBars(quadMap, quad, Direction.NORTH, north);
        createFenceBars(quadMap, quad, Direction.EAST, east);
        createFenceBars(quadMap, quad, Direction.SOUTH, south);
        createFenceBars(quadMap, quad, Direction.WEST, west);
    }

    private static void createFenceBars(QuadMap quadMap, BakedQuad quad, Direction dir, boolean active)
    {
        if (!active)
        {
            return;
        }

        Direction quadDir = quad.direction();
        if (Utils.isY(quadDir))
        {
            QuadModifier mod = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), 6F/16F))
                    .apply(Modifiers.cut(dir.getClockWise(), 9F/16F))
                    .apply(Modifiers.cut(dir.getCounterClockWise(), 9F/16F));

            mod.derive().apply(Modifiers.setPosition(quadDir == Direction.UP ? 15F/16F : 4F/16F))
                    .export(quadMap.get(null));

            mod.apply(Modifiers.setPosition(quadDir == Direction.UP ? 9F/16F : 10F/16F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
        {
            boolean neg = !Utils.isPositive(dir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(neg ? 0F : 10F/16F, 6F/16F, neg ? 6F/16F : 1F, 9F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(neg ? 0F : 10F/16F, 12F/16F, neg ? 6F/16F : 1F, 15F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F/16F, 6F/16F, 9F/16F, 9F/16F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(7F/16F, 12F/16F, 9F/16F, 15F/16F))
                    .export(quadMap.get(quadDir));
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}