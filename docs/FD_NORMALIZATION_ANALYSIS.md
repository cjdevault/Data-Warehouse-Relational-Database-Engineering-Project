# Functional Dependency and Normalization Analysis

---

## 1. MEMBERS

**Schema**

`MEMBERS(member_id, name, phone, email, date_of_birth, emergency_contact_name, emergency_contact_phone, membership_tier)`

**Business constraints**

* `member_id` is the primary key.
* `email` is unique.

### 1.1 Candidate Keys

* `{member_id}`
* `{email}`

Both uniquely identify a member.

### 1.2 Functional Dependencies

1. `member_id → name, phone, email, date_of_birth, emergency_contact_name, emergency_contact_phone, membership_tier`
2. `email → member_id, name, phone, date_of_birth, emergency_contact_name, emergency_contact_phone, membership_tier`

(From 1 and 2, `member_id ↔ email`, but we usually list them as separate FDs.)

### 1.3 Partial Dependencies

* Candidate keys are single attributes, so no proper subset exists.
* **No partial dependencies.**

### 1.4 Transitive Dependencies

* All non-key attributes depend directly on the keys (`member_id` or `email`).
* There is no non-key attribute that determines another non-key attribute as a design rule (e.g., `membership_tier` does *not* determine any other attribute in this table).

**No transitive dependencies.**

### 1.5 Normal Form

* **2NF:** Trivially satisfied (no composite keys).
* **3NF:** For each non-trivial FD `X → A`, either:

  * `X` is a superkey (`member_id`, `email`), or
  * `A` is part of some candidate key (e.g., `member_id → email` and `email` is a key attribute).
    → **MEMBERS is in 3NF.**
* **BCNF:** Every non-trivial FD has a superkey on the LHS (`member_id` and `email` are superkeys).
  → **MEMBERS is in BCNF.**

---

## 2. PETS

**Schema**

`PETS(pet_id, name, species, breed, adoption_status, arrival_date)`

**Business constraints**

* `pet_id` is the primary key.

### 2.1 Candidate Keys

* `{pet_id}`

### 2.2 Functional Dependencies

1. `pet_id → name, species, breed, adoption_status, arrival_date`

### 2.3 Partial Dependencies

* Key is single attribute → **no partial dependencies.**

### 2.4 Transitive Dependencies

* All non-key attributes depend directly on `pet_id`.
* No non-key attribute determines another by schema design.

**No transitive dependencies.**

### 2.5 Normal Form

* **2NF:** Trivially satisfied.
* **3NF:** Only FD has `pet_id` (a key) on LHS.
* **BCNF:** Every non-trivial FD has a key determinant.

→ **PETS is in 3NF and BCNF.**

---

## 3. ROOMS

**Schema**

`ROOMS(room_id, room_name, room_type, capacity, purpose)`

**Business constraints**

* `room_id` is the primary key.

### 3.1 Candidate Keys

* `{room_id}`

### 3.2 Functional Dependencies

1. `room_id → room_name, room_type, capacity, purpose`

### 3.3 Partial Dependencies

* Single-attribute key → **no partial dependencies.**

### 3.4 Transitive Dependencies

* All non-key attributes depend directly on `room_id`.
* No non-key attribute is declared to determine any other non-key attribute.

**No transitive dependencies.**

### 3.5 Normal Form

* **3NF & BCNF:** Only FD determinant is a key.

→ **ROOMS is in 3NF and BCNF.**

---

## 4. RESERVATIONS

**Schema**

`RESERVATIONS(reservation_id, member_id, room_id, reservation_date, start_time, duration_minutes)`

**Business constraints**

* `reservation_id` is the primary key.
* `member_id` and `room_id` are foreign keys but do not determine attributes within this table alone.

### 4.1 Candidate Keys

* `{reservation_id}`

(We are not given any uniqueness rule like `(room_id, reservation_date, start_time)` being unique, so we do not treat that as a key.)

### 4.2 Functional Dependencies

1. `reservation_id → member_id, room_id, reservation_date, start_time, duration_minutes`

### 4.3 Partial Dependencies

* Single-attribute key → **no partial dependencies.**

### 4.4 Transitive Dependencies

* Non-key attributes depend directly on `reservation_id`.
* Relationships like `member_id → member attributes` are **inter-table** and not FDs inside `RESERVATIONS`.

**No transitive dependencies.**

### 4.5 Normal Form

* **3NF & BCNF:** The only non-trivial FD has `reservation_id` (a key) as determinant.

→ **RESERVATIONS is in 3NF and BCNF.**

---

## 5. VISITS

**Schema**

`VISITS(visit_id, reservation_id, check_in_time, check_out_time)`

**Business constraints**

* `visit_id` is the primary key.
* There is a one-to-one relationship: one `Reservation` → one `Visit`.
  This implies `reservation_id` is also unique in this table.

### 5.1 Candidate Keys

* `{visit_id}`
* `{reservation_id}`

Both uniquely identify a visit.

### 5.2 Functional Dependencies

1. `visit_id → reservation_id, check_in_time, check_out_time`
2. `reservation_id → visit_id, check_in_time, check_out_time`

### 5.3 Partial Dependencies

* Both candidate keys are single-attribute.
* **No partial dependencies.**

### 5.4 Transitive Dependencies

