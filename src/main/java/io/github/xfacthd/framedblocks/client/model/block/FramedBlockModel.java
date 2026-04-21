package io.github.xfacthd.framedblocks.client.model.block;

import com.mojang.math.Transformation;
import io.github.xfacthd.framedblocks.api.model.block.FramedBlockDisplayContext;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.joml.Matrix4fc;

import java.util.List;
import java.util.Optional;

public final class FramedBlockModel implements BlockModel {
    private final BlockStateModel model;
    private final Matrix4fc transformation;

    public FramedBlockModel(BlockStateModel model, Matrix4fc transformation) {
        this.model = model;
        this.transformation = transformation;
    }

    @Override
    public void update(BlockModelRenderState output, BlockState state, BlockDisplayContext context, long seed) {
        BlockAndTintGetter level;
        BlockPos pos;
        if (context instanceof FramedBlockDisplayContext ctx) {
            level = ctx;
            pos = ctx.pos();
        } else {
            level = BlockAndTintGetter.EMPTY;
            pos = BlockPos.ZERO;
        }
        int materialFlags = model.materialFlags(level, pos, state);
        List<BlockStateModelPart> partList = output.setupModel(transformation, (materialFlags & BakedQuad.FLAG_TRANSLUCENT) != 0);
        model.collectParts(level, pos, state, output.scratchRandomSource(seed), partList);
        if (output instanceof AdvancedBlockModelRenderState advOut) {
            advOut.setAnimated((materialFlags & BakedQuad.FLAG_ANIMATED) != 0);
        }
        IClientBlockExtensions.of(state).collectDynamicTintValues(state, level, pos, output.tintLayers());
    }

    public record Unbaked(BlockState state, Optional<Transformation> transformation) implements BlockModel.Unbaked {
        @Override
        public BlockModel bake(BakingContext context, Matrix4fc transformation) {
            Matrix4fc modelTransform = Transformation.compose(transformation, this.transformation);
            return new FramedBlockModel(context.modelGetter().apply(state), modelTransform);
        }
    }
}
