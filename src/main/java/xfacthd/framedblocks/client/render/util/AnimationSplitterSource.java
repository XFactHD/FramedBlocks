package xfacthd.framedblocks.client.render.util;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.*;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.FramedConstants;

import java.util.List;
import java.util.Optional;

public final class AnimationSplitterSource implements SpriteSource
{
    private static SpriteSourceType TYPE = null;
    private static final Codec<AnimationSplitterSource> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("resource").forGetter(s -> s.resource),
            ExtraCodecs.nonEmptyList(Frame.CODEC.listOf()).fieldOf("frames").forGetter(s -> s.frames)
    ).apply(inst, AnimationSplitterSource::new));

    private final ResourceLocation resource;
    private final List<Frame> frames;

    public AnimationSplitterSource(ResourceLocation resource, List<Frame> frames)
    {
        this.resource = resource;
        this.frames = frames;
    }

    @Override
    public void run(ResourceManager mgr, Output out)
    {
        ResourceLocation texPath = TEXTURE_ID_CONVERTER.idToFile(resource);
        Optional<Resource> optResource = mgr.getResource(texPath);
        if (optResource.isPresent())
        {
            Resource res = optResource.get();
            LazyLoadedImage image = new LazyLoadedImage(texPath, res, frames.size());
            frames.forEach(frame -> out.add(frame.outLoc, new FrameInstance(res, texPath, image, frame)));
        }
        else
        {
            FramedBlocks.LOGGER.warn("Missing sprite: {}", texPath);
        }
    }

    @Override
    public SpriteSourceType type()
    {
        Preconditions.checkNotNull(TYPE, "SpriteSourceType not registered");
        return TYPE;
    }



    public record Frame(int frameIdx, ResourceLocation outLoc)
    {
        private static final Codec<Frame> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("frame_idx").forGetter(Frame::frameIdx),
                ResourceLocation.CODEC.fieldOf("sprite").forGetter(Frame::outLoc)
        ).apply(inst, Frame::new));
    }

    private record FrameInstance(
            Resource resource, ResourceLocation texPath, LazyLoadedImage lazyImage, Frame frame
    ) implements SpriteSupplier
    {
        @Override
        public SpriteContents get()
        {
            try
            {
                Optional<AnimationMetadataSection> optAnim = resource.metadata().getSection(AnimationMetadataSection.SERIALIZER);
                if (optAnim.isEmpty())
                {
                    throw new IllegalArgumentException("Texture '%s' is not an animated texture".formatted(texPath));
                }

                NativeImage image = lazyImage.get();
                int imgW = image.getWidth();
                int imgH = image.getHeight();

                AnimationMetadataSection anim = optAnim.get();
                FrameSize size = anim.calculateFrameSize(imgW, imgH);
                int frameW = size.width();
                int frameH = size.height();

                int rowSize = (imgW / frameW) * (imgH / frameH);
                checkFrameExists(texPath, anim, frame.frameIdx, rowSize);
                int srcX = (frame.frameIdx % rowSize) * frameW;
                int srcY = (frame.frameIdx / rowSize) * frameH;

                NativeImage imageOut = new NativeImage(NativeImage.Format.RGBA, frameW, frameH, false);
                image.copyRect(imageOut, srcX, srcY, 0, 0, frameW, frameH, false, false);
                return new SpriteContents(frame.outLoc, new FrameSize(frameW, frameH), imageOut, AnimationMetadataSection.EMPTY, null);
            }
            catch (Exception e)
            {
                FramedBlocks.LOGGER.error("Failed to split out frame {}", frame, e);
            }
            finally
            {
                lazyImage.release();
            }
            return MissingTextureAtlasSprite.create();
        }

        private static void checkFrameExists(
                ResourceLocation texPath, AnimationMetadataSection anim, int frameIdx, int rowSize
        )
        {
            boolean[] frameFound = new boolean[1];
            int[] maxIdx = new int[] { -1 };
            anim.forEachFrame((idx, time) ->
            {
                maxIdx[0] = Math.max(maxIdx[0], idx);
                if (idx == frameIdx)
                {
                    frameFound[0] = true;
                }
            });
            if (!frameFound[0] && (maxIdx[0] != -1 || frameIdx >= rowSize))
            {
                int max = maxIdx[0] != -1 ? maxIdx[0] : rowSize;
                throw new IllegalArgumentException("Texture '%s' has no frame with index %d, max index is %d".formatted(
                        texPath, frameIdx, max
                ));
            }
        }

        @Override
        public void discard()
        {
            lazyImage.release();
        }
    }



    // TODO: replace with dedicated event when switching to Neo and the event is merged
    public static void register()
    {
        String name = FramedConstants.MOD_ID + ":anim_splitter";
        TYPE = SpriteSources.register(name, CODEC);
    }
}
