package io.github.xfacthd.framedblocks.client.model.baked;

import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.List;
import java.util.function.Supplier;

public final class CopyingFramedBlockStateModel extends AbstractFramedBlockStateModel {
    private final Supplier<AbstractFramedBlockStateModel> srcModel;

    public CopyingFramedBlockStateModel(BlockStateModel baseModel, BlockState srcState) {
        super(baseModel, srcState, ItemModelInfo.DEFAULT);
        this.srcModel = Lazy.of(() -> ModelUtils.getFramedBlockModel(srcState));
    }

    @Override
    public int collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts, int miscTintOffset) {
        return srcModel.get().collectParts(level, pos, state, random, parts, miscTintOffset);
    }

    @Override
    public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return srcModel.get().particleMaterial(level, pos, state);
    }

    @Override
    public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return srcModel.get().materialFlags(level, pos, state);
    }
}
