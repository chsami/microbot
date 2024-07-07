package net.runelite.client.plugins.microbot.sideloading;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.reflect.ClassPath;
import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.plugins.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
@Singleton
@Slf4j
public class MicrobotPluginManager {

    private final PluginManager pluginManager;

    @Inject
    MicrobotPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public static File[] createSideloadingFolder() {
        final File MICROBOT_PLUGINS = new File(RuneLite.RUNELITE_DIR, "microbot-plugins");
        if (!Files.exists(MICROBOT_PLUGINS.toPath())) {
            try {
                Files.createDirectories(MICROBOT_PLUGINS.toPath());
                System.out.println("Directory for sideloading was created successfully.");
                return MICROBOT_PLUGINS.listFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return MICROBOT_PLUGINS.listFiles();
    }

    public void loadSideLoadPlugins() {
        File[] files = createSideloadingFolder();
        if (files == null)
        {
            return;
        }

        for (File f : files)
        {
            if (f.getName().endsWith(".jar"))
            {
                System.out.println("Side-loading plugin " + f.getName());

                try
                {
                    MicrobotPluginClassLoader classLoader = new MicrobotPluginClassLoader(f, getClass().getClassLoader());

                    List<Class<?>> plugins = ClassPath.from(classLoader)
                            .getAllClasses()
                            .stream()
                            .map(ClassPath.ClassInfo::load)
                            .collect(Collectors.toList());

                    loadPlugins(plugins, null);
                }
                catch (PluginInstantiationException | IOException ex)
                {
                    System.out.println("error sideloading plugin " + ex);
                }
            }
        }
    }

    /**
     * Topologically sort a graph. Uses Kahn's algorithm.
     *
     * @param graph - A directed graph
     * @param <T>   - The type of the item contained in the nodes of the graph
     * @return - A topologically sorted list corresponding to graph.
     * <p>
     * Multiple invocations with the same arguments may return lists that are not equal.
     */
    @VisibleForTesting
    static <T> List<T> topologicalSort(Graph<T> graph) {
        MutableGraph<T> graphCopy = Graphs.copyOf(graph);
        List<T> l = new ArrayList<>();
        Set<T> s = graphCopy.nodes().stream()
                .filter(node -> graphCopy.inDegree(node) == 0)
                .collect(Collectors.toSet());
        while (!s.isEmpty()) {
            Iterator<T> it = s.iterator();
            T n = it.next();
            it.remove();

            l.add(n);

            for (T m : new HashSet<>(graphCopy.successors(n))) {
                graphCopy.removeEdge(n, m);
                if (graphCopy.inDegree(m) == 0) {
                    s.add(m);
                }
            }
        }
        if (!graphCopy.edges().isEmpty()) {
            throw new RuntimeException("Graph has at least one cycle");
        }
        return l;
    }

    public List<Plugin> loadPlugins(List<Class<?>> plugins, BiConsumer<Integer, Integer> onPluginLoaded) throws PluginInstantiationException {
        MutableGraph<Class<? extends Plugin>> graph = GraphBuilder
                .directed()
                .build();

        for (Class<?> clazz : plugins) {
            PluginDescriptor pluginDescriptor = clazz.getAnnotation(PluginDescriptor.class);

            if (pluginDescriptor == null) {
                if (clazz.getSuperclass() == Plugin.class) {
                    log.error("Class {} is a plugin, but has no plugin descriptor", clazz);
                }
                continue;
            }

            if (clazz.getSuperclass() != Plugin.class) {
                log.error("Class {} has plugin descriptor, but is not a plugin", clazz);
                continue;
            }

            graph.addNode((Class<Plugin>) clazz);
        }

        // Build plugin graph
        for (Class<? extends Plugin> pluginClazz : graph.nodes()) {
            PluginDependency[] pluginDependencies = pluginClazz.getAnnotationsByType(PluginDependency.class);

            for (PluginDependency pluginDependency : pluginDependencies) {
                if (graph.nodes().contains(pluginDependency.value())) {
                    graph.putEdge(pluginDependency.value(), pluginClazz);
                }
            }
        }

        if (Graphs.hasCycle(graph)) {
            throw new PluginInstantiationException("Plugin dependency graph contains a cycle!");
        }

        List<Class<? extends Plugin>> sortedPlugins = topologicalSort(graph);

        int loaded = 0;
        List<Plugin> newPlugins = new ArrayList<>();
        for (Class<? extends Plugin> pluginClazz : sortedPlugins) {
            Plugin plugin;
            try {
                plugin = instantiate(pluginManager.getPlugins(), (Class<Plugin>) pluginClazz);
                log.info("Microbot plugin sideloaded " + plugin.getName());
                newPlugins.add(plugin);
                pluginManager.addPlugin(plugin);
            } catch (PluginInstantiationException ex) {
                log.error("Error instantiating plugin!", ex);
            }

            loaded++;
            if (onPluginLoaded != null) {
                onPluginLoaded.accept(loaded, sortedPlugins.size());
            }
        }

        return newPlugins;
    }

    private Plugin instantiate(Collection<Plugin> scannedPlugins, Class<Plugin> clazz) throws PluginInstantiationException {
        PluginDependency[] pluginDependencies = clazz.getAnnotationsByType(PluginDependency.class);
        List<Plugin> deps = new ArrayList<>();
        for (PluginDependency pluginDependency : pluginDependencies) {
            Optional<Plugin> dependency = scannedPlugins.stream().filter(p -> p.getClass() == pluginDependency.value()).findFirst();
            if (!dependency.isPresent()) {
                throw new PluginInstantiationException("Unmet dependency for " + clazz.getSimpleName() + ": " + pluginDependency.value().getSimpleName());
            }
            deps.add(dependency.get());
        }

        Plugin plugin;
        try {
            plugin = clazz.getDeclaredConstructor().newInstance();
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable ex) {
            throw new PluginInstantiationException(ex);
        }

        try {
            Injector parent = RuneLite.getInjector();

            if (deps.size() > 1) {
                List<Module> modules = new ArrayList<>(deps.size());
                for (Plugin p : deps) {
                    // Create a module for each dependency
                    Module module = (Binder binder) ->
                    {
                        binder.bind((Class<Plugin>) p.getClass()).toInstance(p);
                        binder.install(p);
                    };
                    modules.add(module);
                }

                // Create a parent injector containing all of the dependencies
                parent = parent.createChildInjector(modules);
            } else if (!deps.isEmpty()) {
                // With only one dependency we can simply use its injector
                parent = deps.get(0).getInjector();
            }

            // Create injector for the module
            Module pluginModule = (Binder binder) ->
            {
                // Since the plugin itself is a module, it won't bind itself, so we'll bind it here
                binder.bind(clazz).toInstance(plugin);
                binder.install(plugin);
            };
            Injector pluginInjector = parent.createChildInjector(pluginModule);
            plugin.setInjector(pluginInjector);
        } catch (CreationException ex) {
            throw new PluginInstantiationException(ex);
        }

        log.debug("Loaded plugin {}", clazz.getSimpleName());
        return plugin;
    }
}
