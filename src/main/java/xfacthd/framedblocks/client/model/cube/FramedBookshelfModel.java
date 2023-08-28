package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class FramedBookshelfModel extends FramedBlockModel
{
    private final Predicate<Direction> frontFacePred;

    private FramedBookshelfModel(BlockState state, BakedModel baseModel, Predicate<Direction> frontFacePred)
    {
        super(state, baseModel);
        this.frontFacePred = frontFacePred;
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
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
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelUtils.CUTOUT;
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType renderType)
    {
        if (renderType == RenderType.cutout())
        {
            for (Direction dir : Direction.Plane.HORIZONTAL)
            {
                quadMap.get(dir).addAll(baseModel.getQuads(state, dir, rand, data, renderType));
            }
        }
    }



    public static FramedBookshelfModel normal(BlockState state, BakedModel baseModel)
    {
        return new FramedBookshelfModel(state, baseModel, dir -> true);
    }

    public static FramedBookshelfModel chiseled(BlockState state, BakedModel baseModel)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        return new FramedBookshelfModel(state, baseModel, facing::equals);
    }

    public static BlockState itemSourceNormal()
    {
        return FBContent.BLOCK_FRAMED_BOOKSHELF.get().defaultBlockState();
    }

    public static BlockState itemSourceChiseled()
    {
        return FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF.get().defaultBlockState();
    }
}
