package xfacthd.framedblocks.api;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.render.OutlineRenderer;

@SuppressWarnings({ "unused", "SameReturnValue" })
public interface FramedBlocksClientAPI
{
    FramedBlocksClientAPI INSTANCE = Utils.loadService(FramedBlocksClientAPI.class);



    /**
     * Returns the default block color implementation used by FramedBlocks for {@link BlockColor} proxying
     */
    BlockColor defaultBlockColor();

    /**
     * Register an {@link OutlineRenderer} for the given {@link IBlockType}
     * @param type The {@link IBlockType}, must return true for {@link IBlockType#hasSpecialHitbox()}
     */
    void registerOutlineRender(IBlockType type, OutlineRenderer render);

    /**
     * Register a custom {@link GhostRenderBehaviour} for the given {@link Block}s
     */
    void registerGhostRenderBehaviour(GhostRenderBehaviour behaviour, Block... blocks);

    /**
     * Register a custom {@link GhostRenderBehaviour} for the given {@link Item}s
     */
    void registerGhostRenderBehaviour(GhostRenderBehaviour behaviour, Item... items);

    /**
     * Add a {@link ModelProperty} for connected textures data to allow FramedBlocks to look up the data for use
     * in the caching of generated quads in the model
     */
    void addConTexProperty(ModelProperty<?> ctProperty);
}