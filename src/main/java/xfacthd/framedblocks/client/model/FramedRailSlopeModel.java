package xfacthd.framedblocks.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedRailSlopeBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

import javax.annotation.Nullable;
import java.util.*;

public class FramedRailSlopeModel extends FramedSlopeModel
{
    private final BlockState railState;
    private BakedModel railModel = null;

    public FramedRailSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(getSlopeState(state), baseModel);

        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        railState = Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, shape);
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
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData) { return ModelUtils.CUTOUT; }

    private List<BakedQuad> getRailQuads(@Nullable Direction side, RandomSource rand, RenderType layer)
    {
        if (railModel == null)
        {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            railModel = dispatcher.getBlockModel(railState);
        }
        return railModel.getQuads(railState, side, rand, ModelData.EMPTY, layer);
    }

    private static BlockState getSlopeState(BlockState state)
    {
        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        Direction dir = FramedRailSlopeBlock.directionFromShape(shape);

        return FBContent.blockFramedSlope.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, dir);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedRailSlope.get().defaultBlockState().setValue(
                PropertyHolder.ASCENDING_RAIL_SHAPE,
                RailShape.ASCENDING_SOUTH
        );
    }
}