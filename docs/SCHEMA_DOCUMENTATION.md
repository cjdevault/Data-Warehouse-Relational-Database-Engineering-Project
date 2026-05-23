# Database Schema Documentation

## 1. MembershipTier

**Purpose:**Defines membership tiers (e.g., Day Pass, Monthly) and associated discounts.

**Attributes:**

- `tier_id` NUMBER, PRIMARY KEY- `name` VARCHAR2(50), unique tier name- `discount_percent` NUMBER(5,2), percentage discount

**Relationships:**

- One MembershipTier → Many Members- One MembershipTier → Many MembershipPayments

**Constraints:**

- `discount_percent` must be between 0 and 100.
- `name` should be unique.


## 2. MembershipPayment

**Purpose:**Tracks payments made by members for membership tiers.

**Attributes:**

- `membership_payment_id` NUMBER, PRIMARY KEY- `member_id` NUMBER, FK → Member(member_id)- `tier_id` NUMBER, FK → MembershipTier(tier_id)- `payment_date` DATE- `amount` NUMBER(8,2)

**Relationships:**

- Many MembershipPayments → One Member- Many MembershipPayments → One MembershipTier

**Constraints:**

- `amount` must be positive.


## 3. Member

**Purpose:**Stores registered customers who interact with the café (reservations, visits, adoptions, event registrations, orders).

**Attributes:**

- `member_id` NUMBER, PRIMARY KEY- `name` VARCHAR2(100)- `phone` VARCHAR2(20)- `email` VARCHAR2(100)- `date_of_birth` DATE- `emergency_contact_name` VARCHAR2(100)- `emergency_contact_number` VARCHAR2(20)- `membership_tier_id` NUMBER, FK → MembershipTier(tier_id)

**Relationships:**

- One Member → Many Reservations- One Member → Many Visits (via Reservations)- One Member → Many MembershipPayments- One Member → Many AdoptionApplications- One Member → Many EventRegistrations
**Constraints:**

- `email` should be unique.
- `membership_tier_id` must reference a valid MembershipTier.


## 4. Staff

**Purpose:**Represents employees and volunteers, including veterinarians and coordinators.

**Attributes:**

- `staff_id` NUMBER, PRIMARY KEY- `name` VARCHAR2(100)- `role` VARCHAR2(50)- `phone` VARCHAR2(20)- `email` VARCHAR2(100)

**Relationships:**

- One Staff → Many HealthRecords (created_by)- One Staff → Many BehavioralAssessments- One Staff → Many AdoptionApplications (coordinator)- One Staff → Many Events (coordinator)

**Constraints:**

- `email` ideally unique at the application level.


## 5. Room

**Purpose:**Represents physical spaces: cat lounges, private rooms, event areas.

**Attributes:**

- `room_id` NUMBER, PRIMARY KEY- `name` VARCHAR2(50)- `room_type` VARCHAR2(30)- `max_capacity` NUMBER

**Relationships:**

- One Room → Many Pets (housed_in)- One Room → Many Reservations- One Room → Many Events

**Constraints:**

- `max_capacity` must be positive.
- `name` should uniquely identify rooms.


## 6. Pet

**Purpose:**Stores information about all animals housed at or managed by the café.

**Attributes:**

- `pet_id` NUMBER, PRIMARY KEY- `name` VARCHAR2(50)- `species` VARCHAR2(30)- `breed` VARCHAR2(50)- `age` NUMBER- `date_of_arrival` DATE- `room_id` NUMBER, FK → Room(room_id)- `temperament` VARCHAR2(100)- `special_needs` VARCHAR2(200)- `current_status` VARCHAR2(30)

**Relationships:**

- One Pet → Many HealthRecords- One Pet → Many BehavioralAssessments- One Pet → Many AdoptionApplications- One Pet → At most one AdoptionRecord (via AdoptionApplication)

**Constraints:**

- `current_status` uses a controlled set (available_adoption, resident, sick, adopted, etc.).
- `room_id` may be NULL for pets off-site.


## 7. HealthRecord

**Purpose:**Tracks medical and health-related events for pets.

**Attributes:**

- `health_record_id` NUMBER, PRIMARY KEY- `pet_id` NUMBER, FK → Pet(pet_id)- `staff_id` NUMBER, FK → Staff(staff_id)- `record_date` DATE- `record_type` VARCHAR2(50)- `next_due_date` DATE- `status` VARCHAR2(30)

**Relationships:**

- Many HealthRecords → One Pet- Many HealthRecords → One Staff (recorder)

**Constraints:**

- `record_date` cannot be in the future (enforced at application level).
- `status` typically from a controlled set (scheduled, completed, canceled).


## 8. BehavioralAssessment

**Purpose:**Represents behavioral evaluations of pets performed by staff.

**Attributes:**

- `assessment_id` NUMBER, PRIMARY KEY- `pet_id` NUMBER, FK → Pet(pet_id)- `staff_id` NUMBER, FK → Staff(staff_id)- `assessment_date` DATE- `notes` VARCHAR2(4000) (optional)

**Relationships:**

- Many BehavioralAssessments → One Pet- Many BehavioralAssessments → One Staff

**Constraints:**

- Optionally, one assessment per pet per day (enforced at app level).


## 9. AdoptionApplication

**Purpose:**Captures applications from members to adopt specific pets.

**Attributes:**

