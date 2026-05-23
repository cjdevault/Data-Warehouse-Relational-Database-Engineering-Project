-- MEMBERSHIP TIERS
INSERT INTO shaydenlowry.membership_tier VALUES (1, 'Day Pass', 0);
INSERT INTO shaydenlowry.membership_tier VALUES (2, 'Monthly', 10);
INSERT INTO shaydenlowry.membership_tier VALUES (3, 'Annual', 15);

-- STAFF 
INSERT INTO shaydenlowry.staff VALUES (shaydenlowry.seq_staff_id.NEXTVAL, 'Luke Skywalker', 'vet', 'luke.skywalker@petcafe.com', '520-123-0001', DATE '2005-01-10');
INSERT INTO shaydenlowry.staff VALUES (shaydenlowry.seq_staff_id.NEXTVAL, 'Leia Organa', 'coordinator', 'leia.organa@petcafe.com', '520-123-0002', DATE '2006-03-15');
INSERT INTO shaydenlowry.staff VALUES (shaydenlowry.seq_staff_id.NEXTVAL, 'Han Solo', 'handler', 'han.solo@petcafe.com', '520-123-0003', DATE '2008-06-20');
INSERT INTO shaydenlowry.staff VALUES (shaydenlowry.seq_staff_id.NEXTVAL, 'Chewbacca', 'handler', 'chewbacca@petcafe.com', '520-123-0004', DATE '2009-09-05');
INSERT INTO shaydenlowry.staff VALUES (shaydenlowry.seq_staff_id.NEXTVAL, 'ObiWan Kenobi', 'manager', 'obiwan.kenobi@petcafe.com', '520-123-0005', DATE '2003-11-01');
INSERT INTO shaydenlowry.staff VALUES (shaydenlowry.seq_staff_id.NEXTVAL, 'Rey Skywalker', 'barista', 'rey.skywalker@petcafe.com', '520-123-0006', DATE '2015-02-18');

-- ROOMS 
INSERT INTO shaydenlowry.room VALUES (shaydenlowry.seq_room_id.NEXTVAL, 'Small Cat Lounge', 'cat', 8, 'Cat interaction');
INSERT INTO shaydenlowry.room VALUES (shaydenlowry.seq_room_id.NEXTVAL, 'Large Dog Playroom', 'dog', 10, 'Dog interaction');
INSERT INTO shaydenlowry.room VALUES (shaydenlowry.seq_room_id.NEXTVAL, 'Small Animal Room', 'small_animal', 6, 'Small animal interaction');
INSERT INTO shaydenlowry.room VALUES (shaydenlowry.seq_room_id.NEXTVAL, 'Adoption Consultation Office', 'adoption_consultation', 3, 'Adoption meetings');
INSERT INTO shaydenlowry.room VALUES (shaydenlowry.seq_room_id.NEXTVAL, 'Training and Enrichment Room', 'dog', 12, 'Training classes');

