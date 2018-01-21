echo "[Scanner Entrypoint] Up and running"
/etc/init.d/cron restart
while true
do
    echo "Stress me..."
    sleep 5
done
