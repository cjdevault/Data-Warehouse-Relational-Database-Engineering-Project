-- Pet Cafe Database Schema
-- Schema: (oracle username prefix).[schema name]
-- NOTES:
-- replace [oracle username prefix] with your actual username prefix
-- replace [schema name] with your actual schema name 
-- Database: Oracle on aloe.cs.arizona.edu

-- Sequences for auto-incrementing IDs
CREATE SEQUENCE [oracle username prefix].seq_member_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_pet_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_room_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_reservation_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_visit_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_order_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_health_record_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_behavioral_assessment_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_adoption_application_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_adoption_record_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_event_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_membership_payment_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_staff_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_menu_item_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE [oracle username prefix].seq_event_registration_id START WITH 1 INCREMENT BY 1;

-- MEMBERSHIP TIER TABLE
CREATE TABLE [oracle username prefix].membership_tier (
    tier_id NUMBER PRIMARY KEY, 
    name VARCHAR2(50) NOT NULL, 
    discount_percentage NUMBER NOT NULL CHECK (discount_percentage BETWEEN 0 AND 100)
);

-- MEMBER TABLE
CREATE TABLE [oracle username prefix].member (
    member_id NUMBER PRIMARY KEY,
    name VARCHAR2(50) NOT NULL,
    phone VARCHAR2(20) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    date_of_birth DATE NOT NULL,
    emergency_contact_name VARCHAR2(100),
    emergency_contact_phone VARCHAR2(20),
    membership_tier_id NUMBER NOT NULL,
    registration_date DATE DEFAULT SYSDATE,
    CONSTRAINT fk_member_tier FOREIGN KEY (membership_tier_id) 
        REFERENCES [oracle username prefix].membership_tier(tier_id)
);

-- MEMBERSHIP PAYMENT TABLE 
CREATE TABLE [oracle username prefix].membership_payment (
    membership_payment_id NUMBER PRIMARY KEY,
    member_id NUMBER NOT NULL,
    tier_id NUMBER NOT NULL,
    payment_date DATE NOT NULL,
    amount NUMBER(10,2) NOT NULL CHECK (amount > 0),
    CONSTRAINT fk_payment_member FOREIGN KEY (member_id) 
        REFERENCES [oracle username prefix].member(member_id),
    CONSTRAINT fk_payment_tier FOREIGN KEY (tier_id) 
        REFERENCES [oracle username prefix].membership_tier(tier_id)
);

-- ROOM TABLE
CREATE TABLE [oracle username prefix].room (
    room_id NUMBER PRIMARY KEY,
    room_name VARCHAR2(50) NOT NULL,
    room_type VARCHAR2(30) NOT NULL CHECK (room_type IN ('cat', 'dog', 'small_animal', 'adoption_consultation')),
    max_capacity NUMBER NOT NULL CHECK (max_capacity > 0),
    purpose VARCHAR2(100) NOT NULL,
    CONSTRAINT chk_room_type CHECK (room_type IN ('cat', 'dog', 'small_animal', 'adoption_consultation'))
);

-- STAFF TABLE 
CREATE TABLE [oracle username prefix].staff (
    staff_id NUMBER PRIMARY KEY,
    name VARCHAR2(50) NOT NULL,
    role VARCHAR2(30) NOT NULL CHECK (role IN ('handler', 'vet', 'barista', 'coordinator', 'manager')),
    email VARCHAR2(100) UNIQUE NOT NULL,
    phone VARCHAR2(20) NOT NULL,
    hire_date DATE NOT NULL,
    CONSTRAINT chk_role CHECK (role IN ('handler', 'vet', 'barista', 'coordinator', 'manager'))
);

-- PET TABLE
CREATE TABLE [oracle username prefix].pet (
    pet_id NUMBER PRIMARY KEY,
    name VARCHAR2(50) NOT NULL,
    species VARCHAR2(30) NOT NULL,
    breed VARCHAR2(50),
    age NUMBER,
    date_of_arrival DATE,
    room_id NUMBER,
    temperament VARCHAR2(100),
    special_needs VARCHAR2(500),
    current_status VARCHAR2(30) NOT NULL,
    CONSTRAINT fk_pet_room FOREIGN KEY (room_id) 
        REFERENCES [oracle username prefix].room(room_id),
    CONSTRAINT chk_pet_status CHECK (current_status IN ('available_adoption', 'pending', 'adopted', 'resident', 'sick', 'deceased'))
);

