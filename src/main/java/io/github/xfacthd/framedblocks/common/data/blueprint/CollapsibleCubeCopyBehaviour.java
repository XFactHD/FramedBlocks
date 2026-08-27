package io.github.xfacthd.framedblocks.common.data.blueprint;

import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCubeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.component.CollapsibleCubeData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class CollapsibleCubeCopyBehaviour extends DummyDataHandlingCopyBehaviour<CollapsibleCubeData> {
    public CollapsibleCubeCopyBehaviour() {
        super(FBContent.DC_TYPE_COLLAPSIBLE_CUBE_DATA.value(), CollapsibleCubeData.EMPTY);
    }

    @Override
    public void postProcessPaste(Level level, BlockPos pos, @Nullable Player player, BlueprintData data, ItemStack dummyStack) {
        if (level.getBlockEntity(pos) instanceof FramedCollapsibleCubeBlockEntity be) {
            be.updateFaceSolidity();
        }
    }
}
