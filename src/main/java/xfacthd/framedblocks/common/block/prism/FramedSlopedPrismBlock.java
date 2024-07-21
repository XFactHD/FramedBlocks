package xfacthd.framedblocks.common.block.prism;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CompoundDirection;

public class FramedSlopedPrismBlock extends FramedBlock implements IFramedPrismBlock
{
    public FramedSlopedPrismBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.NORTH_UP)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                PropertyHolder.FACING_DIR, BlockStateProperties.WATERLOGGED,
                FramedProperties.SOLID, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(context, this);
    }

    public static <T extends Block & IFramedPrismBlock> BlockState getStateForPlacement(BlockPlaceContext context, T block)
    {
        return PlacementStateBuilder.of(block, context)
                .withCustom((state, modCtx) ->
                {
                    Direction face = modCtx.getClickedFace();
                    Direction orientation;
                    if (Utils.isY(face))
                    {
                        orientation = modCtx.getHorizontalDirection();
                        if (block.isInnerPrism())
                        {
                            orientation = orientation.getOpposite();
                        }
                    }
                    else
                    {
                        Vec3 subHit = Utils.fraction(modCtx.getClickLocation());

                        double xz = (Utils.isX(face) ? subHit.z() : subHit.x()) - .5;
                        double y = subHit.y() - .5;

                        if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz))
                        {
                            if (Utils.isX(face))
                            {
                                orientation = xz < 0 ? Direction.SOUTH : Direction.NORTH;
                            }
                            else
                            {
                                orientation = xz < 0 ? Direction.EAST : Direction.WEST;
                            }
                        }
                        else
                        {
                            orientation = y < 0 ? Direction.UP : Direction.DOWN;
                        }
                    }
                    return state.setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(face, orientation));
                })
                .withYSlope(Utils.isY(context.getClickedFace()))
                .tryWithWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.rotate(rot));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.mirror(mirror));
    }

    @Override
    public BlockState getItemModelSource()
    {
        boolean outer = getBlockType() == BlockType.FRAMED_SLOPED_PRISM;
        CompoundDirection cmpDir = outer ? CompoundDirection.UP_WEST : CompoundDirection.UP_EAST;
        return defaultBlockState().setValue(PropertyHolder.FACING_DIR, cmpDir);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }

    @Override
    public boolean isInnerPrism()
    {
        return getBlockType() != BlockType.FRAMED_SLOPED_PRISM;
    }
}
