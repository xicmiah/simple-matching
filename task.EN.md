# Task description

Write a program for simple order matching on an exchange. The exchange trades four securities ("A", "B", "C" and "D"). The base currency of the exchange is the dollar ("$").

The input data is contained in two files. The first file `clients.txt` contains a list of the clients of the exchange with their initial balances for traded securities and the dollar.
The second file, `orders.txt`, is a list of orders from clients in chronological order.
The result of the program is the file `result.txt`, similar in structure to the file` clients.txt` and containing all client balances after processing all orders.

## File format descriptions

The structure of the files is extremely simple. Each line of the text file contains one entry. The record fields are separated from each other by a tab (\t).
Customer names and security names are strings consisting of alphabetic and numeric ASCII characters without delimiters. Numeric values ​​are represented by integers.

### File `clients.txt`

List of clients has the following fields:
 * Customer name
 * Customer's dollar balance
 * Customer's balance on security "A" in units
 * Balance on security "B"
 * Balance on security "C"
 * Balance on security "D"

An example of several lines of a file:

```
C1  1000    10  5   15  0
C2  2000    3   35  40  10
```

### File `orders.txt`

List of orders has the following format:

 * Name of the customer who submitted the application
 * Operation: "s" - sale or "b" - purchase.
 * Security name
 * Bid/ask price (for one unit)
 * Quantity of securities to be sold or purchased
 
An example of several lines of a file:

```
C1  b   A   7   12
C2  s   A   8   10
```

## Notes

 1. Partial order matching is not necessary. For simplicity, you can match orders only by a complete match of price and quantity.
 1. You do not need to handle the situation of selling and buying from yourself.
 1. Account balances can be processed without transactions.
 1. For simplicity's sake, it's not necessary to handle negative balances.

## Expected results

 * The main result is the file with the final state of client balances after processing the provided data
 * Source code of the project on Github
 * Unit test suite
