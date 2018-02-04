#!/usr/bin/env python
import hashlib


def parse_parameters():
    from argparse import ArgumentParser

    usage = "%(prog)s [options]"
    parser = ArgumentParser(usage=usage)

    parser.add_argument("-n", "--number", dest="num", required=True,
                        default="1", type=int,
                        help="Number of keys to hash.", metavar="NUMBER")

    parser.add_argument("-p", "--prefix", dest="prefix",
                        default="distsystems_replicamanager_", type=str,
                        help="Key prefix. Default: distsystems_replicamanager_", metavar="PREFIX")

    args = parser.parse_args()

    return args


def run(options):
    import operator

    hashes = {}

    for n in range(options.num):
        key = options.prefix + str(n)

        hashes[key] = hashlib.sha1(key.encode('utf-8')).hexdigest()

    ordered_hashes = sorted(hashes.items(), key=operator.itemgetter(1))

    i = 1
    for h in ordered_hashes:
        print(str(i) + " - " + h[0])
        i += 1


def main():

    # Parse args
    args = parse_parameters()

    # Run the algorithm
    run(options=args)


if __name__ == "__main__":
    main()
