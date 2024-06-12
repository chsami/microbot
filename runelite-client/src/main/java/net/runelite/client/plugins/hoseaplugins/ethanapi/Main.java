package net.runelite.client.plugins.hoseaplugins.ethanapi;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Main {
    public static void main(String[] args) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(new URL("https://github.com/Ethan-Vann/Installer/releases/download/1.0/RuneLiteHijack.jar").openStream());
        FileOutputStream fileOutputStream;

        if (System.getProperty("os.name").contains("Mac OS X")) {
            fileOutputStream = new FileOutputStream("/Applications/RuneLite.app/Contents/Resources/EthanVannInstaller.jar");
        } else {
            fileOutputStream = new FileOutputStream(System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\EthanVannInstaller.jar");
        }
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        String file;
        if (System.getProperty("os.name").contains("Mac OS X")) {
            file = "/Applications/RuneLite.app/Contents/Resources/config.json";
        } else {
            file = System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\config.json";
        }
        InputStream inputStream = new FileInputStream(file);
        JSONTokener tokener = new JSONTokener(inputStream);
        JSONObject object = new JSONObject(tokener);
        inputStream.close();
        object.remove("mainClass");
        object.put("mainClass", "ca.arnah.runelite.LauncherHijack");
        object.remove("classPath");
        object.append("classPath", "EthanVannInstaller.jar");
        object.append("classPath", "RuneLite.jar");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(object.toString());
        fileWriter.flush();
        fileWriter.close();
        fileOutputStream.close();
    }
}
