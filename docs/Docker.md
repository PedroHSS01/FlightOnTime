# Docker - Guia de Uso

Este guia explica como executar a aplicação Flight On Time e seus testes usando Docker.

## Pré-requisitos

- Docker Desktop instalado e em execução
- Docker Compose (geralmente incluído no Docker Desktop)

## Estrutura de Containers

A aplicação possui dois serviços configurados:

- **fot-api**: Container principal da aplicação Spring Boot
- **fot-tests**: Container para execução dos testes (sob demanda)

## Iniciar a Aplicação

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

Após iniciar o container, a aplicação estará disponível em:

- URL: `http://localhost:8080`
- Porta VS Code: Verifique a aba **PORTS** no painel inferior

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

### Rodar todos os testes

```powershell
docker compose run --rm fot-tests
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
# Logs em tempo real
docker logs -f fot-api

# Últimas 100 linhas
docker logs --tail 100 fot-api
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
docker-compose build --no-cache
```

### Verificar status dos containers

```powershell
docker-compose ps
```

### Acessar o shell do container

```powershell
docker exec -it fot-api sh
```

## Troubleshooting

### Container não inicia

1. Verifique os logs:
   ```powershell
   docker logs fot-api
   ```

2. Verifique se a porta 8080 está disponível:
   ```powershell
   netstat -ano | findstr :8080
   ```

### Rebuild após mudanças no código

```powershell
docker-compose down
docker-compose up --build
```

### Limpar imagens antigas

```powershell
docker system prune -a
```

## Arquitetura Docker

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
  - Profile padrão (sempre inicia)
  - Rede isolada `fot-network`

- **fot-tests**:
  - Usa imagem Maven diretamente
  - Volume-mounted no código fonte
  - Profile `test` (execução manual)
  - Executa `mvn test`

## Notas Importantes

- O container de testes **não** inicia automaticamente com `docker-compose up`
- Os testes são executados isoladamente e não afetam o container da aplicação
- Mudanças no código requerem rebuild da imagem
- A primeira execução pode demorar devido ao download das dependências Maven
