package xfacthd.framedblocks.api;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.model.SolidFrameMode;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import xfacthd.framedblocks.api.util.WriteOnceHolder;
import xfacthd.framedblocks.api.render.OutlineRenderer;

@SuppressWarnings({ "unused", "SameReturnValue" })
public interface FramedBlocksClientAPI
{
    WriteOnceHolder<FramedBlocksClientAPI> INSTANCE = new WriteOnceHolder<>();

    static FramedBlocksClientAPI getInstance()
    {
        return INSTANCE.get();
    }


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
     * Returns true if the UV remapping should use discrete steps instead of using the interpolated value directly
     */
    boolean useDiscreteUVSteps();

    /**
     * Returns the currently configured {@link ConTexMode}
     */
    ConTexMode getConTexMode();

    /**
     * Returns the currently configured {@link SolidFrameMode}
     */
    SolidFrameMode getSolidFrameMode();

    /**
     * Add a {@link ModelProperty} for connected textures data to allow FramedBlocks to look up the data for use
     * in the caching of generated quads in the model
     */
    void addConTexProperty(ModelProperty<?> ctProperty);

    /**
     * Attempts to extract a connected textures context object from the given {@link ModelData}
     * if available (requires specific support for each mod adding connected textures)
     */
    Object extractCTContext(ModelData data);
}