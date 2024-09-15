package xfacthd.framedblocks.common.block.slopepanelcorner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.VerticalAndWallBlockItem;

public class FramedExtendedCornerSlopePanelBlock extends FramedBlock
{
    public FramedExtendedCornerSlopePanelBlock(BlockType blockType)
    {
        super(blockType);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP, FramedProperties.Y_SLOPE,
                FramedProperties.SOLID, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return FramedCornerSlopePanelBlock.getStateForPlacement(
                this, ctx, getBlockType() == BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL, true
        );
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction side = hit.getDirection();
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        switch (getBlockType())
        {
            case FRAMED_EXT_CORNER_SLOPE_PANEL ->
            {
                if (side == dir.getOpposite() || side == dir.getClockWise())
                {
                    side = Direction.UP;
                }
            }
            case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL ->
            {
                if (side == dir || side == dir.getCounterClockWise())
                {
                    boolean top = state.getValue(FramedProperties.TOP);
                    Vec3 hitVec = hit.getLocation();
                    double y = Utils.fractionInDir(hitVec, top ? Direction.UP : Direction.DOWN);
                    double xz = Utils.fractionInDir(hitVec, side == dir ? dir.getCounterClockWise() : dir) - .5;
                    if (xz * 2D > y)
                    {
                        side = Direction.UP;
                    }
                }
            }
        }
        return rotate(state, side, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        return state.cycle(FramedProperties.TOP);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, Direction.UP, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockItem createBlockItem()
    {
        Block other = switch (getBlockType())
        {
            case FRAMED_EXT_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL.value();
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
        return new VerticalAndWallBlockItem(this, other, new Item.Properties());
    }

    @Override
    public BlockState getItemModelSource()
    {
        boolean inner = getBlockType() == BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL;
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, inner ? Direction.EAST : Direction.WEST);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
