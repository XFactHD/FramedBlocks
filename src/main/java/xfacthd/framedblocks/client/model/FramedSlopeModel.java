package xfacthd.framedblocks.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.SlopeType;

import java.util.*;

public class FramedSlopeModel extends FramedBlockModel
{
    private final Direction dir;
    private final SlopeType type;

    public FramedSlopeModel(IBakedModel baseModel, Direction dir, SlopeType type)
    {
        super(BlockType.FRAMED_SLOPE, baseModel);
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
            if (type == SlopeType.HORIZONTAL)
            {
                if (quad.getFace() == Direction.UP || quad.getFace() == Direction.DOWN)
                {
                    quadIterator.remove();
                    modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                            createTopBottomTriangle(quad, pos, uv, light, dir)
                    ));
                }
            }
            else
            {
                if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
                {
                    quadIterator.remove();
                    modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                            createSideTriangle(quad, pos, uv, light, dir, type)
                    ));
                }
                else if (type == SlopeType.TOP && quad.getFace() == Direction.DOWN || type == SlopeType.BOTTOM && quad.getFace() == Direction.UP)
                {
                    quadIterator.remove();
                    modifiedQuads.add(new BakedQuad(
                            quad.getVertexData(),
                            quad.getTintIndex(),
                            dir.getOpposite(),
                            quad.func_187508_a(),
                            quad.shouldApplyDiffuseLighting()
                    ));
                }
            }
        }

        builder.addAll(quads);
        builder.addAll(modifiedQuads);

        return builder.build();
    }

    public static void createSideTriangle(BakedQuad quad, float[][] pos, float[][] uv, float[][] light, Direction dir, SlopeType type)
    {
        if (quad.getFace() == dir.rotateY())
        {
            if (type == SlopeType.TOP)
            {
                pos[1][1] = pos[0][1];
                uv[1][1] = uv[0][1];
                light[1][1] = light[0][1];
            }
            else if (type == SlopeType.BOTTOM)
            {
                pos[0][1] = pos[1][1];
                uv[0][1] = uv[1][1];
                light[0][1] = light[1][1];
            }
        }
        else if (quad.getFace() == dir.rotateYCCW())
        {
            if (type == SlopeType.TOP)
            {
                pos[2][1] = pos[3][1];
                uv[2][1] = uv[3][1];
                light[2][1] = light[3][1];
            }
            else if (type == SlopeType.BOTTOM)
            {
                pos[3][1] = pos[2][1];
                uv[3][1] = uv[2][1];
                light[3][1] = light[2][1];
            }
        }
    }

    public static void createTopBottomTriangle(BakedQuad quad, float[][] pos, float[][] uv, float[][] light, Direction dir)
    {
        if (quad.getFace() == Direction.UP)
        {
            int posIdx = dir.getAxis() == Direction.Axis.X ? 2 : 0;
            int uvIdx = dir.getAxis() == Direction.Axis.X ? 0 : 0;
            int fromIdx = dir.getAxis() == Direction.Axis.X ? (dir.getHorizontalIndex() + 2) % 4 : dir.getHorizontalIndex();
            int toIdx =   dir.getAxis() == Direction.Axis.X ? (dir.getHorizontalIndex() + 1) % 4 : (dir.getHorizontalIndex() + 2) % 4;

            pos[fromIdx][posIdx] = pos[toIdx][posIdx];
            uv[fromIdx][uvIdx] = uv[toIdx][uvIdx];
            light[fromIdx][uvIdx] = light[toIdx][uvIdx];
        }
        else
        {
            int posIdx = dir.getAxis() == Direction.Axis.X ? 2 : 0;
            int uvIdx = dir.getAxis() == Direction.Axis.X ? 1 : 0;
            int fromIdx = (dir.getHorizontalIndex() + 1) % 4;
            int toIdx = (dir.getHorizontalIndex() + 2) % 4;

            pos[fromIdx][posIdx] = pos[toIdx][posIdx];
            uv[fromIdx][uvIdx] = uv[toIdx][uvIdx];
            light[fromIdx][uvIdx] = light[toIdx][uvIdx];
        }
    }
}