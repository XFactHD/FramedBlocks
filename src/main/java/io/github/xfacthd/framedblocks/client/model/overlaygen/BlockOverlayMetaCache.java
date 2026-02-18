package io.github.xfacthd.framedblocks.client.model.overlaygen;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingManager;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class BlockOverlayMetaCache
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Map<Object, Entry> META_CACHE = new ConcurrentHashMap<>();

    static Entry get(BlockOverlay overlay, @Nullable BlockState state)
    {
        Object key;
        if (state != null && overlay.solidFace().isDynamic())
        {
            StateMerger stateMerger = ModelWrappingManager.tryGetStateMerger(state.getBlock());
            key = new DynamicKey(overlay, stateMerger.apply(state));
        }
        else
        {
            key = overlay;
        }
        return META_CACHE.computeIfAbsent(key, BlockOverlayMetaCache::computeMetadata);
    }

    static void clear(CacheCleaner.Reason reason)
    {
        if (reason == CacheCleaner.Reason.RELOAD || reason == CacheCleaner.Reason.DISCONNECT)
        {
            META_CACHE.clear();
        }
    }

    private static Entry computeMetadata(Object key)
    {
        BlockOverlay overlay;
        BlockState state;
        if (key instanceof DynamicKey(BlockOverlay keyOverlay, BlockState keyState))
        {
            overlay = keyOverlay;
            state = keyState;
        }
        else
        {
            overlay = (BlockOverlay) key;
            state = null;
        }

        BlockOverlay.SolidFace solidFace = overlay.solidFace();
        Set<Direction> fullFaces = Set.of();
        boolean stateDependent = false;
        if (state != null && solidFace.isDynamic())
        {
            fullFaces = solidFace.getDynamicDirections(state);
            if (!fullFaces.isEmpty())
            {
                stateDependent = true;
            }
        }
        if (fullFaces.isEmpty())
        {
            fullFaces = solidFace.getDirections();
        }
        Map<Direction, Set<Direction>> edges;
        if (overlay.edgeTexture() != null)
        {
            edges = new EnumMap<>(Direction.class);
            for (Direction fullFace : fullFaces)
            {
                for (Direction dir : DIRECTIONS)
                {
                    if (dir.getAxis() != fullFace.getAxis() && !fullFaces.contains(dir))
                    {
                        edges.computeIfAbsent(dir, $ -> EnumSet.noneOf(Direction.class)).add(fullFace);
                    }
                }
            }
        }
        else
        {
            edges = Map.of();
        }

        TextureAtlasSprite solidSprite = FluidSpriteCache.getSprite(overlay.solidTexture());
        TextureAtlasSprite edgeSprite = null;
        float edgeHeight = 0F;
        if (overlay.edgeTexture() != null)
        {
            edgeSprite = FluidSpriteCache.getSprite(overlay.edgeTexture());

            SpriteContents contents = edgeSprite.contents();
            if (contents.isAnimated())
            {
                edgeHeight = (float) contents.getUniqueFrames()
                        .mapToDouble(frame -> computeSpriteHeight(contents, frame))
                        .max()
                        .orElse(0D);
            }
            else
            {
                edgeHeight = computeSpriteHeight(contents, 0);
            }
        }

        return new Entry(fullFaces, edges, solidSprite, edgeSprite, edgeHeight, stateDependent);
    }

    private static float computeSpriteHeight(SpriteContents contents, int frame)
    {
        int width = contents.width();
        int height = contents.height();
        int maxV = 0;
        outer: for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if (!contents.isTransparent(frame, x, y))
                {
                    maxV++;
                    continue outer;
                }
                else if (x == width - 1)
                {
                    break outer;
                }
            }
        }
        return maxV / (float) height;
    }

    private record DynamicKey(BlockOverlay overlay, BlockState state) { }

    record Entry(
            Set<Direction> solidFaces,
            Map<Direction, Set<Direction>> edgesByFace,
            TextureAtlasSprite solidSprite,
            @Nullable TextureAtlasSprite edgeSprite,
            float edgeHeight,
            boolean stateDependent
    )
    {
        boolean isFaceAffected(Direction face)
        {
            return solidFaces.contains(face) || edgesByFace.containsKey(face);
        }
    }

    private BlockOverlayMetaCache() { }
}
