package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.Rotation;

import java.util.List;
import java.util.Map;

public class FramedExtendedSlopePanelModel extends FramedBlockModel
{
    private final Direction facing;
    private final Rotation rotation;
    private final Direction orientation;

    public FramedExtendedSlopePanelModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
        this.orientation = rotation.withFacing(facing);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        boolean yAxis = Utils.isY(orientation);
        if (face == orientation)
        {
            Direction cutDir = facing.getOpposite();
            BakedQuad slabQuad = ModelUtils.duplicateQuad(quad);
            if ((yAxis && BakedQuadTransformer.createTopBottomQuad(slabQuad, cutDir, .5F)) ||
                (!yAxis && BakedQuadTransformer.createVerticalSideQuad(slabQuad, cutDir, .5F))
            )
            {
                quadMap.get(face).add(slabQuad);
            }
        }
        else if (face == facing.getOpposite())
        {
            BakedQuad slopeQuad = FramedSlopePanelModel.createSlope(quad, facing, orientation);
            quadMap.get(null).add(slopeQuad);
        }
        else if (face.getAxis() != facing.getAxis() && face.getAxis() != orientation.getAxis())
        {
            BakedQuad triQuad = ModelUtils.duplicateQuad(quad);
            if (yAxis)
            {
                if (BakedQuadTransformer.createVerticalSideTriangleQuad(triQuad, face == facing.getClockWise(), rotation == Rotation.DOWN, 1F, .5F))
                {
                    quadMap.get(face).add(triQuad);
                }
            }
            else
            {
                if (BakedQuadTransformer.createTopBottomTriangleQuad(triQuad, facing.getOpposite(), rotation == Rotation.RIGHT, 1F, .5F))
                {
                    quadMap.get(face).add(triQuad);
                }
            }
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedExtendedSlopePanel.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
