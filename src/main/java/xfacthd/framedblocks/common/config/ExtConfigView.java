package xfacthd.framedblocks.common.config;

import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.client.model.SolidFrameMode;
import xfacthd.framedblocks.client.screen.overlay.OverlayDisplayMode;

import java.util.regex.Pattern;

public final class ExtConfigView
{
    public interface Server extends ConfigView.Server
    {
        void setOverrideIntangibilityConfig(boolean override);

        int getPoweredSawEnergyCapacity();

        int getPoweredSawMaxInput();

        int getPoweredSawConsumption();

        int getPoweredSawCraftingDuration();
    }

    public interface Client extends ConfigView.Client
    {
        int getGhostRenderOpacity();

        /**
         * If true, all recipe permutations will be added to EMI, otherwise only cube->any variants will be added
         */
        boolean showAllRecipePermutationsInEmi();

        SolidFrameMode getSolidFrameMode();

        boolean showButtonPlateOverlay();

        boolean showSpecialCubeOverlay();

        boolean shouldRenderCamoInJade();

        OverlayDisplayMode getStateLockMode();

        OverlayDisplayMode getToggleWaterlogMode();

        OverlayDisplayMode getToggleYSlopeMode();

        OverlayDisplayMode getReinforcementMode();

        OverlayDisplayMode getPrismOffsetMode();

        OverlayDisplayMode getSplitLineMode();

        OverlayDisplayMode getOneWayWindowMode();

        OverlayDisplayMode getFrameBackgroundMode();

        OverlayDisplayMode getCamoRotationMode();
    }

    public interface DevTools extends ConfigView.DevTools
    {
        boolean isDoubleBlockPartHitDebugRendererEnabled();

        boolean isConnectionDebugRendererEnabled();

        boolean isQuadWindingDebugRendererEnabled();

        boolean isStateMergerDebugLoggingEnabled();

        @Nullable
        Pattern getStateMergerDebugFilter();

        boolean isOcclusionShapeDebugRenderingEnabled();
    }



    private ExtConfigView() { }
}
