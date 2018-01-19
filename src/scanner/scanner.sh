echo "Up and running"

alias rabbitmqadmin="python3 /usr/local/bin/rabbitmqadmin.py"
python3 --version

while true;
do
    rabbitmqadmin -V test list exc # exchanges
    sleep 5
done
