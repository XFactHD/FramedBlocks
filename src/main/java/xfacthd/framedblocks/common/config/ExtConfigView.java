package xfacthd.framedblocks.common.config;

import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.client.model.SolidFrameMode;
import xfacthd.framedblocks.client.screen.overlay.BlockInteractOverlay;

public final class ExtConfigView
{
    public interface Server extends ConfigView.Server
    {
        void setOverrideIntangibilityConfig(boolean override);
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

        BlockInteractOverlay.Mode getStateLockMode();

        BlockInteractOverlay.Mode getToggleWaterlogMode();

        BlockInteractOverlay.Mode getToggleYSlopeMode();

        BlockInteractOverlay.Mode getReinforcementMode();

        BlockInteractOverlay.Mode getPrismOffsetMode();

        BlockInteractOverlay.Mode getSplitLineMode();

        BlockInteractOverlay.Mode getOneWayWindowMode();

        BlockInteractOverlay.Mode getFrameBackgroundMode();

        BlockInteractOverlay.Mode getCamoRotationMode();
    }



    private ExtConfigView() { }
}
