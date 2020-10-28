#! /bin/bash

echo "Running JAVA RMI Registry..."
rmiregistry &
sleep 1

echo "Running the Switcher..."

java Switcher