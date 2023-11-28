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
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.fml.ModList;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.client.util.NoAnimationResourceMetadata;

import java.util.List;
import java.util.Optional;

public sealed class AnimationSplitterSource implements SpriteSource permits AnimationSplitterSourceAV
{
    private static final boolean AV_LOADED = ModList.get().isLoaded("atlasviewer");
    private static SpriteSourceType TYPE = null;
    private static final Codec<AnimationSplitterSource> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("resource").forGetter(s -> s.resource),
            ExtraCodecs.nonEmptyList(Frame.CODEC.listOf()).fieldOf("frames").forGetter(s -> s.frames)
    ).apply(inst, AV_LOADED ? AnimationSplitterSourceAV::new : AnimationSplitterSource::new));

    final ResourceLocation resource;
    final List<Frame> frames;

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
            frames.forEach(frame -> out.add(frame.outLoc, createFrameInstance(res, texPath, image, frame)));
        }
        else
        {
            FramedBlocks.LOGGER.warn("Missing sprite: {}", texPath);
        }
    }

    FrameInstance createFrameInstance(Resource res, ResourceLocation texPath, LazyLoadedImage image, Frame frame)
    {
        return new FrameInstance(res, texPath, image, frame);
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

    static sealed class FrameInstance implements SpriteSupplier permits AnimationSplitterSourceAV.FrameInstanceAV
    {
        final Resource resource;
        private final ResourceLocation texPath;
        private final LazyLoadedImage lazyImage;
        private final Frame frame;

        FrameInstance(Resource resource, ResourceLocation texPath, LazyLoadedImage lazyImage, Frame frame)
        {
            this.resource = resource;
            this.texPath = texPath;
            this.lazyImage = lazyImage;
            this.frame = frame;
        }

        @Override
        public SpriteContents apply(SpriteResourceLoader loader)
        {
            try
            {
                ResourceMetadata srcMeta = resource.metadata();
                Optional<AnimationMetadataSection> optAnim = srcMeta.getSection(AnimationMetadataSection.SERIALIZER);
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
                return postProcess(new SpriteContents(
                        frame.outLoc, new FrameSize(frameW, frameH), imageOut, new NoAnimationResourceMetadata(srcMeta)
                ));
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

        SpriteContents postProcess(SpriteContents contents)
        {
            return contents;
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
