# Guía de pruebas - Proyecto Final Kafka + Kafdrop

## 1. Flujo a validar

Cliente → payment-api → Kafka (`payments`) → payment-stream → KTable/State Store → GET de saldos

Kafdrop se usa para evidenciar el tópico `payments`, sus mensajes y offsets.

IP pública actual de la EC2: `3.81.44.40`

> Si la instancia EC2 cambia de IP pública, reemplazarla en los ejemplos.

---

## 2. Prueba 1 - Registrar un abono

```bash
curl -X POST http://3.81.44.40:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": 1,
    "card_id": "12345678",
    "amount": 100.00,
    "type": "A"
  }'
```

Resultado esperado: saldo `100.00` para la tarjeta `12345678`.

---

## 3. Prueba 2 - Registrar un consumo

```bash
curl -X POST http://3.81.44.40:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": 2,
    "card_id": "12345678",
    "amount": 30.00,
    "type": "C"
  }'
```

Resultado esperado: `100.00 - 30.00 = 70.00`.

---

## 4. Prueba 3 - Registrar otra tarjeta

```bash
curl -X POST http://3.81.44.40:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": 3,
    "card_id": "11223344",
    "amount": 50.00,
    "type": "A"
  }'
```

Resultado esperado:
- `12345678` → `70.00`
- `11223344` → `50.00`

---

## 5. Consultar saldos

```bash
curl http://3.81.44.40:8081/api/v1/search
```

Respuesta esperada aproximada:

```json
[
  {
    "card_id": "12345678",
    "total": 70.00
  },
  {
    "card_id": "11223344",
    "total": 50.00
  }
]
```

El orden puede variar.

---

## 6. Pruebas desde Windows PowerShell

### Abono

```powershell
curl.exe -X POST "http://3.81.44.40:8080/api/v1/payments" `
  -H "Content-Type: application/json" `
  -d "{\"timestamp\":1,\"card_id\":\"12345678\",\"amount\":100.00,\"type\":\"A\"}"
```

### Consumo

```powershell
curl.exe -X POST "http://3.81.44.40:8080/api/v1/payments" `
  -H "Content-Type: application/json" `
  -d "{\"timestamp\":2,\"card_id\":\"12345678\",\"amount\":30.00,\"type\":\"C\"}"
```

### Consulta

```powershell
curl.exe "http://3.81.44.40:8081/api/v1/search"
```

---

## 7. Verificar los contenedores en EC2

```bash
docker compose ps
```

Se espera ver:

```text
kafka            Up (healthy)
payment-api      Up
payment-stream   Up
kafdrop          Up
```

---

## 8. Verificar el tópico en Kafka

```bash
docker exec kafka kafka-topics \
  --bootstrap-server kafka:19092 \
  --list
```

Debe aparecer:

```text
payments
```

---

## 9. Evidencia en Kafdrop

Abrir en un navegador:

`http://3.81.44.40:9000`

Luego ingresar a:

`Topics → payments`

La evidencia debe mostrar:
- Topic `payments`
- Partición
- Offset
- Key (`card_id`)
- Value (JSON del pago)

Ejemplo conceptual:

```text
Offset   Key        Type   Amount
0        12345678   A      100.00
1        12345678   C       30.00
2        11223344   A       50.00
```

Tomar una captura donde se observen claramente los offsets y mensajes.

---

## 10. Resultado final esperado

Después de las tres operaciones:

```text
12345678 -> 70.00
11223344 -> 50.00
```

La consulta:

```bash
curl http://3.81.44.40:8081/api/v1/search
```

debe devolver esos saldos calculados por Kafka Streams.
