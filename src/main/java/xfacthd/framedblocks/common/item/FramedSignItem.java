package xfacthd.framedblocks.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedSignBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import javax.annotation.Nullable;

public class FramedSignItem extends StandingAndWallBlockItem
{
    public FramedSignItem()
    {
        this(FBContent.BLOCK_FRAMED_SIGN, FBContent.BLOCK_FRAMED_WALL_SIGN, Direction.DOWN);
    }

    protected FramedSignItem(RegistryObject<Block> standing, RegistryObject<Block> wall, Direction attachFace)
    {
        super(standing.get(), wall.get(), new Properties(), attachFace);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(
            BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state
    )
    {
        boolean hadNBT = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (!level.isClientSide() && !hadNBT && player != null)
        {
            if (level.getBlockEntity(pos) instanceof FramedSignBlockEntity be)
            {
                AbstractFramedSignBlock.openEditScreen(player, be, true);
            }
        }
        return hadNBT;
    }
}