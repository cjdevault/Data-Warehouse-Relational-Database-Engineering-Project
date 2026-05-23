# Data Dictionary

## MembershipTier

| Column           | Type           | Null? | Constraints / Notes                              |
|------------------|----------------|-------|--------------------------------------------------|
| tier_id          | NUMBER         | NO    | Primary key                                      |
| name             | VARCHAR2(50)   | NO    | Unique tier name (e.g., Day Pass, Monthly)       |
| discount_percent | NUMBER(5,2)    | NO    | Percentage discount applied to eligible charges  |


## MembershipPayment

| Column               | Type           | Null? | Constraints / Notes                            |
|----------------------|----------------|-------|------------------------------------------------|
| membership_payment_id| NUMBER         | NO    | Primary key                                    |
| member_id            | NUMBER         | NO    | FK → Member(member_id)                         |
| tier_id              | NUMBER         | NO    | FK → MembershipTier(tier_id)                   |
| payment_date         | DATE           | NO    | Date payment was recorded                      |
| amount               | NUMBER(8,2)    | NO    | Amount charged for membership                  |


## Member

| Column                   | Type           | Null? | Constraints / Notes                                  |
|--------------------------|----------------|-------|------------------------------------------------------|
| member_id                | NUMBER         | NO    | Primary key                                          |
| name                     | VARCHAR2(100)  | NO    |                                                      |
| phone                    | VARCHAR2(20)   | NO    |                                                      |
| email                    | VARCHAR2(100)  | NO    | Unique                                               |
| date_of_birth            | DATE           | NO    |                                                      |
| emergency_contact_name   | VARCHAR2(100)  | YES   |                                                      |
| emergency_contact_number | VARCHAR2(20)   | YES   |                                                      |
| membership_tier_id       | NUMBER         | NO    | FK → MembershipTier(tier_id), current tier          |


## Staff

| Column   | Type           | Null? | Constraints / Notes                     |
|----------|----------------|-------|-----------------------------------------|
| staff_id | NUMBER         | NO    | Primary key                             |
| name     | VARCHAR2(100)  | NO    |                                         |
| role     | VARCHAR2(50)   | NO    | e.g., Vet, Barista, Adoption Coordinator|
| phone    | VARCHAR2(20)   | NO    |                                         |
| email    | VARCHAR2(100)  | NO    | Should be unique                        |


## Room

| Column     | Type           | Null? | Constraints / Notes              |
|------------|----------------|-------|----------------------------------|
| room_id    | NUMBER         | NO    | Primary key                      |
| name       | VARCHAR2(50)   | NO    | Unique room name                 |
| room_type  | VARCHAR2(30)   | NO    | e.g., Cat Lounge, Training Room  |
| max_capacity | NUMBER       | NO    | Must be positive                 |


## Pet

| Column        | Type           | Null? | Constraints / Notes                                              |
|---------------|----------------|-------|------------------------------------------------------------------|
| pet_id        | NUMBER         | NO    | Primary key                                                      |
| name          | VARCHAR2(50)   | NO    |                                                                  |
| species       | VARCHAR2(30)   | NO    | e.g., Cat, Dog                                                   |
| breed         | VARCHAR2(50)   | YES   |                                                                  |
| age           | NUMBER         | YES   | Age in years (approximate)                                      |
| date_of_arrival | DATE         | NO    | Date the pet entered the café                                   |
| room_id       | NUMBER         | YES   | FK → Room(room_id), where the pet is primarily housed           |
| temperament   | VARCHAR2(100)  | YES   | Short description of behavior                                   |
| special_needs | VARCHAR2(200)  | YES   | Medical/dietary/behavioral needs                                |
| current_status| VARCHAR2(30)   | NO    | e.g., available_adoption, resident, sick, adopted, deceased     |


## HealthRecord

| Column          | Type           | Null? | Constraints / Notes                                 |
|-----------------|----------------|-------|-----------------------------------------------------|
| health_record_id| NUMBER         | NO    | Primary key                                         |
| pet_id          | NUMBER         | NO    | FK → Pet(pet_id)                                    |
| staff_id        | NUMBER         | NO    | FK → Staff(staff_id), who recorded/administered care|
| record_date     | DATE           | NO    | Date of the record                                  |
| record_type     | VARCHAR2(50)   | NO    | e.g., Vaccination, Checkup, Medication              |
| next_due_date   | DATE           | YES   | When follow-up is due                               |
| status          | VARCHAR2(30)   | NO    | e.g., scheduled, completed, canceled                |


## BehavioralAssessment

| Column          | Type           | Null? | Constraints / Notes                                |
|-----------------|----------------|-------|----------------------------------------------------|
| assessment_id   | NUMBER         | NO    | Primary key                                        |
| pet_id          | NUMBER         | NO    | FK → Pet(pet_id)                                   |
| staff_id        | NUMBER         | NO    | FK → Staff(staff_id), assessor                     |
| assessment_date | DATE           | NO    | Date of the behavior assessment                    |
| notes           | VARCHAR2(4000) | YES   | Optional detailed notes (if implemented)           |


## AdoptionApplication

| Column        | Type           | Null? | Constraints / Notes                                |
|---------------|----------------|-------|----------------------------------------------------|
| application_id| NUMBER         | NO    | Primary key                                        |
| pet_id        | NUMBER         | NO    | FK → Pet(pet_id)                                   |
| member_id     | NUMBER         | NO    | FK → Member(member_id), applicant                  |
| application_date | DATE        | NO    | Date application was submitted                     |
| status        | VARCHAR2(30)   | NO    | e.g., pending, approved, denied, withdrawn         |
| coordinator_id| NUMBER         | NO    | FK → Staff(staff_id), managing coordinator         |


