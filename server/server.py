#!/usr/bin/env python

import threading, logging, time

from kafka import KafkaConsumer, KafkaProducer

from flask import Flask, request, jsonify, render_template

broker = 'apache-kafka:9092'
topic = 'my-topic'

latest = None

class Producer(threading.Thread):
    daemon = True

    def run(self):
        producer = KafkaProducer(bootstrap_servers=broker)
        n = 1
        while True:
            v = b"ponies!! %d" % (n)
            producer.send(topic, v)
            n = n + 1
            time.sleep(2)

class Consumer(threading.Thread):
    daemon = True

    def run(self):
        global latest

        consumer = KafkaConsumer(bootstrap_servers=broker,
                                 auto_offset_reset='latest')
        consumer.subscribe([topic])

        for message in consumer:
            latest = message.value


app = Flask(__name__)

@app.route("/")
def flask_root():
    return render_template('index.html',
                           categories=["foo", "goo", "moo"],
                           data=[30, 10, 5])

@app.route("/data")
def flask_data():
    return jsonify({"categories": ["foo", "goo", "moo"], "data": [ [ "counts", 60, 20, 10 ] ] })

threads = [
    Producer(),
    Consumer()
]

for t in threads:
    t.start()

app.run(host='0.0.0.0', port=8080)

time.sleep(300)
