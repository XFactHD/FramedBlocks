package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

import javax.annotation.Nonnull;

public class FramedInverseDoubleSlopeSlabModel extends FramedDoubleBlockModel
{
    private final Direction facing;

    public FramedInverseDoubleSlopeSlabModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(PropertyHolder.FACING_HOR);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        BlockState defState = FBContent.blockFramedSlopeSlab.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(PropertyHolder.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, false)
                        .setValue(PropertyHolder.TOP, true),
                defState.setValue(PropertyHolder.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, true)
                        .setValue(PropertyHolder.TOP, false)
        );
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleTileEntity.DATA_RIGHT, getModels().getB());
    }



    public static BlockState itemSource() { return FBContent.blockFramedInverseDoubleSlopeSlab.get().defaultBlockState(); }
}
