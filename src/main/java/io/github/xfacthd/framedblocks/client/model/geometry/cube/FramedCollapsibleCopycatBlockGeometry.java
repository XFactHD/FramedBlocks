package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.model.cache.QuadCacheKey;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FramedCollapsibleCopycatBlockGeometry extends Geometry
{
    public static final String ALT_BASE_MODEL_KEY = "alt_base";
    private static final int UP = Direction.UP.ordinal();
    private static final int DOWN = Direction.DOWN.ordinal();
    private static final int NORTH = Direction.NORTH.ordinal();
    private static final int EAST = Direction.EAST.ordinal();
    private static final int SOUTH = Direction.SOUTH.ordinal();
    private static final int WEST = Direction.WEST.ordinal();

    private final BlockState state;
    private final int solidFaces;
    private final Rotation rotation;
    private final BlockStateModel altBaseModel;

    public FramedCollapsibleCopycatBlockGeometry(GeometryFactory.Context ctx)
    {
        this.state = ctx.state();
        this.solidFaces = ctx.state().getValue(PropertyHolder.SOLID_FACES);
        this.rotation = ctx.state().getValue(PropertyHolder.COPYCAT_ROTATION);
        this.altBaseModel = ctx.auxModels().getModel(ALT_BASE_MODEL_KEY);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, ModelData data)
    {
        Direction quadDir = quad.direction();
        int packedOffsets = PackedCollapsibleBlockOffsets.get(data, state);
        if (packedOffsets == 0)
        {
            quadMap.get(quadDir).add(quad);
            return;
        }

        byte[] offsets = FramedCollapsibleCopycatBlockEntity.unpackOffsets(packedOffsets, rotation);
        boolean solid = (solidFaces & (1 << quadDir.ordinal())) != 0;
        List<QuadModifier> mods = new ArrayList<>(2);
        QuadModifier initialModifier = QuadModifier.of(quad).apply(Modifiers.setPosition((16F - offsets[quadDir.ordinal()]) / 16F));
        if (Utils.isY(quadDir))
        {
            if (offsets[NORTH] > 0 || offsets[SOUTH] > 0)
            {
                FloatPair length = getLenghts(offsets[NORTH], offsets[SOUTH]);
                if (length.valOne > 0F)
                {
                    mods.add(initialModifier
                            .derive()
                            .apply(Modifiers.cut(Direction.SOUTH, length.valOne))
                            .apply(Modifiers.offset(Direction.SOUTH, offsets[NORTH] / 16F))
                    );
                }
                if (length.valTwo > 0F)
                {
                    mods.add(initialModifier
                            .apply(Modifiers.cut(Direction.NORTH, length.valTwo))
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
                                .apply(Modifiers.cut(Direction.EAST, length.valOne))
                                .apply(Modifiers.offset(Direction.EAST, offsets[WEST] / 16F))
                                .export(quadMap.get(solid ? quadDir : null));
                    }
                    if (length.valTwo > 0F)
                    {
                        modifier.apply(Modifiers.cut(Direction.WEST, length.valTwo))
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
                            .apply(Modifiers.cut(axisNeg.getOpposite(), length.valOne))
                            .apply(Modifiers.offset(axisNeg.getOpposite(), offsets[axisMin] / 16F))
                    );
                }
                if (length.valTwo > 0F)
                {
                    mods.add(initialModifier
                            .apply(Modifiers.cut(axisNeg, length.valTwo))
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
                                .apply(Modifiers.cut(Direction.UP, length.valOne))
                                .apply(Modifiers.offset(Direction.UP, offsets[DOWN] / 16F))
                                .export(quadMap.get(solid ? quadDir : null));
                    }
                    if (length.valTwo > 0F)
                    {
                        modifier.apply(Modifiers.cut(Direction.DOWN, length.valTwo))
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
    public boolean useBaseModel()
    {
        return true;
    }

    @Override
    public BlockStateModel getBaseModel(BlockStateModel baseModel, boolean useAltModel)
    {
        return useAltModel ? altBaseModel : baseModel;
    }

    @Override
    public QuadCacheKey makeCacheKey(
            BlockAndTintGetter level,
            BlockPos pos,
            RandomSource random,
            CamoContent<?> camo,
            @Nullable Object ctCtx,
            boolean secondPart,
            boolean emissive,
            ModelData data
    )
    {
        int packedOffsets = PackedCollapsibleBlockOffsets.get(data, state);
        if (packedOffsets != 0)
        {
            return new CollapsibleCopycatBlockQuadCacheKey(camo, ctCtx, secondPart, emissive, packedOffsets);
        }
        return super.makeCacheKey(level, pos, random, camo, ctCtx, secondPart, emissive, data);
    }

    private record FloatPair(float valOne, float valTwo) { }

    private record CollapsibleCopycatBlockQuadCacheKey(
            CamoContent<?> camo,
            @Nullable Object ctCtx,
            boolean secondPart,
            boolean emissive,
            @Nullable Integer packedOffsets
    ) implements QuadCacheKey { }
}
