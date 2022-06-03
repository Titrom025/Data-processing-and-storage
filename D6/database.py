from flask import Flask, request, Response
import psycopg2
import datetime

CURRENT_TIME = '2017-08-15 15:00:00.000000 +00:00'
NEXT_DAY = '2017-08-16 15:00:00.000000 +00:00'


class DB:
	def __init__(self, host, database, user, password, port):
		self.conn = psycopg2.connect(
			host=host, database=database,
			user=user, password=password, port=port)
		
	def _convertDate(self, textDate, next_day=False, twodays=False):
		if textDate.find('.') > 0:
			year, month, day = textDate.split('.')
		elif textDate.find('-') > 0:
			year, month, day = textDate.split('-')
		else:
			raise ValueError('Invalid date')

		if next_day:
			date_obj = datetime.datetime.strptime(f'{year}-{month}-{day}', '%Y-%m-%d').date()
			skip_days = 2 if twodays else 1
			date_obj += datetime.timedelta(days=skip_days)
			year = str(date_obj.year)
			month = str(date_obj.month).zfill(2)
			day = str(date_obj.day).zfill(2)

		return f'{year}-{month}-{day} 00:00:00.000000 +00:00'

	def makeRequest(self, request):
		cur = self.conn.cursor()
		cur.execute(request)

	def makeRequestAndFetch(self, request):
		cur = self.conn.cursor()
		cur.execute(request)
		return cur.fetchall()

	def commitChanges(self):
		self.conn.commit()

	def rollbackChanges(self):
		self.conn.rollback()
		

	def getCities(self):
		airportsData = self.makeRequestAndFetch("""
			SELECT t.*
			FROM bookings.airports_data t
			ORDER BY city
		""")
		cities = set()
		for (_, _, city, _, _) in airportsData:
			cities.add(city['ru'].strip(' '))
		return sorted(cities)

	def getAirports(self):
		airportsData = self.makeRequestAndFetch("""
			SELECT t.*
			FROM bookings.airports_data t
			ORDER BY airport_code
		""")
		airports = []
		for (code, name, city, _, timezone) in airportsData:
			airports.append((code, name["ru"], city, timezone))
		return airports

	def getAirports(self):
		airports_data = self.makeRequestAndFetch("""
			SELECT t.*
			FROM bookings.airports_data t
			ORDER BY airport_code
		""")
		airports = []
		for (code, name, city, _, timezone) in airports_data:
			airports.append((code, name["ru"], city['ru'], timezone))
		return airports

	def getCityAirports(self, city_name):
		airports_data = self.makeRequestAndFetch("""
			SELECT t.*
			FROM bookings.airports_data t
			ORDER BY airport_code
		""")
		airports = []
		for (code, name, city, _, timezone) in airports_data:
			if city_name == city['ru'] or city_name == city['en']:
				airports.append((code, name["ru"], city['ru'], timezone))
		return airports

	def _getSchedule(self, airport_code, date, direction, twodays=False):
		if date is None:
			date_from = CURRENT_TIME
			date_to = NEXT_DAY
		else:
			date_from = self._convertDate(date, twodays=twodays)
			date_to = self._convertDate(date, next_day=True, twodays=twodays)

		flights_data = self.makeRequestAndFetch(f"""
			SELECT t.*
			FROM bookings.flights_v t
			WHERE {direction}_airport = '{airport_code}' and
			scheduled_{direction} >= '{date_from}' and
			scheduled_{direction} <= '{date_to}'
			ORDER BY scheduled_{direction}
		""")
		flights = []
		for flight in flights_data:
			#					 dep_local  arr_local  dep_city   arr_city    flight_id  fligh_no   dep_glob  arr_glob    status      dep_airp   arr_airp    #price
			flights.append((flight[3], flight[5], flight[9], flight[12], flight[0], flight[1], flight[2], flight[4], flight[13], flight[7], flight[10]))
		return flights

	def getInboundSchedule(self, airport_code, date=None, twodays=False):
		return self._getSchedule(airport_code, date, 'arrival', twodays)

	def getOutboundSchedule(self, airport_code, date=None, twodays=False):
		return self._getSchedule(airport_code, date, 'departure', twodays)

	def getPricetable(self, flight_no):
		pricetable = self.makeRequestAndFetch(f"""
			SELECT
				string_agg(pricetb.seat_no, ', ' ORDER BY length(pricetb.seat_no), pricetb.seat_no),
				pricetb.amount,
				pricetb.fare_conditions
			FROM (SELECT bp.seat_no,
							tft.amount,
							tft.fare_conditions
					FROM (SELECT tf.flight_id,
										tf.fare_conditions,
										tf.amount,
										tf.ticket_no
							FROM ticket_flights tf) tft
									INNER JOIN (SELECT ticket_no, flight_id, seat_no FROM boarding_passes) bp
										ON bp.ticket_no = tft.ticket_no and bp.flight_id = tft.flight_id
									LEFT JOIN (SELECT flight_id, flight_no FROM flights_v) f
										ON f.flight_id = tft.flight_id
									WHERE f.flight_no = '{flight_no}'
					GROUP BY f.flight_no, bp.seat_no, tft.amount, tft.fare_conditions) as pricetb
			GROUP BY pricetb.fare_conditions, amount
			ORDER BY amount;
		""")
		prices = {}
		for row in pricetable:
			seat_class = row[2]
			seat_price = row[1]
			seats = row[0]
			if seat_class in prices:
				seat_class += '+'
			prices[seat_class] = (seat_price, seats)

		return prices

	def _get_no_by_id(self, flight_id):
		return self.makeRequestAndFetch(f"""
			SELECT flight_no
			FROM bookings.flights_v t
			WHERE flight_id = '{flight_id}
		'""")[0][0]

	def makeBook(self, flights, passenger_id, passenger_name, seat_class):
		bookRefId = str(self.makeRequestAndFetch("""
			SELECT count(t.book_ref)
			FROM bookings.bookings t"""
		)[0][0])
		bookRefId = 'I' + bookRefId[1:]

		ticket_no = str(self.makeRequestAndFetch("""
			SELECT count(t.ticket_no)
			FROM bookings.tickets t"""
		)[0][0])
		ticket_no = 'I' + ticket_no[1:]

		prices = {}
		total_amount = 0
		for flight_id in flights:
			flight_no = self._get_no_by_id(flight_id)
			pricetb = self.getPricetable(flight_no)
			if seat_class in pricetb:
				total_amount += pricetb[seat_class][0]
				prices[flight_id] = pricetb[seat_class][0]
			else:
				return '{"success": "false"}'

		try:
			self.makeRequest(f"""
				INSERT INTO bookings.bookings (book_ref, book_date, total_amount)
				VALUES ('{bookRefId}', CURRENT_TIMESTAMP, '{total_amount}');
			""")

			self.makeRequest(f"""
				INSERT INTO tickets (ticket_no, book_ref, passenger_id, passenger_name)
				VALUES ('{ticket_no}', '{bookRefId}' , '{passenger_id}', '{passenger_name}');
			""")

			for flight_id in flights:
				self.makeRequest(f"""
					INSERT INTO ticket_flights (ticket_no, flight_id, fare_conditions, amount)
					VALUES ('{ticket_no}', '{flight_id}', '{seat_class}', {prices[flight_id]});
				""")
			self.commitChanges()
			return f'{{"success": "true", "book_ref": "{bookRefId}"}}'
		except:
			self.rollbackChanges()
			return '{"success": "false"}'

	def checkIn(self, flight_id, passenger_id, seat_no, book_ref):
		ticket_no = self.makeRequestAndFetch(f"""
			SELECT t.*
         FROM bookings.tickets t
      	WHERE book_ref = '{book_ref}'
		""")[0][0]

		booked_flight_price = self.makeRequestAndFetch(f"""
			SELECT t.*
      	FROM bookings.ticket_flights t
         WHERE ticket_no = '{ticket_no}' and flight_id = '{flight_id}'
		""")[0][3]

		flight_no = self._get_no_by_id(flight_id)
		pricetb = self.getPricetable(flight_no)

		boarding_no = str(self.makeRequestAndFetch("""
			SELECT count(t.ticket_no)
			FROM bookings.boarding_passes t"""
		)[0][0])

		for seat_class in pricetb:
			price = pricetb[seat_class][0]
			if price == booked_flight_price:
				seats = pricetb[seat_class][1].split(', ')
				if seat_no in seats:
					try:
						self.makeRequest(f"""
							INSERT INTO boarding_passes (ticket_no, flight_id, boarding_no, seat_no)
							VALUES ('{ticket_no}', '{flight_id}', '{boarding_no}', '{seat_no}');
						""")
						self.commitChanges()
						return '{"success": "true"}'
					except:
						self.rollbackChanges()

		return '{"success": "false"}'
		

