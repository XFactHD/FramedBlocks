package io.github.xfacthd.framedblocks.common.config;

import io.github.xfacthd.framedblocks.api.screen.overlay.OverlayDisplayMode;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.client.model.SolidFrameMode;
import org.jspecify.annotations.Nullable;

import java.util.regex.Pattern;

public final class ExtConfigView {
    public interface Server extends ConfigView.Server {
        int getPoweredSawEnergyCapacity();

        int getPoweredSawMaxInput();

        int getPoweredSawConsumption();

        int getPoweredSawCraftingDuration();
    }

    public interface Client extends ConfigView.Client {
        int getGhostRenderOpacity();

        /// If true, all recipe permutations will be added to EMI, otherwise only cube->any variants will be added.
        boolean showAllRecipePermutationsInEmi();

        SolidFrameMode getSolidFrameMode();

        boolean showButtonPlateOverlay();

        boolean showSpecialCubeOverlay();

        boolean shouldRenderCamoInJade();

        boolean showCamoCraftingInJei();

        OverlayDisplayMode getMaxOverlayMode();

        OverlayDisplayMode getStateLockMode();

        OverlayDisplayMode getToggleWaterlogMode();

        OverlayDisplayMode getToggleAltSlopeMode();

        OverlayDisplayMode getReinforcementMode();

        OverlayDisplayMode getPrismOffsetMode();

        OverlayDisplayMode getSplitLineMode();

        OverlayDisplayMode getOneWayWindowMode();

        OverlayDisplayMode getFrameBackgroundMode();

        OverlayDisplayMode getCamoRotationMode();

        OverlayDisplayMode getTrapdoorTextureRotationMode();

        OverlayDisplayMode getCopycatStyleMode();
    }

    public interface DevTools extends ConfigView.DevTools {
        boolean isDoubleBlockPartHitDebugRendererEnabled();

        boolean isConnectionDebugRendererEnabled();

        boolean isQuadWindingDebugRendererEnabled();

        boolean isStateMergerDebugLoggingEnabled();

        @Nullable Pattern getStateMergerDebugFilter();

        boolean isOcclusionShapeDebugRenderingEnabled();

        boolean isCollapsibleBlockDebugRendererEnabled();
    }

    private ExtConfigView() { }
}
