package io.github.xfacthd.framedblocks.api.model.block;

import io.github.xfacthd.framedblocks.api.render.fakelevel.DelegatingBlockRenderFakeLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

@SuppressWarnings("ExtendsUtilityClass")
public final class FramedBlockDisplayContext extends BlockDisplayContext implements DelegatingBlockRenderFakeLevel
{
    private final BlockAndTintGetter realLevel;
    private final BlockPos pos;
    private final BlockState state;
    private final ModelData modelData;

    public FramedBlockDisplayContext(BlockAndTintGetter realLevel, BlockPos pos, BlockState state, ModelData modelData)
    {
        this.realLevel = realLevel;
        this.pos = pos;
        this.state = state;
        this.modelData = modelData;
    }

    @Override
    public BlockAndTintGetter realLevel()
    {
        return realLevel;
    }

    @Override
    public BlockPos pos()
    {
        return pos;
    }

    @Override
    public BlockState state()
    {
        return state;
    }

    @Override
    public ModelData modelData()
    {
        return modelData;
    }
}
