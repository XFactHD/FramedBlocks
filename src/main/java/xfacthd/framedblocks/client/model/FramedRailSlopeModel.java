package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class FramedRailSlopeModel extends BakedModelProxy
{
    private final BlockState slopeState;
    private final BlockState railState;
    private IBakedModel slopeModel = null;
    private IBakedModel railModel = null;

    public FramedRailSlopeModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel);

        Direction dir = state.get(PropertyHolder.FACING_HOR);
        RailShape shape = state.get(PropertyHolder.ASCENDING_RAIL_SHAPE);

        slopeState = FBContent.blockFramedSlope.get().getDefaultState().with(PropertyHolder.FACING_HOR, dir);
        railState = Blocks.RAIL.getDefaultState().with(BlockStateProperties.RAIL_SHAPE, shape);
    }

    public FramedRailSlopeModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedRailSlope.get().getDefaultState()
                        .with(PropertyHolder.FACING_HOR, Direction.SOUTH)
                        .with(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH),
                baseModel
        );
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        List<BakedQuad> quads = new ArrayList<>(getSlopeQuads(side, rand, extraData));

        RenderType layer = MinecraftForgeClient.getRenderLayer();
        if (layer == RenderType.getCutout() || layer == null)
        {
            quads.addAll(getRailQuads(side, rand));
        }

        return quads;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    private List<BakedQuad> getSlopeQuads(@Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        if (slopeModel == null)
        {
            BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            slopeModel = dispatcher.getModelForState(slopeState);
        }
        return slopeModel.getQuads(slopeState, side, rand, extraData);
    }

    private List<BakedQuad> getRailQuads(@Nullable Direction side, Random rand)
    {
        if (railModel == null)
        {
            BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            railModel = dispatcher.getModelForState(railState);
        }
        return railModel.getQuads(railState, side, rand, EmptyModelData.INSTANCE);
    }
}