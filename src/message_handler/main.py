#!/usr/bin/env python
import pika
import requests


def parse_parameters():
    from argparse import ArgumentParser

    usage = "%(prog)s [options]"
    parser = ArgumentParser(usage=usage)

    parser.add_argument("-b", "--broker", dest="broker",
                        default="distsystems_rabbitmq_1", type=str,
                        help="Broker address. Default: distsystems_rabbitmq_1", metavar="ADDRESS")

    parser.add_argument("-a", "--address", dest="address",
                        default="distsystems_datamanager_1:8080", type=str,
                        help="IP\Hostname of the Datamanager. Default: distsystems_datamanager_1:8080", metavar="ADDRESS")

    parser.add_argument("-U", "--url", dest="url",
                        default="/datamanager-web/datamanager/scanners/", type=str,
                        help="Url path to the resource. Default: /datamanager-web/datamanager/scanners/", metavar="URL")

    parser.add_argument("-e", "--exchange", dest="exchange",
                        default="amq.topic", type=str,
                        help="Exchange name. Default: amq.topic", metavar="NAME")

    parser.add_argument("-u", "--username", dest="username", required=True,
                        default=None, type=str,
                        help="Username", metavar="USERNAME")

    parser.add_argument("-p", "--password", dest="password",
                        default=None, type=str, required=True,
                        help="Password", metavar="PASSWORD")

    # Topics list. Use like:    python arg.py -t 1234 2345 3456 4567
    parser.add_argument("-t", "--topics", dest="topics", nargs="+", help="Topics the handler should listen on", default=["*.monitor.uptime", "*.monitor.memory", "*.monitor.cpu", "*.monitor.ifs", "*.monitor.ios"])

    args = parser.parse_args()

    return args


def run(options):
    import time

    credentials = pika.PlainCredentials(options.username, options.password)

    connection = None
    while True:
        try:
            connection  = pika.BlockingConnection(pika.ConnectionParameters(host=options.broker, credentials=credentials))
            break
        except pika.exceptions.ConnectionClosed as e:
            print("[INFO] Cannot reach the broker:" + str(e) + "\nRetrying in 5s")

        except pika.exceptions.ProbableAuthenticationError as e:
            # if e[0] == 403:
            print("[ERROR] Probable authentication error:" + str(e) +"\nWrong credentials? Retrying in 5s")

        time.sleep(5)

    channel = connection.channel()

    channel.exchange_declare(exchange=options.exchange,
                             exchange_type='topic', passive=True)

    result = channel.queue_declare(durable=True)#, passive=True)
    queue_name = result.method.queue

    # Bind to all topics
    for binding_key in options.topics:
        channel.queue_bind(exchange=options.exchange,
                           queue=queue_name,
                           routing_key=binding_key)

    resource_url = "http://" + options.address + options.url
    print(" [*] Waiting for logs. To exit press CTRL+C")

    def callback(ch, method, properties, body):
        #print(" [x] %r:%r:%r:%r" % (method.routing_key, body, ch, properties))

        splitted_routing_key = method.routing_key.split('.')
        print(" [x] ScannerID: {:s}, Topic: {:s}, Body: {:s}".format(splitted_routing_key[0], splitted_routing_key[-1], body.decode("utf-8")))
        print(resource_url + splitted_routing_key[0] + "/" + splitted_routing_key[-1])
        try:
            r = requests.post(resource_url + splitted_routing_key[0] + "/" + splitted_routing_key[-1], data=body.decode("utf-8"))
            #print(" POST status: " + r.status_code + " " + r.reason)
        except requests.exceptions.ConnectionError:
            print("Cannot reach Datamanager!")


    channel.basic_consume(callback,
                          queue=queue_name,
                          no_ack=True)

    channel.start_consuming()


def main():

    # Parse args
    args = parse_parameters()

    # Run the algorithm
    run(options=args)


if __name__ == "__main__":
    main()
