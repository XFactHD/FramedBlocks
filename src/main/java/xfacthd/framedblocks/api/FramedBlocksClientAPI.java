package xfacthd.framedblocks.api;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.render.OutlineRenderer;

import java.util.function.Predicate;

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

    /**
     * Generate overlay quads with the given texture based on all quads on the given side and insert them in the given
     * quad map after the existing quads on the given side of the active render type
     * @param quadMap The {@link QuadMap} containing all transformed quads
     * @param side The side (or {@code null} whose quads shall be operated on
     * @param sprite The texture to be applied to the overlay quads
     */
    void generateOverlayQuads(QuadMap quadMap, @Nullable Direction side, TextureAtlasSprite sprite);

    /**
     * Generate overlay quads with the given texture based on all quads on the given side filtered by the given predicate
     * and insert them in the given quad map after the existing quads on the given side of the active render type
     * @param quadMap The {@link QuadMap} containing all transformed quads
     * @param side The side (or {@code null} whose quads shall be operated on
     * @param sprite The texture to be applied to the overlay quads
     * @param filter The predicate to filter the quads with
     */
    void generateOverlayQuads(QuadMap quadMap, @Nullable Direction side, TextureAtlasSprite sprite, Predicate<BakedQuad> filter);
}
