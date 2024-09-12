package xfacthd.framedblocks.common.util;

import net.neoforged.fml.loading.FMLEnvironment;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused") // Referenced by mixin config
public final class FramedMixinConfigPlugin implements IMixinConfigPlugin
{
    private static final Set<String> DEV_ONLY_MIXINS = Set.of(
            "xfacthd.framedblocks.mixin.client.DevMixinLevelRenderer"
    );

    @Override
    public void onLoad(String mixinPackage) { }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (DEV_ONLY_MIXINS.contains(mixinClassName))
        {
            return !FMLEnvironment.production;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }



    @SuppressWarnings({ "SameParameterValue", "unused" })
    private static boolean checkClassExists(String className)
    {
        try
        {
            Class.forName(className, false, FramedMixinConfigPlugin.class.getClassLoader());
            return true;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }
}
