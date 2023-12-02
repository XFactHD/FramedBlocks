package xfacthd.framedblocks.api.util;

public final class TestProperties
{
    public static final boolean DISABLE_MODEL_QUAD_CACHE = Boolean.getBoolean(
            "framedblocks.test.disable_model_quad_cache"
    );
    public static final boolean ENABLE_BER_RENDER_BOUNDS_DEBUG_RENDERER = Boolean.getBoolean(
            "framedblocks.test.enable_ber_render_bounds_debug_renderer"
    );
    public static final boolean ENABLE_DOUBLE_BLOCK_PART_HIT_DEBUG_RENDERER = Boolean.getBoolean(
            "framedblocks.test.enable_double_block_part_hit_debug_renderer"
    );



    private TestProperties() { }
}
