package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;

public final class BlockInteractOverlayLayer implements LayeredDraw.Layer
{
    private static final List<BlockInteractOverlay> OVERLAYS = List.of(
            new StateLockOverlay(),
            new ToggleWaterloggableOverlay(),
            new ToggleYSlopeOverlay(),
            new ReinforcementOverlay(),
            new PrismOffsetOverlay(),
            new SplitLineOverlay(),
            new OneWayWindowOverlay(),
            new FrameBackgroundOverlay(),
            new CamoRotationOverlay()
    );

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta)
    {
        String renderedOverlay = null;
        for (BlockInteractOverlay overlay : OVERLAYS)
        {
            if (overlay.render(graphics))
            {
                if (FMLEnvironment.production) break;

                if (renderedOverlay != null)
                {
                    String msg = "Only one overlay may be active at any time, encountered collision between '%s' and '%s'"
                            .formatted(renderedOverlay, overlay.getName());
                    throw new IllegalStateException(msg);
                }
                renderedOverlay = overlay.getName();
            }
        }
    }



    public static void onResourceReload(@SuppressWarnings("unused") ResourceManager manager)
    {
        OVERLAYS.forEach(overlay -> overlay.textWidthValid = false);
    }
}
