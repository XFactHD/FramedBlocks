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
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.client.model.slope.FramedSlopeGeometry;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.*;

public class FramedRailSlopeGeometry extends FramedSlopeGeometry
{
    private final BlockState railState;

    private FramedRailSlopeGeometry(GeometryFactory.Context ctx, BlockState railBlock, EnumProperty<RailShape> shapeProperty)
    {
        super(new GeometryFactory.Context(getSlopeState(ctx.state()), ctx.baseModel(), ctx.modelLookup(), ctx.textureLookup()));

        RailShape shape = ctx.state().getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        railState = railBlock.setValue(shapeProperty, shape);
    }

    @Override
    public void getAdditionalQuads(
            QuadMap quadMap,
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
        return ModelUtils.getRenderTypes(railState, rand, ModelData.EMPTY);
    }

    private List<BakedQuad> getRailQuads(@Nullable Direction side, RandomSource rand, RenderType layer)
    {
        return ModelUtils.getModel(railState).getQuads(railState, side, rand, ModelData.EMPTY, layer);
    }

    private static BlockState getSlopeState(BlockState state)
    {
        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        Direction dir = FramedUtils.getDirectionFromAscendingRailShape(shape);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return FBContent.BLOCK_FRAMED_SLOPE.value()
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
}