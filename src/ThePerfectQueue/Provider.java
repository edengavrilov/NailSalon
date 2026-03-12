package ThePerfectQueue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeSet;

public class Provider extends User implements Cancellable {
    private SpecialtyType specialty;
    private int maxCustomers;
    private NavigableSet<LocalDateTime> bookedAppointments; 
    private DayOfWeek[] workingDays; 
    private LocalTime workStart, workEnd;
    private Queue<Customer> waitingList;
    private static final Scanner scanner = new Scanner(System.in); 

    public Provider(userType userType, String userName, String password, String firstName, String lastName, String phoneNum, String email, LocalDate birthDate, SpecialtyType specialty, int maxCustomers, DayOfWeek[] workingDays, LocalTime workStart, LocalTime workEnd) {
        super(userType, userName, password, firstName, lastName, phoneNum, email, birthDate);
        this.specialty = specialty;
        this.maxCustomers = maxCustomers;
        this.bookedAppointments = new TreeSet<>();
        this.workingDays = workingDays;
        this.workStart = workStart;
        this.workEnd = workEnd;
        this.waitingList = new LinkedList<>();
    }

    public SpecialtyType getSpecialty() {
        return specialty;
    }

    public int getMaxCustomers() {
        return maxCustomers;
    }

    public NavigableSet<LocalDateTime> getBookedAppointments() {
        return bookedAppointments;
    }

    public void addAppointment(LocalDateTime appointmentTime) {
        bookedAppointments.add(appointmentTime);
    }

    @Override
    public void extendedRegistration() {
        System.out.println("Select your specialty:");
        for (SpecialtyType specialty : SpecialtyType.values()) {
            System.out.println(specialty.getCode() + " - " + specialty.name());
        }

        int specialtyCode;
        while (true) {
            try {
                specialtyCode = scanner.nextInt();
                SpecialtyType selectedSpecialty = SpecialtyType.fromCode(specialtyCode);
                if (!validateNotNull(selectedSpecialty, "Invalid specialty selection.")) continue;
                this.specialty = selectedSpecialty;
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid specialty code.");
                scanner.nextLine(); 
            }
        }

        System.out.println("Enter the maximum number of customers per day:");
        while (true) {
            try {
                this.maxCustomers = scanner.nextInt();
                if (maxCustomers <= 0) throw new IllegalArgumentException("Number of customers must be greater than 0.");
                break;
            } catch (Exception e) {
                System.out.println("Invalid number. Please enter a valid number.");
                scanner.nextLine();
            }
        }

        // ✅ הזנת ימי עבודה עם טיפול בשגיאות
        System.out.println("Enter the number of working days per week:");
        int numOfDays;
        while (true) {
            try {
                numOfDays = scanner.nextInt();
                if (numOfDays < 1 || numOfDays > 7) throw new IllegalArgumentException("Number of working days must be between 1 and 7.");
                break;
            } catch (Exception e) {
                System.out.println("Invalid number. Enter a number between 1-7.");
                scanner.nextLine();
            }
        }

        DayOfWeek[] workDays = new DayOfWeek[numOfDays];
        System.out.println("Enter working days (1 = Monday, 7 = Sunday):");
        for (int i = 0; i < numOfDays; i++) {
            while (true) {
                try {
                    int dayNum = scanner.nextInt();
                    DayOfWeek selectedDay = DayOfWeek.of((dayNum % 7) + 1);
                    if (!validateNotNull(selectedDay, "Invalid day selection.")) continue;
                    workDays[i] = selectedDay;
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid day. Enter a number between 1-7.");
                    scanner.nextLine();
                }
            }
        }
        this.workingDays = workDays;

        // ✅ הזנת שעות עבודה עם טיפול בשגיאות
        int startHour = getValidHour("Enter a valid start working hour (0-23): ");

        int endHour = getValidHour("Enter a valid end working hour (0-23): ");

        this.workStart = LocalTime.of(startHour, 0);
        this.workEnd = LocalTime.of(endHour, 0);

    }

    @Override
    public void cancelAppointment(Appointment appointment) {
        if (!validateNotNull(appointment, "Error: No appointment selected.")) return;
        if (bookedAppointments.remove(appointment.getAppointmentTime())) {
            System.out.println("Appointment canceled successfully.");
            notifyNextCustomer(); // אם יש רשימת המתנה – תור הבא מקבל הודעה
        } else {
            System.out.println("Error: Appointment not found.");
        }
    }

