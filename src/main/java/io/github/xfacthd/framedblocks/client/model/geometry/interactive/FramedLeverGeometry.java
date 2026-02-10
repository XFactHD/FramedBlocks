package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.geometry.QuadListModifier;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public class FramedLeverGeometry extends Geometry
{
    private static final QuadListModifier HANDLE_FILTER = QuadListModifier.filtering(ClientUtils::isDummyTexture);
    private static final BlockState AUX_SHADER_STATE = Blocks.LEVER.defaultBlockState();

    private static final float MIN_SMALL = 5F/16F;
    private static final float MAX_SMALL = 11F/16F;
    private static final float MIN_LARGE = 4F/16F;
    private static final float MAX_LARGE = 12F/16F;
    private static final float HEIGHT = 3F/16F;

    private final BlockState state;
    private final BlockStateModel baseModel;
    private final Direction dir;
    private final AttachFace face;

    public FramedLeverGeometry(GeometryFactory.Context ctx)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.face = ctx.state().getValue(BlockStateProperties.ATTACH_FACE);
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data, @Nullable Object cacheKeyUserData)
    {
        consumer.acceptAll(baseModel, level, pos, random, state, true, false, false, false, AUX_SHADER_STATE, HANDLE_FILTER);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, ModelData modelData)
    {
        Direction quadDir = quad.direction();
        Direction facing = getFacing();
        boolean quadInDir = quadDir == facing;

        if (Utils.isY(facing))
        {
            if (quadDir.getAxis() == facing.getAxis())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getAxis(), MAX_LARGE))
                        .apply(Modifiers.cut(dir.getClockWise().getAxis(), MAX_SMALL))
                        .applyIf(Modifiers.setPosition(HEIGHT), quadInDir)
                        .export(quadMap.get(quadInDir ? null : quadDir));
            }
            else
            {
                boolean smallSide = dir.getAxis() == quadDir.getAxis();

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(facing, HEIGHT))
                        .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), smallSide ? MAX_SMALL : MAX_LARGE))
                        .apply(Modifiers.setPosition(smallSide ? MAX_LARGE : MAX_SMALL))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            if (quadDir.getAxis() == facing.getAxis())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(MIN_SMALL, MIN_LARGE, MAX_SMALL, MAX_LARGE))
                        .applyIf(Modifiers.setPosition(HEIGHT), quadInDir)
                        .export(quadMap.get(quadInDir ? null : quadDir));
            }
            else if (Utils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, HEIGHT))
                        .apply(Modifiers.cut(dir.getClockWise().getAxis(), MAX_SMALL))
                        .apply(Modifiers.setPosition(MAX_LARGE))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, HEIGHT))
                        .apply(Modifiers.cut(Direction.Axis.Y, MAX_LARGE))
                        .apply(Modifiers.setPosition(MAX_SMALL))
                        .export(quadMap.get(null));
            }
        }
    }

    private Direction getFacing()
    {
        return switch (face)
        {
            case FLOOR -> Direction.UP;
            case WALL -> dir;
            case CEILING -> Direction.DOWN;
        };
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}