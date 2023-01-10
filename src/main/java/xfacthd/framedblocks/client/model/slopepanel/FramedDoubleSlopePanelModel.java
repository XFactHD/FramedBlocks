package xfacthd.framedblocks.client.model.slopepanel;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedDoubleSlopePanelModel extends FramedDoubleBlockModel
{
    private final HorizontalRotation rotation;

    public FramedDoubleSlopePanelModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, true);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        return switch (rotation)
        {
            case LEFT, RIGHT -> super.getParticleIcon(data);
            case UP -> getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
            case DOWN -> getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getA());
        };
    }



    public static BlockState itemSource() { return FBContent.blockFramedDoubleSlopePanel.get().defaultBlockState(); }
}
