package xfacthd.framedblocks.tests;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.test.TestUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

import java.util.*;

@GameTestHolder(FramedConstants.MOD_ID)
public final class LightSourceTests
{
    private static final String BATCH_NAME = "lightsource";
    private static final String STRUCTURE_NAME = FramedConstants.MOD_ID + ":lightsourcetests.box";

    @GameTestGenerator
    public static Collection<TestFunction> generateLightSourceTests()
    {
        return Arrays.stream(BlockType.values())
                .filter(LightSourceTests::isNotSelfEmitting)
                .map(type -> Utils.rl(type.getName()))
                .map(BuiltInRegistries.BLOCK::get)
                .filter(b -> b != Blocks.AIR)
                .map(LightSourceTests::getTestState)
                .map(state -> new TestFunction(
                        BATCH_NAME,
                        getTestName(state),
                        STRUCTURE_NAME,
                        100,
                        0,
                        true,
                        helper -> TestUtils.testBlockLightEmission(helper, state, getCamoSides(state.getBlock()))
                ))
                .toList();
    }

    private static boolean isNotSelfEmitting(BlockType type)
    {
        return type != BlockType.FRAMED_TORCH &&
                type != BlockType.FRAMED_WALL_TORCH &&
                type != BlockType.FRAMED_SOUL_TORCH &&
                type != BlockType.FRAMED_SOUL_WALL_TORCH &&
                type != BlockType.FRAMED_REDSTONE_TORCH &&
                type != BlockType.FRAMED_REDSTONE_WALL_TORCH;
    }

    private static BlockState getTestState(Block block)
    {
        Preconditions.checkArgument(block instanceof IFramedBlock);

        IBlockType type = ((IFramedBlock) block).getBlockType();
        BlockState state = block.defaultBlockState();
        return switch ((BlockType) type)
        {
            case FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL,
                 FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W,
                 FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL,
                 FRAMED_STACKED_CORNER_SLOPE_PANEL_W,
                 FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W
                    -> state.setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W
                    -> state.setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
            default -> state;
        };
    }

    private static String getTestName(BlockState state)
    {
        ResourceLocation regName = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return String.format("lightsourcetests.test_%s", regName.getPath());
    }

    private static List<Direction> getCamoSides(Block block)
    {
        Preconditions.checkArgument(block instanceof IFramedBlock);

        IBlockType type = ((IFramedBlock) block).getBlockType();
        if (!type.isDoubleBlock() || !(type instanceof BlockType))
        {
            return List.of(Direction.UP);
        }

        return switch ((BlockType) type)
        {
            case FRAMED_DIVIDED_SLAB,
                 FRAMED_DOUBLE_PANEL,
                 FRAMED_DOUBLE_SLOPE_PANEL,
                 FRAMED_INV_DOUBLE_SLOPE_PANEL,
                 FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL,
                 FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_STACKED_SLOPE_PANEL,
                 FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER,
                 FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W,
                 FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL,
                 FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W,
                 FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W,
                 FRAMED_STACKED_CORNER_SLOPE_PANEL,
                 FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL,
                 FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> List.of(Direction.NORTH, Direction.SOUTH);

            case FRAMED_DIVIDED_PANEL_VERTICAL,
                 FRAMED_DIVIDED_SLOPE,
                 FRAMED_DIVIDED_STAIRS,
                 FRAMED_SLICED_STAIRS_SLAB,
                 FRAMED_VERTICAL_SLICED_STAIRS -> List.of(Direction.EAST, Direction.WEST);

            case FRAMED_CHECKERED_SLAB -> List.of(Direction.NORTH, Direction.WEST);

            default -> List.of(Direction.DOWN, Direction.UP);
        };
    }

    private LightSourceTests() { }
}
