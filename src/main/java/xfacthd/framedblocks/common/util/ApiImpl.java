package xfacthd.framedblocks.common.util;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.client.model.FluidDummyModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class ApiImpl implements FramedBlocksAPI
{
    @Override
    public String modid() { return FramedBlocks.MODID; }

    @Override
    public BlockState defaultModelState() { return FBContent.blockFramedCube.get().defaultBlockState(); }

    @Override
    public BlockEntityType<FramedBlockEntity> defaultBlockEntity() { return FBContent.blockEntityTypeFramedBlock.get(); }

    @Override
    public BakedModel createFluidModel(Fluid fluid) { return new FluidDummyModel(fluid); }

    @Override
    public boolean isFramedHammer(ItemStack stack) { return stack.getItem() == FBContent.itemFramedHammer.get(); }

    @Override
    public boolean isFramedDoubleBlockEntity(FramedBlockEntity be) { return be instanceof FramedDoubleBlockEntity; }

    @Override
    public boolean areBlocksFireproof() { return CommonConfig.fireproofBlocks; }

    @Override
    public ItemStack getSecondaryCamo(FramedBlockEntity be)
    {
        if (be instanceof FramedDoubleBlockEntity dbe)
        {
            return dbe.getCamoStackTwo();
        }
        return ItemStack.EMPTY;
    }
}