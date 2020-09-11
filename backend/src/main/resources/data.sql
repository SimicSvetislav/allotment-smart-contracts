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
VALUES (2, "Fruskogorska 30", "Novi Sad", 
		"Srbija", "Lims", null);

INSERT INTO accomodation (id, address, city, country, name, account)
VALUES (3, "Fruskogorska 20", "Novi Sad", 
		"Srbija", "Park org", null);

INSERT INTO agency (id, address, city, country, name, account)
VALUES (4, "Grčkoškolska 10", "Novi Sad", 
		"Srbija", "Kon Tiki Travel", null);
		
INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id)
VALUES ("ppera", "pera@gmail.com", "Petar Petrovic", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"061/123-45-67", 1);

INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id)
VALUES ("perica", "perica@gmail.com", "Perica Peric", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"061/123-45-67", 1);
		
INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id)
VALUES ("mmika", "mika@yahoo.com", "Miroslav Mikic", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"068/987-65-43", 2);
		
INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id)
VALUES ("jova", "jova@gmail.com", "Jovan Jovanovic", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"068/987-65-43", 3);
		
INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id)
VALUES ("jovica", "jovica@gmail.com", "Jovica Jovic", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"064/412-353", 3);
		
INSERT INTO representative (display_name, email, full_name, password, phone_number, representing_id)
VALUES ("kontiki", "kontiki@gmail.com", "Kristijan Trajkovic", 
		"$2a$10$TYEJ0OSTcthhSaYJccW2l.WvFEvtSehJ0njXf6OPXB7KW0pfh0/nW", 
		"064/412-353", 4);

INSERT INTO hotel (address, city, country, name, org_id)
VALUES ("Nikole Pašića 27", "Novi Sad", "Srbija", "Hotel Fontana", 2);

INSERT INTO hotel (address, city, country, name, org_id)
VALUES ("Futoška 109", "Novi Sad", "Srbija", "Prezident Hotel", 2);

INSERT INTO hotel (address, city, country, name, org_id)
VALUES ("Primorska 50", "Novi Sad", "Srbija", "Hotel Garden", 2);

INSERT INTO hotel (address, city, country, name, org_id)
VALUES ("Nikole Tesle 1", "Subotica", "Srbija", "Vila Park", 3);

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

-- Park org

INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (1, 5, 4);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (2, 15, 4);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (3, 5, 4);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (4, 4, 4);

INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (1, 5, 5);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (2, 15, 5);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (3, 5, 5);
INSERT INTO rooms_info(beds, no_rooms, hotel_id)
VALUE (4, 4, 5);

-- INSERT INTO room (beds, hotel_id)
-- VALUES (3, 1);
-- INSERT INTO room (beds, hotel_id)
-- VALUES (3, 1);
-- INSERT INTO room (beds, hotel_id)
-- VALUES (3, 1);
-- INSERT INTO room (beds, hotel_id)
-- VALUES (2, 1);

-- INSERT INTO contract (address, end_date, start_date, status, accomodation_id, agency_id, ag_repr_id, acc_repr_id)
-- VALUE ("0x4727170965d5a69b83551c01dce032209cb8c0f2", 
--	   "2020-05-01", "2020-10-31", "NEG", 2, 1, 1, 0);
	   
-- INSERT INTO contract (address, end_date, start_date, status, accomodation_id, agency_id, ag_repr_id, acc_repr_id)
-- VALUE ("0x60aac89c241da220b409f2b037bfded720412479", 
--	   "2020-03-21", "2020-04-21", "NEG", 2, 1, 0, 2);