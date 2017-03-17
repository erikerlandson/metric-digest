#!/usr/bin/env python

import threading, logging, time

import json

from kafka import KafkaConsumer, KafkaProducer

from flask import Flask, request, jsonify, render_template

# todo: command-line and/or env
broker = 'apache-kafka:9092'
topic = 'test-digest'

# todo: should probably be hardened with mutex
latest = { "x": [0.0, 1.0], "d": [1.0] }

class Consumer(threading.Thread):
    daemon = True

    def run(self):
        global latest

        consumer = KafkaConsumer(bootstrap_servers=broker,
                                 auto_offset_reset='latest')
        consumer.subscribe([topic])

        for message in consumer:
            latest = json.loads(message.value)

app = Flask(__name__)

@app.route("/")
def flask_root():
    return render_template('index.html', xdata = latest["x"], ddata = latest["d"])

@app.route("/data")
def flask_data():
    return jsonify({"xdata": latest["x"], "ddata": latest["d"] })

threads = [ Consumer() ]

for t in threads:
    t.start()

app.run(host='0.0.0.0', port=8080)
