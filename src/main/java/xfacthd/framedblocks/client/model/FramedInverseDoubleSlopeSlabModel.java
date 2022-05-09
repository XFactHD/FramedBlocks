package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedInverseDoubleSlopeSlabBlockEntity;

public class FramedInverseDoubleSlopeSlabModel extends FramedDoubleBlockModel
{
    private final Direction facing;

    public FramedInverseDoubleSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedInverseDoubleSlopeSlabBlockEntity.getBlockPair(facing);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
    }



    public static BlockState itemSource() { return FBContent.blockFramedInverseDoubleSlopeSlab.get().defaultBlockState(); }
}
