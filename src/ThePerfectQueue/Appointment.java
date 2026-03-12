package ThePerfectQueue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Appointment {
    private Customer customer;
    private Provider provider;
    private TreatmentType treatment;
    private LocalDateTime appointmentTime;
    private double price;
    private boolean confirmed;
    private boolean pendingCancellation; // האם התור בהמתנה לביטול
    private String cancellationReason; // סיבת הביטול
    private LocalDateTime suggestedNewTime; // זמן חלופי מוצע לתור
    private boolean forceCancel; // האם הביטול חובה או לא

    public Appointment(Customer customer, Provider provider, TreatmentType treatment, LocalDateTime appointmentTime, double price) {
        if (!validateNotNull(customer, "Error: Customer cannot be null.") ||
            !validateNotNull(provider, "Error: Provider cannot be null.") ||
            !validateNotNull(treatment, "Error: Treatment cannot be null.") ||
            !validateNotNull(appointmentTime, "Error: Appointment time cannot be null.")) {
            throw new IllegalArgumentException("Invalid appointment parameters.");
        }

        this.customer = customer;
        this.provider = provider;
        this.treatment = treatment;
        this.appointmentTime = appointmentTime;
        this.price = price;
        this.confirmed = false;
        this.pendingCancellation = false;
        this.cancellationReason = null;
        this.suggestedNewTime = null;
        this.forceCancel = false;
    }


    private static boolean validateNotNull(Object obj, String errorMessage) {
        if (obj == null) {
            System.out.println(errorMessage);
            return false;
        }
        return true;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Provider getProvider() {
        return provider;
    }

    public TreatmentType getTreatment() {
        return treatment;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public double getPrice() {
        return price;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
    
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void confirmAppointment(boolean status) {
        this.confirmed = status;
        System.out.println(status ? "Appointment confirmed!" : "Appointment canceled.");
    }

    public boolean isPendingCancellation() {
        return pendingCancellation;
    }

    public void setPendingCancellation(boolean pendingCancellation) {
        this.pendingCancellation = pendingCancellation;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getSuggestedNewTime() {
        return suggestedNewTime;
    }

    public void setSuggestedNewTime(LocalDateTime suggestedNewTime) {
        this.suggestedNewTime = suggestedNewTime;
    }

    public boolean isForceCancel() {
        return forceCancel;
    }

    public void setForceCancel(boolean forceCancel) {
        this.forceCancel = forceCancel;
    }

    public boolean rescheduleAppointment(LocalDateTime newTime) {
        if (!validateNotNull(provider, "Error: Provider is missing.")) return false;
        if (!validateNotNull(newTime, "Error: Suggested time cannot be null.")) return false;

        if (!provider.getBookedAppointments().contains(newTime)) {
            provider.getBookedAppointments().remove(appointmentTime); // מסירים את התור הישן
            this.appointmentTime = newTime; // מעדכנים את התור לזמן החדש
            provider.addAppointment(newTime); // מוסיפים את התור החדש
            System.out.println("Appointment successfully rescheduled to: " + appointmentTime);
            return true;
        } else {
            System.out.println("The suggested time is already booked.");
            return false;
        }
    }

    public static Appointment createAppointment(Customer customer, List<TreatmentType> availableTreatments) {
        if (!validateNotNull(customer, "Error: Customer cannot be null.") ||
            !validateNotNull(availableTreatments, "Error: Treatment list cannot be null.") ||
            availableTreatments.isEmpty()) {
            System.out.println("No available treatments.");
            return null;
        }

        TreatmentType selectedTreatment = customer.selectTreatment(availableTreatments);
        if (!validateNotNull(selectedTreatment, "No treatment selected.")) return null;

        Provider selectedProvider = customer.selectProviderForTreatment(selectedTreatment);
        if (!validateNotNull(selectedProvider, "No provider selected.")) return null;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter appointment date and time (YYYY-MM-DD HH:MM):");
        String dateTimeInput = scanner.nextLine();
        LocalDateTime appointmentTime;

        try {
            appointmentTime = LocalDateTime.parse(dateTimeInput.replace(" ", "T"));
        } catch (Exception e) {
            System.out.println("Invalid date format. Please enter in YYYY-MM-DD HH:MM format.");
            return null;
        }

        selectedProvider.addAppointment(customer, selectedTreatment, appointmentTime);
        Appointment appointment = new Appointment(customer, selectedProvider, selectedTreatment, appointmentTime, selectedTreatment.getPrice());
        customer.addAppointment(appointment);

        System.out.println("Appointment successfully booked!");
        return appointment;
    }

    @Override
    public String toString() {
        return "📅 Appointment Details:\n" +
                "👤 Customer: " + customer.getFirstName() + " " + customer.getLastName() + "\n" +
                "💆 Provider: " + provider.getFirstName() + " " + provider.getLastName() + "\n" +
                "💅 Treatment: " + treatment.getName() + "\n" +
                "🕒 Time: " + appointmentTime + "\n" +
                "💲 Price: $" + price + "\n" +
                "✅ Confirmed: " + (confirmed ? "✔ Yes" : "❌ No") + "\n" +
                "🚨 Pending Cancellation: " + (pendingCancellation ? "🕒 Yes" : "✅ No") + "\n" +
                (cancellationReason != null ? "❌ Reason: " + cancellationReason + "\n" : "") +
                (suggestedNewTime != null ? "📆 Suggested New Time: " + suggestedNewTime + "\n" : "");
    }

}
