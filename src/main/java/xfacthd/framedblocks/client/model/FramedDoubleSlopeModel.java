package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.SlopeType;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

public class FramedDoubleSlopeModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoubleSlopeModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, false);
        this.state = state;
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = state.getValue(PropertyHolder.FACING_HOR);

        BlockState stateOne = FBContent.blockFramedSlope.get().defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, type)
                .setValue(PropertyHolder.FACING_HOR, facing);
        BlockState stateTwo = FBContent.blockFramedSlope.get().defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, type == SlopeType.HORIZONTAL ? type : type.getOpposite())
                .setValue(PropertyHolder.FACING_HOR, facing.getOpposite());

        return new Tuple<>(stateOne, stateTwo);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.BOTTOM)
        {
            return getSpriteOrDefault(data, FramedDoubleTileEntity.DATA_RIGHT, getModels().getA());
        }
        else if (type == SlopeType.TOP)
        {
            return getSpriteOrDefault(data, FramedDoubleTileEntity.DATA_LEFT, getModels().getB());
        }
        return super.getParticleTexture(data);
    }
}