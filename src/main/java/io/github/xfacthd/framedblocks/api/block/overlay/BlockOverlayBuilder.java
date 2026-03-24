package io.github.xfacthd.framedblocks.api.block.overlay;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.Optionull;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class BlockOverlayBuilder
{
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

    BlockOverlayBuilder(String namespace)
    {
        this.namespace = namespace;
    }

    public BlockOverlayBuilder solidTexture(String solidTexture)
    {
        return solidTexture(Utils.id(namespace, BlockOverlay.TEXTURE_PREFIX + solidTexture));
    }

    public BlockOverlayBuilder solidTexture(Identifier solidTexture)
    {
        this.solidTexture = solidTexture;
        return this;
    }

    public BlockOverlayBuilder edgeTexture(String edgeTexture)
    {
        return edgeTexture(Utils.id(namespace, BlockOverlay.TEXTURE_PREFIX + edgeTexture + "_edge"));
    }

    public BlockOverlayBuilder edgeTexture(Identifier edgeTexture)
    {
        this.edgeTexture = edgeTexture;
        return this;
    }

    public BlockOverlayBuilder solidFace(BlockOverlay.SolidFace solidFace)
    {
        this.solidFace = solidFace;
        return this;
    }

    @SuppressWarnings("deprecation")
    public BlockOverlayBuilder tintSource(Block tintSource)
    {
        this.tintSource = tintSource.builtInRegistryHolder();
        return this;
    }

    @SuppressWarnings("deprecation")
    public BlockOverlayBuilder sourceItem(Item sourceItem)
    {
        this.sourceItem = sourceItem.builtInRegistryHolder();
        return this;
    }

    public BlockOverlayBuilder translucent()
    {
        this.translucent = true;
        return this;
    }

    public BlockOverlay build()
    {
        Objects.requireNonNull(solidTexture, "No solid texture specified");
        Objects.requireNonNull(solidFace, "No solid face specified");
        Objects.requireNonNull(sourceItem, "No source item specified");
        return new BlockOverlay(solidTexture, edgeTexture, solidFace, Optionull.map(this.tintSource, TintSource::new), sourceItem, translucent);
    }
}
