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
import xfacthd.framedblocks.common.blockentity.FramedFlatExtendedDoubleSlopePanelCornerBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedFlatExtendedInnerDoubleSlopePanelCornerModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final HorizontalRotation rotation;

    public FramedFlatExtendedInnerDoubleSlopePanelCornerModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        if (rotation == HorizontalRotation.UP || rotation == HorizontalRotation.RIGHT)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getA());
        }
        return super.getParticleIcon(data);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedFlatExtendedDoubleSlopePanelCornerBlockEntity.getInnerBlockPair(facing, rotation);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedFlatExtendedInnerDoubleSlopePanelCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
    }
}