    public LocalDateTime findClosestAvailableApmnt(LocalDateTime currentAppointment, TreatmentType treatment) {
        int treatmentDuration = treatment.getDurationMinutes();
        LocalDateTime newTime = currentAppointment;
        
        while (true) {  
            if (Arrays.asList(workingDays).contains(newTime.getDayOfWeek())) {
                LocalDateTime checkTime = newTime.withHour(workStart.getHour()).withMinute(workStart.getMinute());
                while (checkTime.toLocalTime().plusMinutes(treatmentDuration).isBefore(workEnd)) {
                    if (!bookedAppointments.contains(checkTime)) {
                        return checkTime;
                    }
                    checkTime = checkTime.plusMinutes(treatmentDuration);
                }
            }
            newTime = newTime.plusDays(1).withHour(workStart.getHour()).withMinute(workStart.getMinute());
        }
    }

    public void addAppointment(Customer customer, TreatmentType treatment, LocalDateTime appointmentTime) {
        if (!Arrays.asList(workingDays).contains(appointmentTime.getDayOfWeek())) {
            System.out.println("Error: The selected day is not a working day.");
            return;
        }

        if (appointmentTime.toLocalTime().isBefore(workStart) || appointmentTime.toLocalTime().isAfter(workEnd)) {
            System.out.println("Error: Appointment time is outside of working hours.");
            return;
        }

        if (bookedAppointments.size() >= maxCustomers) {
            System.out.println("Error: Maximum number of customers reached for this day.");
            return;
        }

        bookedAppointments.add(appointmentTime);
        System.out.println("Appointment scheduled for " + customer.getFirstName() + " on " + appointmentTime + " for " + treatment.getName());
    }

    public void displaySchedule() {
        System.out.println("\nProvider's Full Schedule:");
        
        // הצגת ימי עבודה
        System.out.print("Working Days: ");
        if (workingDays.length == 0) {
            System.out.println("No working days set.");
        } else {
            for (DayOfWeek day : workingDays) {
                System.out.print(day + " ");
            }
            System.out.println();
        }

        System.out.println("Working Hours: " + workStart + " - " + workEnd);
        
        displayAppointments();
    }

    public void displayAppointments() {
        System.out.println("\nScheduled Appointments:");

        if (bookedAppointments.isEmpty()) {
            System.out.println("No appointments booked.");
            return;
        }

        System.out.println("1 - Show all appointments");
        System.out.println("2 - Filter by date");
        System.out.println("3 - Filter by treatment type");

        int choice = getValidNumber(1, 3);
        LocalDate filterDate = null;
        SpecialtyType filterSpecialty = null; // שינוי לסינון לפי Enum במקום שם

        if (choice == 2) {
            System.out.print("Enter date to filter (YYYY-MM-DD): ");
            filterDate = LocalDate.parse(scanner.next());
        } else if (choice == 3) {
            System.out.println("Select treatment type:");
            for (SpecialtyType specialty : SpecialtyType.values()) { 
                System.out.println(specialty.getCode() + " - " + specialty.name());
            }
            System.out.print("Enter the specialty code: ");
            int specialtyCode = getValidNumber(1, SpecialtyType.values().length);
            filterSpecialty = SpecialtyType.fromCode(specialtyCode);
        }

        boolean foundAppointments = false;

        for (LocalDateTime time : bookedAppointments) {
            Appointment app = findAppointmentByTime(time);
            if (app == null || app.getTreatment() == null) continue;

            if (filterDate != null && !app.getAppointmentTime().toLocalDate().equals(filterDate)) continue;

            if (filterSpecialty != null && app.getTreatment().getSpecialty() != filterSpecialty) continue; // סינון לפי סוג טיפול

            System.out.println("---------------------------------");
            System.out.println("🕒 Time: " + app.getAppointmentTime());
            System.out.println("👩 Customer: " + app.getCustomer().getFirstName() + " " + app.getCustomer().getLastName());
            System.out.println("💆 Treatment: " + app.getTreatment().getName());
            System.out.println("💲 Price: $" + app.getPrice());
            System.out.println("📌 Status: " + (app.isConfirmed() ? "✔ Confirmed" : "❌ Not Confirmed"));

            foundAppointments = true;
        }

        if (!foundAppointments) {
            System.out.println("No appointments found matching the selected criteria.");
        }
    }