- `application_id` NUMBER, PRIMARY KEY- `pet_id` NUMBER, FK → Pet(pet_id)- `member_id` NUMBER, FK → Member(member_id)- `application_date` DATE- `status` VARCHAR2(30)- `coordinator_id` NUMBER, FK → Staff(staff_id)

**Relationships:**

- Many AdoptionApplications → One Pet- Many AdoptionApplications → One Member- Many AdoptionApplications → One Staff (coordinator)- One AdoptionApplication → Zero or One AdoptionRecord

**Constraints:**

- `status` from controlled set (pending, approved, denied, withdrawn).


## 10. AdoptionRecord

**Purpose:**Represents successful adoptions linked to approved applications.

**Attributes:**

- `adoption_record_id` NUMBER, PRIMARY KEY- `application_id` NUMBER, FK → AdoptionApplication(application_id)- `adoption_date` DATE- `adoption_fee` NUMBER(8,2)

**Relationships:**

- One AdoptionApplication → One AdoptionRecord (1:1)
**Constraints:**

- `application_id` must reference an approved application.
- `adoption_fee` must be non-negative (enforced at app level).


## 11. Reservation

**Purpose:**Stores bookings made by members to use rooms at specific times.

**Attributes:**

- `reservation_id` NUMBER, PRIMARY KEY- `member_id` NUMBER, FK → Member(member_id)- `room_id` NUMBER, FK → Room(room_id)- `reservation_date` DATE- `start_time` TIMESTAMP- `end_time` TIMESTAMP- `status` VARCHAR2(20)

**Relationships:**

- One Member → Many Reservations- One Room → Many Reservations- One Reservation → One Visit

**Constraints:**

- `start_time < end_time`.- `status` uses values like booked, completed, cancelled, no_show.


## 12. Visit

**Purpose:**Represents an actual in-café visit derived from a reservation.

**Attributes:**

- `visit_id` NUMBER, PRIMARY KEY- `reservation_id` NUMBER, FK → Reservation(reservation_id)- `current_membership_tier` VARCHAR2(50)- `check_in_time` TIMESTAMP- `check_out_time` TIMESTAMP (nullable)

**Relationships:**

- One Reservation → One Visit- One Visit → Many Orders
**Constraints:**

- `check_in_time` set at check-in.- `check_out_time` must be ≥ `check_in_time` if not NULL.- `current_membership_tier` is a snapshot and not updated retroactively.


## 13. MenuItem

**Purpose:**Stores the café’s menu offerings.

**Attributes:**

- `menu_item_id` NUMBER, PRIMARY KEY- `name` VARCHAR2(100)- `food_price` NUMBER(6,2)- `category` VARCHAR2(50)- `description` VARCHAR2(200)

**Relationships:**

- One MenuItem → Many OrderItems

**Constraints:**

- `food_price` must be ≥ 0.- `name` should be unique to avoid ambiguity.


## 14. Order

**Purpose:**Represents a single billable order placed during a visit, possibly containing multiple items.

**Attributes:**

- `order_id` NUMBER, PRIMARY KEY- `visit_id` NUMBER, FK → Visit(visit_id)- `order_time` TIMESTAMP- `final_price` NUMBER(8,2)- `payment_status` VARCHAR2(20)- `discount_amount` NUMBER(8,2)

**Relationships:**

- One Visit → Many Orders- One Order → Many OrderItems
**Constraints:**

- `final_price` and `discount_amount` are computed from OrderItems and discounts.- `payment_status` typically pending, paid, or void.- `member_id` is not stored, as it can be derived from Visit/Reservation/Member (BCNF design).


## 15. OrderItem

**Purpose:**Line items within an Order, tying menu items to quantities and prices.

**Attributes:**

- `order_id` NUMBER, FK → Order(order_id)- `menu_item_id` NUMBER, FK → MenuItem(menu_item_id)- `quantity` NUMBER- `item_price` NUMBER(6,2)

**Primary Key:** `(order_id, menu_item_id)`

**Relationships:**

- Many OrderItems → One Order- Many OrderItems → One MenuItem
**Constraints:**

- `quantity` must be > 0.- `item_price` is the effective price at the time of ordering (may differ from current MenuItem price).


## 16. Event

**Purpose:**Represents scheduled events (workshops, parties, adoption days, etc.).

**Attributes:**

- `event_id` NUMBER, PRIMARY KEY- `name` VARCHAR2(100)- `event_type` VARCHAR2(50)- `room_id` NUMBER, FK → Room(room_id)- `start_date_time` TIMESTAMP- `end_date_time` TIMESTAMP- `coordinator_id` NUMBER, FK → Staff(staff_id)

**Relationships:**

- One Room → Many Events- One Staff → Many Events (as coordinator)- One Event → Many EventRegistrations
**Constraints:**

- `start_date_time < end_date_time`.- `event_type` comes from a controlled set (e.g., workshop, training, adoption_event).


## 17. EventRegistration

**Purpose:**Stores sign-ups and payments for events by members.

**Attributes:**

- `event_registration_id` NUMBER, PRIMARY KEY- `event_id` NUMBER, FK → Event(event_id)- `member_id` NUMBER, FK → Member(member_id)- `attendance_status` VARCHAR2(20)- `amount_paid` NUMBER(8,2)- `payment_status` VARCHAR2(20)

**Relationships:**

- One Event → Many EventRegistrations- One Member → Many EventRegistrations
**Constraints:**

- A member may register at most once per event: Unique(event_id, member_id).- `attendance_status` uses values like registered, attended, no_show.- `amount_paid` must be ≥ 0.
