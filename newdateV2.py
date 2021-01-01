from datetime import datetime, timedelta

dt_string = "01/01/2020 12:00:00"

dt_object = datetime.strptime(dt_string, "%m/%d/%Y %H:%M:%S")
print("Initial Date: ", dt_object)

number_list = []
n = int(input("Enter number of elements: "))
for i in range(0, n):
    ele = int(input("Enter minutes: "))
    future_date = dt_object + timedelta(minutes=ele)
    number_list.append(future_date)

print('List:', *number_list, sep='\n- ')
