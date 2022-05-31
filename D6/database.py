from flask import Flask, request, Response
import psycopg2

CURRENT_TIME = '2017-08-15 15:00:00.000000 +00:00'
NEXT_DAY = '2017-08-16 15:00:00.000000 +00:00'

class DB:
	def __init__(self, host, database, user, password, port):
		self.conn = psycopg2.connect(
			host=host, database=database,
			user=user, password=password, port=port)

	def makeRequest(self, request):
		cur = self.conn.cursor()
		cur.execute(request)
		return cur.fetchall()

	def getCities(self):
		airportsData = self.makeRequest("""
			SELECT t.*
			FROM bookings.airports_data t
			ORDER BY city
		""")
		cities = set()
		for (_, _, city, _, _) in airportsData:
			cities.add(city['ru'])
		return sorted(cities)

	def getAirports(self):
		airportsData = self.makeRequest("""
			SELECT t.*
			FROM bookings.airports_data t
			ORDER BY airport_code
		""")
		airports = []
		for (code, name, city, _, timezone) in airportsData:
			airports.append((code, name["ru"], city, timezone))
		return airports

	def getAirports(self):
		airports_data = self.makeRequest("""
			SELECT t.*
			FROM bookings.airports_data t
			ORDER BY airport_code
		""")
		airports = []
		for (code, name, city, _, timezone) in airports_data:
			airports.append((code, name["ru"], city['ru'], timezone))
		return airports

	def getCityAirports(self, city_name):
		airports_data = self.makeRequest("""
			SELECT t.*
			FROM bookings.airports_data t
			ORDER BY airport_code
		""")
		airports = []
		for (code, name, city, _, timezone) in airports_data:
			if city_name == city['ru'] or city_name == city['en']:
				airports.append((code, name["ru"], city['ru'], timezone))
		return airports

	def getInboundSchedule(self, airport_code):
		flights_data = self.makeRequest(f"""
			SELECT t.*
			FROM bookings.flights_v t
			WHERE arrival_airport = '{airport_code}' and
			scheduled_arrival > '{CURRENT_TIME}' and
			scheduled_arrival < '{NEXT_DAY}'
			ORDER BY scheduled_arrival
		""")
		flights = []
		for flight in flights_data:
			flights.append((flight[1], flight[5], flight[9], flight[13]))
		return flights

	def getOutboundSchedule(self, airport_code):
		flights_data = self.makeRequest(f"""
			SELECT t.*
			FROM bookings.flights_v t
			WHERE departure_airport = '{airport_code}' and
			scheduled_departure > '{CURRENT_TIME}' and
			scheduled_departure < '{NEXT_DAY}'
			ORDER BY scheduled_departure
		""")
		flights = []
		for flight in flights_data:
			flights.append((flight[1], flight[3], flight[12], flight[13]))
		return flights
