-- INSERT INTO hotel (dtype, address, city, country, name, account)
-- VALUES ("Agency", "Bulevar Oslobođenja", "Novi Sad", 
--		"Srbija", "Sabra", null);
		
-- INSERT INTO organization (dtype, address, city, country, name, account)
-- VALUES ("Accomodation", "Fruskogorska", "Novi Sad", 
--		"Srbija", "Lims", null);

-- INSERT INTO organization (dtype, address, city, country, name, account)
-- VALUES ("Accomodation", "Fruskogorska", "Novi Sad", 
--		"Srbija", "Park org", null);

INSERT INTO agency (id, address, city, country, name, account)
VALUES (1, "Bulevar Oslobođenja 30", "Novi Sad", 
		"Srbija", "Sabra", null);
		
INSERT INTO accomodation (id, address, city, country, name, account)
VALUES (2, "Fruskogorska", "Novi Sad", 
		"Srbija", "Lims", null);

INSERT INTO accomodation (id, address, city, country, name, account)
VALUES (3, "Fruskogorska", "Novi Sad", 
		"Srbija", "Park org", null);
		
INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id)
VALUES ("ppera", "pera@gmail.com", "Petar Petrovic", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"061/123-45-67", 1);

INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id)
VALUES ("mmika", "mika@yahoo.com", "Miroslav Mikic", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"068/987-65-43", 2);

INSERT INTO hotel (address, city, country, name, org_id)
VALUES ("Nikole Pašića 27", "Novi Sad", "Srbija", "Hotel Fontana", 2);

INSERT INTO hotel (address, city, country, name, org_id)
VALUES ("Futoška 109", "Novi Sad", "Srbija", "Prezident Hotel", 2);

INSERT INTO hotel (address, city, country, name, org_id)
VALUES ("Primorska 50", "Novi Sad", "Srbija", "Hotel Garden", 2);

INSERT INTO hotel (address, city, country, name, org_id)
VALUES ("Nikole Tesle 1", "Novi Sad", "Srbija", "Vila Park", 3);

INSERT INTO hotel (address, city, country, name, org_id)
VALUES ("Novosadskog sajma 35", "Novi Sad", "Srbija", "Hotel Park", 3);

INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (1, 10, 1);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (2, 5, 1);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (3, 5, 1);

INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (1, 5, 2);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (2, 15, 2);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (3, 5, 2);

INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (4, 5, 3);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (2, 10, 3);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (3, 5, 3);

INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (1, 5, 4);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (2, 15, 4);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (3, 5, 4);

INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (1, 5, 5);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (2, 15, 5);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (3, 5, 5);

-- INSERT INTO room (beds, hotel_id)
-- VALUES (3, 1);
-- INSERT INTO room (beds, hotel_id)
-- VALUES (3, 1);
-- INSERT INTO room (beds, hotel_id)
-- VALUES (3, 1);
-- INSERT INTO room (beds, hotel_id)
-- VALUES (2, 1);

