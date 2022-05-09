package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedDoubleCornerBlockEntity;
import xfacthd.framedblocks.common.data.*;

public class FramedDoubleCornerModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoubleCornerModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.state = state;
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        Direction facing = state.getValue(PropertyHolder.FACING_HOR);

        return FramedDoubleCornerBlockEntity.getBlockPair(type, facing);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type == CornerType.BOTTOM)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getA());
        }
        else if (type.isTop())
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getB());
        }
        return super.getParticleIcon(data);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedDoubleCorner.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, Direction.WEST);
    }
}