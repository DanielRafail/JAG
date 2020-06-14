DROP DATABASE IF EXISTS emails;
CREATE DATABASE emails;


DROP USER IF EXISTS daniel;
CREATE USER daniel IDENTIFIED BY 'password';
GRANT ALL ON emails.* to daniel;


use emails;
drop procedure if exists deleteEmail;
drop procedure if exists deleteFolders;
drop procedure if exists deleteReceivers;
Delimiter //

-- procedure to add Email
create procedure emails.deleteEmail(in id int)
begin
    delete emails.email from emails.email where email.email_id = id;
end//

-- procedure to add folders
create procedure emails.deleteFolders
(in name varchar(255)) 
begin
    Delete emails.folders from emails.folders where folders.name = name; 
end//

-- procedure to add folders
create procedure emails.deleteReceivers
(in type varchar(4), in email varchar(255)) 
begin
    Delete emails.receive from emails.receive where receiveType = type and receiveEmail = email; 
end//

Delimiter ;

drop function if exists findEmail;
drop function if exists findReceiver;
drop function if exists findAttachments;
drop function if exists findFolders;
drop function if exists findBridge;

delimiter //

-- function to find Email
create  function findEmail(id int)
returns char(1) DETERMINISTIC
begin
    declare countValue int;
        select count(*) into countValue from (select email_id from emails.email where 
        email_id = id) searcher;
    if(countValue > 0) then
        return '1';
    else 
        return '0';
    end if;
end//

-- function to find receiver
create function emails.findReceiver(type varchar(5), email varchar(255)) 
returns char(1) DETERMINISTIC
begin
    declare countValue int;
    select count(*) into countValue from (select * from emails.receive where 
    receiveType = type and receiveEmail = email) searcher;
    if (countValue > 0) then
        return '1';
    else 
        return '0';
    end if;
end//

-- function to find attachments
create function emails.findAttachments(type varchar(10), name varchar(50), value blob) 
returns char(1) deterministic
begin
    declare countValue int;
    declare id int;
    SELECT email_id into id FROM email ORDER BY email_id DESC LIMIT 1;
    select count(*) into countValue from (select * from emails.attachment where 
    attachmentType = type and attachmentValue = value and attachmentName = name and email_id = id) searcher;
    if (countValue > 0) then
        return '1';
    else 
        return '0';
    end if;
end//

-- function to find folders
create function emails.findFolders(name varchar(50)) 
returns char(1) deterministic
begin
    declare countValue int;
    select count(*) into countValue from (select * from emails.folders 
    where folders.name = name) searcher;
    if  (countValue > 0) then
        return '1';
    else 
        return '0';
    end if;
end//

-- function to find items from bridging table
create function emails.findBridge(id int,type varchar(3), email varchar(255))
returns char(1) deterministic
begin
    declare countValue int;
    declare r_id int;
    select receive_id into r_id from emails.receive where receiveType = type and receiveEmail = email;
    select count(*) into countValue from (select * from emails.bridge where receive_id = r_id and email_id = id) receiveAlias;
    if (countValue > 0) then
        return '1';
    else 
        return '0';
    end if;
end//

delimiter;

-- drop procedures if they exist
drop function if exists addEmail;
drop procedure if exists addReceiver;
drop procedure if exists addAttachments;
drop procedure if exists addFolders;
drop procedure if exists addEmailReceive;
drop procedure if exists addBridge;
drop function if exists getFolder;

Delimiter //

-- function to add Email
create function emails.addEmail(subject varchar(255),text varchar(5000), html varchar(5000), dateSent timestamp, dateReceived timestamp, priority int, isread char(1), folder varchar(50), newSender varchar(100))
returns int deterministic
begin
    declare varFolder_id int;
    declare returnID int;
    select folders_id into varFolder_id from emails.folders where name = folder;
    INSERT INTO emails.email (subject, textMsg, htmlMsg, dateSent ,dateReceived ,priority, isread, folders_id, sender)  
    VALUES (subject, text, html, dateSent, dateReceived, priority, isread, varFolder_id, newSender); 
    select email_id into returnID from email order by email_id desc limit 1;
    return returnID;
end//

-- procedure to addreceiver
create procedure emails.addReceiver(in type varchar(4), in email varchar(255) ,in name varchar(255)) 
begin
    declare id int;
    SELECT email_id into id FROM email ORDER BY email_id DESC LIMIT 1;
    INSERT INTO emails.receive (receiveType, receiveEmail, receiveName) values (type, email, name); 
end//

-- procedure to add attachments
create procedure emails.addAttachments(in type varchar(10), in name varchar(255), in value blob) 
begin
    declare id int;
    SELECT email_id into id FROM email ORDER BY email_id DESC LIMIT 1;
    INSERT INTO emails.attachment values (type, name, value, id); 
end//

-- procedure to add folders
create procedure emails.addFolders
(in name varchar(255)) 
begin
    INSERT INTO emails.folders (name) values (name); 
end //

-- change receivedDate
create procedure emails.addEmailReceive(in subject varchar(255), in text varchar(5000), in html varchar(5000), in dateSent timestamp, in dateReceived timestamp, in priority int, in isread char(1), in folder varchar(50))
begin
 declare varFolder_id int;
    select folders_id into varFolder_id from emails.folders where name = folder;
    INSERT INTO emails.email (subject, textMsg, htmlMsg, dateSent ,dateReceived ,priority, isread, folders_id)  
    VALUES (subject, text, html, dateSent, dateReceived, priority, isread, varFolder_id); 
end//

create procedure emails.addBridge(in id int,in type varchar(4), in email varchar(255))
begin
    declare varFolder_id int;
    declare r_id int;
    select receive_id into r_id from emails.receive where receiveType = type and receiveEmail = email;
    Insert into emails.bridge values (r_id, id);
end//


-- function to get folders
create function emails.getFolders(folderName varchar(50)) 
returns int deterministic
begin
    declare varFolder_id int;
    select folders_id into varFolder_id from folders where name = folderName;
    return varFolder_id;
end//

Delimiter ;