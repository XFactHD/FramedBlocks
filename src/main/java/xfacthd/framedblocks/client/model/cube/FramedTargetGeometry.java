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
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.model.util.ModelUtils;

import java.util.ArrayList;

public class FramedTargetGeometry implements Geometry
{
    public static final ResourceLocation OVERLAY_LOCATION = Utils.rl("block/target_overlay");
    public static final int OVERLAY_TINT_IDX = 1024;

    private final BlockState state;
    private final BakedModel overlayModel;

    public FramedTargetGeometry(GeometryFactory.Context ctx)
    {
        this.state = ctx.state();
        this.overlayModel = ctx.modelAccessor().get(OVERLAY_LOCATION);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad) { }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelUtils.CUTOUT;
    }

    @Override
    public void getAdditionalQuads(ArrayList<BakedQuad> quads, Direction side, RandomSource rand, ModelData data, RenderType renderType)
    {
        Utils.copyAll(overlayModel.getQuads(state, side, rand, data, renderType), quads);
    }
}
