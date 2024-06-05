package xfacthd.framedblocks.client.model.door;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.TranslatedItemModelInfo;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

public class FramedDoorGeometry extends Geometry
{
    private static final TranslatedItemModelInfo ITEM_MODEL_INFO = TranslatedItemModelInfo.handOrGui(0F, 0F, -.5F);

    private final Direction dir;
    private final boolean hingeRight;
    private final boolean open;
    private final boolean gate;

    public FramedDoorGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.hingeRight = ctx.state().getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.RIGHT;
        this.open = ctx.state().getValue(BlockStateProperties.OPEN);
        this.gate = ctx.state().is(FBContent.BLOCK_FRAMED_GATE) || ctx.state().is(FBContent.BLOCK_FRAMED_IRON_GATE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction faceDir = dir;
        if (open) { faceDir = hingeRight ? faceDir.getCounterClockWise() : faceDir.getClockWise(); }

        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(faceDir, 3F/16F))
                    .export(quadMap.get(quadDir));
        }
        else
        {
            if (quadDir == faceDir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(3F/16F))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(faceDir, 3F/16F))
                        .export(quadMap.get(quadDir));
            }
        }
    }

    @Override
    public ItemModelInfo getItemModelInfo()
    {
        return gate ? ITEM_MODEL_INFO : super.getItemModelInfo();
    }
}
