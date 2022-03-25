package xfacthd.framedblocks.util;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.LogTestReporter;

public class VerboseLogTestReporter extends LogTestReporter
{
    @Override
    public void onTestFailed(GameTestInfo info)
    {
        super.onTestFailed(info);

        if (info.getError() != null)
        {
            info.getError().printStackTrace();
        }
    }
}
