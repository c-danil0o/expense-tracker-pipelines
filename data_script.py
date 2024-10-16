import random
from datetime import datetime, timedelta
import hashlib

NUM_USERS = 20
NUM_TRANSACTIONS = 1500
NUM_REMINDERS = 100
NUM_GROUPS = 6
USER_TYPES = ['Basic', 'Premium']
GENDERS = ['Male', 'Female', 'Other', 'Male','Male','Female','Female']
NAMES = ['Oliver', 'Emma', 'James', 'Sophia', 'Liam', 'Isabella', 'Noah', 'Mia', 'Elijah', 'Amelia', 
'Mason', 'Harper', 'Lucas', 'Evelyn', 'Logan', 'Avery', 'Alexander', 'Ella', 'Ethan', 
'Scarlett', 'Aiden', 'Emily', 'Jacob', 'Elizabeth', 'Michael', 'Sofia', 'William', 
'Charlotte', 'Daniel', 'Victoria', 'Benjamin', 'Madison', 'Sebastian', 'Aria', 'Henry', 
'Chloe', 'Matthew', 'Penelope', 'Owen', 'Luna', 'Jackson']
LAST_NAMES = ['Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller', 'Davis', 'Rodriguez', 
'Martinez', 'Hernandez', 'Lopez', 'Gonzalez', 'Wilson', 'Anderson', 'Thomas', 'Taylor', 
'Moore', 'Jackson', 'Martin', 'Lee', 'Perez', 'Thompson', 'White', 'Harris', 'Sanchez', 
'Clark', 'Ramirez', 'Lewis', 'Robinson']

TRANSACTION_TYPES = ['Income', 'Expense']
TRANSACTION_STATUSES = ['Scheduled', 'InProgress', 'Done']
REMINDER_TYPES = ['Total', 'BudgetCap']
REPEAT_TYPES = ['None', 'Weekly', 'Monthly', 'Yearly']
COUNTRIES = ['US', 'UK', 'DE', 'FR', 'AU']
CURRENCIES = ['USD', 'EUR', 'GBP', 'AUD']
TRANSACTION_GROUPS = {
    'Groceries': [
        'Grocery Store', 
        'Supermarket', 
        'Online Groceries'
    ],
    'Utilities': [
        'Electricity Bill', 
        'Water Bill', 
        'Internet Bill', 
        'Gas Bill'
    ],
    'Entertainment': [
        'Movie Tickets', 
        'Concert Tickets', 
        'Streaming Subscription', 
        'Music Subscription'
    ],
    'Shopping': [
        'Clothing Store', 
        'Electronics Store', 
        'Furniture Store'
    ],
    'Transportation': [
        'Taxi Ride', 
        'Gas Station', 
        'Bus Ticket', 
        'Car Rental'
    ],
    'Healthcare': [
        'Pharmacy', 
        'Doctor Appointment', 
        'Health Insurance', 
        'Medical Supplies'
    ],
    'Dining': [
        'Restaurant', 
        'Fast Food', 
        'Coffee Shop', 
        'Pizza Delivery'
    ]
}
GROUP_IDS = list(TRANSACTION_GROUPS.keys())

def random_date(start, end) -> datetime:
    return start + timedelta(days=random.randint(0, (end - start).days))


def random_transaction_amount(group):
    if group == 'Groceries':
        return round(random.uniform(20, 150), 2)
    elif group == 'Utilities':
        return round(random.uniform(50, 300), 2)
    elif group == 'Entertainment':
        return round(random.uniform(10, 100), 2)
    elif group == 'Shopping':
        return round(random.uniform(30, 1000), 2) 
    elif group == 'Transportation':
        return round(random.uniform(5, 200), 2)
    elif group == 'Healthcare':
        return round(random.uniform(30, 500), 2)  
    else:
        return round(random.uniform(10, 200), 2)


sql_script = []

sql_script.append("-- Populating Users Table")
for i in range(1, NUM_USERS + 1):
    first_name = random.choice(NAMES)
    last_name = random.choice(LAST_NAMES)
    email = f'{str.lower(first_name)}.{str.lower(last_name)}{i}@example.com'
    gender = random.choice(GENDERS)
    user_type = random.choice(USER_TYPES)
    country = random.choice(COUNTRIES)
    currency = random.choice(CURRENCIES)
    birth_date = random_date(datetime(1970, 1, 1), datetime(2005, 1, 1)).date()
    registered_at = random_date(datetime(2022, 1, 1), datetime(2024, 9,1))
    funds = round(random.uniform(100, 10000), 2)
    reserved_funds = round(random.uniform(0, funds), 2)
    password = "$2a$10$xPn8MyNiuAR1Xgy88li1suzVG6pmwvhJi7sXZEc10HQ0k6/6nMVYi"

    sql_script.append(f"""
    INSERT INTO user (email, first_name, last_name, type, currency, country, gender, birth_date, registered_at, funds, reserved_funds, password) 
    VALUES ('{email}', '{first_name}', '{last_name}', '{user_type}', '{currency}', '{country}', '{gender}', '{birth_date}', '{registered_at}', {funds}, {reserved_funds}, '{password}');
    """)

sql_script.append("\n-- Populating Transaction Groups Table")
for key in TRANSACTION_GROUPS.keys():
    budget_cap = round(random.uniform(500, 5000), 2) if random.choice([True, False]) else 'NULL'
    user_id = random.randint(1, NUM_USERS)

    sql_script.append(f"""
    INSERT INTO transaction_group (budget_cap, name, user_id) 
    VALUES ({budget_cap}, '{key}', {user_id});
    """)

sql_script.append("\n-- Populating Transactions Table")
for i in range(1, NUM_TRANSACTIONS + 1):
    user_id = random.randint(1, NUM_USERS)
    timestamp = random_date(datetime(2024, 4, 1, random.randrange(0, 24), random.randrange(0,60)), datetime(2025, 1, 1, random.randrange(0, 24), random.randrange(0,60)))
    transaction_type = random.choice(TRANSACTION_TYPES)
    currency = random.choice(CURRENCIES)
    status = ""
    if timestamp > datetime.today():
        status = "Scheduled"
    elif timestamp == datetime.today():
        status = "InProgress"
    else:
        status = "Done"
    repeat_type = random.choice(REPEAT_TYPES)
    group_id = random.randint(1, NUM_GROUPS)
    name = random.choice(TRANSACTION_GROUPS[GROUP_IDS[group_id]])
    amount = random_transaction_amount(GROUP_IDS[group_id])
    sql_script.append(f"""
    INSERT INTO transaction (user_user_id, timestamp, type, currency, amount, status, repeat_type, transaction_group, name) 
    VALUES ({user_id}, '{timestamp}', '{transaction_type}', '{currency}', {amount}, '{status}', '{repeat_type}', {group_id}, '{name}');
    """)

# sql_script.append("\n-- Populating Reminders Table")
# for i in range(1, NUM_REMINDERS + 1):
#     user_id = random.randint(1, NUM_USERS)
#     reminder_type = random.choice(REMINDER_TYPES)
#     next_run = random_date(datetime(2023, 9, 1), datetime(2024, 12, 31))
#     days_span = random.randint(1, 30)
#     group_id = random.randint(1, NUM_GROUPS) if reminder_type == 'BudgetCap' else 'NULL'

#     sql_script.append(f"""
#     INSERT INTO Reminder (type, user_id, next_run, days_span, transaction_group) 
#     VALUES ('{reminder_type}', {user_id}, '{next_run}', {days_span}, {group_id});
#     """)

final_sql_script = "\n".join(sql_script)
with open("populate_db.sql", "w+") as file:
    file.write(final_sql_script)

