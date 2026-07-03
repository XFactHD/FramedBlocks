package io.github.xfacthd.framedblocks.api.block.overlay;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.Optionull;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/// Datagen builder for [BlockOverlay]s.
@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class BlockOverlayBuilder {
    private final String namespace;
    @Nullable
    private Identifier solidTexture;
    @Nullable
    private Identifier edgeTexture;
    private BlockOverlay.@Nullable SolidFace solidFace = null;
    @Nullable
    private Holder<Block> tintSource;
    @Nullable
    private Holder<Item> sourceItem;
    private boolean translucent;

    BlockOverlayBuilder(String namespace) {
        this.namespace = namespace;
    }

    /// Set the solid texture applied by this overlay. Required.
    ///
    /// @param solidTexture The texture path of the solid texture relative to the [BlockOverlay#TEXTURE_PREFIX] folder in the namespace of this builder
    /// @return this builder
    public BlockOverlayBuilder solidTexture(String solidTexture) {
        return solidTexture(Utils.id(namespace, BlockOverlay.TEXTURE_PREFIX + solidTexture));
    }

    /// Set the solid texture applied by this overlay. Required.
    ///
    /// @param solidTexture The texture path of the solid texture
    /// @return this builder
    public BlockOverlayBuilder solidTexture(Identifier solidTexture) {
        this.solidTexture = solidTexture;
        return this;
    }

    /// Set the edge texture applied by this overlay. Optional.
    ///
    /// @param edgeTexture The texture path of the edge texture relative to the [BlockOverlay#TEXTURE_PREFIX] folder in the namespace of this builder
    /// @return this builder
    public BlockOverlayBuilder edgeTexture(String edgeTexture) {
        return edgeTexture(Utils.id(namespace, BlockOverlay.TEXTURE_PREFIX + edgeTexture + "_edge"));
    }

    /// Set the edge texture applied by this overlay. Optional.
    ///
    /// @param edgeTexture The texture path of the edge texture
    /// @return this builder
    public BlockOverlayBuilder edgeTexture(Identifier edgeTexture) {
        this.edgeTexture = edgeTexture;
        return this;
    }

    /// Set which faces of a block this overlay's solid texture will be applied to. Required.
    ///
    /// @param solidFace The faces the solid texture will be applied to
    /// @return this builder
    public BlockOverlayBuilder solidFace(BlockOverlay.SolidFace solidFace) {
        this.solidFace = solidFace;
        return this;
    }

    /// Set the block to pull the tint color from. Optional.
    ///
    /// @param tintSource The block to pull the tint color from.
    /// @return this builder
    @SuppressWarnings("deprecation")
    public BlockOverlayBuilder tintSource(Block tintSource) {
        this.tintSource = tintSource.builtInRegistryHolder();
        return this;
    }

    /// Set the item to use for applying this overlay. Required.
    ///
    /// @param sourceItem The application item
    /// @return this builder
    @SuppressWarnings("deprecation")
    public BlockOverlayBuilder sourceItem(Item sourceItem) {
        this.sourceItem = sourceItem.builtInRegistryHolder();
        return this;
    }

    /// Mark this overlay as translucent.
    ///
    /// @return this builder
    public BlockOverlayBuilder translucent() {
        this.translucent = true;
        return this;
    }

    /// {@return the builder overlay}
    public BlockOverlay build() {
        Objects.requireNonNull(solidTexture, "No solid texture specified");
        Objects.requireNonNull(solidFace, "No solid face specified");
        Objects.requireNonNull(sourceItem, "No source item specified");
        return new BlockOverlay(solidTexture, edgeTexture, solidFace, Optionull.map(this.tintSource, TintSource::new), sourceItem, translucent);
    }
}
