package io.github.xfacthd.framedblocks.client.model.geometry.torch;

import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class FramedWallTorchGeometry extends Geometry
{
    private static final Vector3f ROTATION_ORIGIN = new Vector3f(0, 3.5F/16F, 8F/16F);
    private static final float MIN = 7F/16F;
    private static final float MAX = 9F/16F;
    private static final float HEIGHT = 8F/16F;
    private static final float HEIGHT_REDSTONE_LIT = 7F/16F;
    private static final float BOTTOM = 12.5F/16F;

    private final BlockState state;
    private final BlockStateModel baseModel;
    private final float yAngle;
    private final BlockState auxShaderState;
    private final float height;

    private FramedWallTorchGeometry(GeometryFactory.Context ctx, BlockState auxShaderState, float height)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.yAngle = 270F - ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
        this.auxShaderState = auxShaderState;
        this.height = height;
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data, @Nullable Object cacheKeyUserData)
    {
        consumer.acceptAll(baseModel, level, pos, random, state, true, false, false, false, auxShaderState, null);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, ModelData modelData)
    {
        /*
        "from": [-1, 3.5, 7],
		"to": [1, 11.5, 9],
        "rotation": {"angle": -22.5, "axis": "z", "origin": [0, 3.5, 8]},
        */

        Direction quadDir = quad.direction();
        if (quadDir == Direction.DOWN)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(MIN, MIN, MAX, MAX))
                    .apply(Modifiers.setPosition(BOTTOM))
                    .apply(Modifiers.offset(Direction.WEST, .5F))
                    .apply(applyRotation(yAngle))
                    .export(quadMap.get(null));
        }
        else if (quadDir != Direction.UP)
        {
            boolean xAxis = Utils.isX(quadDir);
            boolean east = quadDir == Direction.EAST;
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(MIN, 0, MAX, height))
                    .applyIf(Modifiers.setPosition(east ? 1F/16F : 17F/16F), xAxis)
                    .applyIf(Modifiers.setPosition(MAX), !xAxis)
                    .applyIf(Modifiers.offset(Direction.WEST, .5F), !xAxis)
                    .apply(Modifiers.offset(Direction.UP, 3.5F/16F))
                    .apply(applyRotation(yAngle))
                    .export(quadMap.get(null));
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }

    private static QuadModifier.Modifier applyRotation(float yAngle)
    {
        return data ->
                Modifiers.rotate(Direction.Axis.Z, ROTATION_ORIGIN, -22.5F, false).accept(data) &&
                        Modifiers.rotateCentered(Direction.Axis.Y, yAngle, false).accept(data);
    }

    public static FramedWallTorchGeometry normal(GeometryFactory.Context ctx)
    {
        return new FramedWallTorchGeometry(ctx, Blocks.WALL_TORCH.defaultBlockState(), HEIGHT);
    }

    public static FramedWallTorchGeometry soul(GeometryFactory.Context ctx)
    {
        return new FramedWallTorchGeometry(ctx, Blocks.SOUL_WALL_TORCH.defaultBlockState(), HEIGHT);
    }

    public static FramedWallTorchGeometry copper(GeometryFactory.Context ctx)
    {
        return new FramedWallTorchGeometry(ctx, Blocks.COPPER_WALL_TORCH.defaultBlockState(), HEIGHT);
    }

    public static FramedWallTorchGeometry redstone(GeometryFactory.Context ctx)
    {
        float topHeight = ctx.state().getValue(BlockStateProperties.LIT) ? HEIGHT_REDSTONE_LIT : HEIGHT;
        return new FramedWallTorchGeometry(ctx, Blocks.REDSTONE_WALL_TORCH.defaultBlockState(), topHeight);
    }
}
