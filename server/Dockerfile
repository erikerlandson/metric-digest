FROM mattf/openshift-kafka

RUN curl --silent --show-error --retry 5 https://bootstrap.pypa.io/get-pip.py | python

RUN pip install kafka flask

RUN mkdir /opt/kafka/bin/templates

COPY server.py /opt/kafka/bin/
COPY templates/index.html /opt/kafka/bin/templates/
