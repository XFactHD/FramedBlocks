package xfacthd.framedblocks.client.model.v1;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.SlopeType;

import java.util.*;

public class FramedInnerPrismCornerModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedInnerPrismCornerModel(IBakedModel baseModel, Direction dir, boolean top)
    {
        super(BlockType.FRAMED_INNER_PRISM_CORNER, baseModel);
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
            //This is necessary because the blockstate ignores one property leading to only 4 instead of 8 models being loaded
            quad = ModelUtils.duplicateQuad(quad);

            if (top && quad.getFace() == Direction.DOWN || !top && quad.getFace() == Direction.UP)
            {
                BakedQuad finalQuad = quad;
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        FramedSlopeModel.createTopBottomTriangle(finalQuad, pos, uv, light, dir)
                ));
            }
            else if (ModelUtils.isFacingTowards(quad, dir.getOpposite()) && ModelUtils.isFacingTowards(quad, dir.rotateY()))
            {
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        createSlopeTriangle(pos, uv, light, dir, top)
                ));
                ForgeHooksClient.fillNormal(quad.getVertexData(), dir.rotateY());
            }
            else if (quad.getFace() == dir.getOpposite() || quad.getFace() == dir.rotateY())
            {
                Direction fakeDir = quad.getFace() == dir.getOpposite() ? dir.rotateYCCW() : dir;
                BakedQuad finalQuad = quad;
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        FramedSlopeModel.createSideTriangle(finalQuad, pos, uv, light, fakeDir, top ? SlopeType.TOP : SlopeType.BOTTOM)
                ));
            }
            else
            {
                builder.add(quad);
            }
        }

        return builder.build();
    }

    public static void createSlopeTriangle(float[][] pos, float[][] uv, float[][] light, Direction dir, boolean top)
    {
        if (top)
        {
            switch (dir)
            {
                case NORTH:
                {
                    pos[0][0] = 1;
                    pos[0][2] = 1;
                    pos[3][0] = 1;
                    pos[3][2] = 1;
                    break;
                }
                case EAST:
                {
                    pos[0][0] = 0;
                    pos[0][2] = 1;
                    pos[3][0] = 0;
                    pos[3][2] = 1;
                    break;
                }
                case SOUTH:
                {
                    pos[0][0] = 0;
                    pos[0][2] = 0;
                    pos[3][0] = 0;
                    pos[3][2] = 0;
                    break;
                }
                case WEST:
                {
                    pos[0][0] = 1;
                    pos[0][2] = 0;
                    pos[3][0] = 1;
                    pos[3][2] = 0;
                    break;
                }
            }

            float min = uv[0][0];
            float max = uv[3][0];
            float mid = min + ((max - min) / 2F);
            uv[0][0] = mid;
            uv[3][0] = mid;

            min = light[0][0];
            max = light[3][0];
            mid = min + ((max - min) / 2F);
            light[0][0] = mid;
            light[3][0] = mid;
        }
        else
        {
            switch (dir)
            {
                case NORTH:
                {
                    pos[1][0] = 1;
                    pos[1][2] = 1;
                    pos[2][0] = 1;
                    pos[2][2] = 1;
                    break;
                }
                case EAST:
                {
                    pos[1][0] = 0;
                    pos[1][2] = 1;
                    pos[2][0] = 0;
                    pos[2][2] = 1;
                    break;
                }
                case SOUTH:
                {
                    pos[1][0] = 0;
                    pos[1][2] = 0;
                    pos[2][0] = 0;
                    pos[2][2] = 0;
                    break;
                }
                case WEST:
                {
                    pos[1][0] = 1;
                    pos[1][2] = 0;
                    pos[2][0] = 1;
                    pos[2][2] = 0;
                    break;
                }
            }

            float min = uv[1][0];
            float max = uv[2][0];
            float mid = min + ((max - min) / 2F);
            uv[1][0] = mid;
            uv[2][0] = mid;

            min = light[1][0];
            max = light[2][0];
            mid = min + ((max - min) / 2F);
            light[1][0] = mid;
            light[2][0] = mid;
        }
    }
}