import enum
from flask import Flask, request, Response
import database
import datetime

app = Flask(__name__)

db = database.DB(host="localhost", database="demo",
					  user="titrom", password="", port="5435")


@app.route('/getCities', methods=['GET'])
def getCities():
	cities = db.getCities()
	response_html = '['
	for index, city in enumerate(cities):
		response_html += '{' + f'"City": "{city}"' + '},'
	return response_html + ']'

@app.route('/getAirports', methods=['GET'])
def getAirports():
	airports = db.getAirports()
	response_html = '['
	for index, (code, name, city, timezone) in enumerate(airports):
		response_html += '{' + f'"Code": "{code}", "Name": "{name}", "City": "{city}", "Timezone": "{timezone}"' + '},'
	return response_html + ']'

@app.route('/getCityAirports', methods=['GET'])
def getCityAirports():
	city = request.args.get("city")
	airports = db.getCityAirports(city)
	response_html = '['
	for index, (code, name, city, timezone) in enumerate(airports):
		response_html += '{' + f'"Code": "{code}", "Name": "{name}", "City": "{city}", "Timezone": "{timezone}"' + '},'
	return response_html + ']'

@app.route('/getInboundSchedule', methods=['GET'])
def getInboundSchedule():
	airport = request.args.get("airport")
	flights = db.getInboundSchedule(airport)
	response_html = '['
	for flight in flights:
		_, arr_local, dep_city, _, _, flight_no, _, _, status, _, _ = flight
		response_html += '{' + f'"NO": "{flight_no}", "Arrival time": "{arr_local}", "From": "{dep_city}", "Status": "{status}"' + '},'
	return response_html + ']'

@app.route('/getOutboundSchedule', methods=['GET'])
def getOutboundSchedule():
	airport = request.args.get("airport")
	flights = db.getOutboundSchedule(airport)
	response_html = '['
	for flight in flights:
		dep_local, _, _, arr_city, _, flight_no, _, _, status, _, _ = flight
		response_html += '{' + f'"NO": "{flight_no}", "Departure time": "{dep_local}", "To": "{arr_city}", "Status": "{status}"' + '},'
	return response_html + ']'


def _buildRoutes(source_airport, departure_airport, date, seat_class, limit):
	response_html = '['
	ids2flight = {}
	from_flights = {}

	source_list = set([source_airport])
	handled_surces = set()

	for _ in range(int(limit) + 1):
		for source in source_list.copy():
			if source in handled_surces:
				continue
			handled_surces.add(source)

			flights = db.getOutboundSchedule(source, date, twodays=True)
			for flight in flights:  
				dep_local, arr_local, dep_city, arr_city, flight_id, flight_no, dep_glob, arr_glob, status, dep_airp, arr_airp = flight

				if (dep_airp not in from_flights):
					from_flights[dep_airp] = []

				if flight_id not in ids2flight:
					from_flights[dep_airp].append(flight)
					ids2flight[flight_id] = flight
					# response_html += '{' + f'№ {flight_no}, Departure time: {dep_glob}, Arrival time: {arr_glob}, From: {dep_city}, To: {arr_city}, Status: {status}\n' + '}'
					source_list.add(arr_airp)


	routes = []
	new_routes = []
	correct_routes = []

	for source in [source_airport]:
		for flight in from_flights[source]:
			routes.append([flight[4]])
			
	for _ in range(int(limit) + 1):
		for route in routes:
			last_flight = ids2flight[route[-1]]
			last_flight_dest = last_flight[10]
			if last_flight_dest == departure_airport:
				correct_routes.append([ids2flight[route_flight] for route_flight in route])
			else:
				if last_flight_dest in from_flights:
					for new_flight in from_flights[last_flight_dest]:
						new_flight_dest = new_flight[3]
						if new_flight_dest in [ids2flight[route_flight][2] for route_flight in route]:
							continue
						last_flight_arrival = last_flight[7]
						new_flight_departure = new_flight[6]
						if new_flight_departure > last_flight_arrival:
							new_routes.append(route + [new_flight[4]])


		routes = new_routes.copy()
		new_routes = []


	price_tables = {}
	for route in correct_routes:
		response_html += '['
		total_price = 0
		for flight in route:
			_, _, dep_city, arr_city, flight_id, flight_no, dep_glob, arr_glob, _, _, _ = flight
			if flight_no in price_tables:
				pricetb = price_tables[flight_no]
			else:
				pricetb = db.getPricetable(flight_no)
				price_tables[flight_no] = pricetb

			price = '-'
			if seat_class in pricetb:
				price = float(pricetb[seat_class][0])
				total_price += price

			response_html += '{' + f'"ID": "{flight_id}", "NO": "{flight_no}", "Departure time": "{dep_glob}", "Arrival time": "{arr_glob}", "From": "{dep_city}", "To": "{arr_city}", "Price": "{price}"' + '},'
		response_html += '],'
	return response_html + ']'

@app.route('/findRoutes', methods=['GET'])
def findRoutes():
	source = request.args.get("source")
	destination = request.args.get("destination")
	date = request.args.get("date")
	seat_class = request.args.get("seat_class")
	limit = request.args.get("limit")
	routes = _buildRoutes(source, destination, date, seat_class, limit)
	return routes

@app.route('/createBooking', methods=['POST'])
def createBooking():
	json = request.get_json()
	flights = json["flights"]
	passenger_id = json["passenger_id"]
	passenger_name = json["passenger_name"]
	seat_class = json["seat_class"]
	return db.makeBook(flights, passenger_id, passenger_name, seat_class)

@app.route('/checkIn', methods=['POST'])
def checkIn():
	json = request.get_json()
	flight_id = json["flight_id"]
	passenger_id = json["passenger_id"]
	seat_no = json["seat_no"]
	book_ref = json["book_ref"]
	return db.checkIn(flight_id, passenger_id, seat_no, book_ref)

if __name__ == '__main__':
   app.run(host="0.0.0.0", port=5000)