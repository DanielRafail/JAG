
use emails;


-- dropping the tables
drop table if exists emails.folders;
drop table if exists emails.email;
drop table if exists emails.receive;
drop table if exists emails.attachment;
drop table if exists bridge;

-- creating tables 
create table emails.folders(
name varchar(50) not null,
folders_id int AUTO_INCREMENT,
primary key (folders_id) 
);

create table email(
subject varchar(255),
textMsg varchar(5000), 
htmlMsg varchar(5000),
dateSent timestamp not null,
dateReceived timestamp,
priority int not null,
isread char(1) not null,
email_id int AUTO_INCREMENT primary key,
sender varchar(100) not null,
folders_id int references emails.folders(folders_id) on delete cascade
);


create Table receive(
receiveType varchar(4) not null,
receiveEmail varchar(255) not null,
receiveName varchar(255),
receive_id int AUTO_INCREMENT primary key
);

create table bridge(
receive_id int not null references emails.receive(receive_id),
email_id int not null references emails.email(email_id)
);

create Table attachment(
attachmentType varchar(10) not null,
attachmentName varchar(255) not null,
-- would of done not null normally, but took it off because the teacher asked for mock data
attachmentValue blob,
email_id int references emails.email(email_id) on delete cascade
);

-- add mock data
-- email
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Ligula Nullam Foundation","Octavia","9603 Iaculis, Rd.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",4,0,1,18, "Krystal.Erickson@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Orci Limited","Scott","P.O. Box 656, 2455 Sagittis Ave","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",4,1,2,4, "Kennedy.Riddle@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Cursus Et Eros Company","Thaddeus","P.O. Box 479, 4037 Placerat Road","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",4,0,3,3, "Micheal.Sanderson@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Lorem Eu LLP","Ira","Ap -- 716-2105 Nulla Ave","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",5,0,4,4, "Blaine.Friedman@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Lectus Rutrum Urna Inc.","Cheryl","Ap -- 484-2987 In Ave","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",3,1,5,17, "Shanon Fields@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Convallis Convallis Dolor Industries","Hillary","7902 Ante. Av.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",2,1,6,24, "Ruby-Rose.Love@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Sociis Consulting","Mannix","P.O. Box 630, 1511 Nunc Rd.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",5,1,7,20, "Kwame.Cano@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Consequat Foundation","Seth","4433 Auctor Rd.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",3,1,8,3, "Alissa.Thompson@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Ullamcorper Limited","Elaine","Ap -- 108-2398 Lectus. St.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",3,1,9,5, "Clarke.Wolfe@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Volutpat Ltd","Gregory","Ap -- 454-282 Quisque St.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",4,0,10,23, "Arianne.Bird@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Turpis Aliquam LLC","Edward","Ap -- 413-167 Tempor St.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",5,1,11,10, "RaniaRead@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Nulla Associates","Nissim","604 Luctus Av.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",3,0,12,5, "Isabella.Sharp@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Velit Associates","Tyrone","Ap -- 469-4847 Congue Rd.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",2,1,13,7, "Kaycee.Nicholls@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Rhoncus Institute","Wang","477-2628 Aliquam Road","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",3,1,14,17, "Zaina.Hastings@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Luctus Ut Corp.","Elaine","9327 Magna Av.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",5,1,15,13, "Alan.Trejo@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Velit Pellentesque Ultricies Incorporated","Buckminster","932-3636 Lacinia Av.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",1,1,16,21, "Willie.Booker@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Integer Institute","Desirae","Ap -- 481-5081 Amet, Road","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",4,0,17,18, "Allana.Lane@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Nibh Institute","Nissim","411-344 Maecenas St.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",3,1,18,20, "Donell.Browning@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Maecenas Malesuada Incorporated","MacKenzie","Ap -- 880-8038 Tincidunt Av.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",2,1,19,13, "Maciej.Adams@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Vitae Mauris Sit Institute","Gil","330-3171 Et Road","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",5,0,20,1, "Tiah.Bryan@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Dui Cum Sociis Incorporated","Madison","701-4944 Eget St.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",5,0,21,13, "Zayne.Sloan@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Duis Mi Enim LLC","Ian","916-6617 Tincidunt. St.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",1,0,22,3, "Chester.Atherton@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Sit LLC","Howard","Ap -- 866-144 Aliquet Rd.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",3,0,23,21, "Keith.Coulson@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Ornare Egestas Ligula Corporation","Zephr","489-2078 Elementum, Av.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",1,1,24,4, "Robert.Petty@gmail.com");
--INSERT INTO `email` (`subject`,`textMsg`,`htmlMsg`,`dateSent`,`dateReceived`,`priority`,`isread`,`email_id`,`folders_id`, `sender`) VALUES ("Proin Mi PC","Damian","P.O. Box 631, 6185 Nec, Av.","2018-09-28 15:31:34.000","2018-09-28 15:31:34.000",5,0,25,5, "Kealan.Ferrell@gmail.com");
-- Receive
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("cc","ultrices.a.auctor@Suspendisseac.com","consequat",1);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("cc","nec.leo.Morbi@dignissimpharetra.co.uk","Duis",2);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("cc","Praesent.interdum@vulputateeuodio.net","ligula.",3);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("from","varius@pharetraNamac.ca","massa",4);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("from","dapibus@interdum.co.uk","amet",5);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("from","pulvinar@euaugueporttitor.org","Ut",6);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("to","convallis@imperdietornareIn.org","egestas.",7);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("to","ut.quam.vel@egestaslaciniaSed.co.uk","justo",8);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("to","diam.dictum@turpisnon.edu","lectus.",9);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("bcc","eu.dolor@ullamcorpervelitin.com","malesuada",10);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("bcc","Vivamus.sit@liberoet.com","mi",11);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("bcc","sed.sapien.Nunc@orci.org","hendrerit",12);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("cc","sollicitudin@etrisusQuisque.co.uk","turpis.",13);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("cc","eleifend.Cras.sed@elit.edu","aliquet",14);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("cc","metus.Aliquam.erat@semconsequat.edu","morbi",15);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("from","dolor.Fusce@tristiquenequevenenatis.ca","ultrices,",16);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("from","lacus.Cras@etultrices.com","enim",17);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("from","tellus.eu@aliquetmagna.edu","cursus.",18);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("to","eu@urnaNullamlobortis.co.uk","risus.",19);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("to","non.lobortis.quis@turpisAliquamadipiscing.ca","Nunc",20);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("to","at@aliquamenimnec.ca","Proin",21);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("bcc","lacus@vulputate.ca","egestas.",22);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("bcc","faucibus.ut@nonenim.org","sed",23);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("bcc","ultricies.sem.magna@natoquepenatibuset.org","rhoncus.",24);
--INSERT INTO `receive` (`receiveType`,`receiveEmail`,`receiveName`,`receive_id`) VALUES ("cc","nec.cursus.a@Cras.ca","auctor",25);
-- Folder
INSERT INTO `folders` (`name`,`folders_id`) VALUES ("Inbox",1);
INSERT INTO `folders` (`name`,`folders_id`) VALUES ("Sent",2);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("Sed",1);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("Praesent",2);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("et",3);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("neque.",4);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("Cum",5);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("Donec",6);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("varius",7);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("risus.",8);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("velit",9);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("velit.",10);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("Aenean",11);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("nunc.",12);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("mauris",13);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("Proin",14);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("congue,",15);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("montes,",16);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("ut",17);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("nec",18);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("luctus",19);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("euismod",20);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("tristique",21);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("et",22);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("non,",23);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("nisl.",24);
--INSERT INTO `folders` (`name`,`folders_id`) VALUES ("risus.",25);
-- bridging table
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (24,15);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (17,25);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (5,16);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (4,15);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (17,25);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (6,20);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (2,24);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (21,25);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (25,2);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (10,15);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (23,19);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (20,22);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (24,16);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (7,14);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (9,17);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (22,15);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (10,14);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (7,16);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (8,5);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (10,3);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (25,23);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (19,9);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (8,11);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (6,4);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (17,21);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (1,17);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (16,6);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (1,25);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (2,6);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (17,12);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (6,8);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (12,1);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (16,18);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (11,1);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (17,2);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (2,24);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (10,14);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (14,19);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (14,5);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (10,14);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (10,5);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (6,2);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (15,1);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (9,20);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (3,9);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (25,10);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (6,9);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (6,16);
--INSERT INTO `bridge` (`receive_id`,`email_id`) VALUES (6,17);
-- attachments
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","ut", null, 18);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","Mauris", null,20);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","mauris.", null,10);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","nibh.", null,8);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","Donec", null,25);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","Nunc", null,15);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","Nulla", null,17);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","lobortis", null,8);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","urna", null,8);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","nulla.", null,23);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","egestas", null,6);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","sit", null,3);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","id,", null,12);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","porttitor", null,2);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","quis", null,3);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","nostra,", null,15);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","eu,", null,13);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","Donec", null,2);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","non,", null,14);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","vulputate", null,7);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","posuere", null,23);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","ac", null,9);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","Nulla", null,8);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("attachment","Sed", null,14);
--INSERT INTO `attachment` (`attachmentType`,`attachmentName`,`attachmentValue`,`email_id`) VALUES ("embedded","faucibus", null,16);

