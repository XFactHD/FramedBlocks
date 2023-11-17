package xfacthd.framedblocks.client.model.rail;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.util.ModelCache;
import xfacthd.framedblocks.client.model.slope.FramedSlopeGeometry;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.FramedUtils;

import javax.annotation.Nullable;
import java.util.*;

public class FramedRailSlopeGeometry extends FramedSlopeGeometry
{
    private final BlockState railState;

    private FramedRailSlopeGeometry(GeometryFactory.Context ctx, BlockState railBlock, EnumProperty<RailShape> shapeProperty)
    {
        super(new GeometryFactory.Context(getSlopeState(ctx.state()), ctx.baseModel(), ctx.modelAccessor()));

        RailShape shape = ctx.state().getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        railState = railBlock.setValue(shapeProperty, shape);
    }

    @Override
    public void getAdditionalQuads(
            QuadMap quadMap,
            BlockState state,
            RandomSource rand,
            ModelData data,
            RenderType renderType
    )
    {
        quadMap.get(null).addAll(getRailQuads(null, rand, renderType));

        for (Direction side : Direction.values())
        {
            quadMap.get(side).addAll(getRailQuads(side, rand, renderType));
        }
    }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelCache.getRenderTypes(railState, rand, ModelData.EMPTY);
    }

    private List<BakedQuad> getRailQuads(@Nullable Direction side, RandomSource rand, RenderType layer)
    {
        return ModelCache.getModel(railState).getQuads(railState, side, rand, ModelData.EMPTY, layer);
    }

    private static BlockState getSlopeState(BlockState state)
    {
        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        Direction dir = FramedUtils.getDirectionFromAscendingRailShape(shape);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return FBContent.BLOCK_FRAMED_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, dir)
                .setValue(FramedProperties.Y_SLOPE, ySlope);
    }



    public static FramedRailSlopeGeometry normal(GeometryFactory.Context ctx)
    {
        return new FramedRailSlopeGeometry(ctx, Blocks.RAIL.defaultBlockState(), BlockStateProperties.RAIL_SHAPE);
    }

    public static FramedRailSlopeGeometry powered(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(BlockStateProperties.POWERED);
        return new FramedRailSlopeGeometry(
                ctx,
                Blocks.POWERED_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, powered),
                BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }

    public static FramedRailSlopeGeometry detector(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(BlockStateProperties.POWERED);
        return new FramedRailSlopeGeometry(
                ctx,
                Blocks.DETECTOR_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, powered),
                BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }

    public static FramedRailSlopeGeometry activator(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(BlockStateProperties.POWERED);
        return new FramedRailSlopeGeometry(
                ctx,
                Blocks.ACTIVATOR_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, powered),
                BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }

    public static BlockState itemSourceNormal()
    {
        return FBContent.BLOCK_FRAMED_RAIL_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH);
    }

    public static BlockState itemSourcePowered()
    {
        return FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH);
    }

    public static BlockState itemSourceDetector()
    {
        return FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH);
    }

    public static BlockState itemSourceActivator()
    {
        return FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH);
    }
}