import csv
import random
from datetime import date, timedelta
import uuid

# Seed for reproducibility
random.seed(42)

# ================================
# 1. Name Pools
# ================================

# 75% Modern Indian Names
indian_first_names_male = [
    "Aarav", "Vihaan", "Arjun", "Ayaan", "Reyansh", "Krishna", "Sai", "Rohan", "Aryan", "Ishaan",
    "Aditya", "Vivaan", "Rudra", "Shaurya", "Atharv", "Aniket", "Yash", "Dhruv", "Kabir", "Nihal"
]
indian_first_names_female = [
    "Aaradhya", "Diya", "Saumya", "Ananya", "Pari", "Siya", "Riya", "Myra", "Kiara", "Zara",
    "Navya", "Isha", "Tara", "Aditi", "Sneha", "Priya", "Neha", "Kavya", "Tanvi", "Rhea"
]
indian_last_names = [
    "Sharma", "Verma", "Gupta", "Singh", "Patel", "Mehta", "Joshi", "Reddy", "Nair", "Malhotra",
    "Kumar", "Rao", "Iyer", "Menon", "Chopra", "Agarwal", "Bose", "Saxena", "Thakur", "Yadav"
]

# 25% Global Names
global_first_names_male = [
    "James", "Michael", "David", "Alexander", "Liam", "Noah", "Oliver", "Elijah", "Lucas", "Mason"
]
global_first_names_female = [
    "Emma", "Olivia", "Sophia", "Isabella", "Ava", "Mia", "Charlotte", "Amelia", "Harper", "Evelyn"
]
global_last_names = [
    "Smith", "Johnson", "Brown", "Davis", "Wilson", "Miller", "Taylor", "Anderson", "Thomas", "Jackson"
]

# Cities (India + Global)
indian_cities = [
    ("Mumbai", "Maharashtra"), ("Delhi", "Delhi"), ("Bengaluru", "Karnataka"), ("Hyderabad", "Telangana"),
    ("Chennai", "Tamil Nadu"), ("Kolkata", "West Bengal"), ("Pune", "Maharashtra"), ("Ahmedabad", "Gujarat"),
    ("Jaipur", "Rajasthan"), ("Lucknow", "Uttar Pradesh"), ("Chandigarh", "Chandigarh"), ("Coimbatore", "Tamil Nadu"),
    ("Indore", "Madhya Pradesh"), ("Bhopal", "Madhya Pradesh"), ("Patna", "Bihar"), ("Vadodara", "Gujarat"),
    ("Visakhapatnam", "Andhra Pradesh"), ("Nagpur", "Maharashtra"), ("Surat", "Gujarat"), ("Kochi", "Kerala")
]
global_cities = [
    ("New York", "NY", "USA"), ("London", "", "UK"), ("Sydney", "NSW", "Australia"), ("Toronto", "ON", "Canada"),
    ("Singapore", "", "Singapore"), ("Dubai", "", "UAE"), ("Tokyo", "", "Japan"), ("Paris", "", "France"),
    ("Los Angeles", "CA", "USA"), ("Berlin", "", "Germany")
]

# Genders
genders = ["Male", "Female", "Other"]

# Email domains
domains = ["gmail.com", "yahoo.com", "outlook.com", "hotmail.com", "icloud.com"]

# ================================
# 2. Helper Functions
# ================================

def random_date(start_year=1980, end_year=2005):
    start = date(start_year, 1, 1)
    end = date(end_year, 12, 31)
    delta = end - start
    return start + timedelta(days=random.randint(0, delta.days))

def generate_phone():
    return f"{random.randint(700,999)}-{random.randint(100,999)}-{random.randint(1000,9999)}"

def generate_indian_name(gender):
    if gender == "Male":
        first = random.choice(indian_first_names_male)
    else:
        first = random.choice(indian_first_names_female)
    last = random.choice(indian_last_names)
    return f"{first} {last}"

def generate_global_name(gender):
    if gender == "Male":
        first = random.choice(global_first_names_male)
    else:
        first = random.choice(global_first_names_female)
    last = random.choice(global_last_names)
    return f"{first} {last}"

def generate_address(is_indian):
    if is_indian:
        city, state = random.choice(indian_cities)
        street = f"{random.randint(1, 200)}/{random.randint(1, 50)} {random.choice(['MG Road', 'Linking Road', 'Station Road', 'Nehru Nagar', 'Gandhi Colony', 'Sector 18', 'Koramangala', 'Andheri East', 'Bandra West', 'Civil Lines'])}"
        return f'"{street}, {city}, {state}, India"'
    else:
        city, state, country = random.choice(global_cities)
        street = f"{random.randint(100, 999)} {random.choice(['Main St', 'Oak Avenue', 'Elm Street', 'Broadway', 'King Street'])}"
        addr = f'"{street}, {city}'
        if state:
            addr += f", {state}"
        addr += f", {country}"
        return addr

# ================================
# 3. Generate Data
# ================================

total_users = 1000
indian_ratio = 0.75
num_indian = int(total_users * indian_ratio)  # 750
num_global = total_users - num_indian       # 250

# Track used emails and phones
used_emails = set()
used_phones = set()

def unique_email(base):
    while True:
        suffix = random.randint(1, 9999)
        email = f"{base.lower().replace(' ', '.')}{suffix}@{random.choice(domains)}"
        if email not in used_emails:
            used_emails.add(email)
            return email

def unique_phone():
    while True:
        phone = generate_phone()
        if phone not in used_phones:
            used_phones.add(phone)
            return phone

# Open CSV
with open('UserDetails.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow([
        "UserDetailID", "Email", "Name", "PhoneNumber", "Address",
        "DateOfBirth", "Gender", "ProfilePhotoURL", "UserID"
    ])

    user_id = 1

    # Generate 750 Indian + 250 Global users
    for i in range(total_users):
        is_indian = i < num_indian
        gender = random.choice(genders)

        if is_indian:
            name = generate_indian_name(gender)
        else:
            name = generate_global_name(gender)

        # Email: base from name
        base_name = name.split()[0] + "." + name.split()[1]
        email = unique_email(base_name)

        phone = unique_phone()
        address = generate_address(is_indian)
        dob = random_date(1980, 2005)
        photo = f"https://example.com/photos/user{user_id}.jpg"

        writer.writerow([
            user_id,
            email,
            name,
            phone,
            address,
            dob,
            gender,
            photo,
            user_id
        ])
        user_id += 1

print("UserDetails.csv generated successfully!")
print(f"   - Total: 1000 users")
print(f"   - Indian: 750 (75%)")
print(f"   - Global: 250 (25%)")
print(f"   - All emails & phones are UNIQUE")