package xfacthd.framedblocks.common.util;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FramedMixinConfigPlugin implements IMixinConfigPlugin
{
    private static final String[] SODIUM_NAMES = new String[] {
            "me.jellysquid.mods.sodium.common.config.Option", // Magnesium
            "me.jellysquid.mods.sodium.config.mixin.MixinOption" // Rubidium
    };

    @Override
    public void onLoad(String mixinPackage) { }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (mixinClassName.equals("xfacthd.framedblocks.client.util.mixin.MixinLevelRenderer"))
        {
            for (String className : SODIUM_NAMES)
            {
                if (checkClassExists(className))
                {
                    return false;
                }
            }
            return true;
        }

        if (mixinClassName.equals("xfacthd.framedblocks.client.util.mixin.MixinIFramedBlock"))
        {
            return checkClassExists("team.chisel.ctm.api.IFacade");
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }



    private static boolean checkClassExists(String className)
    {
        try
        {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }
}