package xfacthd.framedblocks.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.SlopeType;

import java.util.*;

public class FramedInnerThreewayCornerModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedInnerThreewayCornerModel(IBakedModel baseModel, Direction dir, boolean top)
    {
        super(baseModel);
        this.dir = dir;
        this.top = top;
    }

    @Override
    protected ImmutableList<BakedQuad> prepareBaseQuads(BlockState state, Random rand)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        List<BakedQuad> quads = getAllQuads(baseModel, state, new Random());
        List<BakedQuad> modifiedQuads = new ArrayList<>();

        Iterator<BakedQuad> quadIterator = quads.listIterator();
        while (quadIterator.hasNext())
        {
            BakedQuad quad = quadIterator.next();
            if (ModelUtils.isFacingTowards(quad, dir.getOpposite()) && ModelUtils.isFacingTowards(quad, dir.rotateYCCW()))
            {
                quadIterator.remove();

                BakedQuad finalQuad = quad;
                modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        FramedThreewayCornerModel.createVerticalSlopeTriangle(finalQuad, pos, uv, !top)
                ));
            }
            else if (isHorSlopeQuad(quad, dir, top))
            {
                quadIterator.remove();

                Direction quadFace = ModelUtils.isFacingTowards(quad, dir.getOpposite()) ? dir.getOpposite() : dir.rotateYCCW();
                quad = new BakedQuad(
                        quad.getVertexData(),
                        quad.getTintIndex(),
                        quadFace,
                        quad.func_187508_a(),
                        quad.shouldApplyDiffuseLighting()
                );

                BakedQuad finalQuad = quad;
                modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        createSlopeTriangle(finalQuad, pos, uv, dir)));
            }
            else if ((top && quad.getFace() == Direction.DOWN) || (!top && quad.getFace() == Direction.UP))
            {
                quadIterator.remove();

                BakedQuad finalQuad = quad;
                modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        FramedSlopeModel.createTopBottomTriangle(finalQuad, pos, uv, light, dir.rotateY())
                ));
            }
            else if (quad.getFace() == dir.getOpposite() || quad.getFace() == dir.rotateYCCW())
            {
                quadIterator.remove();

                BakedQuad finalQuad = quad;
                modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                {
                    Direction fakeDir = finalQuad.getFace() == dir.getOpposite() ? dir.rotateY() : dir;
                    SlopeType fakeType = top ? SlopeType.TOP : SlopeType.BOTTOM;
                    FramedSlopeModel.createSideTriangle(finalQuad, pos, uv, light, fakeDir, fakeType);
                }));
            }
        }

        builder.addAll(quads);
        builder.addAll(modifiedQuads);

        return builder.build();
    }

    private static boolean isHorSlopeQuad(BakedQuad quad, Direction dir, boolean top)
    {
        if (top && !ModelUtils.isFacingTowards(quad, Direction.DOWN)) { return false; }
        if (!top && !ModelUtils.isFacingTowards(quad, Direction.UP)) { return false; }

        return ModelUtils.isFacingTowards(quad, dir.getOpposite()) || ModelUtils.isFacingTowards(quad, dir.rotateYCCW());
    }

    private static void createSlopeTriangle(BakedQuad quad, float[][] pos, float[][] uv, Direction dir)
    {
        boolean xDir = dir.getAxis() == Direction.Axis.X;
        boolean xFace = ModelUtils.isFacingTowards(quad, Direction.EAST) || ModelUtils.isFacingTowards(quad, Direction.WEST);
        int coord1 = xFace == xDir ? 2 : 0;

        boolean topCWFace = ModelUtils.isFacingTowards(quad, dir.rotateYCCW());
        int coord2 = topCWFace ? 1 : 3;

        pos[coord1][0] = .5F;
        pos[coord2][0] = .5F;
        pos[coord1][1] = .5F;
        pos[coord2][1] = .5F;
        pos[coord1][2] = .5F;
        pos[coord2][2] = .5F;

        uv[coord1][0] = quad.func_187508_a().getInterpolatedU(8);
        uv[coord2][0] = quad.func_187508_a().getInterpolatedU(8);
        uv[coord1][1] = quad.func_187508_a().getInterpolatedV(8);
        uv[coord2][1] = quad.func_187508_a().getInterpolatedV(8);
    }
}