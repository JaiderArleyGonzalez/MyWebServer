package edu.arep.runtime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassFinder {

    public static List<Class<?>> findClasses(String packageName) throws ClassNotFoundException, IOException {
        String path = packageName.replace('.', File.separatorChar);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ArrayList<File> directories = new ArrayList<>();
        try {
            String resource = packageName.replace('.', '/');
            File[] resources = new File(classLoader.getResource(resource).toURI()).listFiles();
            for (File resourceFile : resources) {
                if (resourceFile.isDirectory()) {
                    directories.add(resourceFile);
                }
            }
        } catch (NullPointerException | java.net.URISyntaxException e) {
            throw new ClassNotFoundException(packageName + " (" + path + ") does not appear to be a valid package", e);
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : directories) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}