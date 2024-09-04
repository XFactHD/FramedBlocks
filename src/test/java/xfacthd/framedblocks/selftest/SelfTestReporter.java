package xfacthd.framedblocks.selftest;

import com.google.common.base.Stopwatch;
import org.slf4j.event.Level;
import xfacthd.framedblocks.FramedBlocks;

import java.util.ArrayList;
import java.util.List;

public final class SelfTestReporter
{
    private final List<Entry> lines = new ArrayList<>();
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private State state = State.ROOT;

    public SelfTestReporter()
    {
        info("=======================================");
        info("Running self-test");
        stopwatch.start();
        state = State.IDLE;
    }

    public void startTest(String name)
    {
        entry(Level.INFO, "Checking " + name);
        if (state != State.IDLE)
        {
            throw new IllegalStateException("Encountered invalid state %s when starting test '%s'".formatted(state, name));
        }
        state = State.IN_TEST;
    }

    public void endTest()
    {
        state = State.IDLE;
    }

    public void info(String text, Object... params)
    {
        entry(Level.INFO, text, params);
    }

    public void warn(String text, Object... params)
    {
        entry(Level.WARN, text, params);
    }

    public void error(String text, Object... params)
    {
        entry(Level.ERROR, text, params);
    }

    public void entry(Level logLevel, String text, Object... params)
    {
        if (state != State.ROOT)
        {
            text = "  ".repeat(state.indent) + text;
        }
        lines.add(new Entry(logLevel, text, params));
    }

    public void finish()
    {
        if (state != State.IDLE)
        {
            throw new IllegalStateException("Encountered invalid state %s when finishing".formatted(state));
        }
        state = State.ROOT;
        stopwatch.stop();
        info("Self test completed in {}", stopwatch);
        info("=======================================");

        lines.forEach(entry -> FramedBlocks.LOGGER.atLevel(entry.logLevel).log(entry.text, entry.params));
    }

    private record Entry(Level logLevel, String text, Object... params) { }

    private enum State
    {
        ROOT(0),
        IDLE(1),
        IN_TEST(2);

        private final int indent;

        State(int indent)
        {
            this.indent = indent;
        }
    }
}
