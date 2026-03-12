package ThePerfectQueue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static List<User> users = new ArrayList<>();
    private static List<TreatmentType> availableTreatments = new ArrayList<>();

    public static void main(String[] args) {
    	initializeTreatments();

        while (true) {
            System.out.println("\nChoose an option:\n1) Register\n2) Log in\n3) Exit");
            int choice = getValidNumber(1, 3);

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> loginUser();
                case 3 -> {
                    System.out.println("Goodbye! Thanks for using 'The Perfect Queue'.");
                    return;
                }
            }
        }
    }

    private static void initializeTreatments() {
        availableTreatments.add(new TreatmentType("Manicure", SpecialtyType.MANICURE, 60, 50.0));
        availableTreatments.add(new TreatmentType("Pedicure", SpecialtyType.PEDICURE, 75, 60.0));
        availableTreatments.add(new TreatmentType("Hair Styling", SpecialtyType.HAIR_STYLING, 90, 100.0));
        availableTreatments.add(new TreatmentType("Makeup", SpecialtyType.MAKEUP, 45, 80.0));
        availableTreatments.add(new TreatmentType("Skin Care", SpecialtyType.SKIN_CARE, 90, 120.0));
        availableTreatments.add(new TreatmentType("Massage", SpecialtyType.MASSAGE, 60, 90.0));

        System.out.println("Welcome to The Perfect Queue!");
    }
    
    private static Appointment selectAppointment(Provider provider) {
        if (provider.getBookedAppointments().isEmpty()) {
            System.out.println("No appointments available.");
            return null;
        }

        System.out.println("\nSelect an appointment:");
        List<LocalDateTime> appointmentsList = new ArrayList<>(provider.getBookedAppointments());
        
        for (int i = 0; i < appointmentsList.size(); i++) {
            System.out.println((i + 1) + ") " + appointmentsList.get(i));
        }

        System.out.print("Enter the number of the appointment: ");
        int choice = getValidNumber(1, appointmentsList.size());

        return provider.findAppointmentByTime(appointmentsList.get(choice - 1));
    }

    private static int getValidNumber(int min, int max) {
        while (true) {
            try {
                int num = scanner.nextInt();
                scanner.nextLine();
                if (num >= min && num <= max) return num;
                System.out.println("Invalid choice. Enter a number between " + min + " and " + max);
            } catch (Exception e) {
                System.out.println("Invalid input. Enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private static void registerUser() {
        System.out.println("Choose user type:\n1) Customer\n2) Provider\n3) Manager");
        int userTypeChoice = getValidNumber(1, 3);
        userType selectedType = userType.fromCode(userTypeChoice);

        System.out.print("Enter username: ");
        String userName = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Enter phone number: ");
        String phoneNum = scanner.nextLine();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter birth date (YYYY-MM-DD): ");
        LocalDate birthDate = LocalDate.parse(scanner.nextLine());

        User newUser = switch (selectedType) {
            case CUSTOMER -> new Customer(selectedType, userName, password, firstName, lastName, phoneNum, email, birthDate);
            case PROVIDER -> new Provider(selectedType, userName, password, firstName, lastName, phoneNum, email, birthDate, null, 0, new DayOfWeek[]{}, null, null);
            case MANAGER -> new Manager(selectedType, userName, password, firstName, lastName, phoneNum, email, birthDate);
        };

        newUser.extendedRegistration();
        users.add(newUser);
        System.out.println("Registration successful! Welcome, " + newUser.getFirstName() + "!");
    }

    private static void loginUser() {
        System.out.print("Enter username: ");
        String userName = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.login(userName, password)) {
                System.out.println("Login successful! Welcome, " + user.getFirstName());
                showUserMenu(user);
                return;
            }
        }
        System.out.println("Login failed. Incorrect username or password.");
    }

    private static void showUserMenu(User user) {
        if (user instanceof Customer) {
            showCustomerMenu((Customer) user , availableTreatments);
        } else if (user instanceof Provider) {
            showProviderMenu((Provider) user);
        } else if (user instanceof Manager) {
            showManagerMenu((Manager) user);
        }
    }

    public static void showCustomerMenu(Customer customer, List<TreatmentType> availableTreatments) {
        while (true) {
            System.out.println("\n👤 Customer Menu:");
            System.out.println("1 - Schedule New Appointment");
            System.out.println("2 - Cancel Appointment");
            System.out.println("3 - View Upcoming Appointments");
            System.out.println("4 - View Appointment History");
            System.out.println("5 - Join Waiting List");
            System.out.println("6 - Update Personal Details");
            System.out.println("7 - Logout");

            int choice = getValidNumber(1, 7);
            switch (choice) {
                case 1 -> { 
                    Appointment newAppointment = Customer.createAppointment(customer, availableTreatments);
                    if (newAppointment != null) {
                        System.out.println("Your appointment has been successfully scheduled!");
                    }
                }
                case 2 -> { 
                    if (customer.getAppointments().isEmpty()) {
                        System.out.println("No appointments found.");
                        break;
                    }

                    System.out.println("\nSelect an appointment to cancel:");
                    for (int i = 0; i < customer.getAppointments().size(); i++) {
                        System.out.println((i + 1) + ") " + customer.getAppointments().get(i));
                    }

                    System.out.print("Enter the number of the appointment to cancel: ");
                    int appointmentChoice = getValidNumber(1, customer.getAppointments().size());
                    Appointment selectedAppointment = customer.getAppointments().get(appointmentChoice - 1);

                    customer.cancelAppointment(selectedAppointment);
                }
                case 3 -> { 
                    if (customer.getAppointments().isEmpty()) {
                        System.out.println("No upcoming appointments.");
                        break;
                    }
                    System.out.println("\nUpcoming Appointments:");
                    for (Appointment appointment : customer.getAppointments()) {
                        System.out.println(appointment);
                    }
                }
                case 4 -> { 
                    System.out.println("\nAppointment History:");
                    if (customer.getAppointments().isEmpty()) {
                        System.out.println("No past appointments.");
                    } else {
                        for (Appointment appointment : customer.getAppointments()) {
                            System.out.println(appointment);
                        }
                    }
                }
                case 5 -> { 
                    System.out.println("Select a provider to join their waiting list:");
                    Provider selectedProvider = customer.selectProviderForTreatment(null);
                    if (selectedProvider != null) {
                        customer.requestToJoinWaitingList(selectedProvider);
                    }
                }
                case 6 -> customer.updatePersonalDetails();   
                case 7 -> {
                    System.out.println("Logging out...");
                    return;
                }
            }
        }
    }

    private static void showProviderMenu(Provider provider) {
        while (true) {
            System.out.println("\n👩‍⚕️ Provider Menu:");
            System.out.println("1 - Display Schedule");
            System.out.println("2 - Update Availability");
            System.out.println("3 - Send Appointment Reminders");
            System.out.println("4 - Show Appointment History");
            System.out.println("5 - Cancel Appointment");
            System.out.println("6 - Confirm Appointment");
            System.out.println("7 - Request Appointment Cancellation");
            System.out.println("8 - Update Personal Details");
            System.out.println("9 - Logout");

            int choice = getValidNumber(1, 9);
            switch (choice) {
                case 1 -> provider.displaySchedule();
                case 2 -> provider.updateAvailability();
                case 3 -> provider.sendAppointmentReminders();
                case 4 -> provider.displayAppointments();
                case 5 -> {
                    Appointment selectedAppointment = selectAppointment(provider);
                    if (selectedAppointment != null) {
                        provider.cancelAppointment(selectedAppointment);
                    } else {
                        System.out.println("No appointment selected.");
                    }
                }
                case 6 -> {
                    Appointment selectedAppointment = selectAppointment(provider);
                    if (selectedAppointment != null) {
                        provider.confirmAppointment(selectedAppointment);
                    } else {
                        System.out.println("No appointment selected.");
                    }
                }
                case 7 -> {
                    Appointment selectedAppointment = selectAppointment(provider);
                    if (selectedAppointment != null) {
                        provider.requestCancellation(selectedAppointment);
                    } else {
                        System.out.println("No appointment selected.");
                    }
                }
                case 8 -> provider.updatePersonalDetails();
                case 9 -> {
                    System.out.println("Logging out...");
                    return;
                }
            }
        }
    }

    private static void showManagerMenu(Manager manager) {
        while (true) {
            System.out.println("\n👩‍💼 Manager Menu:");
            System.out.println("1 - View Monthly Reports");
            System.out.println("2 - View Customer History");
            System.out.println("3 - View Provider History");
            System.out.println("4 - Update Personal Details");
            System.out.println("5 - Logout");

            int choice = getValidNumber(1, 5);
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter month for report (1-12): ");
                    int month = getValidNumber(1, 12);
                    manager.monthlyAppointments(Month.of(month));
                }
                case 2 -> {
                    List<Customer> customers = manager.getCustomers();
                    if (customers.isEmpty()) {
                        System.out.println("No customers available.");
                        continue;  // חזרה לתפריט במקום יציאה
                    }

                    System.out.println("Select customer:");
                    for (int i = 0; i < customers.size(); i++) 
                        System.out.println((i + 1) + ") " + customers.get(i).getFirstName() + " " + customers.get(i).getLastName());

                    System.out.print("Enter the number of the customer: ");
                    int customerChoice = getValidNumber(1, customers.size());

                    Customer selectedCustomer = customers.get(customerChoice - 1);
                    manager.displayCustomerHistory(selectedCustomer);
                }
                case 3 -> {
                    List<Provider> providers = manager.getProviders();
                    if (providers.isEmpty()) {
                        System.out.println("No providers available.");
                        continue;  
                    }

                    System.out.println("Select provider:");
                    for (int i = 0; i < providers.size(); i++) 
                        System.out.println((i + 1) + ") " + providers.get(i).getFirstName() + " " + providers.get(i).getLastName());

                    System.out.print("Enter the number of the provider: ");
                    int providerChoice = getValidNumber(1, providers.size());

                    Provider selectedProvider = providers.get(providerChoice - 1);
                    manager.providerHistory(selectedProvider);
                }
                case 4 -> manager.updatePersonalDetails();
                case 5 -> {
                    System.out.println("Logging out...");
                    return;
                }
            }
        }
    }

}
