package xfacthd.framedblocks.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.CornerType;
import xfacthd.framedblocks.common.data.SlopeType;

import java.util.*;

public class FramedInnerCornerSlopeModel extends FramedBlockModel
{
    private final Direction dir;
    private final CornerType type;

    public FramedInnerCornerSlopeModel(IBakedModel baseModel, Direction dir, CornerType type)
    {
        super(baseModel);
        this.dir = dir;
        this.type = type;
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
            if (!type.isHorizontal() && (quad.getFace() == dir.getOpposite() || quad.getFace() == dir.rotateYCCW()))
            {
                quadIterator.remove();
                BakedQuad finalQuad = quad;
                modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                {
                    Direction fakeDir = finalQuad.getFace() == dir.getOpposite() ? dir.rotateY() : dir;
                    SlopeType fakeType = type == CornerType.TOP ? SlopeType.TOP : SlopeType.BOTTOM;
                    FramedSlopeModel.createSideTriangle(finalQuad, pos, uv, light, fakeDir, fakeType);
                }));
            }
            else if (type == CornerType.BOTTOM && quad.getFace() == Direction.UP || type == CornerType.TOP && quad.getFace() == Direction.DOWN)
            {
                quad = new BakedQuad(
                        quad.getVertexData(),
                        quad.getTintIndex(),
                        ModelUtils.findHorizontalFacing(quad),
                        quad.func_187508_a(),
                        quad.shouldApplyDiffuseLighting()
                );

                quadIterator.remove();
                BakedQuad finalQuad = quad;
                modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        createSlopeTriangle(finalQuad, pos, uv, light, dir, type)
                ));
            }
        }

        builder.addAll(quads);
        builder.addAll(modifiedQuads);

        return builder.build();
    }

    public static void createSlopeTriangle(BakedQuad quad, float[][] pos, float[][] uv, float[][] light, Direction dir, CornerType type)
    {
        //Needs special casing for some reason
        if (type == CornerType.TOP && quad.getFace() == dir.rotateYCCW())
        {
            if (dir.getAxis() == Direction.Axis.Z)
            {
                pos[0][1] = pos[1][1];
                pos[0][0] = pos[1][0];
            }
            else
            {
                pos[0][1] = pos[1][1];
                pos[0][2] = pos[1][2];
            }

            uv[0][1] = uv[1][1];
            light[0][1] = light[1][1];
        }
        else if (!type.isHorizontal())
        {
            CornerType fakeType = (type == CornerType.TOP) ? CornerType.BOTTOM : CornerType.TOP;
            FramedCornerSlopeModel.createSlopeTriangle(quad, pos, uv, light, dir, fakeType);
        }
        else
        {
            //TODO: implement
        }
    }
}