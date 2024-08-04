package xfacthd.framedblocks.common.data.camo.block;

import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainer;
import xfacthd.framedblocks.common.FBContent;

public final class BlockCamoContainer extends AbstractBlockCamoContainer<BlockCamoContainer>
{
    public BlockCamoContainer(BlockState state)
    {
        super(state);
    }

    @Override
    public int hashCode()
    {
        return content.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != BlockCamoContainer.class) return false;
        return content.equals(((BlockCamoContainer) obj).content);
    }

    @Override
    public String toString()
    {
        return "BlockCamoContainer{" + content + "}";
    }

    @Override
    public BlockCamoContainerFactory getFactory()
    {
        return FBContent.FACTORY_BLOCK.value();
    }
}
