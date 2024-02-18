package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedLatticeModel extends FramedBlockModel
{
    private static final float MIN_COORD = 6F/16F;
    private static final float MAX_COORD = 10F/16F;
    private static final float MIN_COORD_THICK = 4F/16F;
    private static final float MAX_COORD_THICK = 12F/16F;

    private final boolean xAxis;
    private final boolean yAxis;
    private final boolean zAxis;
    private final float minCoord;
    private final float maxCoord;

    public FramedLatticeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.xAxis = state.getValue(FramedProperties.X_AXIS);
        this.yAxis = state.getValue(FramedProperties.Y_AXIS);
        this.zAxis = state.getValue(FramedProperties.Z_AXIS);
        boolean thick = state.getBlock() == FBContent.BLOCK_FRAMED_THICK_LATTICE.get();
        this.minCoord = thick ? MIN_COORD_THICK : MIN_COORD;
        this.maxCoord = thick ? MAX_COORD_THICK : MAX_COORD;
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(minCoord, minCoord, maxCoord, maxCoord))
                    .applyIf(Modifiers.setPosition(maxCoord), !yAxis)
                    .export(quadMap.get(yAxis ? quadDir : null));

            if (xAxis)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(0F, minCoord, minCoord, maxCoord))
                        .apply(Modifiers.setPosition(maxCoord))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(maxCoord, minCoord, 1F, maxCoord))
                        .apply(Modifiers.setPosition(maxCoord))
                        .export(quadMap.get(null));
            }

            if (zAxis)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(minCoord, 0F, maxCoord, minCoord))
                        .apply(Modifiers.setPosition(maxCoord))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(minCoord, maxCoord, maxCoord, 1F))
                        .apply(Modifiers.setPosition(maxCoord))
                        .export(quadMap.get(null));
            }
        }
        else if (Utils.isX(quadDir))
        {
            createHorizontalStrutSideQuads(quadMap, quad, xAxis, zAxis);
        }
        else if (Utils.isZ(quadDir))
        {
            createHorizontalStrutSideQuads(quadMap, quad, zAxis, xAxis);
        }

        if (!Utils.isY(quadDir) && yAxis)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(minCoord, 0F, maxCoord, minCoord))
                    .apply(Modifiers.setPosition(maxCoord))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(minCoord, maxCoord, maxCoord, 1F))
                    .apply(Modifiers.setPosition(maxCoord))
                    .export(quadMap.get(null));
        }
    }

    private void createHorizontalStrutSideQuads(
            Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, boolean frontAxis, boolean sideAxis
    )
    {
        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSide(minCoord, minCoord, maxCoord, maxCoord))
                .applyIf(Modifiers.setPosition(maxCoord), !frontAxis)
                .export(quadMap.get(frontAxis ? quad.getDirection() : null));

        if (sideAxis)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(0F, minCoord, minCoord, maxCoord))
                    .apply(Modifiers.setPosition(maxCoord))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(maxCoord, minCoord, 1F, maxCoord))
                    .apply(Modifiers.setPosition(maxCoord))
                    .export(quadMap.get(null));
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}