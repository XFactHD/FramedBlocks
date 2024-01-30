package xfacthd.framedblocks.api.util;

import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;

public final class TestProperties
{
    public static final boolean DISABLE_MODEL_QUAD_CACHE = Boolean.getBoolean(
            "framedblocks.test.disable_model_quad_cache"
    );
    public static final boolean ENABLE_DOUBLE_BLOCK_PART_HIT_DEBUG_RENDERER = Boolean.getBoolean(
            "framedblocks.test.enable_double_block_part_hit_debug_renderer"
    );
    public static final boolean ENABLE_CONNECTION_DEBUG_RENDERER = Boolean.getBoolean(
            "framedblocks.test.enable_connection_debug_renderer"
    );
    /**
     * If enabled, all model wrapper registrations will print which state properties of the associated block
     * are used as-is and which ones are handled by the {@link StateMerger}
     */
    public static final boolean ENABLE_STATE_MERGER_DEBUG_LOGGING = Boolean.getBoolean(
            "framedblocks.test.enable_state_merger_debug_logging"
    );
    /**
     * Specify a regex filter to filter for which blocks the {@link StateMerger} info is printed
     */
    public static final String STATE_MERGER_DEBUG_FILTER = System.getProperty(
            "framedblocks.test.state_merger_debug_filter"
    );



    private TestProperties() { }
}
