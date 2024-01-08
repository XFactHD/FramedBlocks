package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.function.Predicate;

public class FramedBookshelfGeometry implements Geometry
{
    private final BakedModel baseModel;
    private final Predicate<Direction> frontFacePred;

    private FramedBookshelfGeometry(GeometryFactory.Context ctx, Predicate<Direction> frontFacePred)
    {
        this.baseModel = ctx.baseModel();
        this.frontFacePred = frontFacePred;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir) || !frontFacePred.test(quadDir))
        {
            return;
        }

        List<BakedQuad> quads = quadMap.get(quadDir);

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSideUpDown(true, 1F/16F))
                .export(quads);

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSideUpDown(false, 1F/16F))
                .export(quads);

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSideLeftRight(true, 1F/16F))
                .apply(Modifiers.cutSideUpDown(15F/16F))
                .export(quads);

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSideLeftRight(false, 1F/16F))
                .apply(Modifiers.cutSideUpDown(15F/16F))
                .export(quads);

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSideUpDown(9F/16F))
                .apply(Modifiers.cutSideLeftRight(15F/16F))
                .export(quads);
    }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelUtils.CUTOUT;
    }

    @Override
    public void getAdditionalQuads(QuadMap quadMap, BlockState state, RandomSource rand, ModelData data, RenderType renderType)
    {
        if (renderType == RenderType.cutout())
        {
            for (Direction dir : Direction.Plane.HORIZONTAL)
            {
                quadMap.get(dir).addAll(baseModel.getQuads(state, dir, rand, data, renderType));
            }
        }
    }



    public static FramedBookshelfGeometry normal(GeometryFactory.Context ctx)
    {
        return new FramedBookshelfGeometry(ctx, dir -> true);
    }

    public static FramedBookshelfGeometry chiseled(GeometryFactory.Context ctx)
    {
        Direction facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        return new FramedBookshelfGeometry(ctx, facing::equals);
    }
}