-- MEMBERS 
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Darth Sidious', '520-234-0001', 'darth.sidious@petcafe.com', DATE '1950-01-01', 'Darth Vader', '520-987-0001', 3, DATE '2023-01-01');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Yoda', '520-234-0002', 'yoda@petcafe.com', DATE '1940-05-04', 'Mace Windu', '520-987-0002', 2, DATE '2023-01-10');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Anakin Skywalker', '520-234-0003', 'anakin.skywalker@petcafe.com', DATE '1985-03-14', 'Padme Amidala', '520-987-0003', 1, DATE '2023-02-01');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Padme Amidala', '520-234-0004', 'padme.amidala@petcafe.com', DATE '1986-10-31', 'Anakin Skywalker', '520-987-0004', 2, DATE '2023-02-10');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Mace Windu', '520-234-0005', 'mace.windu@petcafe.com', DATE '1975-07-07', 'Yoda', '520-987-0005', 2, DATE '2023-03-01');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Boba Fett', '520-234-0006', 'boba.fett@petcafe.com', DATE '1988-12-12', 'Jango Fett', '520-987-0006', 1, DATE '2023-03-15');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Lando Calrissian', '520-234-0007', 'lando.calrissian@petcafe.com', DATE '1972-04-20', 'Han Solo', '520-987-0007', 3, DATE '2023-04-01');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Ahsoka Tano', '520-234-0008', 'ahsoka.tano@petcafe.com', DATE '1995-01-15', 'Anakin Skywalker', '520-987-0008', 2, DATE '2023-04-10');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Grogu', '520-234-0009', 'grogu@petcafe.com', DATE '2019-11-12', 'Din Djarin', '520-987-0009', 1, DATE '2023-05-01');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Jabba the Hutt', '520-234-0010', 'jabba@petcafe.com', DATE '1960-08-08', 'Bib Fortuna', '520-987-0010', 1, DATE '2023-05-10');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Din Djarin', '520-234-0011', 'din.djarin@petcafe.com', DATE '1984-02-02', 'Grogu', '520-987-0011', 2, DATE '2023-06-01');
INSERT INTO shaydenlowry.member VALUES (shaydenlowry.seq_member_id.NEXTVAL, 'Poe Dameron', '520-234-0012', 'poe.dameron@petcafe.com', DATE '1987-09-09', 'Finn', '520-987-0012', 3, DATE '2023-06-15');

-- MEMBERSHIP PAYMENTS 
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 1, 3, DATE '2023-01-01', 180.00);
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 2, 2, DATE '2023-01-10', 60.00);
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 3, 1, DATE '2023-02-01', 20.00);
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 4, 2, DATE '2023-02-10', 60.00);
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 5, 2, DATE '2023-03-01', 60.00);
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 6, 1, DATE '2023-03-15', 20.00);
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 7, 3, DATE '2023-04-01', 180.00);
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 8, 2, DATE '2023-04-10', 60.00);
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 9, 1, DATE '2023-05-01', 20.00);
INSERT INTO shaydenlowry.membership_payment VALUES (shaydenlowry.seq_membership_payment_id.NEXTVAL, 10, 1, DATE '2023-05-10', 20.00);

-- MENU ITEMS 
INSERT INTO shaydenlowry.menu_item VALUES (shaydenlowry.seq_menu_item_id.NEXTVAL, 'Latte', 5.00, 'coffee', 'Classic latte');
INSERT INTO shaydenlowry.menu_item VALUES (shaydenlowry.seq_menu_item_id.NEXTVAL, 'Drip Coffee', 3.50, 'coffee', 'House blend');
INSERT INTO shaydenlowry.menu_item VALUES (shaydenlowry.seq_menu_item_id.NEXTVAL, 'Cold Brew', 4.25, 'coffee', 'Cold brew coffee');
INSERT INTO shaydenlowry.menu_item VALUES (shaydenlowry.seq_menu_item_id.NEXTVAL, 'Chocolate Chip Cookie', 3.00, 'pastry', 'Cookie');
INSERT INTO shaydenlowry.menu_item VALUES (shaydenlowry.seq_menu_item_id.NEXTVAL, 'Avocado Toast', 7.50, 'light_meal', 'Avocado toast');
INSERT INTO shaydenlowry.menu_item VALUES (shaydenlowry.seq_menu_item_id.NEXTVAL, 'Veggie Sandwich', 8.50, 'light_meal', 'Sandwich');
INSERT INTO shaydenlowry.menu_item VALUES (shaydenlowry.seq_menu_item_id.NEXTVAL, 'Herbal Tea', 3.25, 'tea', 'Herbal tea');
INSERT INTO shaydenlowry.menu_item VALUES (shaydenlowry.seq_menu_item_id.NEXTVAL, 'Croissant', 2.75, 'pastry', 'Croissant');

