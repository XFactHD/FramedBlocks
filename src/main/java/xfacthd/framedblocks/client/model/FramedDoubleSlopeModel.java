package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedDoubleSlopeBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.SlopeType;

public class FramedDoubleSlopeModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoubleSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, false);
        this.state = state;
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = state.getValue(PropertyHolder.FACING_HOR);

        return FramedDoubleSlopeBlockEntity.getBlockPair(type, facing);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.BOTTOM)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getA());
        }
        else if (type == SlopeType.TOP)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getB());
        }
        return super.getParticleIcon(data);
    }
}