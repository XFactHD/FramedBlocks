package xfacthd.framedblocks.client.apiimpl;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.client.data.ConTexDataHandler;
import xfacthd.framedblocks.client.render.special.BlockOutlineRenderer;
import xfacthd.framedblocks.client.render.special.GhostBlockRenderer;
import xfacthd.framedblocks.client.util.FramedBlockColor;

@SuppressWarnings("unused")
public final class ClientApiImpl implements FramedBlocksClientAPI
{
    @Override
    public BlockColor defaultBlockColor()
    {
        return FramedBlockColor.INSTANCE;
    }

    @Override
    public void registerOutlineRender(IBlockType type, OutlineRenderer render)
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
    public void addConTexProperty(ModelProperty<?> ctProperty)
    {
        ConTexDataHandler.addConTexProperty(ctProperty);
    }
}