package xfacthd.framedblocks.api;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.material.Fluid;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.WriteOnceHolder;
import xfacthd.framedblocks.api.util.client.OutlineRender;

@SuppressWarnings("unused")
public interface FramedBlocksClientAPI
{
    WriteOnceHolder<FramedBlocksClientAPI> INSTANCE = new WriteOnceHolder<>();

    static FramedBlocksClientAPI getInstance() { return INSTANCE.get(); }


    /**
     * Returns the default block color implementation used by FramedBlocks for {@link BlockColor} proxying
     */
    BlockColor defaultBlockColor();

    /**
     * Creates a {@link BakedModel} filling the whole block volume and using the given {@link Fluid}'s textures
     * mimicking the standard fluid rendering for use as a camo model
     */
    BakedModel createFluidModel(Fluid fluid);

    /**
     * Register an {@link OutlineRender} for the given {@link IBlockType}
     * @param type The {@link IBlockType}, must return true for {@link IBlockType#hasSpecialHitbox()}
     */
    void registerOutlineRender(IBlockType type, OutlineRender render);
}