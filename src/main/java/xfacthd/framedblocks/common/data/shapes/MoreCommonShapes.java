package xfacthd.framedblocks.common.data.shapes;

import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.CommonShapes;
import xfacthd.framedblocks.api.shapes.ShapeGenerator;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class MoreCommonShapes
{
    public static final ShapeGenerator TOP_HALF_SLAB_GENERATOR = CommonShapes.createSlabGenerator(PropertyHolder.TOP_HALF);
    public static final ShapeGenerator FRONT_INV_PANEL_GENERATOR = CommonShapes.createPanelGenerator(FramedProperties.FACING_HOR, PropertyHolder.FRONT);



    private MoreCommonShapes() { }
}
