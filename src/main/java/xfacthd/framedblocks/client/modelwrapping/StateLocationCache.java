package xfacthd.framedblocks.client.modelwrapping;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class StateLocationCache
{
    private static final BiMap<ModelResourceLocation, BlockState> STATE_LOCATIONS = HashBiMap.create();

    public static void clear()
    {
        STATE_LOCATIONS.clear();
    }

    public static BlockState getStateFromLocation(ResourceLocation blockId, Block block, ModelResourceLocation id)
    {
        if (!STATE_LOCATIONS.containsKey(id))
        {
            computeStateLocationsForBlock(block, blockId);
        }
        return STATE_LOCATIONS.get(id);
    }

    public static ModelResourceLocation getLocationFromState(BlockState state, @Nullable ResourceLocation blockId)
    {
        BiMap<BlockState, ModelResourceLocation> inverse = STATE_LOCATIONS.inverse();
        if (!inverse.containsKey(state))
        {
            if (blockId == null)
            {
                blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            }
            computeStateLocationsForBlock(state.getBlock(), blockId);
        }
        return inverse.get(state);
    }

    private static void computeStateLocationsForBlock(Block block, ResourceLocation blockId)
    {
        block.getStateDefinition().getPossibleStates().forEach(state ->
                STATE_LOCATIONS.put(BlockModelShaper.stateToModelLocation(blockId, state), state)
        );
    }



    private StateLocationCache() { }
}
