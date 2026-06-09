# Dynamic Table Encoder

This is a Java implementation of a custom substitution cipher. It takes a plaintext string and encodes it into a new string using a dynamic shift table.

## How It Works

The encoder relies on a specific 44-character reference table:
`ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789()*+,-./`

* **The Offset:** You choose any character from the reference table to be the offset. This choice decides how far the table shifts.
* **The Shift:** The table shifts "down" based on the position of your chosen offset character. 
* **The Prefix:** The very first character of the final encoded message will always be your offset character. The decoder uses this to know how to reverse the process.
* **Unmapped Characters:** Any character not listed in the 44-character table (such as spaces or lowercase letters) is ignored by the shift and mapped back to itself.

## Examples

Given the plaintext **"HELLO WORLD"**:

* If the offset character is **B** (shifts the table by 1): The encoded string is `BGDKKN VNQKC`.
* If the offset character is **F** (shifts the table by 5): The encoded string is `FC/GGJ RJMG.`.

## Code Structure and Usage

The solution is built with Object-Oriented Programming (OOP) concepts. It defines a `SubstitutionCipher` interface which is implemented by the `DynamicTableEncoder` class. 
