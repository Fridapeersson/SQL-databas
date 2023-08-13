package org.example.view;

import org.example.controller.AccountManager;
import org.example.controller.LoginManager;
import org.example.controller.TransactionManager;
import org.example.controller.UserManager;
import org.example.model.Accounts;
import org.example.model.Transactions;
import org.example.model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static org.example.Main.*;
import static org.example.controller.TransactionManager.handleSendMoney;
import static org.example.controller.UserManager.handleUpdateDetails;
import static org.example.model.Users.getUserIdBySocialSecurityNumber;

public class viewManager {

    public static void startMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        while (choice != 1 && choice != 2) {
            System.out.println("Välkommen! Välj följande alternativ");
            System.out.println("1. Logga in");
            System.out.println("2. Skapa konto");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ogiltig inmatning, försök igen!");
                continue;
            }
        }

//        LoginManager login = new LoginManager();
//        Scanner scanner = new Scanner(System.in);

        if (choice == 1) {
            handleLogin();
        } else if (choice == 2) {
            handleCreateAccount();
            choice = 0;
        } else {
            System.out.println("Ogiltligt val, prova igen!");
        }
    }


    public static void handleMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        TransactionManager transactionManager = new TransactionManager();

        while(choice != 8) {
            System.out.println("Välj ett alternativ");
            System.out.println("1. Hantera swosh konto");
            System.out.println("2. Radera konto");
            System.out.println("3. Skicka pengar");
            System.out.println("4. Uppdatera användar uppgifter");
            System.out.println("5. Se mina skickade transaktioner");
            System.out.println("6. Se mina mottagna transaktioner");
            System.out.println("7. Visa mina Swosh-konto");
            System.out.println("8. Logga ut");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ogiltlig inmatning, prova igen!");
                continue;
            }


            switch(choice) {
                case 1:
                    handleSwoshAccountMenu();
                    break;
                case 2:
                    handleRemoveSwoshAccount();
                    break;
                case 3:
                    handleSendMoney();
                    break;
                case 4:
                    handleUpdateDetails();
                    break;
                case 5:
                    handleSentTransactions(transactionManager, loggedInUserId);
                    break;
                case 6:
                    handleReceivedTransactions(transactionManager, loggedInUserId);
                    break;
                case 7:
                    handleAccountView();
                    break;
                case 8:
                    System.out.println("Du är nu utloggad, välkommen åter!");
                    System.out.println("_____________________");
                    loggedInUserId = -1;
                    startMenu();
                    break;
                default:
                    System.out.println("Ogiltligt val, prova igen!");
            }
        }
    }


    public static void handleLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ange personnummer: ");
        String social_security_number = scanner.nextLine();

        //Om användaren skriver in bokstäver
        try{
            Long.parseLong(social_security_number);
        } catch(NumberFormatException e) {
            System.out.println("Ange endast siffror");
            handleLogin();
            return;
        }

        System.out.print("Ange lösenord: ");
        String password = scanner.nextLine();

        //
        boolean loggedIn = LoginManager.login(social_security_number, password);

        if (loggedIn) {
            System.out.println("Inloggning lyckades!");

            int userId = getUserIdBySocialSecurityNumber(social_security_number);

            if(userId != -1 ) {
                loggedInUserId = userId;
                System.out.println("Användarens id: " + userId);
                //anropar menyval om inloggning lyckades
                handleMenu();
            } else {
                System.out.println("Kunde inte hitta användar id");
            }
        } else {
            System.out.println("Felaktigt personnummer eller lösenord.");

            System.out.println("Vill du försöka igen? (ja eller nej)");
            String answer = scanner.nextLine();

            if(answer.equalsIgnoreCase("ja")){
                handleLogin();
            } else {
                System.out.println("Programmet avslutas");
            }
        }
    }


    public static void handleCreateAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Skriv in ditt förnamn: ");
        String first_name = scanner.nextLine();

        System.out.print("Skriv in ditt efternamn: ");
        String last_name = scanner.nextLine();

        System.out.print("Skriv in ditt personnummer: ");
        long social_security_number = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                social_security_number = Long.parseLong(scanner.nextLine());
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt personnummer, prova igen och ange endast siffror");
                System.out.print("Skriv in ditt personnummer: ");
            }
        }

        System.out.print("Skriv in din epost: ");
        String email = scanner.nextLine();

        System.out.print("Skriv in ditt telefonnummer: ");
        long phone_number = 0;
        validInput = false;

        while (!validInput) {
            try {
                phone_number = Long.parseLong(scanner.nextLine());
                String phoneNumberString = Long.toString(phone_number);
                if (phoneNumberString.length() != 10) {
                    throw new NumberFormatException();
                }
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt telefonnummer, prova igen och ange endast 10-siffrigt telefonnummer");
                System.out.print("Skriv in ditt telefonnummer: ");
            }
        }

        System.out.print("Skriv in ett lösenord: ");
        String password = scanner.nextLine();

        UserManager userManager = new UserManager();
        Users user = new Users(first_name, last_name, social_security_number, email, phone_number, password);

        userManager.addUser(user);
    }

    public static void handleAccountView() {
        UserManager userManager = new UserManager();
        AccountManager accountManager = new AccountManager();

        List<Accounts> userAccounts = accountManager.getAccountByUserId(loggedInUserId);

        if(userAccounts.isEmpty()) {
            System.out.println("Du har inga swosh konto ännu");
        } else {
            System.out.println("Du har följande swosh konton: ");


            for(Accounts acc : userAccounts) {
                System.out.println("Kontonummer: " + acc.getAccount_number());
                System.out.println("Saldo: " + acc.getBalance());
                System.out.println("_________________________");
            }
        }
    }


    public static void handleSwoshAccountMenu() {
        Scanner scanner = new Scanner(System.in);

        int choice = 0;
        while(choice != 3) {
            System.out.println("1. Lägg till ett swosh konto");
            System.out.println("2. Ta bort ett av dina swosh konto");
            System.out.println("3. Tillbaka till menyn");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch(choice) {
                    case 1:
                        handleAddAdditionalSwoshAccount();
                        System.out.println("Ett nytt konto har lagts till");
                        break;
                    case 2:
                        handleDeleteASpecificAccount();
                        break;
                    case 3:
                        handleMenu();
                    default:
                        System.out.println("Ogiltligt val, prova igen");

                }
            } catch(NumberFormatException e) {
                System.out.println("Ogiltlig inmatning, prova igen");
            }
        }
    }


    private static void handleAddAdditionalSwoshAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Välj belopp att sätta in: ");
        String initialBalance = String.valueOf(scanner.nextDouble());

        //Hömta användarens befintliga uppgifter
        AccountManager accountManager = new AccountManager();
        List<Accounts> userAccounts = accountManager.getAccountByUserId(loggedInUserId);

        if(loggedInUserId != -1) {
            Random random = new Random();
            long accountNumber = random.nextInt(900000000) + 1000000000;

            Accounts newAccount = new Accounts(loggedInUserId, Double.parseDouble(initialBalance));
            //random kontonummer
            newAccount.setAccount_number((accountNumber));

            //Sparar kontot till databsen
            newAccount.save();
        } else {
            System.out.println("Ingen användare inloggad");
        }
    }


    public static void handleDeleteASpecificAccount() {
        AccountManager accountManager = new AccountManager();
        List<Accounts> userAccounts = accountManager.getAccountByUserId(loggedInUserId);

        if (userAccounts.isEmpty()) {
            System.out.println("Du har inga konton att ta bort.");
            return;
        }

        System.out.println("Välj ett konto att ta bort:");

        int choice = 1;
        for (Accounts account : userAccounts) {
            System.out.println(choice + ". Ta bort konto " + choice);
            choice++;
        }

        Scanner scanner = new Scanner(System.in);
        int selectedAccount = scanner.nextInt();

        if (selectedAccount < 0 || selectedAccount > userAccounts.size()) {
            System.out.println("Ogiltigt val. Vänligen försök igen.");
            return;
        }

        Accounts accountToRemove = userAccounts.get(selectedAccount - 1);
        accountManager.removeAccountByNumber(loggedInUserId, accountToRemove.getAccount_number());
    }


    public static void handleRemoveSwoshAccount() {
        Scanner scanner = new Scanner(System.in);

        // Kontrollera om användaren är inloggad
        if (loggedInUserId == 0) {
            System.out.println("Ingen användare är för närvarande inloggad.");
            return;
        }

        // Kontrollera om användaren vill ta bort kontot
        System.out.println("Är du säker på att du vill radera ditt konto? (ja/nej)");
        String answer = scanner.nextLine();

        if (answer.equalsIgnoreCase("ja")) {
            try (Connection conn = getConnection()) {
                // Ta bort transaktioner för användaren
                String deleteTransactionsQuery = "DELETE FROM transactions WHERE user_id IN (SELECT id FROM accounts WHERE user_id = ?)";
                PreparedStatement prepStatement1 = conn.prepareStatement(deleteTransactionsQuery);
                prepStatement1.setInt(1, loggedInUserId);
                prepStatement1.executeUpdate();

                // Ta bort konton för användaren
                String deleteAccountsQuery = "DELETE FROM accounts WHERE user_id = ?";
                PreparedStatement prepStatement2 = conn.prepareStatement(deleteAccountsQuery);
                prepStatement2.setInt(1, loggedInUserId);
                prepStatement2.executeUpdate();

                // Ta bort användaren
                String deleteUserQuery = "DELETE FROM users WHERE id = ?";
                PreparedStatement prepStatement3 = conn.prepareStatement(deleteUserQuery);
                prepStatement3.setInt(1, loggedInUserId);
                prepStatement3.executeUpdate();

                System.out.println("Användaren med id " + loggedInUserId + " och all tillhörande information har tagits bort från databasen.");
                startMenu();
            } catch (SQLException e) {
                System.out.println("Ett fel uppstod vid borttagning av användaren från databasen: " + e.getMessage());
            }
        } else {
            System.out.println("Borttagning avbruten. Ditt konto har inte raderats.");
        }
    }


    public static void handleSentTransactions(TransactionManager transactionManager, int loggedInUserId) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;

        System.out.println("Ange startdatum (YYYY-MM-DD): ");
        String start = scanner.nextLine();
        try {
            startDate = dateFormat.parse(start);
        } catch (ParseException e) {
            System.out.println("Ogiltigt datumformat!");
            return;
        }

        System.out.println("Ange slutdatum (YYYY-MM-DD): ");
        String end = scanner.nextLine();
        try {
            endDate = dateFormat.parse(end);
        } catch (ParseException e) {
            System.out.println("Ogiltigt datumformat!");
            return;
        }

        List<Transactions> transactions = transactionManager.getTransactionsByUserIdAndDateRange(loggedInUserId, startDate, endDate);
        System.out.println(loggedInUserId);

        if (transactions.isEmpty()) {
            System.out.println("Du har inga transaktioner mellan de angivna datumen.");
        } else {
            System.out.println("Här är dina transaktioner mellan " + start + " och " + end + ":");

            for (Transactions transaction : transactions) {
                String formattedDate = dateFormat.format(transaction.getCreated());
                String transactionType = transaction.getTransactionType();
                System.out.println("Du " + transactionType + " den " + formattedDate);
                System.out.println("Beloppet: " + transaction.getAmount());
                System.out.println("______________________________________");
            }
        }
    }


    public static void handleReceivedTransactions(TransactionManager transactionManager, int loggedInUserId) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;

        System.out.println("Ange startdatum (YYYY-MM-DD): ");
        String start = scanner.nextLine();
        try {
            startDate = dateFormat.parse(start);
        } catch (ParseException e) {
            System.out.println("Ogiltigt datumformat!");
            return;
        }

        System.out.println("Ange slutdatum (YYYY-MM-DD): ");
        String end = scanner.nextLine();
        try {
            endDate = dateFormat.parse(end);
        } catch (ParseException e) {
            System.out.println("Ogiltigt datumformat!");
            return;
        }

        List<Transactions> transactions = transactionManager.getTransactionsByUserIdAndDateRangeReceived(loggedInUserId, startDate, endDate);

        if (transactions.isEmpty()) {
            System.out.println("Du har inga mottagna transaktioner mellan de angivna datumen.");
        } else {
            System.out.println("Här är dina mottagna transaktioner mellan " + start + " och " + end + ":");

            for (Transactions transaction : transactions) {
                String formattedDate = dateFormat.format(transaction.getCreated());
                System.out.println("Mottog från användar-ID: " + transaction.getUser_id());
                System.out.println("Belopp: " + transaction.getAmount());
                System.out.println("Skapad datum: " + formattedDate);
                System.out.println("-----------------------");
            }
        }
    }

}
