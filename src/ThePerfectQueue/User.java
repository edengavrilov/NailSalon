package ThePerfectQueue;

import java.time.LocalDate;
import java.util.Scanner;

public abstract class User {

    private userType userType;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNum;
    private String email;
    private LocalDate birthDate;
    private static final Scanner scanner = new Scanner(System.in);

    public User() {
        this.userType = null;
        this.userName = "";
        this.password = "";
        this.firstName = "";
        this.lastName = "";
        this.phoneNum = "";
        this.email = "";
        this.birthDate = null;
    }

    public User(userType userType, String userName, String password, String firstName, String lastName, String phoneNum, String email, LocalDate birthDate) {
        if (userType == null || userName.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
            lastName.isEmpty() || phoneNum.isEmpty() || email.isEmpty() || birthDate == null) {
            throw new IllegalArgumentException("All fields must be filled.");
        }
        setPhoneNum(phoneNum);
        setEmail(email);
        this.userType = userType;
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }
    
    public User(User other) {
        this.userType = other.userType;
        this.userName = other.userName;
        this.password = other.password;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.phoneNum = other.phoneNum;
        this.email = other.email;
        this.birthDate = other.birthDate;
    }
    
    public userType getUserType() {
        return userType;
    }

    public void setUserType(userType userType) {
        this.userType = userType;
    }
    
    public String getUserName() {   
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPhoneNum() { 
        return phoneNum;
    }
    
    public void setPhoneNum(String phoneNum) { 
        if (!isValidPhoneNumber(phoneNum)) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }
        this.phoneNum = phoneNum;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    protected static boolean validateNotNull(Object obj, String errorMessage) {
        if (obj == null) {
            System.out.println("❌ " + errorMessage);
            return false;
        }
        return true;
    }
    
    public boolean login(String userName, String password) {
        if (userName == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null.");
        }
        return this.userName.equals(userName) && this.password.equals(password);
    }
    
    public void basicRegistration(userType userType, String userName, String password, String firstName, String lastName, String phoneNum, String email, LocalDate birthDate) {
        if (userType == null || userName.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
            lastName.isEmpty() || phoneNum.isEmpty() || email.isEmpty() || birthDate == null) {
            throw new IllegalArgumentException("All fields must be filled.");
        }
        setPhoneNum(phoneNum);
        setEmail(email);
        this.userType = userType;
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }
    
    public abstract void extendedRegistration();
    
    public void updatePersonalDetails() {
        System.out.println("Update Personal Details:");
        
        System.out.print("Enter new first name (or press Enter to keep '" + firstName + "'): ");
        String newFirstName = scanner.nextLine().trim();
        if (!newFirstName.isEmpty()) {
            firstName = newFirstName;
        }

        System.out.print("Enter new last name (or press Enter to keep '" + lastName + "'): ");
        String newLastName = scanner.nextLine().trim();
        if (!newLastName.isEmpty()) {
            lastName = newLastName;
        }

        System.out.print("Enter new phone number (or press Enter to keep '" + phoneNum + "'): ");
        String newPhoneNum = scanner.nextLine().trim();
        if (!newPhoneNum.isEmpty()) {
            setPhoneNum(newPhoneNum);
        }

        System.out.print("Enter new email (or press Enter to keep '" + email + "'): ");
        String newEmail = scanner.nextLine().trim();
        if (!newEmail.isEmpty()) {
            setEmail(newEmail);
        }

        System.out.println("Personal details updated successfully!");
    }
    
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}"); 
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"); 
    }

    @Override
    public String toString() {
        return "User{" +
               "userType='" + userType + '\'' +
               ", userName='" + userName + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", phoneNum='" + phoneNum + '\'' +
               ", email='" + email + '\'' +
               ", birthDate=" + birthDate +
               '}';
    }
}
