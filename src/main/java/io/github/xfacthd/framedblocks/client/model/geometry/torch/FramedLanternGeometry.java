package io.github.xfacthd.framedblocks.client.model.geometry.torch;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.MultiQuadModifier;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.ChainType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FramedLanternGeometry extends Geometry
{
    private static final Vector3f ROT_ORIGIN = new Vector3f(.5F, .5F, .5F);

    private final BlockState state;
    private final boolean hanging;
    private final ChainType chain;
    private final boolean closedHead;
    private final BlockStateModel baseModel;
    private final BlockState auxShaderState;

    private FramedLanternGeometry(GeometryFactory.Context ctx, boolean closedHead, BlockState auxShaderState)
    {
        this.state = ctx.state();
        this.hanging = ctx.state().getValue(BlockStateProperties.HANGING);
        this.chain = ctx.state().getValue(PropertyHolder.CHAIN_TYPE);
        this.closedHead = closedHead;
        this.baseModel = ctx.baseModel();
        this.auxShaderState = auxShaderState;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir))
        {
            boolean up = quadDir == Direction.UP;

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(5F/16F, 5F/16F, 11F/16F, 11F/16F))
                    .applyIf(Modifiers.setPosition(15F/16F), hanging && !up)
                    .applyIf(Modifiers.setPosition(hanging ? 8F/16F : 7F/16F), up)
                    .export(hanging || up ? quadMap.get(null) : quadMap.get(Direction.DOWN));

            if (up)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                        .apply(Modifiers.setPosition(hanging ? 10F/16F : 9F/16F))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            List<QuadModifier> sideMods = List.of(
                    QuadModifier.of(quad).apply(Modifiers.cutSide(5F/16F, 0, 6F/16F, 7F/16F)),
                    QuadModifier.of(quad).apply(Modifiers.cutSide(10F/16F, 0, 11F/16F, 7F/16F)),
                    QuadModifier.of(quad).apply(Modifiers.cutSide(6F/16F, 0, 10F/16F, 1F/16F)),
                    QuadModifier.of(quad).apply(Modifiers.cutSide(6F/16F, 6F/16F, 10F/16F, 7F/16F))
            );
            for (QuadModifier mod : sideMods)
            {
                mod.applyIf(Modifiers.offset(Direction.UP, 1F/16F), hanging)
                        .apply(Modifiers.setPosition(11F/16F))
                        .export(quadMap.get(null));
            }

            QuadModifier.of(quad).apply(Modifiers.cutSide(6F/16F, closedHead ? 7F/16F : 8F/16F, 10F/16F, 9F/16F))
                    .applyIf(Modifiers.offset(Direction.UP, 1F/16F), hanging)
                    .apply(Modifiers.setPosition(10F/16F))
                    .export(quadMap.get(null));

            if (chain == ChainType.CAMO)
            {
                createCamoChain(quadMap, quad, quadDir);
            }
        }
    }

    private void createCamoChain(QuadMap quadMap, BakedQuad quad, Direction quadDir)
    {
        Direction.Axis quadPerpAxis = DirUtils.isX(quadDir) ? Direction.Axis.Z : Direction.Axis.X;
        Direction dirNeg = quadPerpAxis.getNegative();
        Direction dirPos = quadPerpAxis.getPositive();

        List<MultiQuadModifier> modifiers = new ArrayList<>();
        MultiQuadModifier baseEdgeMod = new MultiQuadModifier(
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dirNeg, 10F/16F))
                        .apply(Modifiers.cut(dirPos,  7F/16F))
                        .apply(Modifiers.offset(dirPos, .5F/16F)),
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dirNeg,  7F/16F))
                        .apply(Modifiers.cut(dirPos, 10F/16F))
                        .apply(Modifiers.offset(dirNeg, .5F/16F))
        );

        if (DirUtils.isX(quadDir) || !hanging)
        {
            modifiers.add(
                    baseEdgeMod.derive()
                            .apply(Modifiers.cut(Direction.DOWN, hanging ? 6F/16F : 7F/16F))
                            .apply(Modifiers.cut(Direction.UP, hanging ? 12F/16F : 11F/16F))
            );
        }
        for (int i = 0; i < 2; i++)
        {
            if (!hanging && i == 0) continue;

            float height = i == 0 ? 2 : (hanging ? 5 : 6);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 8.5F/16F))
                    .apply(Modifiers.cut(Direction.DOWN, height / 16F))
                    .apply(Modifiers.cut(Direction.UP, (16F - height + 1F) / 16F))
                    .apply(Modifiers.setPosition(.5F))
                    .apply(Modifiers.rotate(Direction.Axis.Y, ROT_ORIGIN, 45, false))
                    .export(quadMap.get(null));
        }

        if (hanging)
        {
            if (DirUtils.isX(quadDir))
            {
                modifiers.add(
                        baseEdgeMod.derive()
                                .apply(Modifiers.cut(Direction.DOWN, 2F/16F))
                );
            }
            else if (DirUtils.isZ(quadDir))
            {
                modifiers.add(
                        baseEdgeMod.derive()
                                .apply(Modifiers.cut(Direction.DOWN, 5F/16F))
                                .apply(Modifiers.cut(Direction.UP, 15F/16F))

                );
            }
        }

        for (MultiQuadModifier mod : modifiers)
        {
            mod.apply(Modifiers.setPosition(.5F))
                    .apply(Modifiers.rotate(Direction.Axis.Y, ROT_ORIGIN, 45, false))
                    .export(quadMap.get(null));
        }
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        consumer.acceptAll(baseModel, level, pos, random, state, true, false, false, false, auxShaderState, null);
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }

    public static FramedLanternGeometry normal(GeometryFactory.Context ctx)
    {
        return new FramedLanternGeometry(ctx, false, Blocks.LANTERN.defaultBlockState());
    }

    public static FramedLanternGeometry soul(GeometryFactory.Context ctx)
    {
        return new FramedLanternGeometry(ctx, true, Blocks.SOUL_LANTERN.defaultBlockState());
    }

    public static FramedLanternGeometry copper(GeometryFactory.Context ctx)
    {
        return new FramedLanternGeometry(ctx, true, Blocks.COPPER_LANTERN.unaffected().defaultBlockState());
    }
}
