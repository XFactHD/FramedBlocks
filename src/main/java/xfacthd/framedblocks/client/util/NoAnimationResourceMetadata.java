package xfacthd.framedblocks.client.util;

import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.ResourceMetadata;

import java.util.Optional;

/**
 * Metadata wrapper which hides animation metadata from the sprite contents built by the animation splitter source
 */
public final class NoAnimationResourceMetadata implements ResourceMetadata
{
    private final ResourceMetadata wrapped;

    public NoAnimationResourceMetadata(ResourceMetadata wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public <T> Optional<T> getSection(MetadataSectionSerializer<T> serializer)
    {
        if (serializer == AnimationMetadataSection.SERIALIZER)
        {
            return Optional.empty();
        }
        return wrapped.getSection(serializer);
    }
}
