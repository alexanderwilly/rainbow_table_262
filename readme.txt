Alexander Willy Johan - A1 - 7907795 - 262 - T06

~~ Creating Rainbow Table ~~
This program is the implementation of Rainbow Table. 
The user enters a hash value into the program, and the system performs a rainbow table to find the corresponding pre-image.
The rainbow table is generated from a given text file that contains a list of possible passwords.

The program read in the list of possible passwords.
Then, Apply the hash function to the password to produce a hash value, which is referred to as the current hash, and then store it in the hash list.
Then, apply the reduction function to the current hash using the following formula:

r = MD5(password) mod sizeOfFile
where:
- MD5(password) is the produced hash value in hexadecimal and then converted into BigInteger
- sizeOfFile is the number of possible passwords stored in the given text file.
Therefore, the value of r is between 0 (inclusive) to the sizeOfFile (exclusive).

Then, the value of r is used to find the next possible password which will be marked as used and then hashed. Repeat the previous step 5 more times. 
In the end, the hash list will contain 6 of the password-hash value pairs.

Then, get the first password and the last hash value from the hash list, and then store it in the rainbow table.
Repeat the steps from applying the hash and reduction function for other every unused password in the given password text file.








~~ Applying Rainbow Table ~~
When the rainbow table has been generated, pre-images of the password may be found.
The system requests a hash value from the user. The hash value requested is in MD5 hash, which produces a 32 character in hexadecimal.

Then, check whether the user input hash value is found in the rainbow table.
If not, keep reducing and hash until the hash value is found in the rainbow table, or until all possible password is covered. 

Once the relevant hash value is found, 
Take the corresponding password of the relevant hash in the rainbow table, then hash the corresponding password to obtain the corresponding hash value. Check whether the corresponding hash value equals the user input hash value. If not, repeat the process by reducing the corresponding hash value, get the password from the reduction function, and hash it, then check whether both hash values are the same, until both match or until the system tries all possible passwords in the password list.






~~ How to run ~~
The program is runnable using IDE such as IntelliJ, or using command prompt or Windows Powershell
a) Using IntelliJ
1. Create new project and apply the relevant settings, including the jdk (using jdk 1.8)
2. Paste the Rainbow.java file inside src folder (make src folder if not exists)
3. Paste the Passwords.txt in the same directory as ".idea", "lib", "out", and "src" folder (not inside src)
4. Run the Rainbow.java program using the IDE
	- On the top left, find "Run"
	- Press "Run Rainbow.java" or "Run"
5. Once it runs, it displays the number of password/words read in and number of passwords in rainbow table 
to show that the program has successfully created the rainbow table.
6. Then the program will ask to enter the password hash
7. After password hash is entered, the program will find the pre image using the rainbow table

b) Using command prompt / powershell
1. Run command prompt / powershell in the same directory of the "Rainbow.java" file
* Make sure that the Password.txt file is in the same directory with the Rainbow.java file
2. Execute the following command in the terminal:
	- javac Rainbow.java
	- java Rainbow.java
	or
	- javac Rainbow.java
	- java Rainbow
3. Once it runs, it displays the number of password/words read in and number of passwords in rainbow table 
to show that the program has successfully created the rainbow table.
4. Then the program will ask to enter the password hash
5. After password hash is entered, the program will find the pre image using the rainbow table
	