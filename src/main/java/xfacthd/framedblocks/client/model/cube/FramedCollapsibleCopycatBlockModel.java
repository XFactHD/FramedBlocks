package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.*;

public class FramedCollapsibleCopycatBlockModel extends FramedBlockModel
{
    public static final ResourceLocation ALT_BASE_MODEL_LOC = Utils.rl("block/framed_collapsible_copycat_block_alt");
    private static final int UP = Direction.UP.ordinal();
    private static final int DOWN = Direction.DOWN.ordinal();
    private static final int NORTH = Direction.NORTH.ordinal();
    private static final int EAST = Direction.EAST.ordinal();
    private static final int SOUTH = Direction.SOUTH.ordinal();
    private static final int WEST = Direction.WEST.ordinal();
    private static BakedModel altBaseModel = null;

    private final int solidFaces;

    public FramedCollapsibleCopycatBlockModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.solidFaces = state.getValue(PropertyHolder.SOLID_FACES);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, ModelData data)
    {
        Direction quadDir = quad.getDirection();
        Integer packedOffsets = data.get(FramedCollapsibleCopycatBlockEntity.OFFSETS);
        if (packedOffsets == null)
        {
            quadMap.get(quadDir).add(quad);
            return;
        }

        byte[] offsets = FramedCollapsibleCopycatBlockEntity.unpackOffsets(packedOffsets);
        boolean solid = (solidFaces & (1 << quadDir.ordinal())) != 0;
        List<QuadModifier> mods = new ArrayList<>(2);
        QuadModifier initialModifier = QuadModifier.geometry(quad).apply(Modifiers.setPosition((16F - offsets[quadDir.ordinal()]) / 16F));
        if (Utils.isY(quadDir))
        {
            if (offsets[NORTH] > 0 || offsets[SOUTH] > 0)
            {
                FloatPair length = getLenghts(offsets[NORTH], offsets[SOUTH]);
                if (length.valOne > 0F)
                {
                    mods.add(initialModifier
                            .derive()
                            .apply(Modifiers.cutTopBottom(Direction.SOUTH, length.valOne))
                            .apply(Modifiers.offset(Direction.SOUTH, offsets[NORTH] / 16F))
                    );
                }
                if (length.valTwo > 0F)
                {
                    mods.add(initialModifier
                            .apply(Modifiers.cutTopBottom(Direction.NORTH, length.valTwo))
                            .apply(Modifiers.offset(Direction.NORTH, offsets[SOUTH] / 16F))
                    );
                }
            }
            else
            {
                mods.add(initialModifier);
            }

            if (offsets[EAST] > 0 || offsets[WEST] > 0)
            {
                FloatPair length = getLenghts(offsets[WEST], offsets[EAST]);
                for (QuadModifier modifier : mods)
                {
                    if (length.valOne > 0F)
                    {
                        modifier.derive()
                                .apply(Modifiers.cutTopBottom(Direction.EAST, length.valOne))
                                .apply(Modifiers.offset(Direction.EAST, offsets[WEST] / 16F))
                                .export(quadMap.get(solid ? quadDir : null));
                    }
                    if (length.valTwo > 0F)
                    {
                        modifier.apply(Modifiers.cutTopBottom(Direction.WEST, length.valTwo))
                                .apply(Modifiers.offset(Direction.WEST, offsets[EAST] / 16F))
                                .export(quadMap.get(solid ? quadDir : null));
                    }
                }
            }
            else
            {
                for (QuadModifier modifier : mods)
                {
                    modifier.export(quadMap.get(solid ? quadDir : null));
                }
            }
        }
        else
        {
            boolean xAxis = Utils.isX(quadDir);
            Direction axisNeg = xAxis ? Direction.NORTH : Direction.WEST;
            int axisMin = xAxis ? NORTH : WEST;
            int axisMax = xAxis ? SOUTH : EAST;
            if (offsets[axisMin] > 0 || offsets[axisMax] > 0)
            {
                FloatPair length = getLenghts(offsets[axisMin], offsets[axisMax]);
                if (length.valOne > 0F)
                {
                    mods.add(initialModifier
                            .derive()
                            .apply(Modifiers.cutSideLeftRight(axisNeg.getOpposite(), length.valOne))
                            .apply(Modifiers.offset(axisNeg.getOpposite(), offsets[axisMin] / 16F))
                    );
                }
                if (length.valTwo > 0F)
                {
                    mods.add(initialModifier
                            .apply(Modifiers.cutSideLeftRight(axisNeg, length.valTwo))
                            .apply(Modifiers.offset(axisNeg, offsets[axisMax] / 16F))
                    );
                }
            }
            else
            {
                mods.add(initialModifier);
            }

            if (offsets[DOWN] > 0 || offsets[UP] > 0)
            {
                FloatPair length = getLenghts(offsets[DOWN], offsets[UP]);
                for (QuadModifier modifier : mods)
                {
                    if (length.valOne > 0F)
                    {
                        modifier.derive()
                                .apply(Modifiers.cutSideUpDown(false, length.valOne))
                                .apply(Modifiers.offset(Direction.UP, offsets[DOWN] / 16F))
                                .export(quadMap.get(solid ? quadDir : null));
                    }
                    if (length.valTwo > 0F)
                    {
                        modifier.apply(Modifiers.cutSideUpDown(true, length.valTwo))
                                .apply(Modifiers.offset(Direction.DOWN, offsets[UP] / 16F))
                                .export(quadMap.get(solid ? quadDir : null));
                    }
                }
            }
            else
            {
                for (QuadModifier modifier : mods)
                {
                    modifier.export(quadMap.get(solid ? quadDir : null));
                }
            }
        }
    }

    private static FloatPair getLenghts(int offsetMin, int offsetMax)
    {
        float length = (16 - offsetMin - offsetMax) / 2F;
        boolean ceilFirst = offsetMin > offsetMax;
        return new FloatPair(
                (float)(ceilFirst ? Math.ceil(length) : Math.floor(length)) / 16F,
                (float)(ceilFirst ? Math.floor(length) : Math.ceil(length)) / 16F
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad) { }

    @Override
    protected boolean useBaseModel()
    {
        return true;
    }

    @Override
    protected BakedModel getCamoModel(BlockState camoState, boolean useBaseModel, boolean useAltModel)
    {
        if (useBaseModel && useAltModel)
        {
            return altBaseModel;
        }
        return super.getCamoModel(camoState, useBaseModel, useAltModel);
    }

    @Override
    protected QuadCacheKey makeCacheKey(BlockState state, Object ctCtx, ModelData data)
    {
        Integer packedOffsets = data.get(FramedCollapsibleCopycatBlockEntity.OFFSETS);
        return new CollapsibleCopycatBlockQuadCacheKey(state, ctCtx, packedOffsets);
    }

    private record FloatPair(float valOne, float valTwo) { }

    private record CollapsibleCopycatBlockQuadCacheKey(BlockState state, Object ctCtx, Integer packedOffsets) implements QuadCacheKey { }



    public static void captureAltBaseModel(Map<ResourceLocation, BakedModel> models)
    {
        altBaseModel = models.get(ALT_BASE_MODEL_LOC);
    }
}
