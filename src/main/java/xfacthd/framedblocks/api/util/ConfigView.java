package xfacthd.framedblocks.api.util;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import xfacthd.framedblocks.api.predicate.contex.ConTexMode;

@SuppressWarnings("unused")
public final class ConfigView
{
    public interface Server
    {
        Server INSTANCE = Utils.loadService(ConfigView.Server.class);



        /**
         * If true, blocks with {@code BlockEntities} can be placed in Framed blocks
         */
        boolean allowBlockEntities();

        /**
         * If true, certain blocks can be made intangible
         */
        boolean enableIntangibility();

        /**
         * Get the item used to make blocks intangible
         * @deprecated No longer configurable, use {@link Utils#PHANTOM_PASTE} instead
         */
        @Deprecated(forRemoval = true)
        default Item getIntangibilityMarkerItem()
        {
            return Utils.PHANTOM_PASTE.value();
        }

        /**
         * If true, the One-Way Window is owned by the player who placed it and can only be configured by said player
         */
        boolean isOneWayWindowOwnable();

        /**
         * {@return true if the camo item should be consumed on application and dropped on removal of the camo}
         */
        boolean shouldConsumeCamoItem();

        /**
         * {@return the light value to use when glowstone is applied to a block}
         */
        int getGlowstoneLightLevel();

        /**
         * Returns the current value of the {@code fireproofBlocks} setting in the common config
         */
        boolean areBlocksFireproof();
    }

    public interface Client
    {
        Client INSTANCE = Utils.loadService(ConfigView.Client.class);



        /**
         * If true, a placement preview will be rendered while holding a framed block
         */
        boolean showGhostBlocks();

        /**
         * If true, an alternative {@link RenderType} is used for the placement preview in an attempt to improve
         * compatibility with certain shaders
         */
        boolean useAltGhostRenderer();

        /**
         * If true, custom shape-adapted selection boxes are drawn, otherwise the collision box is drawn
         */
        boolean useFancySelectionBoxes();

        /**
         * If true, all faces should be checked for interaction with neighboring blocks for culling purposes,
         * else only full faces should be checked against neighboring blocks
         */
        boolean detailedCullingEnabled();

        /**
         * Returns true if the UV remapping should use discrete steps instead of using the interpolated value directly
         */
        boolean useDiscreteUVSteps();

        /**
         * Returns the currently configured {@link ConTexMode}
         */
        ConTexMode getConTexMode();

        /**
         * Returns the verbosity of messages displayed when a block cannot be used as a camo
         */
        CamoMessageVerbosity getCamoMessageVerbosity();

        /**
         * Returns whether ambient occlusion should be forced on framed blocks which glow through applied glowstone dust
         */
        boolean shouldForceAmbientOcclusionOnGlowingBlocks();

        /**
         * Returns whether item models of framed blocks should render with the camo stored on the stack, if present
         */
        boolean shouldRenderItemModelsWithCamo();
    }

    public interface DevTools
    {
        DevTools INSTANCE = Utils.loadService(DevTools.class);
    }



    private ConfigView() { }
}
