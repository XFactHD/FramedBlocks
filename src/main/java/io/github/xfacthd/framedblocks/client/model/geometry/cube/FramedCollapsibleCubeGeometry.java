package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCubeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FramedCollapsibleCubeGeometry extends Geometry {
    public static final String DEFAULT_ALT_BASE_MODEL_KEY = "alt_base";
    public static final String COPYCAT_ALT_BASE_MODEL_KEY = "alt_copycat";

    private final BlockState state;
    private final boolean copycat;
    private final int solidFaces;
    private final BlockStateModel altBaseModel;

    public FramedCollapsibleCubeGeometry(GeometryFactory.Context ctx) {
        this.state = ctx.state();
        this.copycat = ctx.state().getValue(FramedProperties.COPYCAT_STYLE);
        this.solidFaces = ctx.state().getValue(PropertyHolder.SOLID_FACES);
        this.altBaseModel = ctx.auxModels().getModel(copycat ? COPYCAT_ALT_BASE_MODEL_KEY : DEFAULT_ALT_BASE_MODEL_KEY);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        Direction quadDir = quad.direction();
        int packedOffsets = PackedCollapsibleBlockOffsets.unwrap(cacheKeyUserData, state);
        if (packedOffsets == 0) {
            quadMap.getOrCreate(quadDir).add(quad);
            return;
        }

        byte[] offsets = FramedCollapsibleCubeBlockEntity.unpackOffsets(packedOffsets);
        boolean solid = (solidFaces & (1 << quadDir.ordinal())) != 0;
        List<QuadModifier> mods = new ArrayList<>(2);

        Iterator<Direction.Axis> axes = DirUtils.getPerpendicularAxes(quadDir.getAxis()).iterator();
        Direction.Axis axisOne = axes.next();
        Direction.Axis axisTwo = axes.next();

        float offOneNeg = offsets[axisOne.getNegative().ordinal()] / 16F;
        float offOnePos = offsets[axisOne.getPositive().ordinal()] / 16F;
        float offTwoNeg = offsets[axisTwo.getNegative().ordinal()] / 16F;
        float offTwoPos = offsets[axisTwo.getPositive().ordinal()] / 16F;
        byte depthOff = offsets[quadDir.ordinal()];

        boolean fullOne = Mth.equal(offOneNeg, 0F) && Mth.equal(offOnePos, 0F);
        boolean fullTwo = Mth.equal(offTwoNeg, 0F) && Mth.equal(offTwoPos, 0F);

        QuadModifier baseModifier = QuadModifier.of(quad);
        if (depthOff > 0) {
            baseModifier = baseModifier.apply(Modifiers.setPosition((16F - depthOff) / 16F));
        }
        if (fullOne && fullTwo) {
            if (depthOff == 0) {
                baseModifier.discard();
                return;
            }

            mods.add(baseModifier);
        } else if (copycat) {
            if (fullOne) {
                mods.add(baseModifier.derive().apply(Modifiers.cutCopycat(axisTwo.getNegative(), offTwoNeg, offTwoPos)));
                mods.add(baseModifier         .apply(Modifiers.cutCopycat(axisTwo.getPositive(), offTwoNeg, offTwoPos)));
            } else if (fullTwo) {
                mods.add(baseModifier.derive().apply(Modifiers.cutCopycat(axisOne.getNegative(), offOneNeg, offOnePos)));
                mods.add(baseModifier         .apply(Modifiers.cutCopycat(axisOne.getPositive(), offOneNeg, offOnePos)));
            } else {
                mods.add(baseModifier.derive().apply(Modifiers.cutCopycat(axisOne.getNegative(), axisTwo.getNegative(), offOneNeg, offOnePos, offTwoNeg, offTwoPos)));
                mods.add(baseModifier.derive().apply(Modifiers.cutCopycat(axisOne.getNegative(), axisTwo.getPositive(), offOneNeg, offOnePos, offTwoNeg, offTwoPos)));
                mods.add(baseModifier.derive().apply(Modifiers.cutCopycat(axisOne.getPositive(), axisTwo.getNegative(), offOneNeg, offOnePos, offTwoNeg, offTwoPos)));
                mods.add(baseModifier         .apply(Modifiers.cutCopycat(axisOne.getPositive(), axisTwo.getPositive(), offOneNeg, offOnePos, offTwoNeg, offTwoPos)));
            }
        } else {
            mods.add(baseModifier.apply(Modifiers.cut(axisOne, 1F - offOneNeg, 1F - offOnePos)).apply(Modifiers.cut(axisTwo, 1F - offTwoNeg, 1F - offTwoPos)));
        }

        Direction cullFace = solid ? quadDir : null;
        for (QuadModifier modifier : mods) {
            modifier.export(quadMap, cullFace);
        }
    }

    @Override
    public boolean useBaseModel() {
        return true;
    }

    @Override
    public BlockStateModel getBaseModel(BlockStateModel baseModel, boolean useAltModel) {
        return useAltModel ? altBaseModel : baseModel;
    }

    @Override
    public @Nullable Object computeCacheKeyUserData(BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data) {
        return data.get(PackedCollapsibleBlockOffsets.PROPERTY);
    }
}
