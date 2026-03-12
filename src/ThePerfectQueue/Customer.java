package ThePerfectQueue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Customer extends User implements Cancellable {
    private List<Appointment> appointments; // רשימת התורים של הלקוחה
    private boolean hasAllergies;

    public Customer(userType userType, String userName, String password, String firstName, String lastName, String phoneNum, String email, LocalDate birthDate) {
        super(userType, userName, password, firstName, lastName, phoneNum, email, birthDate);
        this.appointments = new ArrayList<>();
        this.hasAllergies = false;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public boolean isHasAllergies() {
        return hasAllergies;
    }

    public void setHasAllergies(boolean hasAllergies) {
        this.hasAllergies = hasAllergies;
    }

    public TreatmentType selectTreatment(List<TreatmentType> availableTreatments) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("💆 Select a treatment:");
        for (int i = 0; i < availableTreatments.size(); i++) {
            System.out.println((i + 1) + ". " + availableTreatments.get(i).getName());
        }

        int treatmentChoice = scanner.nextInt() - 1;
        if (treatmentChoice < 0 || treatmentChoice >= availableTreatments.size()) {
            System.out.println("Invalid selection.");
            return null;
        }

        return availableTreatments.get(treatmentChoice);
    }

    // ✅ בחירת קוסמטיקאית לטיפול שנבחר
    public Provider selectProviderForTreatment(TreatmentType selectedTreatment) {
        if (selectedTreatment == null || selectedTreatment.getProviders().isEmpty()) {
            System.out.println("No providers available for this treatment.");
            return null;
        }

        Scanner scanner = new Scanner(System.in);
        selectedTreatment.displayProviders("Available Providers");

        System.out.println("👩 Select a provider:");
        int providerChoice = scanner.nextInt() - 1;
        if (providerChoice < 0 || providerChoice >= selectedTreatment.getProviders().size()) {
            System.out.println("Invalid selection.");
            return null;
        }

        return selectedTreatment.getProviders().get(providerChoice);
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
    public void cancelAppointment(Appointment appointment) {
        if (!validateNotNull(appointment, "Error: No appointment selected.") || 
            !appointments.contains(appointment)) {
            System.out.println("Error: Appointment does not belong to this customer.");
            return;
        }

        appointments.remove(appointment);
        appointment.getProvider().getBookedAppointments().remove(appointment.getAppointmentTime());
        appointment.confirmAppointment(false);
        System.out.println("Appointment canceled successfully.");
    }

    public void respondToCancellationRequest(Appointment appointment, int response) {
        if (!validateNotNull(appointment, "Error: No appointment selected.") || 
            !appointment.isPendingCancellation()) {
            System.out.println("No pending cancellation request for this appointment.");
            return;
        }

        LocalDateTime suggestedTime = appointment.getSuggestedNewTime();
        boolean forceCancel = appointment.isForceCancel();

        switch (response) {
            case 1:
                appointment.rescheduleAppointment(suggestedTime);
                appointment.setPendingCancellation(false);
                System.out.println("Your appointment has been rescheduled to: " + suggestedTime);
                break;

            case 2: 
                appointment.getProvider().cancelAppointment(appointment);
                appointment.setPendingCancellation(false);
                System.out.println("Your appointment has been canceled.");
                break;

            case 3: 
                if (!forceCancel) {
                    appointment.setPendingCancellation(false);
                    System.out.println("Your appointment remains scheduled as originally planned.");
                } else {
                    System.out.println("This appointment must be canceled as per provider's request.");
                    appointment.getProvider().cancelAppointment(appointment);
                }
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }

    @Override
    public void extendedRegistration() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you have any allergies? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();
        this.hasAllergies = response.equals("yes");
        System.out.println("Customer registration completed successfully!");
    }

    public void addAppointment(Appointment appointment) {
        if (appointment != null) {
            appointments.add(appointment);
        }
    }
    
    public void requestToJoinWaitingList(Provider provider) {
        if (!validateNotNull(provider, "Error: Provider cannot be null.")) return;
        provider.addToWaitingList(this);
        System.out.println("You have been added to the waiting list for " + provider.getFirstName());
    }

}
