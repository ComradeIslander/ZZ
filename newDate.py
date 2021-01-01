from datetime import datetime, timedelta

initial_date = datetime.now()

input_a = input('>')
input_a = int(input_a)

print("initial_date", str(initial_date))

future_date = initial_date + timedelta(minutes=input_a)

print("future_date", str(future_date))
