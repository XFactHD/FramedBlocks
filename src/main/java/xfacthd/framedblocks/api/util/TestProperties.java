package xfacthd.framedblocks.api.util;

public final class TestProperties
{
    public static final boolean DISABLE_MODEL_QUAD_CACHE = Boolean.getBoolean(
            "framedblocks.test.disable_model_quad_cache"
    );
    public static final boolean ENABLE_OCCLUSION_SHAPE_DEBUG_RENDERER = Boolean.getBoolean(
            "framedblocks.test.enable_occlusion_shape_debug_renderer"
    );



    private TestProperties() { }
}
