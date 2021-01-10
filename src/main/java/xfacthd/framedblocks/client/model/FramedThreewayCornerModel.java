package xfacthd.framedblocks.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.SlopeType;

import java.util.*;

public class FramedThreewayCornerModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedThreewayCornerModel(IBakedModel baseModel, Direction dir, boolean top)
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
        for (BakedQuad quad : quads)
        {
            if (ModelUtils.isFacingTowards(quad, dir.getOpposite()) && ModelUtils.isFacingTowards(quad, dir.rotateY()))
            {
                BakedQuad finalQuad = quad;
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        createVerticalSlopeTriangle(finalQuad, pos, uv, top)
                ));
            }
            else if ((top && ModelUtils.isFacingTowards(quad, Direction.DOWN)) || (!top && ModelUtils.isFacingTowards(quad, Direction.UP)))
            {
                Direction quadFace = ModelUtils.isFacingTowards(quad, dir.getOpposite()) ? dir.getOpposite() : dir.rotateY();
                quad = new BakedQuad(
                        quad.getVertexData(),
                        quad.getTintIndex(),
                        quadFace,
                        quad.func_187508_a(),
                        quad.shouldApplyDiffuseLighting()
                );

                BakedQuad finalQuad = quad;
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        createSlopeTriangle(finalQuad, pos, uv, dir, top)
                ));
            }
            else if (quad.getFace() == dir || quad.getFace() == dir.rotateYCCW())
            {
                BakedQuad finalQuad = quad;
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                {
                    Direction fakeDir = finalQuad.getFace() == dir.rotateYCCW() ? dir : dir.rotateYCCW();
                    SlopeType fakeType = top ? SlopeType.TOP : SlopeType.BOTTOM;
                    FramedSlopeModel.createSideTriangle(finalQuad, pos, uv, light, fakeDir, fakeType);
                }));
            }
            else if (!top && quad.getFace() == Direction.DOWN)
            {
                BakedQuad finalQuad = quad;
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        FramedSlopeModel.createTopBottomTriangle(finalQuad, pos, uv, light, dir)
                ));
            }
            else if (top && quad.getFace() == Direction.UP)
            {
                BakedQuad finalQuad = quad;
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        FramedSlopeModel.createTopBottomTriangle(finalQuad, pos, uv, light, Direction.SOUTH)
                ));
            }
        }

        return builder.build();
    }

    public static void createVerticalSlopeTriangle(BakedQuad quad, float[][] pos, float[][] uv, boolean top)
    {
        if (top)
        {
            pos[1][0] = .5F;
            pos[2][0] = .5F;
            pos[1][1] = .5F;
            pos[2][1] = .5F;
            pos[1][2] = .5F;
            pos[2][2] = .5F;

            uv[1][0] = quad.func_187508_a().getInterpolatedU(8);
            uv[2][0] = quad.func_187508_a().getInterpolatedU(8);
            uv[1][1] = quad.func_187508_a().getInterpolatedV(8);
            uv[2][1] = quad.func_187508_a().getInterpolatedV(8);
        }
        else
        {
            pos[0][0] = .5F;
            pos[3][0] = .5F;
            pos[0][1] = .5F;
            pos[3][1] = .5F;
            pos[0][2] = .5F;
            pos[3][2] = .5F;

            uv[0][0] = quad.func_187508_a().getInterpolatedU(8);
            uv[3][0] = quad.func_187508_a().getInterpolatedU(8);
            uv[0][1] = quad.func_187508_a().getInterpolatedV(8);
            uv[3][1] = quad.func_187508_a().getInterpolatedV(8);
        }
    }

    private static void createSlopeTriangle(BakedQuad quad, float[][] pos, float[][] uv, Direction dir, boolean top)
    {
        boolean xDir = dir.getAxis() == Direction.Axis.X;
        boolean xFace = ModelUtils.isFacingTowards(quad, Direction.EAST) || ModelUtils.isFacingTowards(quad, Direction.WEST);
        int coord1 = xFace == xDir ? 2 : 0;

        boolean topCWFace = top && ModelUtils.isFacingTowards(quad, dir.rotateY());
        int coord2 = topCWFace ? 1 : 3;

        if (!top && xDir && !xFace)
        {
            ModelUtils.rotateElement(uv, ModelUtils.isFacingTowards(quad, Direction.SOUTH), 1);
        }
        else if (!top && dir == Direction.SOUTH && ModelUtils.isFacingTowards(quad, Direction.WEST))
        {
            ModelUtils.rotateElement(uv, true, 2);
        }

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