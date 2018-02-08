## A basic container for testing with all the minimal tools for the project
FROM distsystems_scanner

RUN mkdir /run/sshd
RUN apt-get update && apt-get install -y vim git openssh-server
EXPOSE 22

CMD ["/usr/sbin/sshd", "-D", "-e"]
