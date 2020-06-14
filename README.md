# JAG

## How the program works: 

##### Contains 5 main windows : 

## User Explination : 

When you start the program for the first time you will be invited to a screen asking for all the information related to the program. Most of the default values have already been entered for your ease of use, but you may change them as you wish. So far, only one user exists under my name (daniel) and his password is "password". I do not use the information written in this program for any ill intent and I am warning you off the bat that the password is not encrypted under a hash due to the limited amount of time we had to this assignment. Moving onward, everytime you attempt to open the program it will only ask you for your email address and password so that it may authenticate you. 

In either cases, after having logged in (which may take a while as our framework to read emails works somewhat slowly), you will be invited to the hub of the program where you can send emails, read your emails, interact with your folders, create some, delete them, etc.. You can also drag and drop emails from folder to folder, but to do so you must release the mouse on top of the folder's name rather than it's location. 

By double clicking on an email you will enter a page that allows for you to read it and offers options such as replying, forwarding. By clicking on an attachment you may download it. 

You may send an email by clicking the send email button. It will open a new window in which you may enter the email information, add folders, etc...

The refresh buttons will attempt to read any new emails and display them by updating the UI.

You may change languages by clicking and chosing between french and english on the menu bar.

## Coding explination : 

### EmailConfig
1. This window is the first to open when the program is installed. It will ask the user to input all the values needed to make the program work. It will ask for an email address, database information, sql information, and imap and smtp information. This window may also be opened from the EmailReader window in order to change the settings mid-program.

### EmailAuthen
2. This window will be the first to open if EmailConfig exists. It's only purpose is to ask the user for his email and password so that we may authenticate him and allow him to access the main utility of the program

### EmailReader
3. This window is the main hub of the program. It offers many utilities. The user may call EmailSender to write an email, he may create a folder, refresh the page (reverify the emails in the user's account, and apply database changes to the UI), and change the configuration files. Else the user may see all the folders he has created, plus the two defaults (inbox and sent), and access his emails. By double clicking on an email, the user may open EmailReceived and see the email specifics (attachments, text, cc, etc...) and have the option to either forward or reply to the email. The user may also right click on the folders to rename or delete them, or right click on a email to delete it. The user cannot delete the Inbox and Sent folders as they are the defaults. The user may also drag an email to a different folder to change it's location

### EmailReceived
4. This window is called from the EmailReader window and allows a user to read an email in-depth. It also allows the user to either reply to an email, or forward one.

### EmailSender
5. This window is called from the EmailReader window and allows the user to send an email. He may add cc, bcc, receivers, a subject, attachments, but may only write his text in plain text or html (based on a radio box). The email may only be sent if the bare minimal details to send an email are filled (at least one receiver for instance)

___

# Bugs
1. If one were to send himself an email from gmail using his alias rather than email address, then go on JAG and click "Reply All", his email would be displayed, followed by his name (without spaces) and empty <> symboles.  