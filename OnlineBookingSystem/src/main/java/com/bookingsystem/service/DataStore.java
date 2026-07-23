package com.bookingsystem.service;

import com.bookingsystem.model.AppData;
import com.bookingsystem.model.Customer;
import com.bookingsystem.model.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Persists the entire {@link AppData} object graph to a single binary
 * file under the user's home directory using plain Java serialization -
 * no external database or library is required. On first run (no file
 * present yet) a fresh {@link AppData} is created with two example
 * services so the UI is not empty.
 */
public class DataStore {

    private static final Path DATA_DIR = Paths.get(System.getProperty("user.home"), ".onlinebookingsystem");
    private static final Path DATA_FILE = DATA_DIR.resolve("data.ser");

    public AppData load() {
        if (!Files.exists(DATA_FILE)) {
            return createDefaultData();
        }
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(DATA_FILE.toFile())))) {
            Object obj = in.readObject();
            if (obj instanceof AppData) {
                return (AppData) obj;
            }
        } catch (Exception e) {
            // Corrupted or unreadable file: fall back to a fresh data set
            // rather than crashing the application on startup.
        }
        return createDefaultData();
    }

    public void save(AppData data) {
        try {
            Files.createDirectories(DATA_DIR);
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(DATA_FILE.toFile())))) {
                out.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to save application data: " + e.getMessage(), e);
        }
    }

    private AppData createDefaultData() {
        AppData data = new AppData();
        data.getServices().add(new Service("General Consultation", 30, 0.0));
        data.getServices().add(new Service("Extended Session", 60, 0.0));
        return data;
    }
}
