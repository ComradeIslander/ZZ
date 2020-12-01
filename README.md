# ZZ

Doran's Project for MW

Stuff to do:

~~1. Define the year, day, hour, minute, second of two points on the chart and count the number of swings between those two historical price points.~~ __DONE__

~~2. To display the total count of swings of the defined period at the end of the period labeled on the chart.~~ __DONE__

3. To define thresholds within the indicator parameters to mark on the overlay when that number of swings occurs. I.E. 10,20,30,40,etc... when the 10th 
swing occurs I want 10 labeled on the swing on the plotted overlay. __IN PROGRESS__

4. To add up the net price movement (which the indicator calculates for you already) of all the swings and for that number to be displayed along with the total number 
of swings of a given period. __NOT STARTED__


~~5. For this process to run in real-time.~~ __DONE__

6. Include an input for start date and end date. __IN PROGRESS__


Classes:

  CustomCoordinate- subclass of Coordinate class.

  FibonacciLucas- where the idea/code for making a starting date point comes from.

  CustomZigZag- main class where work will be done.
