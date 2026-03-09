package io.github.xfacthd.framedblocks.api.block.overlay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Describes an additional overlay that can be applied on top of a framed block's camo.
 *
 * @param solidTexture The texture ID to apply to faces marked as full coverage via {@code solidFaces}
 * @param edgeTexture  The texture ID to apply to edges adjacent to full faces (top edge of the texture is assumed up/north)
 * @param solidFace    The {@link SolidFace} to which the full texture should be applied
 * @param tintSource   The {@link Block} to pull the tint color from, if applicable
 * @param sourceItem   The {@link Item} to use for applying this overlay
 * @param translucent  Whether the overlay has translucent pixels
 */
public record BlockOverlay(
        Identifier solidTexture,
        @Nullable Identifier edgeTexture,
        SolidFace solidFace,
        @Nullable Holder<Block> tintSource,
        Holder<Item> sourceItem,
        boolean translucent
)
{
    private static final Set<Direction> HORIZONTAL_DIRECTIONS = Set.of(Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new));
    public static final Codec<BlockOverlay> DIRECT_CODEC = RecordCodecBuilder.<BlockOverlay>create(inst -> inst.group(
            Identifier.CODEC.fieldOf("solid_texture").forGetter(BlockOverlay::solidTexture),
            Identifier.CODEC.optionalFieldOf("edge_texture").forGetter(BlockOverlay::edgeTextureForSerialization),
            SolidFace.CODEC.fieldOf("solid_face").forGetter(BlockOverlay::solidFace),
            BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("tint_source").forGetter(BlockOverlay::tintSourceForSerialization),
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("source_item").forGetter(BlockOverlay::sourceItem),
            Codec.BOOL.optionalFieldOf("translucent", false).forGetter(BlockOverlay::translucent)
    ).apply(inst, BlockOverlay::new)).validate(BlockOverlay::validate);
    public static final Codec<Holder<BlockOverlay>> CODEC = RegistryFixedCodec.create(FramedConstants.BLOCK_OVERLAY_REGISTRY_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<BlockOverlay>> STREAM_CODEC = ByteBufCodecs.holderRegistry(FramedConstants.BLOCK_OVERLAY_REGISTRY_KEY);
    public static final String TEXTURE_PREFIX = "block/overlay/";
    public static final int OVERLAY_TINT_INDEX = Integer.MAX_VALUE;

    private BlockOverlay(
            Identifier solidTexture,
            Optional<Identifier> edgeTexture,
            SolidFace solidFace,
            Optional<Holder<Block>> tintSource,
            Holder<Item> sourceItem,
            boolean translucent
    )
    {
        this(solidTexture, edgeTexture.orElse(null), solidFace, tintSource.orElse(null), sourceItem, translucent);
    }

    public boolean isSideSolid(BlockState state, Direction side)
    {
        if (solidFace.dynamic)
        {
            return solidFace.getDynamicDirections(state).contains(side);
        }
        return solidFace.directions.contains(side);
    }

    @SuppressWarnings("NullableProblems")
    private Optional<Identifier> edgeTextureForSerialization()
    {
        return Optional.ofNullable(edgeTexture);
    }

    @SuppressWarnings("NullableProblems")
    private Optional<Holder<Block>> tintSourceForSerialization()
    {
        return Optional.ofNullable(tintSource);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this;
    }

    @Override
    public int hashCode()
    {
        return System.identityHashCode(this);
    }

    public static Component getName(Holder<BlockOverlay> overlay)
    {
        return Component.translatable(getDescriptionId(overlay));
    }

    public static String getDescriptionId(Holder<BlockOverlay> overlay)
    {
        return Util.makeDescriptionId("block_overlay", Utils.getKeyOrThrow(overlay).identifier());
    }

    public static BlockOverlayBuilder builder(String namespace)
    {
        return new BlockOverlayBuilder(namespace);
    }

    private static DataResult<BlockOverlay> validate(BlockOverlay overlay)
    {
        if (overlay.edgeTexture() != null && overlay.solidFace() == BlockOverlay.SolidFace.ALL)
        {
            return DataResult.error(() -> "Overlay requests edge generation but has no non-solid faces");
        }
        return DataResult.success(overlay);
    }

    public enum SolidFace implements StringRepresentable
    {
        ALL(Set.of(Direction.values())),
        TOP(Set.of(Direction.UP)),
        BOTTOM(Set.of(Direction.DOWN)),
        HORIZONTAL(HORIZONTAL_DIRECTIONS),
        VERTICAL(Set.of(Direction.UP, Direction.DOWN)),
        AXIS_TUBE(HORIZONTAL)
        {
            @Override
            public Set<Direction> getDynamicDirections(BlockState state)
            {
                if (state.getBlock() instanceof AxisOverlayCarrier axisOverlayCarrier)
                {
                    return DirUtils.getAxisTubeFaces(axisOverlayCarrier.getAxis(state));
                }
                return Set.of();
            }
        },
        AXIS_CAPS(VERTICAL)
        {
            @Override
            public Set<Direction> getDynamicDirections(BlockState state)
            {
                if (state.getBlock() instanceof AxisOverlayCarrier axisOverlayCarrier)
                {
                    return DirUtils.getAxisCapFaces(axisOverlayCarrier.getAxis(state));
                }
                return Set.of();
            }
        },
        ;

        private static final Codec<SolidFace> CODEC = StringRepresentable.fromEnum(SolidFace::values);

        private final String name = toString().toLowerCase(Locale.ROOT);
        private final Set<Direction> directions;
        private final boolean dynamic;

        SolidFace(Set<Direction> directions)
        {
            this.directions = directions;
            this.dynamic = false;
        }

        SolidFace(SolidFace fallback)
        {
            this.directions = fallback.directions;
            this.dynamic = true;
        }

        public Set<Direction> getDynamicDirections(BlockState state)
        {
            throw new UnsupportedOperationException();
        }

        public Set<Direction> getDirections()
        {
            return directions;
        }

        public boolean isDynamic()
        {
            return dynamic;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }
}
