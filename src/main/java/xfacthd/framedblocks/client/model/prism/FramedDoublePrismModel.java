package xfacthd.framedblocks.client.model.prism;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedDoublePrismModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final Direction.Axis axis;

    public FramedDoublePrismModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, true);
        this.facing = state.getValue(BlockStateProperties.FACING);
        this.axis = state.getValue(BlockStateProperties.AXIS);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        if (facing == Direction.UP)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
        }
        else if (facing == Direction.DOWN || axis != Direction.Axis.Y)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getA());
        }
        return super.getParticleIcon(data);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedDoublePrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP);
    }
}
