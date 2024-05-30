package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.camo.CamoContent;
import xfacthd.framedblocks.api.model.cache.QuadCacheKey;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedCollapsibleBlockGeometry extends Geometry
{
    public static final ResourceLocation ALT_BASE_MODEL_LOC = Utils.rl("block/framed_collapsible_block_alt");
    private static final float MIN_DEPTH = .001F;

    private final Direction collapsedFace;
    private final boolean rotSplitLine;
    private final BakedModel altBaseModel;

    public FramedCollapsibleBlockGeometry(GeometryFactory.Context ctx)
    {
        this.collapsedFace = ctx.state().getValue(PropertyHolder.NULLABLE_FACE).toDirection();
        this.rotSplitLine = ctx.state().getValue(PropertyHolder.ROTATE_SPLIT_LINE);
        this.altBaseModel = ctx.modelLookup().get(ALT_BASE_MODEL_LOC);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, ModelData data)
    {
        Direction quadDir = quad.getDirection();
        if (collapsedFace == null || quadDir == collapsedFace.getOpposite())
        {
            quadMap.get(quadDir).add(quad);
            return;
        }

        Integer offsets = data.get(FramedCollapsibleBlockEntity.OFFSETS);
        float[] vertexPos = new float[] { 1F, 1F, 1F, 1F };
        if (offsets != null && offsets != 0)
        {
            byte[] relOff = FramedCollapsibleBlockEntity.unpackOffsets(offsets);
            boolean allSame = relOff[0] == relOff[1] && relOff[0] == relOff[2] && relOff[0] == relOff[3];
            for (int i = 0; i < 4; i++)
            {
                vertexPos[i] = Math.max(1F - ((float) relOff[i] / 16F), allSame ? MIN_DEPTH : 0F);
            }
        }

        if (quadDir == collapsedFace)
        {
            float diff02 = Math.abs(vertexPos[0] - vertexPos[2]);
            float diff13 = Math.abs(vertexPos[1] - vertexPos[3]);
            boolean rotate = (diff13 > diff02) != rotSplitLine;

            float[] vertexPosTwo = new float[] { 1F, 1F, 1F, 1F };
            System.arraycopy(vertexPos, 0, vertexPosTwo, 0, vertexPos.length);
            if (rotate)
            {
                vertexPos[2] = vertexPos[1] + vertexPos[3] - vertexPos[0];
                vertexPosTwo[0] = vertexPosTwo[1] + vertexPosTwo[3] - vertexPosTwo[2];
            }
            else
            {
                vertexPos[3] = vertexPos[0] + vertexPos[2] - vertexPos[1];
                vertexPosTwo[1] = vertexPosTwo[0] + vertexPosTwo[2] - vertexPosTwo[3];
            }

            if (Utils.isY(collapsedFace))
            {
                rotate ^= collapsedFace == Direction.DOWN;
                float left = rotate ? 0F : 1F;
                float right = rotate ? 1F : 0F;

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.EAST, left, right))
                        .apply(Modifiers.setPosition(vertexPos))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.WEST, left, right))
                        .apply(Modifiers.setPosition(vertexPosTwo))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(quadDir.getCounterClockWise(), rotate ? 1F : 0F, rotate ? 0F : 1F))
                        .apply(Modifiers.setPosition(vertexPos))
                        .export(quadMap.get(null));

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(quadDir.getClockWise(), rotate ? 0F : 1F, rotate ? 1F : 0F))
                        .apply(Modifiers.setPosition(vertexPosTwo))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            if (Utils.isY(collapsedFace))
            {
                boolean top = collapsedFace == Direction.UP;
                int idxOne = getYCollapsedIndexOffset(quadDir);
                int idxTwo = Math.floorMod(idxOne + (top ? 1 : -1), 4);
                float posOne = vertexPos[idxOne];
                float posTwo = vertexPos[idxTwo];

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(!top, posOne, posTwo))
                        .export(quadMap.get(quadDir));
            }
            else if (Utils.isY(quadDir))
            {
                boolean top = quad.getDirection() == Direction.UP;
                float posOne = vertexPos[top ? 0 : 1];
                float posTwo = vertexPos[top ? 3 : 2];

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(collapsedFace, posOne, posTwo))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                boolean right = collapsedFace == quadDir.getClockWise();
                float posTop = vertexPos[right ? 3 : 0];
                float posBot = vertexPos[right ? 2 : 1];

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(collapsedFace, posTop, posBot))
                        .export(quadMap.get(quadDir));
            }
        }
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad) { }

    @Override
    public boolean useBaseModel()
    {
        return true;
    }

    @Override
    public BakedModel getBaseModel(BakedModel baseModel, boolean useAltModel)
    {
        return useAltModel ? altBaseModel : baseModel;
    }

    @Override
    public QuadCacheKey makeCacheKey(CamoContent<?> camo, Object ctCtx, ModelData data)
    {
        Integer packedOffsets = data.get(FramedCollapsibleBlockEntity.OFFSETS);
        return new CollapsibleBlockQuadCacheKey(camo, ctCtx, packedOffsets);
    }

    private int getYCollapsedIndexOffset(Direction quadFace)
    {
        boolean top = collapsedFace == Direction.UP;
        return switch (quadFace)
        {
            case NORTH -> top ? 3 : 2;
            case EAST -> top ? 2 : 3;
            case SOUTH -> top ? 1 : 0;
            case WEST -> top ? 0 : 1;
            case DOWN, UP -> throw new IllegalArgumentException("Invalid facing for y face collapse!");
        };
    }

    private record CollapsibleBlockQuadCacheKey(CamoContent<?> camo, Object ctCtx, Integer packedOffsets) implements QuadCacheKey { }
}
