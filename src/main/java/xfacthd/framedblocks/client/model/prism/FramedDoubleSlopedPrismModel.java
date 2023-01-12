package xfacthd.framedblocks.client.model.prism;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CompoundDirection;

public class FramedDoubleSlopedPrismModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final Direction orientation;

    public FramedDoubleSlopedPrismModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, true);
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        this.facing = cmpDir.direction();
        this.orientation = cmpDir.orientation();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data)
    {
        if (facing == Direction.UP)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
        }
        else if (facing == Direction.DOWN || orientation != Direction.UP)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getA());
        }
        return super.getParticleIcon(data);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedDoubleSlopedPrism.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.UP_EAST);
    }
}
