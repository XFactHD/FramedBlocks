package xfacthd.framedblocks.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.SlopeType;

import java.util.List;
import java.util.Random;

public class FramedPrismCornerModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedPrismCornerModel(IBakedModel baseModel, Direction dir, boolean top)
    {
        super(BlockType.FRAMED_PRISM_CORNER, baseModel);
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
            if (quad.getFace() == Direction.DOWN || quad.getFace() == Direction.UP)
            {
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        createTopBottomTriangle(quad, pos, uv, light, dir)
                ));
            }
            else if (quad.getFace() == dir || quad.getFace() == dir.rotateYCCW())
            {
                Direction fakeDir = quad.getFace() == dir ? dir.rotateYCCW() : dir;
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        FramedSlopeModel.createSideTriangle(quad, pos, uv, light, fakeDir, top ? SlopeType.TOP : SlopeType.BOTTOM)
                ));
            }
            else if (quad.getFace() == dir.rotateY())
            {
                builder.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        createSlopeTriangle(pos, uv, light, dir, top)
                ));
                ForgeHooksClient.fillNormal(quad.getVertexData(), dir.rotateY());
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
                    pos[1][0] = 0;
                    pos[1][2] = 0;
                    pos[2][0] = 0;
                    pos[2][2] = 0;
                    break;
                }
                case EAST:
                {
                    pos[1][0] = 1;
                    pos[1][2] = 0;
                    pos[2][0] = 1;
                    pos[2][2] = 0;
                    break;
                }
                case SOUTH:
                {
                    pos[1][0] = 1;
                    pos[1][2] = 1;
                    pos[2][0] = 1;
                    pos[2][2] = 1;
                    break;
                }
                case WEST:
                {
                    pos[1][0] = 0;
                    pos[1][2] = 1;
                    pos[2][0] = 0;
                    pos[2][2] = 1;
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
        else
        {
            switch (dir)
            {
                case NORTH:
                {
                    pos[0][0] = 0;
                    pos[0][2] = 0;
                    pos[3][0] = 0;
                    pos[3][2] = 0;
                    break;
                }
                case EAST:
                {
                    pos[0][0] = 1;
                    pos[0][2] = 0;
                    pos[3][0] = 1;
                    pos[3][2] = 0;
                    break;
                }
                case SOUTH:
                {
                    pos[0][0] = 1;
                    pos[0][2] = 1;
                    pos[3][0] = 1;
                    pos[3][2] = 1;
                    break;
                }
                case WEST:
                {
                    pos[0][0] = 0;
                    pos[0][2] = 1;
                    pos[3][0] = 0;
                    pos[3][2] = 1;
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
    }

    public static void createTopBottomTriangle(BakedQuad quad, float[][] pos, float[][] uv, float[][] light, Direction dir)
    {
        if (quad.getFace() == Direction.DOWN)
        {
            FramedSlopeModel.createTopBottomTriangle(quad, pos, uv, light, dir);
        }
        else
        {
            pos[0][0] = pos[2][0];
            uv[0][0] = uv[2][0];
            light[0][0] = light[2][0];
        }
    }
}