## AdoptionRecord

| Column             | Type           | Null? | Constraints / Notes                              |
|--------------------|----------------|-------|--------------------------------------------------|
| adoption_record_id | NUMBER         | NO    | Primary key                                      |
| application_id     | NUMBER         | NO    | FK → AdoptionApplication(application_id), 1:1     |
| adoption_date      | DATE           | NO    | Date adoption finalized                          |
| adoption_fee       | NUMBER(8,2)    | NO    | Final fee charged                                |


## Reservation

| Column          | Type           | Null? | Constraints / Notes                                |
|-----------------|----------------|-------|----------------------------------------------------|
| reservation_id  | NUMBER         | NO    | Primary key                                        |
| member_id       | NUMBER         | NO    | FK → Member(member_id)                             |
| room_id         | NUMBER         | NO    | FK → Room(room_id)                                 |
| reservation_date| DATE           | NO    | General date of reservation                        |
| start_time      | TIMESTAMP      | NO    | Start date/time of reservation                     |
| end_time        | TIMESTAMP      | NO    | End date/time of reservation                       |
| status          | VARCHAR2(20)   | NO    | e.g., booked, completed, cancelled, no_show       |


## Visit

| Column                | Type           | Null? | Constraints / Notes                                      |
|-----------------------|----------------|-------|----------------------------------------------------------|
| visit_id              | NUMBER         | NO    | Primary key                                              |
| reservation_id        | NUMBER         | NO    | FK → Reservation(reservation_id), 1:1 relationship       |
| current_membership_tier | VARCHAR2(50) | NO    | Snapshot of member's tier during visit                   |
| check_in_time         | TIMESTAMP      | NO    | Set at check-in                                          |
| check_out_time        | TIMESTAMP      | YES   | Set at check-out, must be ≥ check_in_time               |


## MenuItem

| Column       | Type           | Null? | Constraints / Notes                        |
|--------------|----------------|-------|--------------------------------------------|
| menu_item_id | NUMBER         | NO    | Primary key                                |
| name         | VARCHAR2(100)  | NO    | Unique item name                           |
| food_price   | NUMBER(6,2)    | NO    | Must be ≥ 0                                |
| category     | VARCHAR2(50)   | NO    | e.g., beverage, snack, dessert, meal       |
| description  | VARCHAR2(200)  | YES   | Optional description                       |


## Order

| Column        | Type           | Null? | Constraints / Notes                                        |
|---------------|----------------|-------|------------------------------------------------------------|
| order_id      | NUMBER         | NO    | Primary key                                                |
| visit_id      | NUMBER         | NO    | FK → Visit(visit_id)                                       |
| order_time    | TIMESTAMP      | NO    | When the order was placed                                  |
| final_price   | NUMBER(8,2)    | NO    | Final total after discounts (0 if no items / not finalized)|
| payment_status| VARCHAR2(20)   | NO    | e.g., pending, paid, void                                  |
| discount_amount | NUMBER(8,2)  | NO    | Total discount applied to this order                       |

*Note:* `member_id` is intentionally not stored here; it is derivable via `visit_id → Reservation → Member`. :contentReference[oaicite:1]{index=1}  


## OrderItem

| Column       | Type           | Null? | Constraints / Notes                                           |
|--------------|----------------|-------|---------------------------------------------------------------|
| order_id     | NUMBER         | NO    | Part of composite PK, FK → Order(order_id)                    |
| menu_item_id | NUMBER         | NO    | Part of composite PK, FK → MenuItem(menu_item_id)             |
| quantity     | NUMBER         | NO    | Must be > 0                                                   |
| item_price   | NUMBER(6,2)    | NO    | Price used for this item (snapshot, may duplicate food_price) |

**Primary Key:** `(order_id, menu_item_id)`


## Event

| Column         | Type           | Null? | Constraints / Notes                               |
|----------------|----------------|-------|---------------------------------------------------|
| event_id       | NUMBER         | NO    | Primary key                                       |
| name           | VARCHAR2(100)  | NO    |                                                   |
| room_id        | NUMBER         | NO    | FK → Room(room_id)                                |
| start_date_time| TIMESTAMP      | NO    | Must be before end_date_time                      |
| end_date_time  | TIMESTAMP      | NO    | Must be after start_date_time                     |
| event_type     | VARCHAR2(50)   | NO    | e.g., workshop, training, adoption_event, party   |
| coordinator_id | NUMBER         | NO    | FK → Staff(staff_id), primary event contact       |


## EventRegistration

| Column              | Type           | Null? | Constraints / Notes                                      |
|---------------------|----------------|-------|----------------------------------------------------------|
| event_registration_id | NUMBER       | NO    | Primary key                                              |
| event_id            | NUMBER         | NO    | FK → Event(event_id)                                     |
| member_id           | NUMBER         | NO    | FK → Member(member_id)                                   |
| attendance_status   | VARCHAR2(20)   | NO    | e.g., registered, attended, no_show                      |
| amount_paid         | NUMBER(8,2)    | NO    | Amount paid for this registration                        |
| payment_status      | VARCHAR2(20)   | NO    | e.g., pending, paid, refunded                            |

**Additional Constraint:** Unique(event_id, member_id) to prevent duplicate registrations.
