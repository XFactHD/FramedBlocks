package xfacthd.framedblocks.api.util.test;

public interface TestRunnable extends Runnable
{
    default int getDuration() { return 1; }
}
