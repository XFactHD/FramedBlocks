package xfacthd.framedblocks.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.IIngameOverlay;

import java.util.Objects;

abstract class BlockInteractOverlay implements IIngameOverlay
{
    protected static Player player()
    {
        return Objects.requireNonNull(Minecraft.getInstance().player);
    }

    protected static BlockState getTargettedBlock()
    {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit instanceof BlockHitResult blockHit)
        {
            return Objects.requireNonNull(Minecraft.getInstance().level).getBlockState(blockHit.getBlockPos());
        }
        return Blocks.AIR.defaultBlockState();
    }
}
