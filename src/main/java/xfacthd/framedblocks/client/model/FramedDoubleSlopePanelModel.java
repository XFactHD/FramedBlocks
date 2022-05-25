package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.Rotation;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

public class FramedDoubleSlopePanelModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final Rotation rotation;
    private final boolean front;

    public FramedDoubleSlopePanelModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(PropertyHolder.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
        this.front = state.getValue(PropertyHolder.FRONT);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        BlockState defState = FBContent.blockFramedSlopePanel.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(PropertyHolder.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation)
                        .setValue(PropertyHolder.FRONT, front),
                defState.setValue(PropertyHolder.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.ROTATION, rotation.isVertical() ? rotation.getOpposite() : rotation)
                        .setValue(PropertyHolder.FRONT, !front)
        );
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data)
    {
        if (rotation == Rotation.LEFT || rotation == Rotation.RIGHT)
        {
            return super.getParticleTexture(data);
        }

        if (rotation == Rotation.UP)
        {
            return getSpriteOrDefault(data, FramedDoubleTileEntity.DATA_RIGHT, getModels().getB());
        }
        else
        {
            return getSpriteOrDefault(data, FramedDoubleTileEntity.DATA_LEFT, getModels().getA());
        }
    }



    public static BlockState itemSource() { return FBContent.blockFramedDoubleSlopePanel.get().defaultBlockState(); }
}
