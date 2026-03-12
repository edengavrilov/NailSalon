package ThePerfectQueue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Manager extends User {
    private List<Customer> customers;
    private List<Provider> providers;
    private double revenue;

    public Manager(userType userType, String userName, String password, String firstName, String lastName, String phoneNum, String email, LocalDate birthDate) {
        super(userType, userName, password, firstName, lastName, phoneNum, email, birthDate);
        this.customers = new ArrayList<>();
        this.providers = new ArrayList<>();
        this.revenue = 0.0;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    @Override
    public void extendedRegistration() {
        System.out.println("Admin registration completed successfully!");
    }
    
    public void addCustomer(Customer customer) {
        if (!validateNotNull(customer, "Error: Customer cannot be null.\n") || customers.contains(customer)) {
            System.out.println("Customer already exists.\n");
            return;
        }
        customers.add(customer);
        System.out.println("Customer added successfully.\n");
    }
    
    public void addProvider(Provider provider) {
        if (!validateNotNull(provider, "Error: Provider cannot be null.") || providers.contains(provider)) {
            System.out.println("Provider already exists.\n");
            return;
        }
        providers.add(provider);
        System.out.println("Provider added successfully.\n");
    }

    public void removeProvider(Provider provider) {
        if (!validateNotNull(provider, "Error: Provider cannot be null.\n") || !providers.contains(provider)) {
            System.out.println("Provider not found.\n");
            return;
        }
        providers.remove(provider);
        System.out.println("Provider removed successfully.\n");
    }
    
    public void monthlyAppointments(Month month)
    {
    	System.out.println("\nMonthly Report for " + month + ":");
        int totalAppointments = 0;
        double totalIncome = 0;
        
        for (Provider provider : providers) {
            for (LocalDateTime appointmentTime : provider.getBookedAppointments()) {
                if (appointmentTime.getMonth() == month) {
                    Appointment appointment = provider.findAppointmentByTime(appointmentTime);
                    if (appointment != null) {
                        totalAppointments++;
                        totalIncome += appointment.getPrice();
                    }
                }
            }
        }
        
        System.out.println("/nTotal Appointments: " + totalAppointments);
        System.out.println("/nTotal Revenue: $" + totalIncome);
    }
    
    public void displayCustomerHistory(Customer customer) {
        if (!validateNotNull(customer, "Error: Customer cannot be null.") || customer.getAppointments().isEmpty()) {
            System.out.println("No appointment history found.");
            return;
        }

        System.out.println("\nAppointment History for " + customer.getFirstName() + " " + customer.getLastName() + ":");
        for (Appointment appointment : customer.getAppointments()) {
            System.out.println(appointment);
        }
    }
    
    public void providerHistory(Provider provider)
    {
    	 if (!validateNotNull(provider, "Error: Provider cannot be null.") || provider.getBookedAppointments().isEmpty()) {
             System.out.println("No appointment history found.");
             return;
         }
    	 
         System.out.println("\nAppointment History for Provider: " + provider.getFirstName() + " " + provider.getLastName());
         for (LocalDateTime appointmentTime : provider.getBookedAppointments()) {
             Appointment appointment = provider.findAppointmentByTime(appointmentTime);
             if (appointment != null) {
                 System.out.println(appointment);
             }
         }
    }
}
