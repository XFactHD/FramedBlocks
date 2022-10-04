package xfacthd.framedblocks.client.util;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.client.model.FluidDummyModel;
import xfacthd.framedblocks.client.render.BlockOutlineRenderer;
import xfacthd.framedblocks.client.render.GhostBlockRenderer;

@SuppressWarnings("unused")
public class ClientApiImpl implements FramedBlocksClientAPI
{
    @Override
    public BlockColor defaultBlockColor() { return FramedBlockColor.INSTANCE; }

    @Override
    public BakedModel createFluidModel(Fluid fluid) { return new FluidDummyModel(fluid); }

    @Override
    public void registerOutlineRender(IBlockType type, OutlineRender render)
    {
        BlockOutlineRenderer.registerOutlineRender(type, render);
    }

    @Override
    public void registerGhostRenderBehaviour(GhostRenderBehaviour behaviour, Block... blocks)
    {
        GhostBlockRenderer.registerBehaviour(behaviour, blocks);
    }

    @Override
    public void registerGhostRenderBehaviour(GhostRenderBehaviour behaviour, Item... items)
    {
        GhostBlockRenderer.registerBehaviour(behaviour, items);
    }

    @Override
    public boolean useDiscreteUVSteps() { return ClientConfig.useDiscreteUVSteps; }
}