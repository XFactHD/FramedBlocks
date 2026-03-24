package io.github.xfacthd.framedblocks.api.model.quad;

import com.mojang.logging.LogUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.ref.Cleaner;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;

final class QuadModifierPool
{
    private static final Deque<QuadModifier> POOL = new ConcurrentLinkedDeque<>();
    private static final boolean CHECK_FOR_LEAKS = Boolean.getBoolean("framedblocks.quad_modifier.check_leaks");
    @Nullable
    private static final Cleaner CLEANER = CHECK_FOR_LEAKS ? Cleaner.create() : null;

    static QuadModifier acquire()
    {
        QuadModifier modifier = POOL.pollFirst();
        if (modifier == null)
        {
            if (CHECK_FOR_LEAKS)
            {
                LeakDetector leakDetector = new LeakDetector();
                modifier = new LeakDetectingQuadModifier(false, leakDetector);
                // noinspection DataFlowIssue
                CLEANER.register(modifier, leakDetector);
            }
            else
            {
                modifier = new QuadModifier(false);
            }
        }
        else
        {
            if (CHECK_FOR_LEAKS)
            {
                ((LeakDetectingQuadModifier) modifier).leakDetector.prepare();
            }
            modifier.retired = false;
        }
        return modifier;
    }

    static void release(QuadModifier modifier)
    {
        modifier.retired = true;
        POOL.addLast(modifier);
    }

    static final class LeakDetectingQuadModifier extends QuadModifier
    {
        private final LeakDetector leakDetector;

        private LeakDetectingQuadModifier(boolean failed, LeakDetector leakDetector)
        {
            super(failed);
            this.leakDetector = leakDetector;
        }
    }

    private static final class LeakDetector implements Runnable
    {
        private static final Logger LOGGER = LogUtils.getLogger();
        private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        private static final String UNKNOWN_OWNER = "<Unknown>";
        private static final String OWNER_CLASS_SUFFIX = "Geometry";

        private String owner = UNKNOWN_OWNER;

        private LeakDetector()
        {
            prepare();
        }

        void prepare()
        {
            this.owner = STACK_WALKER.walk(LeakDetector::findOwner).orElse(UNKNOWN_OWNER);
        }

        private static Optional<String> findOwner(Stream<StackWalker.StackFrame> frames)
        {
            return frames.map(StackWalker.StackFrame::getClassName)
                    .filter(name -> name.endsWith(OWNER_CLASS_SUFFIX))
                    .findFirst();
        }

        @Override
        public void run()
        {
            LOGGER.error("QuadModifier leaked! Owner: {}", owner);
        }
    }

    private QuadModifierPool() { }
}
