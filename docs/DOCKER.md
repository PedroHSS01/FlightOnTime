# Docker - Guia de Uso

> **📚 Documentação Completa:**
> - **[README.md](README.md)** - Arquitetura e Fluxo de Dados
> - **[Docker.md](Docker.md)** - Guia de Docker e Containers (você está aqui)

Este guia explica como executar a aplicação Flight On Time e seus testes usando Docker.

## Pré-requisitos

- Docker Desktop instalado e em execução
- Docker Compose (geralmente incluído no Docker Desktop)

## Estrutura de Containers

A aplicação possui três serviços principais e containers adicionais para testes:

### Serviços Principais

- **fot-api**: API Java Spring Boot (porta 8080)
- **ml-wrapper**: Flask ML Wrapper em Python (porta 5000)
- **ml-service-mock**: Mock do serviço de ML (porta 8000) - profile `mock`

### Serviços de Teste

- **fot-tests**: Container para testes Java (sob demanda, profile `test`)
- **ml-wrapper-tests**: Container para testes Python (sob demanda, profile `test`)

### Rede

Todos os containers se comunicam através da rede `fot-network`.

## Iniciar a Aplicação

### Sistema Completo (Recomendado)

```powershell
cd d:\FlightOnTime

# Primeira vez - com build e mock ML
docker compose --profile mock up --build

# Execuções subsequentes
docker compose --profile mock up

# Em background
docker compose --profile mock up -d
```

### Apenas Serviços Essenciais (Sem Mock ML)

```powershell
# Se você tem um ML Service real configurado
docker compose up -d
```

### Primeira vez (com build)

```powershell
cd d:\FlightOnTime
docker-compose up --build
```

### Execuções subsequentes

```powershell
docker-compose up
```

### Executar em background

```powershell
docker-compose up -d
```

## Acessar a Aplicação

Após iniciar os containers, os serviços estarão disponíveis em:

### API Java (Principal)
- **URL**: `http://localhost:8080`
- **Endpoints**:
  - `POST /api/v1/predict` - Fazer predição de voo
  - `GET /api/v1/health` - Health check

### Flask ML Wrapper
- **URL**: `http://localhost:5000`
- **Endpoints**:
  - `POST /predict` - Endpoint de predição
  - `GET /health` - Health check

### Mock ML Service
- **URL**: `http://localhost:8000`
- **Endpoints**:
  - `POST /predict` - Predição mockada
  - `GET /health` - Health check

### Exemplo de Requisição

```powershell
$body = @{
    flightNumber = "AA1234"
    companyName = "AA"
    flightOrigin = "JFK"
    flightDestination = "LAX"
    flightDepartureDate = "2025-12-25T14:30:00"
    flightDistance = 3974
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/predict" `
                  -Method POST `
                  -Body $body `
                  -ContentType "application/json"
```

### Autenticação

A aplicação usa Spring Security com autenticação básica:

- **Usuário**: `user`
- **Senha**: Gerada automaticamente (veja nos logs do container)

Para ver a senha gerada:

```powershell
docker logs fot-api
```

Procure por: `Using generated security password: [senha]`

## Executar Testes

### Testes Java

```powershell
# Rodar todos os testes Java
docker compose --profile test run --rm fot-tests

# Ver relatório detalhado
# Após executar os testes, o relatório estará em:
# fot/target/surefire-reports/
```

### Testes Python

```powershell
# Rodar todos os testes Python
docker compose --profile test run --rm ml-wrapper-tests

# Rodar testes com coverage
docker compose --profile test run --rm ml-wrapper-tests pytest --cov=app tests/
```

### Rodar todos os testes de uma vez

```powershell
# Java e Python
docker compose --profile test run --rm fot-tests
docker compose --profile test run --rm ml-wrapper-tests
```

O container de testes:
- Executa todos os testes unitários
- Exibe os resultados no terminal
- Remove-se automaticamente após a execução (`--rm`)

### Ver relatório detalhado

Após executar os testes, o relatório Surefire estará disponível em:
```
fot/target/surefire-reports/
```

## Comandos Úteis

### Ver logs da aplicação

```powershell
# API Java - Logs em tempo real
docker logs -f fot-api

# Flask Wrapper - Logs em tempo real
docker logs -f ml-wrapper

# Mock ML Service - Logs em tempo real
docker logs -f ml-service-mock

# Últimas 100 linhas de cada serviço
docker logs --tail 100 fot-api
docker logs --tail 100 ml-wrapper
docker logs --tail 100 ml-service-mock
```

### Health Check

```powershell
# Verificar saúde de todos os serviços
curl http://localhost:8080/api/v1/health  # Java API
curl http://localhost:5000/health         # Flask Wrapper
curl http://localhost:8000/health         # Mock ML
```

### Parar a aplicação

```powershell
docker-compose down
```

### Parar e remover volumes

```powershell
docker-compose down -v
```

### Reconstruir a imagem

```powershell
# Reconstruir tudo
docker-compose build --no-cache

