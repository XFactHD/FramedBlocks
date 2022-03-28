package xfacthd.framedblocks.api.type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.CtmPredicate;
import xfacthd.framedblocks.api.util.SideSkipPredicate;

public interface IBlockType
{
    default boolean canOccludeWithSolidCamo() { return false; }

    boolean hasSpecialHitbox();

    CtmPredicate getCtmPredicate();

    SideSkipPredicate getSideSkipPredicate();

    ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states);

    boolean hasSpecialTile();

    boolean hasBlockItem();

    boolean supportsWaterLogging();

    /**
     * @implNote If this method returns true, then the associated block must override {@link net.minecraft.world.level.block.Block#initializeClient(java.util.function.Consumer)}
     * and pass an instance of {@link xfacthd.framedblocks.api.util.client.FramedBlockRenderProperties} to the consumer to avoid crashing when the block is
     * hit while it can be passed through
     */
    default boolean allowMakingIntangible() { return false; }

    default boolean isDoubleBlock() { return false; }

    String getName();

    int compareTo(IBlockType other);
}