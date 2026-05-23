# Pet Café Management System

# 1. Entities and Attributes

## 1.1 Member

- **Primary Key:** member_id

- **Attributes:**
  - name
  - phone
  - email
  - date_of_birth
  - emergency_contact_name
  - emergency_contact_number
  - membership_tier_id (FK → MembershipTier)

- **Notes:** Members make reservations, participate in events, submit adoption applications, and place orders indirectly through visits.


## 1.2 MembershipTier

- **Primary Key:** tier_id

- **Attributes:**
  - name
  - discount_percent

- **Notes:** Defines discount eligibility and membership level.


## 1.3 MembershipPayment

- **Primary Key:** membership_payment_id

- **Foreign Keys:** member_id → Member, tier_id → MembershipTier

- **Attributes:**
  - payment_date
  - amount

## 1.4 Staff

- **Primary Key:** staff_id

- **Attributes:**
  - name
  - role
  - phone
  - email

- **Notes:** Staff may conduct assessments, create health records, coordinate adoptions, and manage events.


## 1.5 Room

- **Primary Key:** room_id

- **Attributes:**
  - name
  - room_type
  - max_capacity

- **Notes:** Rooms are used for reservations, events, and pet housing.


## 1.6 Pet

- **Primary Key:** pet_id

- **Attributes:**
  - name
  - species
  - breed
  - age
  - date_of_arrival
  - room_id (FK → Room)
  - temperament
  - special_needs
  - current_status

- **Notes:** Pets may have many health records, assessments, and adoption applications.


## 1.7 HealthRecord

- **Primary Key:** health_record_id

- **Foreign Keys:** pet_id → Pet, staff_id → Staff

- **Attributes:**- record_date
  - record_type
  - next_due_date
  - status

## 1.8 BehavioralAssessment

- **Primary Key:** assessment_id

- **Foreign Keys:** pet_id → Pet, staff_id → Staff
- **Attributes:**
  - assessment_date
  - notes

## 1.9 AdoptionApplication

- **Primary Key:** application_id**Foreign Keys:** pet_id → Pet, member_id → Member, coordinator_id → Staff

- **Attributes:**
  - application_date
  - status

## 1.10 AdoptionRecord

- **Primary Key:** adoption_record_id

- **Foreign Key:** application_id → AdoptionApplication

- **Attributes:**
  - adoption_date
  - adoption_fee

- **Notes:** Represents successful adoptions (1:1 relationship with approved applications).


## 1.11 Reservation

- **Primary Key:** reservation_id

- **Foreign Keys:** member_id → Member, room_id → Room

- **Attributes:**
  - reservation_date
  - start_time
  - end_time
  - status

- **Notes:** A reservation may result in exactly one Visit.


## 1.12 Visit

- **Primary Key:** visit_id

- **Foreign Key:** reservation_id → Reservation

- **Attributes:**
  - current_membership_tier
  - check_in_time
  - check_out_time

- **Notes:** Visits are required for ordering food and drinks.


## 1.13 MenuItem

- **Primary Key:** menu_item_id

- **Attributes:**
  - name
  - food_price
  - category
  - description

## 1.14 Order

- **Primary Key:** order_id

- **Foreign Key:** visit_id → Visit

- **Attributes:**
  - order_time
  - final_price
  - payment_status
  - discount_amount

- **Notes:** Members do *not* appear here to maintain normalization. Member can always be derived via:Visit → Reservation → Member.


## 1.15 OrderItem

- **Primary Key (Composite):** order_id + menu_item_id

- **Foreign Keys:** order_id → Order, menu_item_id → MenuItem

- **Attributes:**
  - quantity
  - item_price

- **Notes:** Each Order can contain multiple items.


## 1.16 Event

- **Primary Key:** event_id

- **Foreign Keys:** room_id → Room, coordinator_id → Staff

- **Attributes:**
  - name
  - event_type
  - start_date_time
  - end_date_time

## 1.17 EventRegistration

- **Primary Key:** event_registration_id

- **Foreign Keys:** event_id → Event, member_id → Member

- **Attributes:**
  - attendance_status
  - amount_paid
  - payment_status

- **Notes:** A member may register only once per event.


# 2. Relationships

This section summarizes all relationships appearing in the diagram.

### Member ↔ MembershipTier

- Many Members belong to one MembershipTier.

### Member ↔ MembershipPayment

- A Member may have many MembershipPayments.

### Member ↔ Reservation

- A Member can create many Reservations.

### Reservation ↔ Visit

- Each Reservation may generate **exactly one Visit**.
- A Visit must come from a Reservation.

### Visit ↔ Order

- A Visit can have many Orders.
- Orders cannot exist without a Visit.

### Order ↔ OrderItem

- One Order can contain multiple OrderItems.
- OrderItem uses composite primary key (order_id, menu_item_id).

### MenuItem ↔ OrderItem

- One MenuItem can appear in many OrderItems.

### Room ↔ Reservation

- A Room can be reserved multiple times.

### Room ↔ Event

- A Room may host many Events.

### Pet ↔ HealthRecord

- A Pet can have many HealthRecords.

### Pet ↔ BehavioralAssessment

- A Pet can have many assessments.

### Pet ↔ AdoptionApplication

- Many Applications may be filed for the same Pet.

### AdoptionApplication ↔ AdoptionRecord

- 1:1 relationship, only approved applications get a record.

### Staff ↔ BehavioralAssessment

- A Staff member performs assessments.

### Staff ↔ HealthRecord

- A Staff member creates HealthRecords.

### Staff ↔ AdoptionApplication

- Each application has one staff coordinator.

### Staff ↔ Event

- Each Event has one staff coordinator.

### Event ↔ EventRegistration

- A Member may register for many Events; Each Event can have many registered Members.


# 3. Cardinality Summary

| Relationship | Cardinality |
|--------------|-------------|
| Member → MembershipTier | Many-to-One |
| Member → MembershipPayment | One-to-Many |
| Member → Reservation | One-to-Many |
| Reservation → Visit | One-to-One |
| Visit → Order | One-to-Many |
| Order → OrderItem | One-to-Many |
| MenuItem → OrderItem | One-to-Many |
| Room → Reservation | One-to-Many |
| Room → Event | One-to-Many |
| Pet → HealthRecord | One-to-Many |
| Pet → BehavioralAssessment | One-to-Many |
| Pet → AdoptionApplication | One-to-Many |
| AdoptionApplication → AdoptionRecord | One-to-One |
| Staff → Assessments | One-to-Many |
| Staff → HealthRecords | One-to-Many |
| Staff → AdoptionApplications | One-to-Many |
| Staff → Events | One-to-Many |
| Event → EventRegistration | One-to-Many |
| Member → EventRegistration | One-to-Many |


# 4. Normalization Notes

### 4.1 BCNF Compliance
- **Order** does not store `member_id`, preventing FD violation  `order_id → visit_id → reservation_id → member_id`  which would otherwise break BCNF.

- **OrderItem** uses a composite key, adhering to correct decomposition.

- **Visit** stores a *snapshot* of membership tier to preserve historical pricing integrity without violating FD constraints.

### 4.2 Derived Attributes
The following can be derived and thus excluded from storage:

- Member of an Order: Visit → Reservation → Member- Current tier during visit: captured as a snapshot- Order subtotal: sum(OrderItem.quantity × OrderItem.item_price)
