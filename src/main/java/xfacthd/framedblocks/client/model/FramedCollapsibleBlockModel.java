package xfacthd.framedblocks.client.model;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedCollapsibleTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class FramedCollapsibleBlockModel extends BakedModelProxy
{
    private final BlockState state;
    private final Int2ObjectMap<CollapsibleModel> MODEL_CACHE = new Int2ObjectOpenHashMap<>();

    public FramedCollapsibleBlockModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel);
        this.state = state;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        return getModel(extraData).getQuads(state, side, rand, extraData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data)
    {
        return getModel(data).getParticleTexture(data);
    }

    private CollapsibleModel getModel(IModelData extraData)
    {
        Integer offsets = extraData.getData(FramedCollapsibleTileEntity.OFFSETS);
        int packed = offsets == null ? 0 : offsets;
        return MODEL_CACHE.computeIfAbsent(packed, key -> new CollapsibleModel(state, baseModel, key));
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedCollapsibleTileEntity)
        {
            return te.getModelData();
        }
        return tileData;
    }

    /*TODO: this does not handle quads well that are part of a grid (like CTM models)
            - the cut part has holes when the target of one corner is outside a quad's area
            - on the collapsed face each quad takes the full height individually
              (interpolating these is probably impossible due to the inconsistency when only one vertex is moved)
    */
    private static class CollapsibleModel extends FramedBlockModel
    {
        private final Direction collapsedFace;
        private final float[] vertexPos = new float[4];

        public CollapsibleModel(BlockState state, IBakedModel baseModel, int packedOffsets)
        {
            super(state, baseModel);
            this.collapsedFace = state.get(PropertyHolder.COLLAPSED_FACE).toDirection();

            byte[] vertexOffsets = FramedCollapsibleTileEntity.unpackOffsets(packedOffsets);
            for (int i = 0; i < 4; i++)
            {
                vertexPos[i] = 1F - ((float) vertexOffsets[i] / 16F);
            }
        }

        @Override
        protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
        {
            if (collapsedFace == null || quad.getFace() == collapsedFace.getOpposite())
            {
                quadMap.get(quad.getFace()).add(quad);
                return;
            }

            if (quad.getFace() == collapsedFace)
            {
                BakedQuad collapsedQuad = ModelUtils.duplicateQuad(quad);
                BakedQuadTransformer.setVertexPosInFacingDir(collapsedQuad, vertexPos);
                quadMap.get(null).add(collapsedQuad);
            }
            else
            {
                BakedQuad cutQuad = ModelUtils.duplicateQuad(quad);
                if (collapsedFace.getAxis() == Direction.Axis.Y)
                {
                    boolean top = collapsedFace == Direction.UP;
                    int idxOff = getYCollapsedIndexOffset(quad.getFace());
                    float posOne = vertexPos[idxOff];
                    float posTwo;
                    if (top)
                    {
                        posTwo = vertexPos[(idxOff + 1) % 4];
                    }
                    else
                    {
                        int idxTwo = idxOff - 1;
                        if (idxTwo < 0) { idxTwo = 4 + idxTwo; }
                        posTwo = vertexPos[idxTwo % 4];
                    }

                    if (BakedQuadTransformer.createHorizontalSideQuad(cutQuad, !top, posOne, posTwo))
                    {
                        quadMap.get(quad.getFace()).add(cutQuad);
                    }
                }
                else if (quad.getFace().getAxis() == Direction.Axis.Y)
                {
                    boolean top = quad.getFace() == Direction.UP;
                    float posOne = vertexPos[top ? 0 : 1];
                    float posTwo = vertexPos[top ? 3 : 2];

                    if (collapsedFace == Direction.NORTH || (top && collapsedFace == Direction.WEST) || (!top && collapsedFace == Direction.EAST))
                    {
                        float temp = posOne;
                        posOne = posTwo;
                        posTwo = temp;
                    }

                    if (BakedQuadTransformer.createTopBottomQuad(cutQuad, collapsedFace, posOne, posTwo))
                    {
                        quadMap.get(quad.getFace()).add(cutQuad);
                    }
                }
                else
                {
                    boolean right = collapsedFace == cutQuad.getFace().rotateY();
                    float posTop = vertexPos[right ? 3 : 0];
                    float posBot = vertexPos[right ? 2 : 1];

                    if (BakedQuadTransformer.createVerticalSideQuad(cutQuad, collapsedFace, posTop, posBot))
                    {
                        quadMap.get(quad.getFace()).add(cutQuad);
                    }
                }
            }
        }

        @Override
        protected IBakedModel getCamoModel(BlockState camoState)
        {
            if (camoState == FBContent.blockFramedCube.get().getDefaultState()) { return baseModel; }
            return super.getCamoModel(camoState);
        }

        @Override
        protected boolean canRenderBaseModelInLayer(RenderType layer) { return layer == RenderType.getSolid(); }

        private int getYCollapsedIndexOffset(Direction quadFace)
        {
            boolean top = collapsedFace == Direction.UP;
            switch (quadFace)
            {
                case NORTH: return top ? 3 : 2;
                case EAST: return top ? 2 : 3;
                case SOUTH: return top ? 1 : 0;
                case WEST: return top ? 0 : 1;
                default: throw new IllegalArgumentException("Invalid facing for y face collapse!");
            }
        }
    }
}