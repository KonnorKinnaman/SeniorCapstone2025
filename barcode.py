def lookup_sensor(result):
    table = {
        "2540" : "Level Sensor: The Ultrasonic Level Sensor is used to measure the volume of liquid in a space using ultrasonic sound.",
        "2543" : "Coriollis Sensor: A Coriollis Sensor is a sensor that measures the rate that a liquid is flowing using electromagnetics. Inside the coriollis sensor are two curved tubes with two opposing magnets. The magnetic force generated is proportional to the rate of flow of the water, which then pushes the tubes apart. The distance between these tubes is measured and converted into a velocity. "

    }

    for key in table:
        if (key == result):
            sensor = table[key]
        else:
            print("No sensor found!\n")
    return sensor
        
#Rewrite to decode hex only
def get_input():
    hex_str = str(input("Scan sensor to begin:\n"))
    result = bytes.fromhex(hex_str).decode('utf-8')
    sensor = lookup_sensor(result)
    print(result)
    print(type(result))
    return sensor