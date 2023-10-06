package xfacthd.framedblocks.common.config;

import xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import xfacthd.framedblocks.api.util.ConfigView;

public final class ClientConfigViewImpl implements ConfigView.Client
{
    @Override
    public boolean showGhostBlocks()
    {
        return ClientConfig.showGhostBlocks;
    }

    @Override
    public boolean useAltGhostRenderer()
    {
        return ClientConfig.altGhostRenderer;
    }

    @Override
    public boolean useFancySelectionBoxes()
    {
        return ClientConfig.fancyHitboxes;
    }

    @Override
    public boolean detailedCullingEnabled()
    {
        return ClientConfig.detailedCulling;
    }

    @Override
    public boolean useDiscreteUVSteps()
    {
        return ClientConfig.useDiscreteUVSteps;
    }

    @Override
    public ConTexMode getConTexMode()
    {
        return ClientConfig.conTexMode;
    }
}
