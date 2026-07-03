package io.github.xfacthd.framedblocks.api.model.block;

import io.github.xfacthd.framedblocks.api.render.fakelevel.DelegatingBlockRenderFakeLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

/// Extended block display context with the ability to provide level context to the blockstate model backing the block model.
@SuppressWarnings("ExtendsUtilityClass")
public final class FramedBlockDisplayContext extends BlockDisplayContext implements DelegatingBlockRenderFakeLevel {
    private final BlockAndTintGetter realLevel;
    private final BlockPos pos;
    private final BlockState state;
    private final ModelData modelData;

    /// @param realLevel The real level in which the model is being rendered, if available
    /// @param pos       The position the model is being rendered at
    /// @param state     The state of the block for which the model is being rendered
    /// @param modelData The model data provided by the block, if available
    public FramedBlockDisplayContext(BlockAndTintGetter realLevel, BlockPos pos, BlockState state, ModelData modelData) {
        this.realLevel = realLevel;
        this.pos = pos;
        this.state = state;
        this.modelData = modelData;
    }

    @Override
    public BlockAndTintGetter realLevel() {
        return realLevel;
    }

    @Override
    public BlockPos pos() {
        return pos;
    }

    @Override
    public BlockState state() {
        return state;
    }

    @Override
    public ModelData modelData() {
        return modelData;
    }
}
