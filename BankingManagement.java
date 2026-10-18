import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

enum TransactionType {
    INITIAL, DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
}

class BankTransaction {
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;

    public BankTransaction(TransactionType type, BigDecimal amount, BigDecimal balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    @Override
    public String toString() {
        return String.format("Type: %-14s | Amount: %-8s | Balance: %s", type, amount, balanceAfter);
    }
}

class BankAccount {
    private String accountNumber;
    private String accountHolder;
    private BigDecimal balance;
    private ArrayList<BankTransaction> transactionHistory;

    public BankAccount(String accountNumber, String accountHolder, BigDecimal initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialDeposit.setScale(2, RoundingMode.UNNECESSARY);
        this.transactionHistory = new ArrayList<>();
        logTransaction(TransactionType.INITIAL, initialDeposit);
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public BigDecimal getBalance() { return balance; }
    public ArrayList<BankTransaction> getTransactionHistory() { return transactionHistory; }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
        logTransaction(TransactionType.DEPOSIT, amount);
    }

    public boolean withdraw(BigDecimal amount) {
        if (amount.compareTo(balance) > 0) return false;
        balance = balance.subtract(amount);
        logTransaction(TransactionType.WITHDRAWAL, amount);
        return true;
    }
    
    public void depositTransfer(BigDecimal amount) {
        balance = balance.add(amount);
        logTransaction(TransactionType.TRANSFER_IN, amount);
    }

    public boolean withdrawTransfer(BigDecimal amount) {
        if (amount.compareTo(balance) > 0) return false;
        balance = balance.subtract(amount);
        logTransaction(TransactionType.TRANSFER_OUT, amount);
        return true;
    }

    private void logTransaction(TransactionType type, BigDecimal amount) {
        transactionHistory.add(new BankTransaction(type, amount, this.balance));
    }

    @Override
    public String toString() {
        return String.format("Acc No: %-8s | Holder: %-15s | Balance: %s", accountNumber, accountHolder, balance);
    }
}

public class BankingManagement {
    private static final Map<String, BankAccount> accountMap = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    // Dynamic getter for external web resource controllers to connect into later
    public static Map<String, BankAccount> getAccountMap() {
        return accountMap;
    }

    public static void main(String[] args) {
        accountMap.put("1001", new BankAccount("1001", "Deiviekan", new BigDecimal("5000.00")));
        accountMap.put("1002", new BankAccount("1002", "Alex Morgan", new BigDecimal("2500.50")));

        while (true) {
            System.out.println("\n=== SECURE ONLINE BANKING SYSTEM ===");
            System.out.println("1. Create New Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Funds");
            System.out.println("5. View Account Statement");
            System.out.println("6. Display All Accounts");
            System.out.println("7. Exit");
            System.out.print("Enter choice (1-7): ");

            int choice = getValidIntegerInput();

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    performDeposit();
                    break;
                case 3:
                    performWithdrawal();
                    break;
                case 4:
                    performTransfer();
                    break;
                case 5:
                    viewStatement();
                    break;
                case 6:
                    displayAllAccounts();
                    break;
                case 7:
                    System.out.println("Safely exiting core backend service engine.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice dashboard range.");
                    break;
            }
        }
    }

    private static void createAccount() {
        System.out.print("Enter Unique Account Number: ");
        String accNum = scanner.nextLine().trim();
        if (accNum.isEmpty()) {
            System.out.println("Error: Account number cannot be empty.");
            return;
        }
        if (accountMap.containsKey(accNum)) {
            System.out.println("Error: Account exists.");
            return;
        }
        System.out.print("Enter Account Holder Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Error: Account holder name cannot be empty.");
            return;
        }
        System.out.print("Enter Initial Deposit Amount: ");
        BigDecimal initialAmount = getValidMoneyInput(true);

        accountMap.put(accNum, new BankAccount(accNum, name, initialAmount));
        System.out.println("Account created successfully!");
    }

    private static void performDeposit() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        BankAccount account = accountMap.get(accNum);
        if (account == null) { System.out.println("Account not found."); return; }

        System.out.print("Enter amount to deposit: ");
        BigDecimal amount = getValidMoneyInput(false);
        account.deposit(amount);
        System.out.println("Deposited. New Balance: " + account.getBalance());
    }

    private static void performWithdrawal() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        BankAccount account = accountMap.get(accNum);
        if (account == null) { System.out.println("Account not found."); return; }

        System.out.print("Enter amount to withdraw: ");
        BigDecimal amount = getValidMoneyInput(false);
        if (account.withdraw(amount)) {
            System.out.println("Withdrawal successful. New Balance: " + account.getBalance());
        } else {
            System.out.println("Error: Insufficient balance.");
        }
    }

    private static void performTransfer() {
        System.out.print("Enter Source Account Number: ");
        String srcAcc = scanner.nextLine().trim();
        BankAccount source = accountMap.get(srcAcc);
        if (source == null) { System.out.println("Source account not found."); return; }

        System.out.print("Enter Destination Account Number: ");
        String destAcc = scanner.nextLine().trim();
        BankAccount destination = accountMap.get(destAcc);
        if (destination == null) { System.out.println("Destination account not found."); return; }

        if (srcAcc.equalsIgnoreCase(destAcc)) {
            System.out.println("Error: Destination matches source.");
            return;
        }

        System.out.print("Enter amount to transfer: ");
        BigDecimal amount = getValidMoneyInput(false);

        if (source.withdrawTransfer(amount)) {
            destination.depositTransfer(amount);
            System.out.println("Transfer successful! " + amount + " moved.");
        } else {
            System.out.println("Transfer failed: Insufficient funds.");
        }
    }

    private static void viewStatement() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        BankAccount account = accountMap.get(accNum);
        if (account == null) { System.out.println("Account not found."); return; }

        System.out.println("\n--- Account Statement for " + account.getAccountHolder() + " ---");
        ArrayList<BankTransaction> history = account.getTransactionHistory();
        for (BankTransaction t : history) {
            System.out.println(t);
        }
        System.out.printf("Current Net Available Balance: %s%n", account.getBalance());
    }

    private static void displayAllAccounts() {
        if (accountMap.isEmpty()) { System.out.println("No records found."); return; }
        System.out.println("\n--- Active Bank Records ---");
        for (BankAccount acc : accountMap.values()) {
            System.out.println(acc);
        }
    }

    private static int getValidIntegerInput() {
        while (true) {
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("Enter an integer value: "); }
        }
    }

    private static BigDecimal getValidMoneyInput(boolean allowZero) {
        while (true) {
            try {
                BigDecimal value = new BigDecimal(scanner.nextLine().trim()).setScale(2, RoundingMode.UNNECESSARY);
                if (value.signum() < 0 || (!allowZero && value.signum() == 0)) {
                    System.out.print(allowZero ? "Enter a non-negative value: " : "Enter a value greater than zero: ");
                    continue;
                }
                return value;
            } catch (NumberFormatException | ArithmeticException e) {
                System.out.print("Enter a valid amount with at most two decimal places: ");
            }
        }
    }
}