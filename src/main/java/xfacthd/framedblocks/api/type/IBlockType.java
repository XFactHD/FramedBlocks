package xfacthd.framedblocks.api.type;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedDoubleBlockEntity;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.api.shapes.ShapeProvider;

public interface IBlockType
{
    boolean canOccludeWithSolidCamo();

    boolean hasSpecialHitbox();

    @ApiStatus.OverrideOnly
    FullFacePredicate getFullFacePredicate();

    SideSkipPredicate getSideSkipPredicate();

    @ApiStatus.OverrideOnly
    ConnectionPredicate getConnectionPredicate();

    ShapeProvider generateShapes(ImmutableList<BlockState> states);

    boolean hasSpecialTile();

    boolean hasBlockItem();

    boolean supportsWaterLogging();

    boolean supportsConnectedTextures();

    /**
     * {@return the minimum {@link ConTexMode } required for this block to react to texture connections}
     */
    ConTexMode getMinimumConTexMode();

    /**
     * @implNote If this method returns true, then the associated block must override {@link Block#initializeClient(java.util.function.Consumer)}
     * and pass an instance of {@link FramedBlockRenderProperties} to the consumer to avoid crashing when the block is
     * hit while it can be passed through
     */
    boolean allowMakingIntangible();

    /**
     * @return true if this type represents a block that combines two models into one and allows those to have separate
     * camos applied.
     *
     * @apiNote if a block's type returns true from this method, its {@link BlockEntity} must implement
     * {@link IFramedDoubleBlockEntity}. The {@link Block} and {@link BakedModel} are not required to extend or implement
     * any specific class
     */
    default boolean isDoubleBlock()
    {
        return false;
    }

    /**
     * Return true if this block allows locking the state in order to suppress state changes from neighbor updates.
     * Useful to allow blocks like stairs to reside in impossible states, like a corner without neighbors
     * @implNote If this method returns true, then the associated block must have the {@link FramedProperties#STATE_LOCKED} property.
     * The actual update suppression needs to be handled by each block and is not automated
     */
    boolean canLockState();

    String getName();

    int compareTo(IBlockType other);
}