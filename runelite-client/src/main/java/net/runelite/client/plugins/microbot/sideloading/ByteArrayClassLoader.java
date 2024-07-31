package net.runelite.client.plugins.microbot.sideloading;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ByteArrayClassLoader extends ClassLoader {
    public final Map<String, byte[]> classBytes;

    private final ClassLoader parent;

    public ByteArrayClassLoader(byte[] jarBytes, ClassLoader parent) throws IOException {
        super(parent);
        classBytes = new HashMap<>();
        loadJarBytes(jarBytes);

        this.parent = parent;
    }

    private void loadJarBytes(byte[] jarBytes) throws IOException {
        try (JarInputStream jis = new JarInputStream(new ByteArrayInputStream(jarBytes))) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = jis.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    String className = entry.getName().replace("/", ".").replace(".class", "");
                    classBytes.put(className, baos.toByteArray());
                }
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classBytes.get(name);
        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }

        return defineClass(name, bytes, 0, bytes.length);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        try
        {
            return super.loadClass(name);
        }
        catch (ClassNotFoundException ex)
        {
            // fall back to main class loader
            return parent.loadClass(name);
        }
    }
}