-- PETS 
INSERT INTO shaydenlowry.pet VALUES (shaydenlowry.seq_pet_id.NEXTVAL, 'Nibbler', 'cat', 'Domestic Shorthair', 2, DATE '2023-01-10', 1, 'Playful, likes biting', NULL, 'available_adoption');
INSERT INTO shaydenlowry.pet VALUES (shaydenlowry.seq_pet_id.NEXTVAL, 'Bella', 'cat', 'Domestic Shorthair', 3, DATE '2023-01-20', 1, 'Calm', NULL, 'resident');
INSERT INTO shaydenlowry.pet VALUES (shaydenlowry.seq_pet_id.NEXTVAL, 'Max', 'dog', 'Mixed', 4, DATE '2023-02-01', 2, 'Friendly', NULL, 'available_adoption');
INSERT INTO shaydenlowry.pet VALUES (shaydenlowry.seq_pet_id.NEXTVAL, 'Luna', 'dog', 'Mixed', 1, DATE '2023-02-15', 5, 'Energetic', 'Needs extra playtime', 'pending');
INSERT INTO shaydenlowry.pet VALUES (shaydenlowry.seq_pet_id.NEXTVAL, 'Charlie', 'cat', 'Domestic Longhair', 5, DATE '2023-03-01', 1, 'Shy', NULL, 'available_adoption');
INSERT INTO shaydenlowry.pet VALUES (shaydenlowry.seq_pet_id.NEXTVAL, 'Molly', 'dog', 'Mixed', 6, DATE '2023-03-10', 2, 'Chill', NULL, 'resident');
INSERT INTO shaydenlowry.pet VALUES (shaydenlowry.seq_pet_id.NEXTVAL, 'Cooper', 'dog', 'Mixed', 2, DATE '2023-04-01', 5, 'Curious', NULL, 'available_adoption');
INSERT INTO shaydenlowry.pet VALUES (shaydenlowry.seq_pet_id.NEXTVAL, 'Daisy', 'cat', 'Domestic Shorthair', 3, DATE '2023-04-15', 1, 'Affectionate', NULL, 'sick');
INSERT INTO shaydenlowry.pet VALUES (shaydenlowry.seq_pet_id.NEXTVAL, 'Rocky', 'small_animal', 'Rabbit', 2, DATE '2023-05-01', 3, 'Quiet', NULL, 'available_adoption');

-- HEALTH RECORDS 
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 1, 1, DATE '2023-01-15', 'vaccination', DATE '2024-01-15', 'completed', 'Rabies shot');
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 1, 1, DATE '2023-02-10', 'checkup', NULL, 'normal', 'Routine exam');
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 2, 1, DATE '2023-01-25', 'vaccination', DATE '2024-01-25', 'completed', 'Vaccination');
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 3, 1, DATE '2023-02-05', 'checkup', NULL, 'normal', 'Healthy');
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 4, 1, DATE '2023-02-20', 'checkup', NULL, 'follow_up', 'Watch energy levels');
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 5, 1, DATE '2023-03-05', 'vaccination', DATE '2024-03-05', 'completed', 'Core vaccines');
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 6, 1, DATE '2023-03-15', 'checkup', NULL, 'normal', 'All good');
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 7, 1, DATE '2023-04-05', 'vaccination', DATE '2024-04-05', 'completed', 'Vaccination');
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 8, 1, DATE '2023-04-20', 'checkup', NULL, 'monitor', 'Mild illness, check again');
INSERT INTO shaydenlowry.health_record VALUES (shaydenlowry.seq_health_record_id.NEXTVAL, 9, 1, DATE '2023-05-05', 'checkup', NULL, 'normal', 'Healthy rabbit');

