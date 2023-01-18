package xfacthd.framedblocks.client.model.rail;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.util.ModelCache;
import xfacthd.framedblocks.client.model.slope.FramedSlopeModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.FramedUtils;

import javax.annotation.Nullable;
import java.util.*;

public class FramedRailSlopeModel extends FramedSlopeModel
{
    private final BlockState railState;

    private FramedRailSlopeModel(BlockState state, BakedModel baseModel, BlockState railBlock, EnumProperty<RailShape> shapeProperty)
    {
        super(getSlopeState(state), baseModel);

        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        railState = railBlock.setValue(shapeProperty, shape);
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType renderType)
    {
        quadMap.get(null).addAll(getRailQuads(null, rand, renderType));

        for (Direction side : Direction.values())
        {
            quadMap.get(side).addAll(getRailQuads(side, rand, renderType));
        }
    }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
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

        return FBContent.blockFramedSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, dir)
                .setValue(FramedProperties.Y_SLOPE, ySlope);
    }



    public static FramedRailSlopeModel normal(BlockState state, BakedModel baseModel)
    {
        return new FramedRailSlopeModel(state, baseModel, Blocks.RAIL.defaultBlockState(), BlockStateProperties.RAIL_SHAPE);
    }

    public static FramedRailSlopeModel powered(BlockState state, BakedModel baseModel)
    {
        boolean powered = state.getValue(BlockStateProperties.POWERED);
        return new FramedRailSlopeModel(
                state,
                baseModel,
                Blocks.POWERED_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, powered),
                BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }

    public static FramedRailSlopeModel detector(BlockState state, BakedModel baseModel)
    {
        boolean powered = state.getValue(BlockStateProperties.POWERED);
        return new FramedRailSlopeModel(
                state,
                baseModel,
                Blocks.DETECTOR_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, powered),
                BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }

    public static FramedRailSlopeModel activator(BlockState state, BakedModel baseModel)
    {
        boolean powered = state.getValue(BlockStateProperties.POWERED);
        return new FramedRailSlopeModel(
                state,
                baseModel,
                Blocks.ACTIVATOR_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, powered),
                BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }

    public static BlockState itemSourceNormal()
    {
        return FBContent.blockFramedRailSlope.get().defaultBlockState().setValue(
                PropertyHolder.ASCENDING_RAIL_SHAPE,
                RailShape.ASCENDING_SOUTH
        );
    }

    public static BlockState itemSourcePowered()
    {
        return FBContent.blockFramedPoweredRailSlope.get().defaultBlockState().setValue(
                PropertyHolder.ASCENDING_RAIL_SHAPE,
                RailShape.ASCENDING_SOUTH
        );
    }

    public static BlockState itemSourceDetector()
    {
        return FBContent.blockFramedDetectorRailSlope.get().defaultBlockState().setValue(
                PropertyHolder.ASCENDING_RAIL_SHAPE,
                RailShape.ASCENDING_SOUTH
        );
    }

    public static BlockState itemSourceActivator()
    {
        return FBContent.blockFramedActivatorRailSlope.get().defaultBlockState().setValue(
                PropertyHolder.ASCENDING_RAIL_SHAPE,
                RailShape.ASCENDING_SOUTH
        );
    }
}