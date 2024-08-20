package net.runelite.client.plugins.microbot.util.mouse.naturalmouse;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.api.MouseInfoAccessor;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.api.MouseMotionFactory;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.api.SystemCalls;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.support.DefaultMouseMotionNature;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.support.DefaultSpeedManager;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.support.Flow;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.support.MouseMotionNature;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.util.FactoryTemplates;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.util.FlowTemplates;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.util.Pair;

import javax.inject.Inject;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class NaturalMouse {
    public final MouseMotionNature nature;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Inject
    private Client client;
    @Getter
    @Setter
    private List<Flow> flows = List.of(
            new Flow(FlowTemplates.variatingFlow()),
            new Flow(FlowTemplates.slowStartupFlow()),
            new Flow(FlowTemplates.slowStartup2Flow()),
            new Flow(FlowTemplates.jaggedFlow()),
            new Flow(FlowTemplates.interruptedFlow()),
            new Flow(FlowTemplates.interruptedFlow2()),
            new Flow(FlowTemplates.stoppingFlow()),
            new Flow(FlowTemplates.adjustingFlow()),
            new Flow(FlowTemplates.random())
    );

    @Inject
    public NaturalMouse() {
        nature = new DefaultMouseMotionNature();
        nature.setSystemCalls(new SystemCallsImpl());
        nature.setMouseInfo(new MouseInfoImpl());
    }

    public synchronized void moveTo(int dx, int dy) {
//		if(Rs2UiHelper.isStretchedEnabled())
//		{
//			dx = Rs2UiHelper.stretchX(dx);
//			dy = Rs2UiHelper.stretchY(dy);
//		}
        int finalDx = dx;
        int finalDy = dy;

        if (!Microbot.getClient().isClientThread()) {
            move(finalDx, finalDy);
        } else {

            executorService.submit(() -> move(finalDx, finalDy));
        }
    }

    private synchronized void move(int dx, int dy) {
        var motion = getFactory().build(dx, dy);
        try {
            motion.move();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public MouseMotionFactory getFactory() {
        if (Rs2Antiban.getActivityIntensity() == ActivityIntensity.VERY_LOW) {
            log.info("Creating average computer user motion factory");
            return FactoryTemplates.createAverageComputerUserMotionFactory(nature);
        } else if (Rs2Antiban.getActivityIntensity() == ActivityIntensity.LOW) {
            log.info("Creating normal gamer motion factory");
            return FactoryTemplates.createNormalGamerMotionFactory(nature);
        } else if (Rs2Antiban.getActivityIntensity() == ActivityIntensity.MODERATE) {
            log.info("Creating fast gamer motion factory");
            return FactoryTemplates.createFastGamerMotionFactory(nature);
        } else if (Rs2Antiban.getActivityIntensity() == ActivityIntensity.HIGH) {
            log.info("Creating fast gamer motion factory");
            return FactoryTemplates.createFastGamerMotionFactory(nature);
        } else if (Rs2Antiban.getActivityIntensity() == ActivityIntensity.EXTREME) {
            log.info("Creating super fast gamer motion factory");
            return FactoryTemplates.createSuperFastGamerMotionFactory(nature);
        } else {
            log.info("Default: Creating super fast gamer motion factory");
            return FactoryTemplates.createSuperFastGamerMotionFactory(nature);
        }

//		var factory = new MouseMotionFactory();
//		factory.setNature(nature);
//		factory.setRandom(random);
//
//		var manager = new SpeedManagerImpl(flows);
//		factory.setDeviationProvider(new SinusoidalDeviationProvider(15.0));
//		factory.setNoiseProvider(new DefaultNoiseProvider(2.0));
//		factory.getNature().setReactionTimeVariationMs(120);
//		manager.setMouseMovementBaseTimeMs(130);
//
//		var overshootManager = (DefaultOvershootManager) factory.getOvershootManager();
//		overshootManager.setOvershoots(2);
//		factory.setSpeedManager(manager);
//
//		return factory;
    }

    public void moveOffScreen() {
        // 1 in 4 chance of moving off screen
        if (random.nextInt(4) == 0) {
            // Edges of the screen
            int horizontal = random.nextBoolean() ? -1 : client.getCanvasWidth() + 1;
            int vertical = random.nextBoolean() ? -1 : client.getCanvasHeight() + 1;

            boolean exitHorizontally = random.nextBoolean();
            if (exitHorizontally) {
                moveTo(horizontal, random.nextInt(0, client.getCanvasHeight() + 1));
            } else {
                moveTo(random.nextInt(0, client.getCanvasWidth() + 1), vertical);
            }

        }
    }

    // Move to a random point on the screen
    public void moveRandom() {
        moveTo(random.nextInt(0, client.getCanvasWidth() + 1), random.nextInt(0, client.getCanvasHeight() + 1));
    }

    private static class SpeedManagerImpl extends DefaultSpeedManager {
        private SpeedManagerImpl(Collection<Flow> flows) {
            super(flows);
        }

        @Override
        public Pair<Flow, Long> getFlowWithTime(double distance) {
            var pair = super.getFlowWithTime(distance);
            return new Pair<>(pair.x, pair.y);
        }
    }

    private static class MouseInfoImpl implements MouseInfoAccessor {
        @Override
        public Point getMousePosition() {
            return Microbot.getMouse().getMousePosition();
        }
    }

    private class SystemCallsImpl implements SystemCalls {
        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }

        @Override
        public void sleep(long time) {
            Global.sleep((int) time);
        }

        @Override
        public Dimension getScreenSize() {
            return Microbot.getClient().getCanvas().getSize();
        }

        @Override
        public void setMousePosition(int x, int y) {
            Microbot.getMouse().move(x, y);

        }
    }
}
