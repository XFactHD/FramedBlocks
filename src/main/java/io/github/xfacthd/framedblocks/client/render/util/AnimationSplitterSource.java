package io.github.xfacthd.framedblocks.client.render.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.ExtraCodecs;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record AnimationSplitterSource(Identifier resource, List<Frame> frames) implements SpriteSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<AnimationSplitterSource> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Identifier.CODEC.fieldOf("resource").forGetter(s -> s.resource),
            ExtraCodecs.nonEmptyList(Frame.CODEC.listOf()).fieldOf("frames").forGetter(s -> s.frames)
    ).apply(inst, AnimationSplitterSource::new));

    @Override
    public void run(ResourceManager mgr, Output out) {
        run(mgr, out, Set.of());
    }

    @Override
    public void run(ResourceManager mgr, Output out, Set<MetadataSectionType<?>> additionalMetadata) {
        Identifier texPath = TEXTURE_ID_CONVERTER.idToFile(resource);
        Optional<Resource> optResource = mgr.getResource(texPath);
        if (optResource.isPresent()) {
            Resource res = optResource.get();
            LazyLoadedImage image = new LazyLoadedImage(texPath, res, frames.size());
            frames.forEach(frame -> out.add(frame.outLoc, new FrameInstance(res, texPath, image, frame, additionalMetadata)));
        } else {
            LOGGER.warn("Missing sprite: {}", texPath);
        }
    }

    @Override
    public MapCodec<AnimationSplitterSource> codec() {
        return CODEC;
    }

    public record Frame(int frameIdx, Identifier outLoc) {
        private static final Codec<Frame> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("frame_idx").forGetter(Frame::frameIdx),
                Identifier.CODEC.fieldOf("sprite").forGetter(Frame::outLoc)
        ).apply(inst, Frame::new));
    }

    public record FrameInstance(
            Resource resource,
            Identifier texPath,
            LazyLoadedImage lazyImage,
            Frame frame,
            Set<MetadataSectionType<?>> additionalMetadata
    ) implements DiscardableLoader {
        @Override
        public SpriteContents get(SpriteResourceLoader loader) {
            try {
                ResourceMetadata srcMeta = resource.metadata();
                Optional<AnimationMetadataSection> optAnim = srcMeta.getSection(AnimationMetadataSection.TYPE);
                if (optAnim.isEmpty()) {
                    throw new IllegalArgumentException("Texture '%s' is not an animated texture".formatted(texPath));
                }

                NativeImage image = lazyImage.get();
                int imgW = image.getWidth();
                int imgH = image.getHeight();

                AnimationMetadataSection anim = optAnim.get();
                FrameSize size = anim.calculateFrameSize(imgW, imgH);
                int frameW = size.width();
                int frameH = size.height();

                int frameCount = (imgW / frameW) * (imgH / frameH);
                checkFrameExists(texPath, anim, frame.frameIdx, frameCount);
                int srcX = (frame.frameIdx % frameCount) * frameW;
                int srcY = (frame.frameIdx / frameCount) * frameH;

                NativeImage imageOut = new NativeImage(NativeImage.Format.RGBA, frameW, frameH, false);
                image.copyRect(imageOut, srcX, srcY, 0, 0, frameW, frameH, false, false);
                List<MetadataSectionType.WithValue<?>> metaSections = srcMeta.getTypedSections(additionalMetadata);
                Optional<TextureMetadataSection> textureMetadata = srcMeta.getSection(TextureMetadataSection.TYPE);
                return new SpriteContents(frame.outLoc, new FrameSize(frameW, frameH), imageOut, Optional.empty(), metaSections, textureMetadata);
            } catch (Exception e) {
                LOGGER.error("Failed to split out frame {}", frame, e);
            } finally {
                lazyImage.release();
            }
            return MissingTextureAtlasSprite.create();
        }

        private static void checkFrameExists(Identifier texPath, AnimationMetadataSection anim, int frameIdx, int frameCount) {
            boolean frameFound = false;
            int maxIdx = -1;
            if (anim.frames().isPresent()) {
                for (AnimationFrame frame : anim.frames().get()) {
                    maxIdx = Math.max(maxIdx, frame.index());
                    if (frame.index() == frameIdx) {
                        frameFound = true;
                        break;
                    }
                }
            }
            if (!frameFound && (maxIdx != -1 || frameIdx >= frameCount)) {
                int max = maxIdx != -1 ? maxIdx : frameCount;
                throw new IllegalArgumentException("Texture '%s' has no frame with index %d, max index is %d".formatted(
                        texPath, frameIdx, max
                ));
            }
        }

        @Override
        public void discard() {
            lazyImage.release();
        }
    }
}
