# Northstar Banking Demo

A small banking demo with a browser dashboard and a separate Java command-line application. The dashboard stores its demo accounts and ledger in browser local storage; it is not connected to a real banking service.

## Run the dashboard

Requirements: Node.js 18 or later.

```sh
npm run dev
```

Open the local URL printed by the command (by default, <http://127.0.0.1:5173>). Set the `PORT` environment variable to use a different port.

The dashboard supports account creation, deposits, withdrawals, transfers between accounts, balance summaries, and activity filtering. Demo data and changes persist in the current browser.

## Run the Java CLI

Requirements: JDK 17 or later.

```sh
javac BankingManagement.java
java BankingManagement
```

The Java CLI is an independent in-memory application and does not share data with the browser dashboard.

## Note

This project is for demonstration and learning only. It has no authentication, real backend, or production banking security.

## Author

Manasavarshini L.D.