* Non-key attributes (`check_in_time`, `check_out_time`) depend directly on either key.
* There is no chain like `key → non-key B → non-key C` within this relation.

**No transitive dependencies.**

### 5.5 Normal Form

* **3NF:** For each FD, the determinant (`visit_id`, `reservation_id`) is a key.
* **BCNF:** Every non-trivial FD has a key determinant.

→ **VISITS is in 3NF and BCNF.**

---

## 6. MENU_ITEMS

**Schema**

`MENU_ITEMS(menu_item_id, name, price, category)`

**Business constraints**

* `menu_item_id` is the primary key.

### 6.1 Candidate Keys

* `{menu_item_id}`

### 6.2 Functional Dependencies

1. `menu_item_id → name, price, category`

(We are not told that `name` is unique, so we do not assume `name → ...`.)

### 6.3 Partial Dependencies

* Single-attribute key → **no partial dependencies.**

### 6.4 Transitive Dependencies

* All non-key attributes depend directly on `menu_item_id`.
* No non-key attribute determines another by design.

**No transitive dependencies.**

### 6.5 Normal Form

* **3NF & BCNF:** Only FD determinant is a key.

→ **MENU_ITEMS is in 3NF and BCNF.**

---

## 7. ORDERS

**Schema**

`ORDERS(order_id, visit_id, menu_item_id, quantity, order_time)`

**Business constraints**

* `order_id` is the primary key.
* `visit_id` and `menu_item_id` are foreign keys.
* No explicit uniqueness constraint on any composite of `(visit_id, menu_item_id, order_time)` is specified in the schema.

### 7.1 Candidate Keys

* `{order_id}`

### 7.2 Functional Dependencies

1. `order_id → visit_id, menu_item_id, quantity, order_time`

### 7.3 Partial Dependencies

* Single-attribute key → **no partial dependencies.**

### 7.4 Transitive Dependencies

* All non-key attributes depend directly on `order_id`.
* Inter-table FDs (e.g., `visit_id → reservation_id`) are not FDs in this relation.

**No transitive dependencies.**

### 7.5 Normal Form

* **3NF & BCNF:** Every non-trivial FD has `order_id` (a key) as determinant.

→ **ORDERS is in 3NF and BCNF.**

---

## 8. EVENTS

**Schema**

`EVENTS(event_id, name, event_type, room_id, start_date_time, end_date_time)`

**Business constraints**

* `event_id` is the primary key.

### 8.1 Candidate Keys

* `{event_id}`

### 8.2 Functional Dependencies

1. `event_id → name, event_type, room_id, start_date_time, end_date_time`

(We are not given any uniqueness constraint on `(room_id, start_date_time)`, so we do not treat that as a key.)

### 8.3 Partial Dependencies

* Single-attribute key → **no partial dependencies.**

### 8.4 Transitive Dependencies

* All non-key attributes depend directly on `event_id`.
* No non-key attribute is declared to determine another non-key attribute.

**No transitive dependencies.**

### 8.5 Normal Form

* **3NF & BCNF:** Only FD determinant is a key.

→ **EVENTS is in 3NF and BCNF.**

---

## 9. EVENTREGISTRATIONS

**Schema**

`EVENTREGISTRATIONS(registration_id, event_id, member_id, registration_time)`

**Business constraints**

* `registration_id` is the primary key.
* Additional constraint: `UNIQUE(member_id, event_id)` (a member may register at most once for a given event).

### 9.1 Candidate Keys

Because of the unique constraint:

* `{registration_id}`
* `{member_id, event_id}`

Both uniquely identify an event registration row.

### 9.2 Functional Dependencies

1. `registration_id → event_id, member_id, registration_time`
2. `(member_id, event_id) → registration_id, registration_time`

(From these FDs, both `registration_id` and `(member_id, event_id)` are keys.)

### 9.3 Partial Dependencies

* With primary key chosen as `registration_id` (single-attribute), there are no partial dependencies.
* For the composite key `(member_id, event_id)`, the only attributes that depend on it are:

  * `registration_time` (non-key)
  * `registration_id` (key attribute / surrogate ID)
* There is no FD where a proper subset of `(member_id, event_id)` determines a non-key attribute.

**No partial dependencies.**

### 9.4 Transitive Dependencies

* `registration_id` is a key and directly determines all attributes.
* `(member_id, event_id)` is also a key.
* There is no non-key attribute `B` such that `key → B` and `B → C` with `C` non-key; `registration_time` does not determine anything else.

**No transitive dependencies.**

### 9.5 Normal Form

* **3NF:** For each FD, the determinant is a key (`registration_id` or `(member_id, event_id)`).
* **BCNF:** Every non-trivial FD has a key determinant.

→ **EVENTREGISTRATIONS is in 3NF and BCNF.**

---

## Summary

Across all tables:

* Every relation has clearly defined keys and FDs derived from those keys and explicit uniqueness constraints.
* There are **no partial dependencies** (no non-key attributes depending on a proper subset of a composite key within any table).
* There are **no transitive dependencies** among non-key attributes inside any single table; cross-table dependencies are modeled via foreign keys instead.
* Therefore, **all tables are in 3NF**, and because every non-trivial FD in each table has a superkey determinant, **all tables also satisfy BCNF** under the stated assumptions.
