package com.bookingsystem.util;

import com.bookingsystem.model.Reservation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Writes the reservation list out to a simple CSV file that can be opened
 * in any spreadsheet application - no external CSV library required.
 */
public class CsvExporter {

    public void export(List<Reservation> reservations, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Date,Start,End,Customer,Service,Status,Notes\n");
            for (Reservation r : reservations) {
                writer.write(escape(r.getDate().toString()) + ",");
                writer.write(escape(r.getStartTime().toString()) + ",");
                writer.write(escape(r.getEndTime().toString()) + ",");
                writer.write(escape(r.getCustomerNameSnapshot()) + ",");
                writer.write(escape(r.getServiceNameSnapshot()) + ",");
                writer.write(escape(r.getStatus().name()) + ",");
                writer.write(escape(r.getNotes() == null ? "" : r.getNotes()));
                writer.write("\n");
            }
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
