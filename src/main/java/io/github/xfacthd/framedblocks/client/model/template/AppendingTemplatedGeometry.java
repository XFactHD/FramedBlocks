package io.github.xfacthd.framedblocks.client.model.template;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.template.TemplateOverlayProvider;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

final class AppendingTemplatedGeometry extends BasicTemplatedGeometry {
    private final BlockState state;
    private final BlockStateModel baseModel;
    private final boolean includeNull;
    private final boolean cullNonNull;
    @Nullable
    private final BlockState shaderState;

    AppendingTemplatedGeometry(
            BlockState state,
            BlockStateModel baseModel,
            GeometryTemplateSpecEntry geoSpec,
            QuadSpec[][] quadSpecs,
            boolean transformAllQuads,
            @Nullable TemplateOverlayProvider overlay
    ) {
        super(geoSpec, quadSpecs, transformAllQuads, overlay);
        this.state = state;
        this.baseModel = baseModel;
        BaseModelAppenderConfig appenderConfig = Objects.requireNonNull(geoSpec.appenderConfig());
        this.includeNull = appenderConfig.includeNull();
        this.cullNonNull = appenderConfig.cullNonNull();
        this.shaderState = appenderConfig.shaderState();
    }

    @Override
    public boolean hasAdditionalUncachedParts() {
        return true;
    }

    @Override
    public void collectAdditionalPartsUncached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, ModelData data) {
        consumer.acceptAll(baseModel, level, pos, random, state, includeNull, false, cullNonNull, shaderState, null);
    }
}
