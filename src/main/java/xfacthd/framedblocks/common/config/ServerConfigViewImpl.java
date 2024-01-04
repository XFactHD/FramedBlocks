package xfacthd.framedblocks.common.config;

import net.minecraft.world.item.Item;
import xfacthd.framedblocks.api.util.ConfigView;

public final class ServerConfigViewImpl implements ConfigView.Server
{
    @Override
    public boolean allowBlockEntities()
    {
        return ServerConfig.allowBlockEntities;
    }

    @Override
    public boolean enableIntangibility()
    {
        return ServerConfig.enableIntangibleFeature;
    }

    @Override
    public Item getIntangibilityMarkerItem()
    {
        return ServerConfig.intangibleMarkerItem;
    }

    @Override
    public boolean isOneWayWindowOwnable()
    {
        return ServerConfig.oneWayWindowOwnable;
    }

    @Override
    public boolean shouldConsumeCamoItem()
    {
        return ServerConfig.consumeCamoItem;
    }

    @Override
    public int getGlowstoneLightLevel()
    {
        return ServerConfig.glowstoneLightLevel;
    }
}
