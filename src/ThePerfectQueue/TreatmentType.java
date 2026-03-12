package ThePerfectQueue;

import java.util.List;
import java.util.ArrayList;

public class TreatmentType {
    private String name;
    private int durationMinutes;
    private double price;
    private SpecialtyType specialty; 
    private List<Provider> providers;

    public TreatmentType(String name, SpecialtyType specialty, int durationMinutes, double price) {
        setName(name);
        setSpecialty(specialty);
        setDurationMinutes(durationMinutes);
        setPrice(price);
        this.providers = new ArrayList<>();
    }

    private static boolean validateNotNull(Object obj, String errorMessage) {
        if (obj == null) {
            System.out.println(errorMessage);
            return false;
        }
        return true;
    }

    public String getName() {
        return name != null ? name : specialty.name(); 
    }

    public void setName(String name) {
        if (!validateNotNull(name, "Treatment name cannot be empty.") || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment name cannot be empty.");
        }
        this.name = name;
    }

    public SpecialtyType getSpecialty() {
        return specialty;
    }

    public void setSpecialty(SpecialtyType specialty) {
        if (!validateNotNull(specialty, "Specialty cannot be null.")) {
            throw new IllegalArgumentException("Specialty cannot be null.");
        }
        this.specialty = specialty;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0 minutes.");
        }
        this.durationMinutes = durationMinutes;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public boolean addProvider(Provider provider) {
        if (!validateNotNull(provider, "Cannot add a null provider.")) return false;
        if (!providers.contains(provider)) {
            providers.add(provider);
            System.out.println(provider.getFirstName() + " now provides " + name + ".");
            return true;
        }
        System.out.println(provider.getFirstName() + " already provides " + name + ".");
        return false;
    }

    public boolean removeProvider(Provider provider) {
        if (!validateNotNull(provider, "Cannot remove a null provider.")) return false;
        if (providers.remove(provider)) {
            System.out.println(provider.getFirstName() + " no longer provides " + name + ".");
            return true;
        }
        System.out.println(provider.getFirstName() + " is not assigned to " + name + ".");
        return false;
    }

    public void displayProviders(String title) {
        System.out.println("💆 " + title + ": " + name);
        if (providers.isEmpty()) {
            System.out.println("No providers available for this treatment.");
            return;
        }
        int index = 1;
        for (Provider provider : providers) {
            System.out.println(index++ + ". " + provider.getFirstName() + " " + provider.getLastName());
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Treatment: ").append(name)
               .append("\nDuration: ").append(durationMinutes).append(" minutes")
               .append("\nPrice: $").append(price)
               .append("\nAvailable Providers: ");

        if (providers.isEmpty()) {
            builder.append("None");
        } else {
            for (Provider provider : providers) {
                builder.append("\n - ").append(provider.getFirstName()).append(" ").append(provider.getLastName());
            }
        }
        return builder.toString();
    }
}
