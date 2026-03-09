package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.geometry.QuadListModifier;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class FramedItemFrameGeometry extends Geometry
{
    private static final int GLOWING_BRIGHTNESS = 5;
    private static final QuadListModifier GLOWING_LEATHER_MODIFIER = (quadMap, quads, side) ->
            quads.replaceAll(quad -> Objects.requireNonNull(QuadModifier.of(quad).apply(Modifiers.setLightEmission(GLOWING_BRIGHTNESS, false)).exportDirect()));

    private final BlockState state;
    private final BlockStateModel baseModel;
    private final Direction facing;
    private final boolean leather;
    private final boolean mapFrame;
    private final float innerLength;
    private final float innerPos;
    private final float innerMin;
    private final float innerMax;
    private final float outerMin;
    private final float outerMax;
    private final int lightEmission;
    @Nullable
    private final QuadListModifier leatherModifier;

    private FramedItemFrameGeometry(GeometryFactory.Context ctx, boolean glowing)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.facing = ctx.state().getValue(BlockStateProperties.FACING);
        this.leather = ctx.state().getValue(PropertyHolder.LEATHER);
        this.mapFrame = ctx.state().getValue(PropertyHolder.MAP_FRAME);

        this.innerLength = mapFrame ? 15F/16F : 13F/16F;
        this.innerPos = mapFrame ? 1F/16F : 3F/16F;
        this.innerMin = mapFrame ? 1F/16F : 3F/16F;
        this.innerMax = mapFrame ? 15F/16F : 13F/16F;
        this.outerMin = mapFrame ? 0F : 2F/16F;
        this.outerMax = mapFrame ? 1F : 14F/16F;
        this.lightEmission = glowing ? GLOWING_BRIGHTNESS : 0;
        this.leatherModifier = glowing && leather ? GLOWING_LEATHER_MODIFIER : null;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadFace = quad.direction();
        if (DirUtils.isY(facing))
        {
            makeVerticalFrame(quadMap, quad, quadFace);
        }
        else
        {
            makeHorizontalFrame(quadMap, quad, quadFace);
        }
    }

    private void makeVerticalFrame(QuadMap quadMap, BakedQuad quad, Direction quadFace)
    {
        if (quadFace == facing)
        {
            QuadModifier.of(quad)
                    .applyIf(Modifiers.cutTopBottom(outerMin, outerMin, outerMax, outerMax), !mapFrame)
                    .apply(Modifiers.setLightEmission(lightEmission, true))
                    .export(quadMap.get(quadFace));
        }
        else if (quadFace == facing.getOpposite())
        {
            if (!leather && !mapFrame)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(innerMin, innerMin, innerMax, innerMax))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(.5F/16F))
                        .export(quadMap.get(null));
            }

            if (!mapFrame || leather)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(outerMin, outerMin, innerMin, outerMax))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F / 16F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(innerMax, outerMin, outerMax, outerMax))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F / 16F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(innerMin, outerMin, innerMax, innerMin))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F / 16F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(innerMin, innerMax, innerMax, outerMax))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F / 16F))
                        .export(quadMap.get(null));
            }

            if (mapFrame && !leather)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(quadFace));
            }
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), 1F/16F))
                    .applyIf(Modifiers.cut(quadFace.getClockWise().getAxis(), outerMax), !mapFrame)
                    .apply(Modifiers.setLightEmission(lightEmission, true))
                    .applyIf(Modifiers.setPosition(outerMax), !mapFrame)
                    .export(quadMap.get(null));

            if (!mapFrame)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing, 15.5F / 16F))
                        .apply(Modifiers.cut(facing.getOpposite(), 1F / 16F))
                        .apply(Modifiers.cut(quadFace.getClockWise().getAxis(), innerLength))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(innerPos))
                        .export(quadMap.get(null));
            }
        }
    }

    private void makeHorizontalFrame(QuadMap quadMap, BakedQuad quad, Direction quadFace)
    {
        if (quadFace == facing)
        {
            QuadModifier.of(quad)
                    .applyIf(Modifiers.cutSide(outerMin, outerMin, outerMax, outerMax), !mapFrame)
                    .apply(Modifiers.setLightEmission(lightEmission, true))
                    .export(quadMap.get(quadFace));
        }
        else if (quadFace == facing.getOpposite())
        {
            if (!leather && !mapFrame)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(innerMin, innerMin, innerMax, innerMax))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(.5F/16F))
                        .export(quadMap.get(null));
            }

            if (!mapFrame || leather)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(outerMin, outerMin, innerMin, outerMax))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(innerMax, outerMin, outerMax, outerMax))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(innerMin, outerMin, innerMax, innerMin))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(innerMin, innerMax, innerMax, outerMax))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(null));
            }

            if (mapFrame && !leather)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(quadFace));
            }
        }
        else if (DirUtils.isY(quadFace))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), 1F/16F))
                    .applyIf(Modifiers.cut(facing.getClockWise().getAxis(), outerMax), !mapFrame)
                    .apply(Modifiers.setLightEmission(lightEmission, true))
                    .applyIf(Modifiers.setPosition(outerMax), !mapFrame)
                    .export(quadMap.get(null));

            if (!mapFrame)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing, 15.5F/16F))
                        .apply(Modifiers.cut(facing.getOpposite(), 1F/16F))
                        .apply(Modifiers.cut(facing.getClockWise().getAxis(), innerLength))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(innerPos))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), 1F/16F))
                    .applyIf(Modifiers.cut(Direction.Axis.Y, outerMax), !mapFrame)
                    .apply(Modifiers.setLightEmission(lightEmission, true))
                    .applyIf(Modifiers.setPosition(outerMax), !mapFrame)
                    .export(quadMap.get(null));

            if (!mapFrame)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing, 15.5F/16F))
                        .apply(Modifiers.cut(facing.getOpposite(), 1F/16F))
                        .apply(Modifiers.cut(Direction.Axis.Y, innerLength))
                        .apply(Modifiers.setLightEmission(lightEmission, true))
                        .apply(Modifiers.setPosition(innerPos))
                        .export(quadMap.get(null));
            }
        }
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        if (leather)
        {
            consumer.acceptAll(baseModel, level, pos, random, state, true, false, false, false, null, leatherModifier);
        }
    }

    public static FramedItemFrameGeometry normal(GeometryFactory.Context ctx)
    {
        return new FramedItemFrameGeometry(ctx, false);
    }

    public static FramedItemFrameGeometry glowing(GeometryFactory.Context ctx)
    {
        return new FramedItemFrameGeometry(ctx, true);
    }
}
