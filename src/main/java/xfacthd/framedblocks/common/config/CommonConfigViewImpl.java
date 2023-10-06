package xfacthd.framedblocks.common.config;

import xfacthd.framedblocks.api.util.ConfigView;

public final class CommonConfigViewImpl implements ConfigView.Common
{
    @Override
    public boolean areBlocksFireproof()
    {
        return CommonConfig.fireproofBlocks;
    }
}
