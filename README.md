# ✈️ FlightOnTime - Previsão Inteligente de Atrasos de Voos

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.12-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.11-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Tests](https://img.shields.io/badge/Tests-242%20Passing-success?style=for-the-badge)

**API REST + Machine Learning para prever atrasos de voos**

[🚀 Quick Start](#-quick-start-30-segundos) • [📊 Demo](#-exemplo-prático) • [🧪 Testes](#-testes-realizados) • [🏗️ Arquitetura](#-arquitetura-do-sistema)

</div>

---

## 🎯 O Projeto

Sistema completo de **previsão de atrasos de voos** usando Machine Learning:

- ✅ **API REST** robusta (Java Spring Boot)
- ✅ **ML Wrapper** para integração (Python Flask)
- ✅ **242 testes automatizados** (100% aprovados)
- ✅ **Docker Compose** (deploy com 1 comando)
- ✅ **Documentação Swagger** interativa
- ✅ **Validações completas** de dados

### 💡 Como Funciona

```
Cliente → Java API (valida) → Flask ML (integra) → ML Model (prevê) → Resposta JSON
```

**Você envia:** Dados do voo (origem, destino, data, distância)  
**Você recebe:** Previsão (ATRASADO 78% / PONTUAL 85%) com nível de confiança

---

## 🚀 Quick Start (30 segundos)

### Execute agora:

```bash
# 1. Clone
git clone https://github.com/Mateus-Redivo/FlightOnTime.git
cd FlightOnTime

# 2. Suba os 3 containers Docker
docker compose --profile mock up -d

# 3. Teste a API
curl -X POST http://localhost:8080/api/v1/predict \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "LA4001",
    "companyName": "LA",
    "flightOrigin": "GRU",
    "flightDestination": "GIG",
    "flightDepartureDate": "2025-12-31T14:30:00",
    "flightDistance": 358
  }'
```

**Pronto!** A API está rodando em `http://localhost:8080`

---

## 📊 Exemplo Prático

**Request:**
```json
{
  "flightNumber": "LA4001",
  "companyName": "LA",
  "flightOrigin": "GRU",
  "flightDestination": "GIG",
  "flightDepartureDate": "2025-12-31T14:30:00",
  "flightDistance": 358
}
```

**Response:**
```json
{
  "prediction": "DELAYED",
  "probability": 0.78,
  "confidenceLevel": "HIGH",
  "probabilityPercentage": 78.0,
  "formattedProbability": "78.00%",
  "highConfidence": true,
  "summary": "Flight is predicted to be DELAYED with HIGH confidence (78.00%)"
}
```

**Significado:** Voo tem **78% de chance de atrasar** com **alta confiança**.

---

## 🏗️ Arquitetura do Sistema

### Microserviços (3 containers)

```
┌─────────────┐
│   Cliente   │  (Postman/cURL/App)
└──────┬──────┘
       │ POST /api/v1/predict
       ▼
┌─────────────────────┐
│   Java API :8080    │  ✅ Valida dados (Bean Validation)
│   Spring Boot       │  ✅ Trata erros (GlobalExceptionHandler)
└──────┬──────────────┘  ✅ Documenta API (Swagger/OpenAPI)
       │
       ▼
┌─────────────────────┐
│  Flask ML :5000     │  ✅ Integra com ML Model
│  Python Wrapper     │  ✅ Retry logic (3 tentativas)
└──────┬──────────────┘  ✅ Correlation ID (rastreamento)
       │
       ▼
┌─────────────────────┐
│   ML Model :8000    │  ✅ Previsão com ML
│   Mock/Real Service │  ✅ Retorna probabilidade
└─────────────────────┘
```

### Componentes Principais

| Componente | Tecnologia | Função |
|-----------|-----------|--------|
| **Java API** | Spring Boot 3.2.12 | Gateway, validação, tratamento de erros |
| **ML Wrapper** | Python Flask 3.0.0 | Integração com modelo ML |
| **ML Service** | Python (Mock) | Serviço de previsão ML |
| **Docker** | Compose | Orquestração dos 3 containers |

---

## 🧪 Testes Realizados

### ✅ 242 Testes - 100% Aprovados (0 falhas)

| Categoria | Quantidade | Status |
|-----------|------------|--------|
| **Testes Java** | 226 | ✅ 100% |
| **Testes Python** | 16 | ✅ 100% |
| **Cobertura Python** | 78% | ✅ |
| **Tempo Execução** | 42s | ✅ |

### Distribuição dos Testes

**Java (226 testes):**
- Controller (11): validação, happy path, edge cases
- DTOs (153): FlightPredictionRequest/Response/MLService
- Enums (61): FlightPrediction, mapeamentos
- Application (1): context load

**Python (16 testes):**
- ML Client (7): timeout, connection, retry, exceptions
- Routes (9): validation, health checks, integration

**Integração (9 testes):**
- Health checks (3 serviços)
- End-to-end flow (Java → Flask → ML)
- Múltiplas companhias aéreas

**Validação (5 cenários):**
- ✅ Campos vazios → HTTP 400
- ✅ Distância negativa → HTTP 400
- ✅ IATA inválido → HTTP 400
- ✅ Origem = Destino → HTTP 400
- ✅ Data futura (>365d) → HTTP 400

**Resiliência (2 cenários):**
- ✅ ML Service down → HTTP 500 (esperado)
- ✅ Recuperação → HTTP 200 (funciona)

### Performance

- **End-to-End**: 150-200ms
- **ML Wrapper**: 10-15ms
- **Health Checks**: <15ms

📄 **Relatórios:** `RELATÓRIO_TESTES_COMPLETO.md` e `LISTA_TESTES.md`

---

## 🛠️ Tecnologias

### Backend (Java)
- ☕ Java 17
- 🍃 Spring Boot 3.2.12
- 🔒 Spring Security + Validation
- 📚 SpringDoc OpenAPI (Swagger)
- 🧰 Lombok

### ML Integration (Python)
- 🐍 Python 3.11
- 🌶️ Flask 3.0.0
- 🔄 Gunicorn (4 workers)
- ✅ Pydantic (validação)
- 🔁 Retry Logic (3x)

### DevOps
- 🐳 Docker + Docker Compose
- 🔧 Maven (build)
- ✅ JUnit 5 + Pytest
- 📊 Coverage Reports

---

## 📋 Funcionalidades Implementadas

### ✅ Validações Robustas
- `@NotBlank`, `@NotNull`, `@Size`, `@Pattern`
- Custom validator `@ValidFlight`
- Bean Validation completo
- Pydantic models (Python)

### ✅ Tratamento de Erros
- GlobalExceptionHandler (Java)
- Exceções customizadas (Python)
- Respostas HTTP padronizadas
- Mensagens descritivas

### ✅ Observabilidade
- Correlation ID (UUID)
- Structured logging
- Performance metrics
- Health checks

### ✅ Níveis de Confiança
- **HIGH** (≥70%): Alta confiança
- **MEDIUM** (50-69%): Confiança média
- **LOW** (30-49%): Baixa confiança
- **VERY_LOW** (<30%): Muito baixa

---

## 📚 Documentação da API

### Endpoints

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/predict` | Previsão de atraso |
| GET | `/health` | Health check (Flask) |

### Swagger UI

Acesse: `http://localhost:8080/swagger-ui.html`

Documentação interativa com:
- ✅ Descrição de endpoints
- ✅ Modelos de request/response
- ✅ Teste direto na interface
- ✅ Exemplos de uso

---

## 🐳 Docker

### Serviços

```yaml
services:
  fot-api:           # Java Spring Boot (porta 8080)
  ml-wrapper:        # Python Flask (porta 5000)
  ml-service-mock:   # Mock ML (porta 8000)
```

### Comandos Úteis

```bash
# Subir todos os serviços
docker compose --profile mock up -d

# Ver status
docker compose ps

# Ver logs
docker compose logs -f

# Parar
docker compose down

# Rebuild
docker compose build
```

---

## 🎓 Estrutura do Projeto

```
FlightOnTime/
├── fot/                          # Java Spring Boot API
│   ├── src/main/java/com/backend/fot/
│   │   ├── controller/           # PredictionController
│   │   ├── service/              # PredictionService, MLServiceClient
│   │   ├── dto/                  # Request/Response DTOs
│   │   ├── enums/                # FlightPrediction, ConfidenceLevel
│   │   ├── config/               # RestTemplate, Security
│   │   └── exception/            # GlobalExceptionHandler
│   ├── src/test/java/            # 226 testes
│   └── pom.xml                   # Maven dependencies
│
├── mlwrapper/                    # Python Flask ML Wrapper
│   ├── app/
│   │   ├── routes/               # prediction_routes.py
│   │   ├── services/             # ml_client.py
│   │   ├── middleware/           # logging.py
│   │   └── exceptions.py         # Custom exceptions
│   ├── tests/                    # 16 testes
│   └── requirements.txt          # Python dependencies
│
├── mock_ml_service/              # Mock ML Service
│   └── mock_ml_service.py
│
├── docker-compose.yml            # Orquestração
├── README.md                     # Esta documentação
├── RELATÓRIO_TESTES_COMPLETO.md # Relatório detalhado
└── LISTA_TESTES.md              # Lista compacta
```

---

## 🚀 Próximos Passos

### Melhorias Planejadas

- [ ] Spring Boot Actuator (metrics, health)
- [ ] Circuit Breaker (Resilience4j)
- [ ] Aumentar cobertura Python (>85%)
- [ ] CI/CD (GitHub Actions)
- [ ] Frontend com Looker Studio
- [ ] BigQuery integration
- [ ] Endpoint `/api/v1/history`

---

## 👥 Contribuindo

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/AmazingFeature`)
3. Commit (`git commit -m 'Add AmazingFeature'`)
4. Push (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 📄 Licença

MIT License - veja [LICENSE](LICENSE)

---

## 📞 Contato

- **GitHub**: [@Mateus-Redivo](https://github.com/Mateus-Redivo)
- **Issues**: [GitHub Issues](https://github.com/Mateus-Redivo/FlightOnTime/issues)

---

<div align="center">

**Desenvolvido com ❤️ e ☕**

*FlightOnTime - Previsão Inteligente de Atrasos de Voos*

</div>
