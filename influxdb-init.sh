#!/bin/bash

# Wait for InfluxDB to be available
while ! curl --silent --fail http://influxdb:8086/ping; do
  echo "Waiting for InfluxDB to be available..."
  sleep 2
done

# Generate the token for InfluxDB
TOKEN=$(curl -s -X POST "http://influxdb:8086/api/v2/authorizations" \
  -H "Authorization: Token $INFLUXDB_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"orgID\": \"$INFLUXDB_ORG\",
    \"permissions\": [
      {
        \"action\": \"read\",
        \"resource\": {
          \"type\": \"buckets\",
          \"id\": \"$INFLUXDB_BUCKET\"
        }
      },
      {
        \"action\": \"write\",
        \"resource\": {
          \"type\": \"buckets\",
          \"id\": \"$INFLUXDB_BUCKET\"
        }
      }
    ]
  }" | jq -r ".token")

# Output the token
echo "Generated token: $TOKEN"

# Save the token to a file
echo $TOKEN > /influxdb-token.txt

# Make the token available to other services
cp /influxdb-token.txt /etc/influxdb2/influxdb-token.txt
