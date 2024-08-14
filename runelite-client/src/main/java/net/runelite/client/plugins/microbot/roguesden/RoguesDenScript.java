package net.runelite.client.plugins.microbot.roguesden;

import lombok.Getter;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.roguesden.steps.DefaultStep;
import net.runelite.client.plugins.microbot.roguesden.steps.EnterCourseStep;
import net.runelite.client.plugins.microbot.roguesden.steps.Step;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class RoguesDenScript extends Script {
    public static double version = 1.0;

    @Inject
    private LocationBasedStepCalculator stepCalculator;

    @Getter
    private Step currentStep;
    @Getter
    private Step lastKnownStep;
    @Getter
    private Map<Step, Integer> failuresByStep;

    public boolean run(RoguesDenConfig config) {
        Microbot.enableAutoRunOn = false;
        failuresByStep = new HashMap<>();
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                updateScriptState();
                currentStep.execute();

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    private void updateScriptState()
    {
        currentStep = stepCalculator.getCurrentStep();
        if (currentStep instanceof EnterCourseStep && lastKnownStep != null)
        {
            failuresByStep.merge(lastKnownStep, 1, Integer::sum);
            lastKnownStep = null;
        }
        if (!(currentStep instanceof DefaultStep) && !(currentStep instanceof EnterCourseStep))
        {
            lastKnownStep = currentStep;
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
