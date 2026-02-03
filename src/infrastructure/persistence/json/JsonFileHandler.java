package infrastructure.persistence.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import domain.core.ATM;
import domain.core.Customer;
import domain.core.Technician;
import domain.core.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for reading/writing JSON files.
 * Handles polymorphic User types, LocalDateTime serialization, and ATM with totalCash.
 */
public class JsonFileHandler {

    private final Gson gson;
    private final String dataDirectory;

    public JsonFileHandler(String dataDirectory) {
        // Create Gson with custom type adapters
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(User.class, new UserTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(ATM.class, new ATMTypeAdapter())
                .create();
        this.dataDirectory = dataDirectory;

        createDirectoryIfNotExists();
    }

    private void createDirectoryIfNotExists() {
        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public <T> T readFromFile(String filename, Type type) {
        String filepath = dataDirectory + filename;

        try {
            if (!Files.exists(Paths.get(filepath))) {
                return null;
            }

            String json = new String(Files.readAllBytes(Paths.get(filepath)));
            return gson.fromJson(json, type);

        } catch (IOException e) {
            System.err.println("Error reading file: " + filepath);
            e.printStackTrace();
            return null;
        }
    }

    public <T> void writeToFile(String filename, T data) {
        String filepath = dataDirectory + filename;

        try {
            String json = gson.toJson(data);
            Files.write(Paths.get(filepath), json.getBytes());

        } catch (IOException e) {
            System.err.println("Error writing file: " + filepath);
            e.printStackTrace();
        }
    }

    /**
     * Custom TypeAdapter for User polymorphism.
     */
    private static class UserTypeAdapter extends TypeAdapter<User> {

        @Override
        public void write(JsonWriter out, User user) throws IOException {
            if (user == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("role").value(user.getRole().name());
            out.name("userId").value(user.getUserId());
            out.name("name").value(user.getName());
            out.name("pin").value(user.getPin());

            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                out.name("accountIds");
                out.beginArray();
                for (String accountId : customer.getAccountIds()) {
                    out.value(accountId);
                }
                out.endArray();
            }

            if (user instanceof Technician) {
                Technician technician = (Technician) user;
                out.name("technicianId").value(technician.getTechnicianId());
            }

            out.endObject();
        }

        @Override
        public User read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            String role = null;
            String userId = null;
            String name = null;
            String pin = null;
            String technicianId = null;
            java.util.List<String> accountIds = new java.util.ArrayList<>();

            in.beginObject();
            while (in.hasNext()) {
                String fieldName = in.nextName();
                switch (fieldName) {
                    case "role":
                        role = in.nextString();
                        break;
                    case "userId":
                        userId = in.nextString();
                        break;
                    case "name":
                        name = in.nextString();
                        break;
                    case "pin":
                        pin = in.nextString();
                        break;
                    case "technicianId":
                        technicianId = in.nextString();
                        break;
                    case "accountIds":
                        in.beginArray();
                        while (in.hasNext()) {
                            accountIds.add(in.nextString());
                        }
                        in.endArray();
                        break;
                    default:
                        in.skipValue();
                }
            }
            in.endObject();

            if ("CUSTOMER".equals(role)) {
                Customer customer = new Customer(userId, name, pin);
                customer.setAccountIds(accountIds);
                return customer;
            } else if ("TECHNICIAN".equals(role)) {
                Technician technician = new Technician(userId, name, pin, technicianId);
                return technician;
            }

            return null;
        }
    }

    /**
     * Custom TypeAdapter for LocalDateTime serialization.
     * Converts LocalDateTime to/from ISO-8601 format string.
     */
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime dateTime) throws IOException {
            if (dateTime == null) {
                out.nullValue();
            } else {
                out.value(dateTime.format(FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateTimeString = in.nextString();
            return LocalDateTime.parse(dateTimeString, FORMATTER);
        }
    }

    /**
     * Custom TypeAdapter for ATM to include calculated totalCash in JSON.
     */
    private static class ATMTypeAdapter extends TypeAdapter<ATM> {

        @Override
        public void write(JsonWriter out, ATM atm) throws IOException {
            if (atm == null) {
                out.nullValue();
                return;
            }

            out.beginObject();

            // ATM ID
            out.name("atmId").value(atm.getAtmId());

            // Total Cash (calculated field - added for convenience)
            out.name("totalCash").value(atm.getTotalCash());

            // Cash by denomination
            out.name("cashByDenomination");
            out.beginObject();
            for (Map.Entry<Integer, Integer> entry : atm.getCashByDenomination().entrySet()) {
                out.name(entry.getKey().toString()).value(entry.getValue());
            }
            out.endObject();

            // Other properties
            out.name("inkLevel").value(atm.getInkLevel());
            out.name("paperLevel").value(atm.getPaperLevel());
            out.name("softwareVersion").value(atm.getSoftwareVersion());

            out.endObject();
        }

        @Override
        public ATM read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            String atmId = null;
            Map<Integer, Integer> cashByDenomination = new HashMap<>();
            int inkLevel = 0;
            int paperLevel = 0;
            String softwareVersion = null;

            in.beginObject();
            while (in.hasNext()) {
                String fieldName = in.nextName();
                switch (fieldName) {
                    case "atmId":
                        atmId = in.nextString();
                        break;
                    case "totalCash":
                        in.nextDouble(); // Read but ignore (will be recalculated from denominations)
                        break;
                    case "cashByDenomination":
                        in.beginObject();
                        while (in.hasNext()) {
                            int denomination = Integer.parseInt(in.nextName());
                            int count = in.nextInt();
                            cashByDenomination.put(denomination, count);
                        }
                        in.endObject();
                        break;
                    case "inkLevel":
                        inkLevel = in.nextInt();
                        break;
                    case "paperLevel":
                        paperLevel = in.nextInt();
                        break;
                    case "softwareVersion":
                        softwareVersion = in.nextString();
                        break;
                    default:
                        in.skipValue();
                }
            }
            in.endObject();

            // Get ATM singleton and set properties
            ATM atm = ATM.getInstance();
            if (atmId != null) atm.setAtmId(atmId);
            atm.setCashByDenomination(cashByDenomination);
            atm.setInkLevel(inkLevel);
            atm.setPaperLevel(paperLevel);
            if (softwareVersion != null) atm.setSoftwareVersion(softwareVersion);

            return atm;
        }
    }
}