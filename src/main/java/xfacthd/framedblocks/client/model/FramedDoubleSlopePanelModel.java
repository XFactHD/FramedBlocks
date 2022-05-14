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
import xfacthd.framedblocks.common.blockentity.FramedDoubleSlopePanelBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.Rotation;

public class FramedDoubleSlopePanelModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final Rotation rotation;
    private final boolean front;

    public FramedDoubleSlopePanelModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
        this.front = state.getValue(PropertyHolder.FRONT);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedDoubleSlopePanelBlockEntity.getBlockPair(facing, rotation, front);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        if (rotation == Rotation.LEFT || rotation == Rotation.RIGHT)
        {
            return super.getParticleIcon(data);
        }

        if (rotation == Rotation.UP)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
        }
        else
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getA());
        }
    }



    public static BlockState itemSource() { return FBContent.blockFramedDoubleSlopePanel.get().defaultBlockState(); }
}
