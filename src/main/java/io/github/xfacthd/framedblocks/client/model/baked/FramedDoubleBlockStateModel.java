package io.github.xfacthd.framedblocks.client.model.baked;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.cache.DoubleBlockStateCache;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedDoubleBlockData;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.render.fakelevel.FreestandingBlockRenderFakeLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public final class FramedDoubleBlockStateModel extends AbstractFramedBlockStateModel {
    private final BlockAndTintGetter dummyLevel;
    private final DoubleBlockTopInteractionMode particleMode;
    private final DoubleBlockParts parts;
    private final Supplier<PartModels> partModels;

    public FramedDoubleBlockStateModel(GeometryFactory.Context ctx) {
        super(ctx.baseModel(), ctx.state());
        DoubleBlockStateCache cache = ((IFramedDoubleBlock) state.getBlock()).getCache(state);
        this.parts = cache.getParts();
        this.partModels = Lazy.of(() -> new PartModels(
                ModelUtils.getFramedBlockModel(parts.stateOne()),
                ModelUtils.getFramedBlockModel(parts.stateTwo())
        ));
        ModelData dummyData = ModelData.of(AbstractFramedBlockData.PROPERTY, new FramedDoubleBlockData(
                parts,
                new FramedBlockData(state, EmptyCamoContainer.EMPTY, false, null),
                new FramedBlockData(state, EmptyCamoContainer.EMPTY, true, null)
        ));
        this.dummyLevel = new FreestandingBlockRenderFakeLevel.Simple(state, dummyData);
        this.particleMode = cache.getTopInteractionMode();
    }

    @Override
    public int collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> outParts, int miscTintOffset) {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        if (fbData == null) {
            level = dummyLevel;
            pos = BlockPos.ZERO;
        }

        PartModels models = partModels.get();
        miscTintOffset = models.modelOne.collectParts(level, pos, parts.stateOne(), random, outParts, miscTintOffset);
        miscTintOffset = models.modelTwo.collectParts(level, pos, parts.stateTwo(), random, outParts, miscTintOffset);
        return miscTintOffset;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        return switch (particleMode) {
            case FIRST -> getMaterialOrDefault(level, pos, state, fbData, false);
            case SECOND -> getMaterialOrDefault(level, pos, state, fbData, true);
            case BOTH -> {
                Material.Baked sprite = getMaterial(level, pos, state, fbData, false);
                if (sprite != null) {
                    yield sprite;
                }

                sprite = getMaterial(level, pos, state, fbData, true);
                if (sprite != null) {
                    yield sprite;
                }

                yield delegate.particleMaterial();
            }
        };
    }

    @Override
    public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return partModels.get().materialFlags(parts, level, pos);
    }

    /// Returns the camo-dependent particle texture of the side given by `key` when the camo is not air,
    /// else returns the basic "framed block" sprite
    @SuppressWarnings("deprecation")
    private Material.Baked getMaterialOrDefault(BlockAndTintGetter level, BlockPos pos, BlockState state, @Nullable AbstractFramedBlockData data, boolean secondary) {
        Material.Baked material = getMaterial(level, pos, state, data, secondary);
        return material != null ? material : delegate.particleMaterial();
    }

    private Material.@Nullable Baked getMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state, @Nullable AbstractFramedBlockData data, boolean secondary) {
        if (data != null) {
            FramedBlockData fbData = data.unwrap(secondary);
            if (!fbData.getCamoContent().isEmpty()) {
                return partModels.get().getPart(secondary).particleMaterial(level, pos, state);
            }
        }
        return null;
    }

    private record PartModels(AbstractFramedBlockStateModel modelOne, AbstractFramedBlockStateModel modelTwo) {
        AbstractFramedBlockStateModel getPart(boolean second) {
            return second ? modelTwo : modelOne;
        }

        int materialFlags(DoubleBlockParts parts, BlockAndTintGetter level, BlockPos pos) {
            return modelOne.materialFlags(level, pos, parts.stateOne()) | modelTwo.materialFlags(level, pos, parts.stateTwo());
        }
    }
}