    public Appointment findAppointmentByTime(LocalDateTime appointmentTime) {
        for (LocalDateTime time : bookedAppointments) {
            if (time.equals(appointmentTime)) {
                // יצירת אובייקט תור זמני להחזרה
                return new Appointment(null, this, null, time, 0); // צריך לקבל את הפרטים מהמערכת
            }
        }
        return null;
    }

    public void updateAvailability() {
        System.out.println("Current Schedule:");
        displaySchedule(); 

        while (true) {
            int choice = -1;
            while (choice < 1 || choice > 5) {
                try {
                    System.out.println("\nWhat would you like to update?");
                    System.out.println("1 - Add a working day");
                    System.out.println("2 - Remove a working day");
                    System.out.println("3 - Change working hours for a specific day");
                    System.out.println("4 - Mark a day as unavailable (vacation/special event)");
                    System.out.println("5 - Exit");
                    System.out.print("Enter your choice: ");
                    choice = scanner.nextInt();
                    scanner.nextLine(); 
                } catch (Exception e) {
                    System.out.println("❌ Invalid input. Please enter a number between 1-5.");
                    scanner.nextLine(); 
                    choice = -1;
                }
            }

            if (choice == 5) {
                System.out.println("Schedule update completed!");
                break;
            }

            int dayNum = -1;
            while (dayNum < 1 || dayNum > 7) {
                try {
                    System.out.print("Enter the day (1 = Monday, ..., 7 = Sunday): ");
                    dayNum = scanner.nextInt();
                    scanner.nextLine();
                    if (dayNum < 1 || dayNum > 7) {
                        System.out.println("Invalid day. Please enter a number between 1-7.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter a number between 1-7.");
                    scanner.nextLine();
                    dayNum = -1;
                }
            }
            
            DayOfWeek day = DayOfWeek.of(dayNum);

            switch (choice) {
                case 1: 
                    if (!Arrays.asList(workingDays).contains(day)) {
                        DayOfWeek[] newDays = Arrays.copyOf(workingDays, workingDays.length + 1);
                        newDays[newDays.length - 1] = day;
                        workingDays = newDays;
                        System.out.println(day + " added to working days.");
                    } else {
                        System.out.println(day + " is already a working day.");
                    }
                    break;

                case 2: 
                    if (Arrays.asList(workingDays).contains(day)) {
                        workingDays = Arrays.stream(workingDays).filter(d -> d != day).toArray(DayOfWeek[]::new);
                        System.out.println(day + " removed from working days.");
                    } else {
                        System.out.println(day + " is not in the schedule.");
                    }
                    break;

                case 3: 
                    if (Arrays.asList(workingDays).contains(day)) {
                        int startHour = getValidHour("Enter new start working hour (0-23): ");
                        int endHour = getValidHour("Enter new end working hour (0-23): ");
                        
                        this.workStart = LocalTime.of(startHour, 0);
                        this.workEnd = LocalTime.of(endHour, 0);

                        System.out.println("Working hours for " + day + " updated to: " + workStart + " - " + workEnd);
                    } else {
                        System.out.println(day + " is not a working day. Add it first before changing hours.");
                    }
                    break;

                case 4: 
                    workingDays = Arrays.stream(workingDays).filter(d -> d != day).toArray(DayOfWeek[]::new);
                    System.out.println(day + " marked as unavailable.");
                    break;

                default:
                    System.out.println("Invalid choice. Please select again.");
                    break;
            }
        }
    }
        
    private int getValidHour(String prompt) {
        int hour;
        while (true) {
            try {
                System.out.print(prompt);
                if (!scanner.hasNextInt()) {  
                    System.out.println("❌ Invalid input. Please enter a number.");
                    scanner.next(); 
                    continue;
                }
                hour = scanner.nextInt();
                scanner.nextLine(); 
                if (hour >= 0 && hour <= 23) return hour;
                System.out.println("Invalid hour. Please enter a number between 0-23.");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number between 0-23.");
                scanner.nextLine(); 
            }
        }
    }

    public boolean confirmAppointment(Appointment appointment) {
        if (!validateNotNull(appointment, "Error: No appointment selected.")) return false;
        if (!validateNotNull(appointment.getAppointmentTime(), "Error: Appointment time is not set.")) return false;
        if (!bookedAppointments.contains(appointment.getAppointmentTime())) {
            System.out.println("Appointment not found.");
            return false;
        }
        if (!appointment.isConfirmed()) {
            appointment.setConfirmed(true);
            System.out.println("Appointment on " + appointment.getAppointmentTime() + " has been confirmed.");
            return true;
        } else {
            System.out.println("This appointment is already confirmed.");
            return false;
        }
    }
    
    public void requestCancellation(Appointment appointment) {
        if (!validateNotNull(appointment, "Error: No appointment selected.")) return;
        Customer customer = appointment.getCustomer();
        TreatmentType treatment = appointment.getTreatment();
        System.out.println("Select a reason for cancellation:");
        System.out.println("1 - Personal reasons");
        System.out.println("2 - Health issues");
        System.out.println("3 - Vacation or time conflict");
        System.out.println("4 - Other (specify)");
        int reasonChoice = getValidNumber(1, 4);
        String reason;
        switch (reasonChoice) {
            case 1 -> reason = "Personal reasons";
            case 2 -> reason = "Health issues";
            case 3 -> reason = "Vacation or time conflict";
            default -> {
                System.out.println("Enter your reason:");
                reason = scanner.nextLine();
            }
        }
        System.out.println("Should the appointment be canceled no matter what?");
        System.out.println("1 - Yes, cancel the appointment even if the customer refuses.");
        System.out.println("2 - No, keep the appointment if the customer refuses.");
        boolean forceCancel = getValidNumber(1, 2) == 1;
        LocalDateTime closestAvailable = findClosestAvailableApmnt(appointment.getAppointmentTime(), treatment);
        appointment.setPendingCancellation(true);
        appointment.setCancellationReason(reason);
        appointment.setSuggestedNewTime(closestAvailable);
        appointment.setForceCancel(forceCancel);
        System.out.println("\nA cancellation request has been sent to " + customer.getFirstName() + ".");
        System.out.println("Waiting for customer response...");
    }
      
    public void sendAppointmentReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        if (bookedAppointments.isEmpty()) {
            System.out.println("No appointments scheduled for tomorrow.");
            return;
        }
        for (LocalDateTime appointmentTime : bookedAppointments) {
            Appointment appointment = findAppointmentByTime(appointmentTime);
            if (!validateNotNull(appointment, "Erroe: Appointment at " + appointmentTime + " not found in the system.")) continue;
            if (appointmentTime.toLocalDate().equals(tomorrow)) {
                String status = appointment.isConfirmed() ? "✔ Confirmed" : "❌ Not Confirmed";
                System.out.println("\n📩 Reminder sent to " + appointment.getCustomer().getFirstName() + " for their appointment:");
                System.out.println("🕒 Time: " + appointmentTime);
                System.out.println("💆 Treatment: " + appointment.getTreatment().getName());
                System.out.println("💲 Price: $" + appointment.getPrice());
                System.out.println("📌 Status: " + status);
                if (!appointment.isConfirmed())
                    System.out.println("Please remind the customer to confirm their appointment.");
            }
        }
    }
    
    private int getValidNumber(int min, int max) {
        while (true) {
            try {
                if (!scanner.hasNextInt()) {  
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next();  
                    continue;
                }
                int number = scanner.nextInt();
                scanner.nextLine(); 
                if (number >= min && number <= max) return number;
                System.out.println("Invalid choice. Enter a number between " + min + " and " + max + ".");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }
    
    public void addToWaitingList(Customer customer) {
        if (!validateNotNull(customer, "Error: Customer cannot be null.")) return;
        waitingList.add(customer);
        System.out.println( customer.getFirstName() + " added to the waiting list.");
    }
    
    public boolean hasWaitingCustomers() {
        return !waitingList.isEmpty();
    }
    
    public void notifyNextCustomer() {
        if (waitingList.isEmpty()) {
            System.out.println("No customers in the waiting list.");
            return;
        }
        Customer nextCustomer = waitingList.poll(); // לוקחים את הראשונה בתור ומסירים אותה מהרשימה
        System.out.println("Notifying " + nextCustomer.getFirstName() + " about an available slot!");
        
        System.out.println("Sending SMS: 'Hello " + nextCustomer.getFirstName() + ", a slot just opened up! Call us to book it.'");
    }

    public static void closeScanner() { scanner.close(); }
}

