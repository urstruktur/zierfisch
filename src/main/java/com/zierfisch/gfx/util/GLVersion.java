package com.zierfisch.gfx.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
 
import org.lwjgl.opengl.*;
 
public class GLVersion {
     
    private static GLVersion instance;
     
    public static int getMajorVersion() {
        return get().majorVersion;
    }
     
    public static int getMinorVersion() {
        return get().minorVersion;
    }
     
    public static double getVersion() {
        return get().version;
    }
     
    public static String getVersionString() {
        return get().versionString;
    }
     
    public static boolean isExtensionSupported(String extension) {
        return get().extensionMap.containsKey(extension);
    }
     
    public static Collection<String> getSupportedExtensions() {
        return get().extensionMap.keySet();
    }
     
    private static GLVersion get() {
        if (instance == null) {
            instance = new GLVersion();
        }
        return instance;
    }
     
    private final String versionString;
    private final int majorVersion;
    private final int minorVersion;
    private final double version;
    private final Map<String, Boolean> extensionMap;
     
    private GLVersion() {
        versionString = GL11.glGetString(GL11.GL_VERSION);
         
        int majorVersionIndex = versionString.indexOf('.');
        int minorVersionIndex = majorVersionIndex + 1;
        while (minorVersionIndex < versionString.length() && Character.isDigit(minorVersionIndex)) {
            minorVersionIndex++;
        }
        minorVersionIndex++;
         
        majorVersion = Integer.parseInt(versionString.substring(0, majorVersionIndex));
        minorVersion = Integer.parseInt(versionString.substring(majorVersionIndex + 1, minorVersionIndex));
        version = Double.parseDouble(versionString.substring(0, minorVersionIndex));
         
        String[] supportedExtensions;
        if (majorVersion >= 3) {
            int numExtensions = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS);
            supportedExtensions = new String[numExtensions];
            for (int i = 0; i < numExtensions; i++) {
                supportedExtensions[i] = GL30.glGetStringi(GL11.GL_EXTENSIONS, i);
            }
        } else {
            String extensionsAsString = GL11.glGetString(GL11.GL_EXTENSIONS);
            supportedExtensions = extensionsAsString.split(" ");
        }
         
        extensionMap = new HashMap<>();
        for (String extension : supportedExtensions) {
            extensionMap.put(extension, Boolean.TRUE);
        }
    }
     
}