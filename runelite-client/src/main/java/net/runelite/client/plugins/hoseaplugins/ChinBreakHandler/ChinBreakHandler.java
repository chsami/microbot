package net.runelite.client.plugins.ChinBreakHandler;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.ChinBreakHandler.ui.LoginMode;
import net.runelite.client.plugins.ChinBreakHandler.util.IntRandomNumberGenerator;
import net.runelite.client.plugins.Plugin;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

@SuppressWarnings("unused")
@Singleton
public class ChinBreakHandler {
    private final ConfigManager configManager;

    private final Map<Plugin, Boolean> plugins = new TreeMap<>((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    private final PublishSubject<Map<Plugin, Boolean>> pluginsSubject = PublishSubject.create();

    private final Set<Plugin> activePlugins = new HashSet<>();
    private final PublishSubject<Set<Plugin>> activeSubject = PublishSubject.create();

    private final Map<Plugin, Instant> plannedBreaks = new HashMap<>();
    private final PublishSubject<Map<Plugin, Instant>> plannedBreaksSubject = PublishSubject.create();

    private final Map<Plugin, Instant> activeBreaks = new HashMap<>();
    private final PublishSubject<Map<Plugin, Instant>> activeBreaksSubject = PublishSubject.create();
    private final PublishSubject<Pair<Plugin, Instant>> currentActiveBreaksSubject = PublishSubject.create();

    private final Map<Plugin, Instant> startTimes = new HashMap<>();
    private final Map<Plugin, Integer> amountOfBreaks = new HashMap<>();

    private final PublishSubject<Plugin> logoutActionSubject = PublishSubject.create();
    private final PublishSubject<Plugin> loginActionSubject = PublishSubject.create();

    public final PublishSubject<ConfigChanged> configChanged = PublishSubject.create();

    private final Map<Plugin, Map<String, String>> extraData = new HashMap<>();
    private final PublishSubject<Map<Plugin, Map<String, String>>> extraDataSubject = PublishSubject.create();

    @Inject
    ChinBreakHandler(ConfigManager configManager)
    {
        this.configManager = configManager;
    }

    public Map<Plugin, Boolean> getPlugins()
    {
        return plugins;
    }

    public void registerPlugin(Plugin plugin)
    {
        registerPlugin(plugin, true);
    }

    public void registerPlugin(Plugin plugin, boolean configurable)
    {
        plugins.put(plugin, configurable);
        pluginsSubject.onNext(plugins);
    }

    public void unregisterPlugin(Plugin plugin)
    {
        plugins.remove(plugin);
        pluginsSubject.onNext(plugins);
    }

    public @NonNull Observable<Map<Plugin, Boolean>> getPluginObservable()
    {
        return pluginsSubject.hide();
    }

    public Set<Plugin> getActivePlugins()
    {
        return activePlugins;
    }

    public void startPlugin(Plugin plugin)
    {
        activePlugins.add(plugin);
        activeSubject.onNext(activePlugins);

        startTimes.put(plugin, Instant.now());
        amountOfBreaks.put(plugin, 0);
    }

    public void stopPlugin(Plugin plugin)
    {
        activePlugins.remove(plugin);
        activeSubject.onNext(activePlugins);

        removePlannedBreak(plugin);
        stopBreak(plugin);

        startTimes.remove(plugin);
        amountOfBreaks.remove(plugin);
    }

    public @NonNull Observable<Set<Plugin>> getActiveObservable()
    {
        return activeSubject.hide();
    }

    public Map<Plugin, Instant> getPlannedBreaks()
    {
        return plannedBreaks;
    }

    public void planBreak(Plugin plugin, Instant instant)
    {
        plannedBreaks.put(plugin, instant);
        plannedBreaksSubject.onNext(plannedBreaks);
    }

    public void removePlannedBreak(Plugin plugin)
    {
        plannedBreaks.remove(plugin);
        plannedBreaksSubject.onNext(plannedBreaks);
    }

    public @NonNull Observable<Map<Plugin, Instant>> getPlannedBreaksObservable()
    {
        return plannedBreaksSubject.hide();
    }

    public boolean isBreakPlanned(Plugin plugin)
    {
        return plannedBreaks.containsKey(plugin);
    }

    public Instant getPlannedBreak(Plugin plugin)
    {
        return plannedBreaks.get(plugin);
    }

    public boolean shouldBreak(Plugin plugin)
    {
        if (!plannedBreaks.containsKey(plugin))
        {
            return false;
        }

        return Instant.now().isAfter(getPlannedBreak(plugin));
    }

    public Map<Plugin, Instant> getActiveBreaks()
    {
        return activeBreaks;
    }

    public static boolean needsBankPin(Client client) {
        Widget w = client.getWidget(WidgetInfo.BANK_PIN_CONTAINER);
        return w != null && !w.isHidden();
    }

    public static String getBankPin(ConfigManager configManager) {
        LoginMode loginMode = LoginMode.parse(configManager.getConfiguration("piggyBreakHandler", "accountselection"));
        if (loginMode == null) {
            return null;
        }

        if (loginMode == LoginMode.PROFILES) {
            String account = configManager.getConfiguration("piggyBreakHandler", "accountselection-profiles-account");

            if (ChinBreakHandlerPlugin.data == null) {
                return null;
            }

            Optional<String> accountData = Arrays.stream(ChinBreakHandlerPlugin.data.split("\\n"))
                    .filter(s -> s.startsWith(account))
                    .findFirst();

            if (accountData.isPresent())
            {
                String[] parts = accountData.get().split(":");
                if (parts.length == 4)
                {
                    return parts[3];
                } else {
                    return parts[2];
                }
            }

            return null;
        }

        String pin = configManager.getConfiguration("piggyBreakHandler", "accountselection-manual-pin");
        if (pin == null || pin.length() != 4) {
            return null;
        }
        return pin;
    }

    public static int getOrDefaultFrom(Plugin plugin, ConfigManager configManager) {
        String s = configManager.getConfiguration("piggyBreakHandler", ChinBreakHandlerPlugin.sanitizedName(plugin) + "-breakfrom");
        if (s == null || s.isEmpty()) {
            return 60 * 60;
        }
        return Integer.parseInt(s) * 60;
    }
    public static int getOrDefaultTo(Plugin plugin, ConfigManager configManager) {
        String s = configManager.getConfiguration("piggyBreakHandler", ChinBreakHandlerPlugin.sanitizedName(plugin) + "-breakto");
        if (s == null || s.isEmpty()) {
            return 60 * 60;
        }
        return Integer.parseInt(s) * 60;
    }

    public void startBreak(Plugin plugin)
    {
        int from = getOrDefaultFrom(plugin, configManager);
        int to = getOrDefaultTo(plugin, configManager);

        int random = new IntRandomNumberGenerator(from, to).nextInt();

        removePlannedBreak(plugin);

        Instant breakUntil = Instant.now().plus(random, ChronoUnit.SECONDS);

        activeBreaks.put(plugin, breakUntil);
        activeBreaksSubject.onNext(activeBreaks);

        currentActiveBreaksSubject.onNext(Pair.of(plugin, breakUntil));

        if (amountOfBreaks.containsKey(plugin))
        {
            amountOfBreaks.put(plugin, amountOfBreaks.get(plugin) + 1);
        }
        else
        {
            amountOfBreaks.put(plugin, 1);
        }
    }

    public void startBreak(Plugin plugin, Instant instant)
    {
        removePlannedBreak(plugin);

        activeBreaks.put(plugin, instant);
        activeBreaksSubject.onNext(activeBreaks);

        currentActiveBreaksSubject.onNext(Pair.of(plugin, instant));

        if (amountOfBreaks.containsKey(plugin))
        {
            amountOfBreaks.put(plugin, amountOfBreaks.get(plugin) + 1);
        }
        else
        {
            amountOfBreaks.put(plugin, 1);
        }
    }

    public void stopBreak(Plugin plugin)
    {
        activeBreaks.remove(plugin);
        activeBreaksSubject.onNext(activeBreaks);
    }

    public void setExtraData(Plugin plugin, String key, String value)
    {
        extraData.putIfAbsent(plugin, new LinkedHashMap<>());
        extraData.get(plugin).put(key, value);

        extraDataSubject.onNext(extraData);
    }

    public void setExtraData(Plugin plugin, Map<String, String> data)
    {
        extraData.putIfAbsent(plugin, new LinkedHashMap<>());

        data.forEach(
                (key, value) -> extraData.get(plugin).merge(key, value, (existingData, newData) -> newData)
        );

        extraDataSubject.onNext(extraData);
    }

    public void removeExtraData(Plugin plugin, String key)
    {
        if (!extraData.containsKey(plugin))
        {
            return;
        }

        extraData.get(plugin).remove(key);
        extraDataSubject.onNext(extraData);
    }

    public void resetExtraData(Plugin plugin)
    {
        extraData.remove(plugin);
        extraDataSubject.onNext(extraData);
    }

    public @NonNull Observable<Map<Plugin, Map<String, String>>> getExtraDataObservable()
    {
        return extraDataSubject.hide();
    }

    public @NonNull Observable<Map<Plugin, Instant>> getActiveBreaksObservable()
    {
        return activeBreaksSubject.hide();
    }

    public @NonNull Observable<Pair<Plugin, Instant>> getCurrentActiveBreaksObservable()
    {
        return currentActiveBreaksSubject.hide();
    }

    public boolean isBreakActive(Plugin plugin)
    {
        return activeBreaks.containsKey(plugin);
    }

    public Instant getActiveBreak(Plugin plugin)
    {
        return activeBreaks.get(plugin);
    }

    public void logoutNow(Plugin plugin)
    {
        logoutActionSubject.onNext(plugin);
    }

    public @NonNull Observable<Plugin> getlogoutActionObservable()
    {
        return logoutActionSubject.hide();
    }

    public @NonNull Observable<Plugin> getLoginActionObservable() {
        return loginActionSubject.hide();
    }

    public Map<Plugin, Instant> getStartTimes()
    {
        return startTimes;
    }

    public Map<Plugin, Integer> getAmountOfBreaks()
    {
        return amountOfBreaks;
    }

    public int getTotalAmountOfBreaks()
    {
        return amountOfBreaks.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void loginNow(Plugin plugin) {
        loginActionSubject.onNext(plugin);
    }
}