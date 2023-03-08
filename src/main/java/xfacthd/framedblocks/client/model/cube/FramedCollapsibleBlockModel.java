package xfacthd.framedblocks.client.model.cube;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.model.BakedModelProxy;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class FramedCollapsibleBlockModel extends BakedModelProxy
{
    private final BlockState state;
    private final Int2ObjectMap<CollapsibleModel> MODEL_CACHE = new Int2ObjectOpenHashMap<>();

    public FramedCollapsibleBlockModel(BlockState state, BakedModel baseModel)
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
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data)
    {
        return getModel(data).getParticleIcon(data);
    }

    private CollapsibleModel getModel(IModelData extraData)
    {
        Integer offsets = extraData.getData(FramedCollapsibleBlockEntity.OFFSETS);
        int packed = offsets == null ? 0 : offsets;
        return MODEL_CACHE.computeIfAbsent(packed, key -> new CollapsibleModel(state, baseModel, key));
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        if (world.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)
        {
            return be.getModelData();
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
        private static final float MIN_DEPTH = .001F;

        private final Direction collapsedFace;
        private final boolean rotSplitEdge;
        private final float[] vertexPos = new float[4];

        public CollapsibleModel(BlockState state, BakedModel baseModel, int packedOffsets)
        {
            super(state, baseModel);
            this.collapsedFace = state.getValue(PropertyHolder.COLLAPSED_FACE).toDirection();
            this.rotSplitEdge = state.getValue(PropertyHolder.ROTATE_SPLIT_EDGE);

            byte[] vertexOffsets = FramedCollapsibleBlockEntity.unpackOffsets(packedOffsets);
            for (int i = 0; i < 4; i++)
            {
                vertexPos[i] = Math.max(1F - ((float) vertexOffsets[i] / 16F), MIN_DEPTH);
            }
        }

        @Override
        protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
        {
            Direction quadDir = quad.getDirection();
            if (collapsedFace == null || quadDir == collapsedFace.getOpposite())
            {
                quadMap.get(quadDir).add(quad);
                return;
            }

            if (quadDir == collapsedFace)
            {
                float diff02 = Math.abs(vertexPos[0] - vertexPos[2]);
                float diff13 = Math.abs(vertexPos[1] - vertexPos[3]);
                boolean rotate = (diff13 > diff02) != rotSplitEdge;

                QuadModifier.geometry(quad)
                        .apply(Modifiers.setPosition(vertexPos, true))
                        .applyIf(Modifiers.rotateVertices(), rotate)
                        .export(quadMap.get(null));
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

                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideUpDown(!top, posOne, posTwo))
                            .export(quadMap.get(quadDir));
                }
                else if (Utils.isY(quadDir))
                {
                    boolean top = quad.getDirection() == Direction.UP;
                    float posOne = vertexPos[top ? 0 : 1];
                    float posTwo = vertexPos[top ? 3 : 2];

                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutTopBottom(collapsedFace, posOne, posTwo))
                            .export(quadMap.get(quadDir));
                }
                else
                {
                    boolean right = collapsedFace == quadDir.getClockWise();
                    float posTop = vertexPos[right ? 3 : 0];
                    float posBot = vertexPos[right ? 2 : 1];

                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideLeftRight(collapsedFace, posTop, posBot))
                            .export(quadMap.get(quadDir));
                }
            }
        }

        @Override
        protected BakedModel getCamoModel(BlockState camoState)
        {
            if (camoState == FBContent.blockFramedCube.get().defaultBlockState()) { return baseModel; }
            return super.getCamoModel(camoState);
        }

        @Override
        protected boolean canRenderBaseModelInLayer(RenderType layer) { return layer == RenderType.solid(); }

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
    }
}