-- HEALTH RECORD TABLE
CREATE TABLE [oracle username prefix].health_record (
    health_record_id NUMBER PRIMARY KEY,
    pet_id NUMBER NOT NULL,
    staff_id NUMBER NOT NULL,
    record_date DATE NOT NULL,
    record_type VARCHAR2(50) NOT NULL,
    next_due_date DATE,
    status VARCHAR2(50),
    notes VARCHAR2(1000),
    CONSTRAINT fk_health_pet FOREIGN KEY (pet_id) 
        REFERENCES [oracle username prefix].pet(pet_id),
    CONSTRAINT fk_health_staff FOREIGN KEY (staff_id) 
        REFERENCES [oracle username prefix].staff(staff_id),
    CONSTRAINT chk_health_type CHECK (record_type IN ('vaccination', 'checkup', 'grooming', 'feeding', 'medication', 'other'))
);

-- BEHAVIORAL ASSESSMENT TABLE
CREATE TABLE [oracle username prefix].behavioral_assessment (
    assessment_id NUMBER PRIMARY KEY,
    pet_id NUMBER NOT NULL,
    staff_id NUMBER NOT NULL,
    assessment_date DATE NOT NULL,
    assessment_result VARCHAR2(50) NOT NULL,
    notes VARCHAR2(1000),
    CONSTRAINT fk_assessment_pet FOREIGN KEY (pet_id) 
        REFERENCES [oracle username prefix].pet(pet_id),
    CONSTRAINT fk_assessment_staff FOREIGN KEY (staff_id) 
        REFERENCES [oracle username prefix].staff(staff_id),
    CONSTRAINT chk_assessment_result CHECK (assessment_result IN ('approved', 'conditional', 'not_approved', 'pending'))
);

-- ADOPTION APPLICATION TABLE
CREATE TABLE [oracle username prefix].adoption_application (
    application_id NUMBER PRIMARY KEY,
    pet_id NUMBER NOT NULL,
    member_id NUMBER NOT NULL,
    application_date DATE NOT NULL,
    status VARCHAR2(20) NOT NULL,
    coordinator_id NUMBER NOT NULL,
    CONSTRAINT fk_app_pet FOREIGN KEY (pet_id)
        REFERENCES [oracle username prefix].pet(pet_id),
    CONSTRAINT fk_app_member FOREIGN KEY (member_id)
        REFERENCES [oracle username prefix].member(member_id),
    CONSTRAINT fk_app_coordinator FOREIGN KEY (coordinator_id)
        REFERENCES [oracle username prefix].staff(staff_id),
    CONSTRAINT chk_app_status CHECK (status IN ('pending', 'approved', 'rejected', 'withdrawn'))
);


-- ADOPTION RECORD TABLE 
CREATE TABLE [oracle username prefix].adoption_record (
    adoption_record_id NUMBER PRIMARY KEY,
    application_id NUMBER NOT NULL UNIQUE,
    adoption_date DATE NOT NULL,
    adoption_fee NUMBER(10,2) NOT NULL CHECK (adoption_fee >= 0),
    follow_up_date DATE,
    notes VARCHAR2(1000),
    CONSTRAINT fk_adoption_app FOREIGN KEY (application_id) 
        REFERENCES [oracle username prefix].adoption_application(application_id)
);

-- RESERVATION TABLE
CREATE TABLE [oracle username prefix].reservation (
    reservation_id NUMBER PRIMARY KEY,
    member_id NUMBER NOT NULL,
    room_id NUMBER NOT NULL,
    reservation_date DATE NOT NULL,
    reservation_time TIMESTAMP NOT NULL,
    duration_hours NUMBER(3,1) NOT NULL CHECK (duration_hours > 0 AND duration_hours <= 8),
    status VARCHAR2(20) DEFAULT 'confirmed' NOT NULL,
    CONSTRAINT fk_reservation_member FOREIGN KEY (member_id) 
        REFERENCES [oracle username prefix].member(member_id),
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) 
        REFERENCES [oracle username prefix].room(room_id),
    CONSTRAINT chk_reservation_status CHECK (status IN ('confirmed', 'cancelled', 'completed'))
);

-- VISIT TABLE
CREATE TABLE [oracle username prefix].visit (
    visit_id NUMBER PRIMARY KEY,
    reservation_id NUMBER UNIQUE,
    current_membership_tier VARCHAR2(50),
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    CONSTRAINT fk_visit_reservation FOREIGN KEY (reservation_id) 
        REFERENCES [oracle username prefix].reservation(reservation_id),
    CONSTRAINT chk_visit_times CHECK (check_out_time IS NULL OR check_out_time >= check_in_time)
);

-- MENU ITEM TABLE
CREATE TABLE [oracle username prefix].menu_item (
    menu_item_id NUMBER PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    food_price NUMBER(10,2) NOT NULL CHECK (food_price >= 0),
    category VARCHAR2(50),
    description VARCHAR2(500),
    CONSTRAINT chk_menu_category CHECK (category IN ('coffee', 'tea', 'pastry', 'light_meal', 'specialty_drink', 'other'))
);

