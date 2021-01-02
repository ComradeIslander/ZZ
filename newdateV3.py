from datetime import datetime, timedelta

dt_string = "01/01/2020 12:00:00"

dt_object = datetime.strptime(dt_string, "%m/%d/%Y %H:%M:%S")
print("Initial Date: ", dt_object)


def add_minutes(ele):
    future_date = dt_object + timedelta(minutes=ele)
    return future_date


number_list = [610, 687, 776, 875, 987, 1112, 1255, 1416, 1597,
               1800, 2031, 2291, 2584, 2912, 3287, 3707, 4181,
               4713, 5318, 5997, 6765, 7626, 8604, 9704, 10946,
               12339, 13922, 15702, 17711,]
num_list2 = []

for ele in number_list:
    num_list2.append(add_minutes(ele))

print('List:', *num_list2, sep= str(ele) + '\n- ')
