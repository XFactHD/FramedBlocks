package xfacthd.framedblocks.common.config;

import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.common.util.CommonConfig;

public final class CommonConfigViewImpl implements ConfigView.Common
{
    @Override
    public boolean areBlocksFireproof()
    {
        return CommonConfig.fireproofBlocks;
    }
}
