INSERT INTO organization (dtype, address, city, country, name, account)
VALUES ("Agency", "Bulevar Oslobođenja", "Novi Sad", 
		"Srbija", "Sabra", null);
		
INSERT INTO organization (dtype, address, city, country, name, account)
VALUES ("Accomodation", "Fruskogorska", "Novi Sad", 
		"Srbija", "Lims", null);

INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id, account_id)
VALUES ("ppera", "pera@gmail.com", "Petar Petrovic", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"061/123-45-67", 1, 1);

INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id, account_id)
VALUES ("mmika", "mika@yahoo.com", "Miroslav Mikic", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"068/987-65-43", 2, 2);
		