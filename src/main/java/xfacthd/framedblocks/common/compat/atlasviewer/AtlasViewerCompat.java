package xfacthd.framedblocks.common.compat.atlasviewer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.render.util.AnimationSplitterSource;

public final class AtlasViewerCompat
{
    public static final Component LABEL_TEXTURE = Utils.translate("label", "source_tooltip.anim_splitter.texture");
    public static final Component LABEL_FRAMES = Utils.translate("label", "source_tooltip.anim_splitter.frames");

    public static void init(IEventBus modBus)
    {
        if (ModList.get().isLoaded("atlasviewer"))
        {
            try
            {
                if (FMLEnvironment.dist.isClient())
                {
                    GuardedClientAccess.init(modBus);
                }
            }
            catch (Throwable e)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing AtlasViewer integration!", e);
            }
        }
    }



    private static final class GuardedClientAccess
    {
        public static void init(IEventBus modBus)
        {
            modBus.addListener(GuardedClientAccess::onRegisterSpriteSourceDetails);
        }

        private static void onRegisterSpriteSourceDetails(final RegisterSpriteSourceDetailsEvent event)
        {
            event.registerPrimaryResourceGetter(
                    AnimationSplitterSource.FrameInstance.class,
                    AnimationSplitterSource.FrameInstance::resource
            );
            event.registerSourceTooltipAppender(AnimationSplitterSource.class, (src, consumer) ->
            {
                consumer.accept(AtlasViewerCompat.LABEL_TEXTURE, Component.literal(src.resource().toString()));
                consumer.accept(AtlasViewerCompat.LABEL_FRAMES, Component.empty());
                src.frames().forEach(frame -> consumer.accept(
                        null, Component.literal("  - ")
                                .append(Component.literal(Integer.toString(frame.frameIdx())).withStyle(ChatFormatting.ITALIC))
                                .append(": ")
                                .append(Component.literal(frame.outLoc().toString()))
                ));
            });
        }



        private GuardedClientAccess() { }
    }


    private AtlasViewerCompat() { }
}
