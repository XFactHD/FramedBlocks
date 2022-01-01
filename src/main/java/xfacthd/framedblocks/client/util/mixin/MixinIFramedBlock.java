package xfacthd.framedblocks.client.util.mixin;

import org.spongepowered.asm.mixin.Mixin;
import team.chisel.ctm.api.IFacade;
import xfacthd.framedblocks.api.block.IFramedBlock;

@Mixin(IFramedBlock.class)
public interface MixinIFramedBlock extends IFacade { }
