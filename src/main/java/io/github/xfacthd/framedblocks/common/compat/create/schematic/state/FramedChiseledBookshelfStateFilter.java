package io.github.xfacthd.framedblocks.common.compat.create.schematic.state;

import com.simibubi.create.api.schematic.state.SchematicStateFilterRegistry;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jspecify.annotations.Nullable;

public final class FramedChiseledBookshelfStateFilter implements SchematicStateFilterRegistry.StateFilter {
    @Override
    public BlockState filterStates(@Nullable BlockEntity be, BlockState state) {
        for (BooleanProperty property : ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES) {
            state = state.setValue(property, false);
        }
        return state;
    }
}
