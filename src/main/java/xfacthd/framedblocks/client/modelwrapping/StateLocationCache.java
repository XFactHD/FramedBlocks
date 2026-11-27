package xfacthd.framedblocks.client.modelwrapping;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class StateLocationCache
{
    private static final Cache<BlockState, ModelResourceLocation> STATE_TO_LOCATION = CacheBuilder.newBuilder()
            .maximumSize(4096L)
            .build();
    private static final Cache<ModelResourceLocation, BlockState> LOCATION_TO_STATE = CacheBuilder.newBuilder()
            .maximumSize(4096L)
            .build();

    public static void clear()
    {
        STATE_TO_LOCATION.invalidateAll();
        LOCATION_TO_STATE.invalidateAll();
    }

    @Nullable
    public static BlockState getStateFromLocation(ResourceLocation blockId, Block block, ModelResourceLocation id)
    {
        BlockState cached = LOCATION_TO_STATE.getIfPresent(id);
        if (cached != null)
        {
            return cached;
        }
        BlockState parsed = parseStateFromVariant(block, id.getVariant());
        if (parsed != null)
        {
            LOCATION_TO_STATE.put(id, parsed);
        }
        return parsed;
    }

    public static ModelResourceLocation getLocationFromState(BlockState state, @Nullable ResourceLocation blockId)
    {
        ModelResourceLocation cached = STATE_TO_LOCATION.getIfPresent(state);
        if (cached != null)
        {
            return cached;
        }
        ResourceLocation id = blockId != null ? blockId : BuiltInRegistries.BLOCK.getKey(state.getBlock());
        ModelResourceLocation location = BlockModelShaper.stateToModelLocation(id, state);
        STATE_TO_LOCATION.put(state, location);
        return location;
    }

    @Nullable
    private static BlockState parseStateFromVariant(@Nullable Block block, @Nullable String variant)
    {
        if (block == null || variant == null)
        {
            return null;
        }
        if (variant.isEmpty())
        {
            return block.defaultBlockState();
        }
        StateDefinition<Block, BlockState> def = block.getStateDefinition();
        BlockState state = block.defaultBlockState();
        int start = 0;
        int len = variant.length();
        while (start <= len)
        {
            int comma = variant.indexOf(',', start);
            int end = comma >= 0 ? comma : len;
            int eq = variant.indexOf('=', start);
            if (eq <= start || eq >= end)
            {
                return null;
            }
            String name = variant.substring(start, eq);
            String value = variant.substring(eq + 1, end);
            Property<?> prop = def.getProperty(name);
            if (prop == null)
            {
                return null;
            }
            state = applyProperty(state, prop, value);
            if (state == null)
            {
                return null;
            }
            if (comma < 0)
            {
                break;
            }
            start = comma + 1;
        }
        return state;
    }

    @Nullable
    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> prop, String valueStr)
    {
        Optional<T> val = prop.getValue(valueStr);
        return val.map(v -> state.setValue(prop, v)).orElse(null);
    }

    private StateLocationCache() { }
}
