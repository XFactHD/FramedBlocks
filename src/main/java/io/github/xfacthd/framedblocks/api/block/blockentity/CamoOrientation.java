package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public enum CamoOrientation implements StringRepresentable {
    UNKNOWN(null),
    UNSPECIFIED(null),
    NORTH(Direction.NORTH),
    EAST(Direction.EAST),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST);

    private static final CamoOrientation[] VALUES = values();

    private final String name = toString().toLowerCase(Locale.ROOT);
    @Nullable
    private final Direction dir;

    CamoOrientation(@Nullable Direction dir) {
        this.dir = dir;
    }

    public @Nullable Direction resolve(IFramedBlockEntity be) {
        if (this != UNKNOWN) {
            return dir;
        }

        IFramedBlock block = be.getBlock();
        BlockState state = be.getBlockState();
        return block.getHorizontalOrientation(state);
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static CamoOrientation of(@Nullable Direction dir) {
        if (dir == null) {
            return UNSPECIFIED;
        }
        return VALUES[dir.get2DDataValue() + 1];
    }
}