# Reconstruir serviço específico
docker compose build --no-cache fot-api
docker compose build --no-cache ml-wrapper
```

### Verificar status dos containers

```powershell
docker-compose ps

# Ou mais detalhado
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

### Acessar o shell do container

```powershell
# Java API
docker exec -it fot-api sh

# Flask Wrapper
docker exec -it ml-wrapper sh

# Mock ML
docker exec -it ml-service-mock sh
```

## Troubleshooting

### Container não inicia

1. Verifique os logs:
   ```powershell
   docker logs fot-api
   docker logs ml-wrapper
   docker logs ml-service-mock
   ```

2. Verifique se as portas estão disponíveis:
   ```powershell
   netstat -ano | findstr :8080  # Java API
   netstat -ano | findstr :5000  # Flask Wrapper
   netstat -ano | findstr :8000  # Mock ML
   ```

3. Verifique a rede Docker:
   ```powershell
   docker network ls
   docker network inspect fot-network
   ```

### Erro de comunicação entre containers

```powershell
# Testar conectividade
docker exec fot-api ping ml-wrapper
docker exec ml-wrapper ping ml-service

# Verificar resolução DNS
docker exec fot-api nslookup ml-wrapper
```

### Mock ML Service não responde

```powershell
# Verificar se foi iniciado com profile mock
docker ps | Select-String ml-service

# Reiniciar o serviço
docker restart ml-service-mock

# Verificar logs
docker logs ml-service-mock
```

### Rebuild após mudanças no código

```powershell
# Parar tudo
docker-compose down

# Rebuild e subir
docker compose --profile mock up --build

# Ou rebuild apenas serviço específico
docker compose build ml-wrapper
docker compose up -d ml-wrapper
```

### Limpar imagens antigas

```powershell
# Limpar tudo (cuidado!)
docker system prune -a

# Limpar apenas imagens não usadas
docker image prune -a
```

## Arquitetura Docker

### Multi-Container Architecture

O sistema utiliza arquitetura de múltiplos containers:

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│   fot-api       │──────>│   ml-wrapper    │──────>│  ml-service     │
│  (Java/Spring)  │       │  (Flask/Python) │       │   (Mock/Real)   │
│   Port: 8080    │       │   Port: 5000    │       │   Port: 8000    │
└─────────────────┘       └─────────────────┘       └─────────────────┘
        │                         │                         │
        └─────────────────────────┴─────────────────────────┘
                            fot-network
```

### Dockerfile (Multi-stage Build)

O Dockerfile usa duas etapas:

1. **Build Stage**: Compila a aplicação com Maven
   - Imagem base: `maven:3.9-eclipse-temurin-17`
   - Baixa dependências
   - Compila o código fonte
   - Gera o arquivo JAR

2. **Runtime Stage**: Executa a aplicação
   - Imagem base: `eclipse-temurin:17-jre-alpine` (leve)
   - Copia apenas o JAR compilado
   - Expõe a porta 8080

### docker-compose.yml

Configuração dos serviços:

- **fot-api**:
  - Build a partir do Dockerfile
  - Expõe porta 8080
  - Depende de ml-wrapper (health check)
  - Rede `fot-network`
  - Variáveis: ML_SERVICE_URL, ML_SERVICE_TIMEOUT

- **ml-wrapper**:
  - Build a partir do Dockerfile Python
  - Expõe porta 5000
  - Health check configurado
  - Rede `fot-network`
  - Variáveis: ML_SERVICE_URL, FLASK_ENV

- **ml-service** (mock):
  - Profile: `mock`
  - Expõe porta 8000
  - Rede `fot-network`
  - Mock para desenvolvimento/testes

- **fot-tests**:
  - Usa imagem Maven diretamente
  - Volume-mounted no código fonte
  - Profile `test` (execução manual)
  - Executa `mvn test`

- **ml-wrapper-tests**:
  - Profile `test` (execução manual)
  - Executa `pytest`
  - Rede `fot-network`

## Notas Importantes

- O container de testes **não** inicia automaticamente com `docker-compose up`
- Os testes são executados isoladamente e não afetam os containers da aplicação
- Mudanças no código requerem rebuild da imagem (ou use docker-compose.dev.yml)
- A primeira execução pode demorar devido ao download das dependências Maven e Python
- O Mock ML Service requer o profile `--profile mock` para ser iniciado
- Para desenvolvimento com hot-reload, use `mlwrapper/docker-compose.dev.yml`
- A comunicação entre containers usa a rede interna Docker (nomes de containers)
- Health checks garantem que ml-wrapper esteja pronto antes do fot-api iniciar

## 🔗 Próximos Passos

- Veja [README.md](README.md) para entender o fluxo de dados completo

---

**Última atualização:** 21 de dezembro de 2025