-- BEHAVIORAL ASSESSMENTS 
INSERT INTO shaydenlowry.behavioral_assessment VALUES (shaydenlowry.seq_behavioral_assessment_id.NEXTVAL, 1, 3, DATE '2023-01-18', 'approved', 'Very social, good with visitors');
INSERT INTO shaydenlowry.behavioral_assessment VALUES (shaydenlowry.seq_behavioral_assessment_id.NEXTVAL, 2, 3, DATE '2023-01-28', 'conditional', 'Prefers quiet groups');
INSERT INTO shaydenlowry.behavioral_assessment VALUES (shaydenlowry.seq_behavioral_assessment_id.NEXTVAL, 3, 3, DATE '2023-02-07', 'approved', 'Great with kids');
INSERT INTO shaydenlowry.behavioral_assessment VALUES (shaydenlowry.seq_behavioral_assessment_id.NEXTVAL, 4, 3, DATE '2023-02-22', 'pending', 'High energy, needs more training');
INSERT INTO shaydenlowry.behavioral_assessment VALUES (shaydenlowry.seq_behavioral_assessment_id.NEXTVAL, 5, 3, DATE '2023-03-07', 'approved', 'Shy but warms up');
INSERT INTO shaydenlowry.behavioral_assessment VALUES (shaydenlowry.seq_behavioral_assessment_id.NEXTVAL, 6, 3, DATE '2023-03-17', 'approved', 'Relaxed, good for chill visits');
INSERT INTO shaydenlowry.behavioral_assessment VALUES (shaydenlowry.seq_behavioral_assessment_id.NEXTVAL, 7, 3, DATE '2023-04-07', 'approved', 'Very playful');
INSERT INTO shaydenlowry.behavioral_assessment VALUES (shaydenlowry.seq_behavioral_assessment_id.NEXTVAL, 8, 3, DATE '2023-04-22', 'not_approved', 'Sick, no interaction');
INSERT INTO shaydenlowry.behavioral_assessment VALUES (shaydenlowry.seq_behavioral_assessment_id.NEXTVAL, 9, 3, DATE '2023-05-07', 'approved', 'Calm, ok to handle');

-- ADOPTION APPLICATIONS
INSERT INTO shaydenlowry.adoption_application VALUES (shaydenlowry.seq_adoption_application_id.NEXTVAL, 1, 3, DATE '2023-05-10', 'pending', 2);
INSERT INTO shaydenlowry.adoption_application VALUES (shaydenlowry.seq_adoption_application_id.NEXTVAL, 3, 4, DATE '2023-05-15', 'approved', 2);
INSERT INTO shaydenlowry.adoption_application VALUES (shaydenlowry.seq_adoption_application_id.NEXTVAL, 5, 1, DATE '2023-05-20', 'rejected', 2);
INSERT INTO shaydenlowry.adoption_application VALUES (shaydenlowry.seq_adoption_application_id.NEXTVAL, 7, 8, DATE '2023-06-01', 'approved', 2);
INSERT INTO shaydenlowry.adoption_application VALUES (shaydenlowry.seq_adoption_application_id.NEXTVAL, 9, 2, DATE '2023-06-10', 'withdrawn', 2);
INSERT INTO shaydenlowry.adoption_application VALUES (shaydenlowry.seq_adoption_application_id.NEXTVAL, 1, 6, DATE '2023-06-15', 'pending', 2);

-- ADOPTION RECORDS 
INSERT INTO shaydenlowry.adoption_record VALUES (shaydenlowry.seq_adoption_record_id.NEXTVAL, 2, DATE '2023-06-05', 120.00, DATE '2023-07-05', 'Follow up scheduled');
INSERT INTO shaydenlowry.adoption_record VALUES (shaydenlowry.seq_adoption_record_id.NEXTVAL, 4, DATE '2023-06-20', 150.00, DATE '2023-07-20', 'Follow up scheduled');
INSERT INTO shaydenlowry.adoption_record VALUES (shaydenlowry.seq_adoption_record_id.NEXTVAL, 3, DATE '2023-06-25', 90.00, NULL, 'Adoption closed after review');

