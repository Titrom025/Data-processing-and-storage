import enum
from flask import Flask, request, Response
import database

app = Flask(__name__)

db = database.DB(host="localhost", database="demo",
					  user="titrom", password="", port="5435")


@app.route('/getCities', methods=['GET'])
def getCities():
	cities = db.getCities()
	response_html = '<html>'
	for index, city in enumerate(cities):
		response_html += f'<div>{index}: City: {city}\n</div>'
	return response_html + '</html>'

@app.route('/getAirports', methods=['GET'])
def getAirports():
	airports = db.getAirports()
	response_html = '<html>'
	for index, (code, name, city, timezone) in enumerate(airports):
		response_html += f'<div>{index}: Code: {code}, Name: {name}, City: {city}, Timezone: {timezone}\n</div>'
	return response_html + '</html>'

@app.route('/getCityAirports', methods=['GET'])
def getCityAirports():
	city = request.args.get("city")
	airports = db.getCityAirports(city)
	response_html = '<html>'
	for index, (code, name, city, timezone) in enumerate(airports):
		response_html += f'<div>{index}: Code: {code}, Name: {name}, City: {city}, Timezone: {timezone}\n</div>'
	return response_html + '</html>'

@app.route('/getInboundSchedule', methods=['GET'])
def getInboundSchedule():
	airport = request.args.get("airport")
	flights = db.getInboundSchedule(airport)
	response_html = '<html>'
	for (flight_no, arrival_time, departure_city, status) in flights:
		response_html += f'<div>№ {flight_no}, Arrival time: {arrival_time}, From: {departure_city}, Status: {status}\n</div>'
	return response_html + '</html>'

@app.route('/getOutboundSchedule', methods=['GET'])
def getOutboundSchedule():
	airport = request.args.get("airport")
	flights = db.getOutboundSchedule(airport)
	response_html = '<html>'
	for (flight_no, arrival_time, departure_city, status) in flights:
		response_html += f'<div>№ {flight_no}, Departure time: {arrival_time}, TO: {departure_city}, Status: {status}\n</div>'
	return response_html + '</html>'

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000)