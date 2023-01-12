package xfacthd.framedblocks.api.test;

public interface TestRunnable extends Runnable
{
    default int getDuration() { return 1; }
}
