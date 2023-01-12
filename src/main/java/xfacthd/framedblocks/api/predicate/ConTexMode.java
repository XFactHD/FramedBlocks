package xfacthd.framedblocks.api.predicate;

/**
 * Enum of the possible connected texture support modes
 */
public enum ConTexMode
{
    /**
     * All support for connected textures is disabled
     */
    NONE,
    /**
     * Only faces covering the full block face (i.e. the full face at the outer block
     * edge on a Framed Slab) can display connected textures
     */
    FULL_FACE,
    /**
     * Faces covering the full block face and faces whose connecting neighbor occludes
     * a full face of the framed block (i.e. a full block covering the full face of a
     * Framed Slab) can display connected textures
     */
    FULL_CON_FACE,
    /**
     * Faces falling under the above rules and faces whose connecting neighbor occludes
     * a face returning true from the block's {@link SideSkipPredicate} (i.e. two stacked
     * Framed Fence posts) can display connected textures
     */
    DETAILED;

    public boolean atleast(ConTexMode mode)
    {
        return ordinal() >= mode.ordinal();
    }
}
