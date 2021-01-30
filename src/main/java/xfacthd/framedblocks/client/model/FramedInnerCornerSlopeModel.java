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
                        quad.getSprite(),
                        quad.applyDiffuseLighting()
                );

                quadIterator.remove();
                BakedQuad finalQuad = quad;
                modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        createSlopeTriangle(finalQuad, pos, uv, light, dir, type)
                ));
            }
            else if (type.isHorizontal())
            {
                if (!ModelUtils.isFacingTowards(quad, dir.getOpposite()))
                {
                    BakedQuad finalQuad = quad;
                    if ((type.isTop() && quad.getFace() == Direction.DOWN) || (!type.isTop() && quad.getFace() == Direction.UP))
                    {
                        quadIterator.remove();

                        modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                        {
                            Direction fakeDir = type.isRight() ? dir.rotateY() : dir;
                            FramedSlopeModel.createTopBottomTriangle(finalQuad, pos, uv, light, fakeDir);
                        }));
                    }
                    else if ((type.isRight() && quad.getFace() == dir.rotateYCCW()) || (!type.isRight() && quad.getFace() == dir.rotateY()))
                    {
                        quadIterator.remove();

                        modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                                createSideTriangle(finalQuad, pos, uv, light, dir, type)
                        ));
                    }
                }
                else
                {
                    quadIterator.remove();

                    Direction quadFace = quad.getFace();
                    if (ModelUtils.isFacingTowards(quad, Direction.UP) || ModelUtils.isFacingTowards(quad, Direction.DOWN) &&
                            dir == Direction.NORTH && !type.isRight())
                    {
                        quadFace = dir.getOpposite();
                    }

                    quad = new BakedQuad(
                            quad.getVertexData(),
                            quad.getTintIndex(),
                            quadFace,
                            quad.getSprite(),
                            quad.applyDiffuseLighting()
                    );

                    BakedQuad finalQuad = quad;
                    modifiedQuads.add(ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
                            createSlopeTriangle(finalQuad, pos, uv, light, dir, type)
                    ));
                }
            }
        }

        builder.addAll(quads);
        builder.addAll(modifiedQuads);

        return builder.build();
    }

    private static void createSlopeTriangle(BakedQuad quad, float[][] pos, float[][] uv, float[][] light, Direction dir, CornerType type)
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
            if (ModelUtils.isFacingTowards(quad, Direction.UP) || ModelUtils.isFacingTowards(quad, Direction.DOWN))
            {
                if (dir.getAxis() == Direction.Axis.X)
                {
                    boolean cw = (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE) == type.isTop();
                    ModelUtils.rotateElement(uv, cw, 1);
                }
                else if (dir == Direction.SOUTH)
                {
                    ModelUtils.rotateElement(uv, true, 2);
                }

                int coord2 = dir.getAxis() == Direction.Axis.X ? 0 : 2;
                if (type == CornerType.HORIZONTAL_TOP_LEFT || type == CornerType.HORIZONTAL_BOTTOM_RIGHT)
                {
                    pos[3][1] = pos[2][1];
                    pos[3][coord2] = pos[2][coord2];

                    uv[3][1] = uv[2][1];
                    light[3][1] = light[2][1];
                }
                else if (type == CornerType.HORIZONTAL_TOP_RIGHT || type == CornerType.HORIZONTAL_BOTTOM_LEFT)
                {
                    pos[2][1] = pos[3][1];
                    pos[2][coord2] = pos[3][coord2];

                    uv[2][1] = uv[3][1];
                    light[2][1] = light[3][1];
                }
            }
            else
            {
                int coord2 = dir.getAxis() == Direction.Axis.X ? 0 : 2;
                if (type == CornerType.HORIZONTAL_TOP_LEFT || type == CornerType.HORIZONTAL_BOTTOM_RIGHT)
                {
                    pos[0][1] = pos[3][1];
                    pos[0][coord2] = pos[3][coord2];

                    uv[0][1] = uv[3][1];
                    light[0][1] = light[3][1];
                }
                else if (type == CornerType.HORIZONTAL_TOP_RIGHT || type == CornerType.HORIZONTAL_BOTTOM_LEFT)
                {
                    pos[1][1] = pos[2][1];
                    pos[1][coord2] = pos[2][coord2];

                    uv[1][1] = uv[2][1];
                    light[1][1] = light[2][1];
                }
            }
        }
    }

    private static void createSideTriangle(BakedQuad quad, float[][] pos, float[][] uv, float[][] light, Direction dir, CornerType type)
    {
        if (type.isTop() == (quad.getFace() == dir.rotateY()))
        {
            pos[2][1] = pos[1][1];
            uv[2][1] = uv[1][1];
            light[2][1] = light[1][1];
        }
        else
        {
            pos[3][1] = pos[0][1];
            uv[3][1] = uv[0][1];
            light[3][1] = light[0][1];
        }
    }
}