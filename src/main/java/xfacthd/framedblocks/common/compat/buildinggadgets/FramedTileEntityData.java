package xfacthd.framedblocks.common.compat.buildinggadgets;

import com.direwolf20.buildinggadgets.common.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.ITileDataSerializer;
import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.NBTTileEntityData;
import com.direwolf20.buildinggadgets.common.tainted.building.view.BuildContext;
import com.direwolf20.buildinggadgets.common.tainted.inventory.materials.MaterialList;
import com.direwolf20.buildinggadgets.common.tainted.inventory.materials.objects.UniqueItem;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.Utils;

class FramedTileEntityData extends NBTTileEntityData
{
    public FramedTileEntityData(FramedTileEntity te) { super(te.writeToBlueprint(), buildMaterialList(te)); }

    public FramedTileEntityData(CompoundNBT data, MaterialList materials) { super(data, materials); }

    @Override
    public boolean placeIn(BuildContext context, BlockState state, BlockPos pos)
    {
        BuildingGadgets.LOG.trace("Placing {} with Tile NBT at {}.", state, pos);
        context.getWorld().setBlock(pos, state, 0);

        Utils.enqueueImmediateTask(context.getWorld(), () ->
        {
            TileEntity te = context.getWorld().getBlockEntity(pos);
            if (te != null)
            {
                CompoundNBT nbt = getNBT();
                nbt.putInt("x", pos.getX());
                nbt.putInt("y", pos.getY());
                nbt.putInt("z", pos.getZ());

                try
                {
                    te.load(state, nbt);
                }
                catch (Exception e)
                {
                    BuildingGadgets.LOG.debug("Failed to apply Tile NBT Data to {} at {} in Context {}", state, pos, context, e);
                }
            }
        }, true);

        return true;
    }

    @Override
    public ITileDataSerializer getSerializer() { return BuildingGadgetsCompat.FRAMED_SERIALIZER.get(); }

    @SuppressWarnings("deprecation")
    private static MaterialList buildMaterialList(FramedTileEntity te)
    {
        MaterialList.SimpleBuilder builder = MaterialList.simpleBuilder();

        //Add base materials
        if (te.getBlock() == FBContent.blockFramedDoubleSlab.get())
        {
            builder.add(
                    UniqueItem.ofStack(new ItemStack(FBContent.blockFramedSlab.get())),
                    UniqueItem.ofStack(new ItemStack(FBContent.blockFramedSlab.get()))
            );
        }
        else if (te.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            builder.add(
                    UniqueItem.ofStack(new ItemStack(FBContent.blockFramedPanel.get())),
                    UniqueItem.ofStack(new ItemStack(FBContent.blockFramedPanel.get()))
            );
        }
        else
        {
            builder.add(UniqueItem.ofStack(new ItemStack(te.getBlockState().getBlock())));
        }

        //Add main camo stack
        if (!te.getCamoState().isAir())
        {
            builder.add(UniqueItem.ofStack(te.getCamoStack().copy()));
        }

        //Add secondary camo stack
        if (te instanceof FramedDoubleTileEntity && !((FramedDoubleTileEntity) te).getCamoStateTwo().isAir())
        {
            builder.add(UniqueItem.ofStack(((FramedDoubleTileEntity) te).getCamoStackTwo().copy()));
        }

        //Add glowstone
        if (te.isGlowing())
        {
            builder.add(UniqueItem.ofStack(new ItemStack(Items.GLOWSTONE_DUST)));
        }

        return builder.build();
    }
}