package io.github.xfacthd.framedblocks.api.util;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import org.jetbrains.annotations.ApiStatus;

/// Provides access to most config values from the config types used by FramedBlocks.
@SuppressWarnings("unused")
public final class ConfigView {
    /// Provides access to the values of the server config.
    @ApiStatus.NonExtendable
    public interface Server {
        Server INSTANCE = Utils.loadService(ConfigView.Server.class);

        /// {@return whether blocks with BEs can be used as camos}
        boolean allowBlockEntities();

        /// {@return whether framed blocks can be made intangible}
        boolean enableIntangibility();

        /// {@return whether the One-Way Window is owned by the player who placed it and can only be configured by said player}
        boolean isOneWayWindowOwnable();

        /// {@return whether the camo item should be consumed on application and dropped on removal of the camo}
        boolean shouldConsumeCamoItem();

        /// {@return the light value to use when glowstone is applied to a block}
        int getGlowstoneLightLevel();

        /// {@return whether framed blocks are immune to fire and lava regardless of their camo}
        boolean areBlocksFireproof();
    }

    /// Provides access to the values of the client config.
    @ApiStatus.NonExtendable
    public interface Client {
        Client INSTANCE = Utils.loadService(ConfigView.Client.class);

        /// {@return whether a placement preview will be rendered while holding a framed block}
        boolean showGhostBlocks();

        /// {@return whether an alternative render type should be used for the placement preview in an attempt to improve compatibility with certain shaders}
        boolean useAltGhostRenderer();

        /// {@return whether custom shape-adapted selection boxes are drawn instead of the collision box}
        boolean useFancySelectionBoxes();

        /// {@return whether all faces should be checked for interaction with neighboring blocks for culling purposes instead of only full faces}
        boolean detailedCullingEnabled();

        /// {@return the detail level of connected textures support}
        ConTexMode getConTexMode();

        /// {@return the verbosity of messages displayed when a block cannot be used as a camo}
        CamoMessageVerbosity getCamoMessageVerbosity();

        /// {@return whether ambient occlusion should be forced on framed blocks which glow through applied glowstone dust}
        boolean shouldForceAmbientOcclusionOnGlowingBlocks();

        /// {@return whether item models of framed blocks should render with the camo stored on the stack, if present}
        boolean shouldRenderItemModelsWithCamo();
    }

    /// Provides access to the values of the dev-tools client config.
    @ApiStatus.NonExtendable
    public interface DevTools {
        DevTools INSTANCE = Utils.loadService(DevTools.class);
    }

    private ConfigView() { }
}
