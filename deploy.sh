echo "Building app..."
./mvnw clean package -DskipTests

echo "Deploy files to server..."
scp -r -i C:/Users/nguye/OneDrive/Desktop/swd392 target/mentorbooking.jar root@167.71.220.5:/var/www/be/

ssh -i C:/Users/nguye/OneDrive/Desktop/swd392 root@167.71.220.5 <<EOF
pid=\$(sudo lsof -t -i :8080)

if [ -z "\$pid" ]; then
    echo "Start server..."
else
    echo "Restart server..."
    sudo kill -9 "\$pid"
fi
cd /var/www/be
java -jar mentorbooking.jar
EOF

exit
echo "Done!"