-- RESERVATIONS 
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 1, 1, DATE '2023-06-01', TIMESTAMP '2023-06-01 10:00:00', 1.0, 'completed');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 2, 2, DATE '2023-06-01', TIMESTAMP '2023-06-01 11:30:00', 1.5, 'completed');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 3, 1, DATE '2023-06-02', TIMESTAMP '2023-06-02 09:00:00', 2.0, 'completed');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 4, 3, DATE '2023-06-02', TIMESTAMP '2023-06-02 14:00:00', 1.0, 'cancelled');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 5, 2, DATE '2023-06-03', TIMESTAMP '2023-06-03 13:00:00', 1.0, 'completed');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 6, 1, DATE '2023-06-03', TIMESTAMP '2023-06-03 15:00:00', 1.5, 'completed');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 7, 4, DATE '2023-06-04', TIMESTAMP '2023-06-04 10:00:00', 1.0, 'confirmed');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 8, 5, DATE '2023-06-04', TIMESTAMP '2023-06-04 16:00:00', 2.0, 'completed');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 9, 3, DATE '2023-06-05', TIMESTAMP '2023-06-05 09:30:00', 1.0, 'completed');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 10, 2, DATE '2023-06-05', TIMESTAMP '2023-06-05 17:00:00', 1.0, 'cancelled');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 11, 1, DATE '2023-06-06', TIMESTAMP '2023-06-06 11:00:00', 1.5, 'completed');
INSERT INTO shaydenlowry.reservation VALUES (shaydenlowry.seq_reservation_id.NEXTVAL, 12, 2, DATE '2023-06-06', TIMESTAMP '2023-06-06 13:00:00', 1.0, 'confirmed');

-- VISITS 
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 1, 'Annual', TIMESTAMP '2023-06-01 10:00:00', TIMESTAMP '2023-06-01 11:00:00');
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 2, 'Monthly', TIMESTAMP '2023-06-01 11:30:00', TIMESTAMP '2023-06-01 13:00:00');
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 3, 'Day Pass', TIMESTAMP '2023-06-02 09:00:00', TIMESTAMP '2023-06-02 11:00:00');
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 5, 'Monthly', TIMESTAMP '2023-06-03 13:00:00', TIMESTAMP '2023-06-03 14:00:00');
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 6, 'Day Pass', TIMESTAMP '2023-06-03 15:00:00', TIMESTAMP '2023-06-03 16:30:00');
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 7, 'Annual', TIMESTAMP '2023-06-04 10:00:00', NULL);
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 8, 'Monthly', TIMESTAMP '2023-06-04 16:00:00', TIMESTAMP '2023-06-04 18:00:00');
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 9, 'Day Pass', TIMESTAMP '2023-06-05 09:30:00', TIMESTAMP '2023-06-05 10:30:00');
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 11, 'Monthly', TIMESTAMP '2023-06-06 11:00:00', TIMESTAMP '2023-06-06 12:30:00');
INSERT INTO shaydenlowry.visit VALUES (shaydenlowry.seq_visit_id.NEXTVAL, 12, 'Annual', TIMESTAMP '2023-06-06 13:00:00', NULL);

-- CAFE ORDERS
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 1, TIMESTAMP '2023-06-01 10:15:00', 8.50, 'paid', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 1, TIMESTAMP '2023-06-01 10:30:00', 3.00, 'paid', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 2, TIMESTAMP '2023-06-01 11:45:00', 12.00, 'paid', 1.20);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 3, TIMESTAMP '2023-06-02 09:30:00', 5.00, 'paid', 0.50);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 3, TIMESTAMP '2023-06-02 10:15:00', 6.50, 'paid', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 4, TIMESTAMP '2023-06-03 13:10:00', 7.50, 'paid', 0.75);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 5, TIMESTAMP '2023-06-03 15:10:00', 4.25, 'paid', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 7, TIMESTAMP '2023-06-04 10:20:00', 9.00, 'pending', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 7, TIMESTAMP '2023-06-04 10:40:00', 3.50, 'pending', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 8, TIMESTAMP '2023-06-04 16:20:00', 11.75, 'paid', 1.18);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 9, TIMESTAMP '2023-06-05 09:40:00', 5.00, 'paid', 0.50);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 9, TIMESTAMP '2023-06-05 10:10:00', 3.00, 'paid', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 9, TIMESTAMP '2023-06-05 10:20:00', 2.75, 'paid', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 1, TIMESTAMP '2023-06-01 10:50:00', 4.25, 'paid', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 2, TIMESTAMP '2023-06-01 12:30:00', 7.50, 'paid', 0.75);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 5, TIMESTAMP '2023-06-03 15:40:00', 6.00, 'paid', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 8, TIMESTAMP '2023-06-04 17:10:00', 4.25, 'paid', 0.00);
INSERT INTO shaydenlowry.cafe_order VALUES (shaydenlowry.seq_order_id.NEXTVAL, 10, TIMESTAMP '2023-06-06 13:20:00', 5.00, 'pending', 0.50);

