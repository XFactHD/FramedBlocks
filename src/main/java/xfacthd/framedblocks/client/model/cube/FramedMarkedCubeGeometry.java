package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.util.Utils;

import java.util.*;

public class FramedMarkedCubeGeometry extends FramedCubeGeometry
{
    public static final ResourceLocation SLIME_FRAME_LOCATION = Utils.rl("block/slime_frame");
    public static final ResourceLocation REDSTONE_FRAME_LOCATION = Utils.rl("block/redstone_frame");

    private final BlockState state;
    private final BakedModel frameModel;

    private FramedMarkedCubeGeometry(GeometryFactory.Context ctx, ResourceLocation frameLocation)
    {
        super(ctx);
        this.state = ctx.state();
        this.frameModel = ctx.modelLookup().get(frameLocation);
    }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        FramedBlockData fbData = extraData.get(FramedBlockData.PROPERTY);
        if (fbData != null && !fbData.getCamoState().isAir())
        {
            return ModelUtils.CUTOUT;
        }
        return ChunkRenderTypeSet.none();
    }

    @Override
    public void getAdditionalQuads(ArrayList<BakedQuad> quads, Direction side, RandomSource rand, ModelData data, RenderType renderType)
    {
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (fbData != null && !fbData.getCamoState().isAir())
        {
            Utils.copyAll(frameModel.getQuads(state, side, rand, data, renderType), quads);
        }
    }



    public static FramedMarkedCubeGeometry slime(GeometryFactory.Context ctx)
    {
        return new FramedMarkedCubeGeometry(ctx, SLIME_FRAME_LOCATION);
    }

    public static FramedMarkedCubeGeometry redstone(GeometryFactory.Context ctx)
    {
        return new FramedMarkedCubeGeometry(ctx, REDSTONE_FRAME_LOCATION);
    }
}
