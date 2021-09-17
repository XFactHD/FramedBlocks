package xfacthd.framedblocks.api;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.WriteOnceHolder;

public interface FramedBlocksAPI
{
    WriteOnceHolder<FramedBlocksAPI> INSTANCE = new WriteOnceHolder<>();

    static FramedBlocksAPI getInstance() { return INSTANCE.get(); }



    String modid();

    BlockEntityType<FramedBlockEntity> defaultBlockEntity();

    BlockState defaultModelState();

    BakedModel createFluidModel(Fluid fluid);

    boolean isFramedHammer(ItemStack stack);

    boolean isFramedDoubleBlockEntity(FramedBlockEntity be);

    boolean areBlocksFireproof();

    ItemStack getSecondaryCamo(FramedBlockEntity be);
}