-- ORDER ITEMS (composite key, no sequence used here)
INSERT INTO shaydenlowry.order_item VALUES (1, 1, 1, 5.00);
INSERT INTO shaydenlowry.order_item VALUES (1, 4, 1, 3.50);
INSERT INTO shaydenlowry.order_item VALUES (2, 4, 1, 3.00);
INSERT INTO shaydenlowry.order_item VALUES (3, 5, 1, 7.50);
INSERT INTO shaydenlowry.order_item VALUES (3, 1, 1, 4.50);
INSERT INTO shaydenlowry.order_item VALUES (4, 1, 1, 5.00);
INSERT INTO shaydenlowry.order_item VALUES (5, 8, 1, 2.75);
INSERT INTO shaydenlowry.order_item VALUES (5, 7, 1, 3.75);
INSERT INTO shaydenlowry.order_item VALUES (6, 5, 1, 7.50);
INSERT INTO shaydenlowry.order_item VALUES (7, 3, 1, 4.25);
INSERT INTO shaydenlowry.order_item VALUES (8, 6, 1, 8.50);
INSERT INTO shaydenlowry.order_item VALUES (9, 2, 1, 3.50);
INSERT INTO shaydenlowry.order_item VALUES (10, 5, 1, 7.50);
INSERT INTO shaydenlowry.order_item VALUES (10, 4, 1, 4.25);
INSERT INTO shaydenlowry.order_item VALUES (11, 1, 1, 5.00);
INSERT INTO shaydenlowry.order_item VALUES (12, 4, 1, 3.00);
INSERT INTO shaydenlowry.order_item VALUES (13, 8, 1, 2.75);
INSERT INTO shaydenlowry.order_item VALUES (14, 3, 1, 4.25);
INSERT INTO shaydenlowry.order_item VALUES (15, 5, 1, 7.50);
INSERT INTO shaydenlowry.order_item VALUES (16, 6, 1, 6.00);
INSERT INTO shaydenlowry.order_item VALUES (17, 3, 1, 4.25);
INSERT INTO shaydenlowry.order_item VALUES (18, 1, 1, 5.00);

-- EVENTS (past)
INSERT INTO shaydenlowry.event VALUES (shaydenlowry.seq_event_id.NEXTVAL, 'Kitten Social Hour', 4, TIMESTAMP '2023-06-10 10:00:00', TIMESTAMP '2023-06-10 11:30:00', 10, 'adoption_event', 'Meet available kittens', 2);
INSERT INTO shaydenlowry.event VALUES (shaydenlowry.seq_event_id.NEXTVAL, 'Puppy Training Basics', 5, TIMESTAMP '2023-06-12 17:00:00', TIMESTAMP '2023-06-12 18:30:00', 12, 'training', 'Intro to puppy training', 5);
INSERT INTO shaydenlowry.event VALUES (shaydenlowry.seq_event_id.NEXTVAL, 'Small Animal Care Workshop', 4, TIMESTAMP '2023-06-15 15:00:00', TIMESTAMP '2023-06-15 16:30:00', 8, 'workshop', 'Care tips for small animals', 2);
INSERT INTO shaydenlowry.event VALUES (shaydenlowry.seq_event_id.NEXTVAL, 'Adoption Open House', 5, TIMESTAMP '2023-07-01 11:00:00', TIMESTAMP '2023-07-01 13:00:00', 20, 'adoption_event', 'Meet lots of animals', 5);
INSERT INTO shaydenlowry.event VALUES (shaydenlowry.seq_event_id.NEXTVAL, 'Coffee With Cats', 1, TIMESTAMP '2023-07-05 09:00:00', TIMESTAMP '2023-07-05 10:30:00', 8, 'other', 'Relax with cats and coffee', 2);
INSERT INTO shaydenlowry.event VALUES (shaydenlowry.seq_event_id.NEXTVAL, 'Dog Enrichment Class', 5, TIMESTAMP '2023-07-10 16:00:00', TIMESTAMP '2023-07-10 17:30:00', 12, 'training', 'Enrichment activities for dogs', 5);