-- ORDER TABLE (Normalized - member_id removed)
CREATE TABLE [oracle username prefix].cafe_order (
    order_id NUMBER PRIMARY KEY,
    visit_id NUMBER NOT NULL,
    order_time TIMESTAMP NOT NULL,
    final_price NUMBER(10,2) NOT NULL CHECK (final_price >= 0),
    payment_status VARCHAR2(20) NOT NULL,
    discount_amount NUMBER(10,2) DEFAULT 0 CHECK (discount_amount >= 0),
    CONSTRAINT fk_order_visit FOREIGN KEY (visit_id) 
        REFERENCES [oracle username prefix].visit(visit_id),
    CONSTRAINT chk_payment_status CHECK (payment_status IN ('pending', 'paid', 'refunded'))
);

-- ORDER ITEM TABLE (Composite PK)
CREATE TABLE [oracle username prefix].order_item (
    order_id NUMBER NOT NULL,
    menu_item_id NUMBER NOT NULL,
    quantity NUMBER NOT NULL CHECK (quantity > 0),
    item_price NUMBER(10,2) NOT NULL CHECK (item_price >= 0),
    CONSTRAINT pk_order_item PRIMARY KEY (order_id, menu_item_id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) 
        REFERENCES [oracle username prefix].cafe_order(order_id),
    CONSTRAINT fk_order_item_menu FOREIGN KEY (menu_item_id) 
        REFERENCES [oracle username prefix].menu_item(menu_item_id)
);

-- EVENT TABLE
CREATE TABLE [oracle username prefix].event (
    event_id NUMBER PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    room_id NUMBER NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    max_capacity NUMBER NOT NULL CHECK (max_capacity > 0),
    event_type VARCHAR2(50),
    description VARCHAR2(1000),
    coordinator_id NUMBER NOT NULL,
    CONSTRAINT fk_event_coordinator FOREIGN KEY (coordinator_id)
        REFERENCES [oracle username prefix].staff(staff_id),
    CONSTRAINT fk_event_room FOREIGN KEY (room_id) 
        REFERENCES [oracle username prefix].room(room_id),
    CONSTRAINT chk_event_times CHECK (end_date_time > start_date_time),
    CONSTRAINT chk_event_type CHECK (event_type IN ('workshop', 'training', 'adoption_event', 'birthday_party', 'other'))
);

-- EVENT REGISTRATION TABLE
CREATE TABLE [oracle username prefix].event_registration (
    event_registration_id NUMBER PRIMARY KEY,
    event_id NUMBER NOT NULL,
    member_id NUMBER NOT NULL,
    attendance_status VARCHAR2(30),
    amount_paid NUMBER(10,2) DEFAULT 0 CHECK (amount_paid >= 0),
    payment_status VARCHAR2(20) NOT NULL,
    registration_date DATE DEFAULT SYSDATE,
    CONSTRAINT fk_reg_event FOREIGN KEY (event_id) 
        REFERENCES [oracle username prefix].event(event_id),
    CONSTRAINT fk_reg_member FOREIGN KEY (member_id) 
        REFERENCES [oracle username prefix].member(member_id),
    CONSTRAINT chk_attendance_status CHECK (attendance_status IN ('registered', 'attended', 'no_show', 'cancelled')),
    CONSTRAINT chk_reg_payment_status CHECK (payment_status IN ('pending', 'paid', 'refunded')),
    CONSTRAINT uk_event_member UNIQUE (event_id, member_id)  -- One registration per member per event
);

-- INDEXES FOR PERFORMANCE
CREATE INDEX idx_member_phone ON [oracle username prefix].member(phone);
CREATE INDEX idx_pet_status ON [oracle username prefix].pet(current_status);
CREATE INDEX idx_reservation_date ON [oracle username prefix].reservation(reservation_date);
CREATE INDEX idx_reservation_member ON [oracle username prefix].reservation(member_id);
CREATE INDEX idx_order_visit ON [oracle username prefix].cafe_order(visit_id);
CREATE INDEX idx_health_pet ON [oracle username prefix].health_record(pet_id);
CREATE INDEX idx_app_pet ON [oracle username prefix].adoption_application(pet_id);
CREATE INDEX idx_app_member ON [oracle username prefix].adoption_application(member_id);
CREATE INDEX idx_event_date ON [oracle username prefix].event(start_date_time);

-- GRANT PERMISSIONS (if needed)
-- GRANT SELECT ON [oracle username prefix].member TO PUBLIC;
-- (Add other grants as needed) 