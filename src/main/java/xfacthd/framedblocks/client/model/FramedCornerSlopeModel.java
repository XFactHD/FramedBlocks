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

public class FramedCornerSlopeModel extends FramedBlockModel
{
    private final Direction dir;
    private final CornerType type;

    public FramedCornerSlopeModel(IBakedModel baseModel, Direction dir, CornerType type)
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
            if (type == CornerType.BOTTOM && quad.getFace() == Direction.UP)
            {
                quad = new BakedQuad(
                        quad.getVertexData(),
                        quad.getTintIndex(),
                        dir.getOpposite(),
                        quad.func_187508_a(),
                        quad.shouldApplyDiffuseLighting()
                );
            }
            else if (type == CornerType.TOP && quad.getFace() == Direction.DOWN)
            {
                Direction face = ModelUtils.findHorizontalFacing(quad);
                quad = new BakedQuad(
                        quad.getVertexData(),
                        quad.getTintIndex(),
                        face,
                        quad.func_187508_a(),
                        quad.shouldApplyDiffuseLighting()
                );
            }

            if (type.isHorizontal())
            {
                Direction altDir = type.isRight() ? dir.rotateYCCW() : dir.rotateY();
                if (!ModelUtils.isFacingTowards(quad, dir.getOpposite()) && !ModelUtils.isFacingTowards(quad, altDir))
                {
                    if (!type.isTop() && quad.getFace() == Direction.DOWN || type.isTop() && quad.getFace() == Direction.UP)
                    {
                        quadIterator.remove();
                        modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                                createTopBottomTriangle(pos, uv, light, dir, type)));
                    }
                    else if (quad.getFace() == altDir.getOpposite())
                    {
                        quadIterator.remove();
                        modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                                createSideTriangle(pos, uv, light, type)));
                    }
                }
                else
                {
                    quadIterator.remove();
                    BakedQuad finalQuad = quad;
                    modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                            createSlopeTriangle(finalQuad, pos, uv, light, dir, type)));
                }
            }
            else
            {
                if (quad.getFace() == dir.getOpposite() || quad.getFace() == dir.rotateY())
                {
                    quadIterator.remove();
                    BakedQuad finalQuad = quad;
                    modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                            createSlopeTriangle(finalQuad, pos, uv, light, dir, type)
                    ));
                }
                else if (quad.getFace() == dir || quad.getFace() == dir.rotateYCCW())
                {
                    quadIterator.remove();

                    Direction fakeDir = quad.getFace() == dir ? dir.rotateYCCW() : dir;
                    BakedQuad finalQuad = quad;
                    modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                    {
                        SlopeType fakeType = type == CornerType.TOP ? SlopeType.TOP : SlopeType.BOTTOM;
                        FramedSlopeModel.createSideTriangle(finalQuad, pos, uv, light, fakeDir, fakeType);
                    }));
                }
            }
        }

        builder.addAll(quads);
        builder.addAll(modifiedQuads);

        return builder.build();
    }

    public static void createSlopeTriangle(BakedQuad quad, float[][] pos, float[][] uv, float[][] light, Direction dir, CornerType type)
    {
        if (type.isHorizontal())
        {
            if (ModelUtils.isFacingTowards(quad, Direction.UP) || ModelUtils.isFacingTowards(quad, Direction.DOWN))
            {
                int count = (dir.getHorizontalIndex() + 2) % 4;
                ModelUtils.rotateElement(uv, !type.isTop(), count);

                int posIdx = dir.getAxis() == Direction.Axis.Z ? 2 : 0;
                int fromIdx;
                int toIdx;

                if (type.isRight() == type.isTop())
                {
                    fromIdx = 2;
                    toIdx = 3;
                }
                else
                {
                    fromIdx = 3;
                    toIdx = 2;
                }

                pos[fromIdx][1] = pos[toIdx][1];
                pos[fromIdx][posIdx] = pos[toIdx][posIdx];
                uv[fromIdx][1] = uv[toIdx][1];
                light[fromIdx][1] = light[toIdx][1];
            }
            else
            {
                if (type.isTop() != type.isRight())
                {
                    ModelUtils.rotateElement(uv, true, 1);
                }

                int fromIdx = type.isRight() == type.isTop() ? 1 : 3;
                int toIdx = 2;

                pos[fromIdx][1] = pos[toIdx][1];
                uv[fromIdx][1] = uv[toIdx][1];
                light[fromIdx][1] = light[toIdx][1];
            }
        }
        else
        {
            if (quad.getFace() == dir.getOpposite())
            {
                int posIdx = dir.getAxis() == Direction.Axis.Z ? 2 : 0;

                if (type == CornerType.TOP)
                {
                    pos[2][1] = pos[3][1];
                    pos[2][posIdx] = pos[3][posIdx];
                    uv[2][1] = uv[3][1];
                    light[2][1] = light[3][1];
                }
                else if (type == CornerType.BOTTOM)
                {
                    pos[3][1] = pos[2][1];
                    pos[3][posIdx] = pos[2][posIdx];
                    uv[3][1] = uv[2][1];
                    light[3][1] = light[2][1];
                }
            }
            else
            {
                int posIdx = dir.getAxis() == Direction.Axis.Z ? 0 : 2;

                if (type == CornerType.TOP)
                {
                    pos[1][1] = pos[0][1];
                    pos[1][posIdx] = pos[0][posIdx];
                    uv[1][1] = uv[0][1];
                    light[1][1] = light[0][1];
                }
                else if (type == CornerType.BOTTOM)
                {
                    int count = (dir.getHorizontalIndex() + 2) % 4;
                    ModelUtils.rotateElement(uv, true, count);

                    pos[3][1] = pos[0][1];
                    pos[3][posIdx] = pos[0][posIdx];
                    uv[3][1] = uv[0][1];
                    light[3][1] = light[0][1];
                }
            }
        }
    }

    public static void createTopBottomTriangle(float[][] pos, float[][] uv, float[][] light, Direction dir, CornerType type)
    {
        int posIdx = dir.getAxis() == Direction.Axis.X ? 2 : 0;
        int uvIdx =  dir.getAxis() == Direction.Axis.X ? 1 : 0;
        int fromIdx;
        int toIdx;

        if (type.isRight() == type.isTop())
        {
            fromIdx = 1;
            toIdx =   2;
        }
        else
        {
            fromIdx = 0;
            toIdx =   3;
        }

        pos[fromIdx][posIdx] = pos[toIdx][posIdx];
        uv[fromIdx][uvIdx] = uv[toIdx][uvIdx];
        light[fromIdx][uvIdx] = light[toIdx][uvIdx];
    }

    public static void createSideTriangle(float[][] pos, float[][] uv, float[][] light, CornerType type)
    {
        int posIdx = 1;
        int uvIdx =  1;
        int fromIdx;
        int toIdx;

        if (type.isRight() == type.isTop())
        {
            fromIdx = 2;
            toIdx = 1;
        }
        else
        {
            fromIdx = 3;
            toIdx = 0;
        }

        pos[fromIdx][posIdx] = pos[toIdx][posIdx];
        uv[fromIdx][uvIdx] = uv[toIdx][uvIdx];
        light[fromIdx][uvIdx] = light[toIdx][uvIdx];
    }
}