#!/usr/bin/env bash
set -e

BASE_URL="${BASE_URL:-http://3.81.44.40}"

echo "1) Abono de 100 a 12345678"
curl -sS -X POST "$BASE_URL:8080/api/v1/payments" \
  -H "Content-Type: application/json" \
  -d '{"timestamp":1,"card_id":"12345678","amount":100.00,"type":"A"}'
echo

echo "2) Consumo de 30 a 12345678"
curl -sS -X POST "$BASE_URL:8080/api/v1/payments" \
  -H "Content-Type: application/json" \
  -d '{"timestamp":2,"card_id":"12345678","amount":30.00,"type":"C"}'
echo

echo "3) Abono de 50 a 11223344"
curl -sS -X POST "$BASE_URL:8080/api/v1/payments" \
  -H "Content-Type: application/json" \
  -d '{"timestamp":3,"card_id":"11223344","amount":50.00,"type":"A"}'
echo

sleep 2

echo "4) Consulta de saldos"
curl -sS "$BASE_URL:8081/api/v1/search"
echo

echo "5) Kafdrop"
echo "$BASE_URL:9000"