-- EVENTS (future)

INSERT INTO shaydenlowry.event VALUES (shaydenlowry.seq_event_id.NEXTVAL, 'New Years Kittens', 1, TIMESTAMP '2026-01-05 10:00:00', TIMESTAMP '2026-01-05 11:30:00', 8, 'adoption_event', 'Celebrate the new year with the adoptable kittens', 2);
INSERT INTO shaydenlowry.event VALUES (shaydenlowry.seq_event_id.NEXTVAL, 'Puppy Training Basics', 5, TIMESTAMP '2026-01-10 16:00:00', TIMESTAMP '2026-01-10 18:00:00', 12, 'training', 'Intro to puppy training', 5);
INSERT INTO shaydenlowry.event VALUES (shaydenlowry.seq_event_id.NEXTVAL, 'Small Animal Care Workshop', 3, TIMESTAMP '2026-01-15 14:00:00', TIMESTAMP '2026-01-15 16:00:00', 6, 'workshop', 'Care tips for small animals', 2);

-- EVENT REGISTRATIONS (past)
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 1, 1, 'attended', 20.00, 'paid', DATE '2023-06-05');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 1, 3, 'attended', 20.00, 'paid', DATE '2023-06-06');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 1, 4, 'no_show', 20.00, 'paid', DATE '2023-06-07');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 2, 2, 'attended', 25.00, 'paid', DATE '2023-06-08');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 2, 5, 'attended', 25.00, 'paid', DATE '2023-06-09');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 3, 6, 'attended', 15.00, 'paid', DATE '2023-06-10');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 3, 7, 'cancelled', 0.00, 'paid', DATE '2023-06-11');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 4, 8, 'attended', 30.00, 'paid', DATE '2023-06-20');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 4, 9, 'attended', 30.00, 'paid', DATE '2023-06-20');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 4, 10, 'attended', 30.00, 'paid', DATE '2023-06-21');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 5, 2, 'attended', 10.00, 'paid', DATE '2023-06-25');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 5, 3, 'attended', 10.00, 'paid', DATE '2023-06-25');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 5, 11, 'attended', 10.00, 'paid', DATE '2023-06-26');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 6, 1, 'attended', 25.00, 'paid', DATE '2023-06-30');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 6, 4, 'attended', 25.00, 'paid', DATE '2023-06-30');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 6, 6, 'attended', 25.00, 'paid', DATE '2023-07-01');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 2, 9, 'attended', 25.00, 'paid', DATE '2023-06-10');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 3, 10, 'attended', 15.00, 'paid', DATE '2023-06-12');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 1, 2, 'attended', 20.00, 'paid', DATE '2023-06-05');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 2, 7, 'no_show', 25.00, 'paid', DATE '2023-06-09');

-- EVENT REGISTRATIONS (future)

INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 7, 1, 'registered', 25.00, 'paid',    DATE '2025-12-15');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 7, 3, 'registered', 25.00, 'paid',    DATE '2025-12-16');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 7, 8, 'registered', 25.00, 'pending', DATE '2025-12-18');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 8, 2, 'registered', 30.00, 'paid',    DATE '2025-12-20');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 8, 5, 'registered', 30.00, 'pending', DATE '2025-12-21');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 8, 11, 'registered', 30.00, 'paid',   DATE '2025-12-22');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 9, 4, 'registered', 15.00, 'paid',    DATE '2025-12-23');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 9, 6, 'registered', 15.00, 'pending', DATE '2025-12-24');
INSERT INTO shaydenlowry.event_registration VALUES (shaydenlowry.seq_event_registration_id.NEXTVAL, 9, 9, 'registered', 15.00, 'paid',    DATE '2025-12-24');

COMMIT;