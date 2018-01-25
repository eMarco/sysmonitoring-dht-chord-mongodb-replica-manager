#!/usr/bin/env python
import pika
import sys


def parse_parameters():
    from argparse import ArgumentParser

    usage = "%(prog)s [options]"
    parser = ArgumentParser(usage=usage)

    parser.add_argument("-b", "--broker", dest="broker",
                        default="distsystems_rabbitmq_1", type=str,
                        help="Broker address. Default: distsystems_rabbitmq_1", metavar="ADDRESS")

    parser.add_argument("-e", "--exchange", dest="exchange",
                        default="amq.topic", type=str,
                        help="Exchange name. Default: amq.topic", metavar="NAME")

    parser.add_argument("-r", "--routingkey", dest="routing_key", required=True,
                        default=None, type=str,
                        help="Routing key", metavar="ROUTING_KEY")

    parser.add_argument("-m", "--message", dest="message", required=True,
                        default=None, type=str,
                        help="Message to be sent", metavar="MESSAGE")

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

    channel     = connection.channel()

    channel.exchange_declare(exchange=options.exchange, exchange_type='topic', passive=True)

    channel.basic_publish(exchange=options.exchange,
                          routing_key=options.routing_key,
                          body=options.message)

    print(" [x] Sent %r:%r" % (options.routing_key, options.message))

    connection.close()


def main():

    # Parse args
    args = parse_parameters()

    # Run the algorithm
    run(options=args)


if __name__ == "__main__":
    main()
