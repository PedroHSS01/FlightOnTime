# Histórico de Mudanças

Todas as mudanças notáveis no projeto FlightOnTime serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [Não Lançado]

## [1.0.0] - 2025-12-30

### 🎉 Lançamento Inicial

#### Adicionado

- **API Java Spring Boot** (Porta 8080)
  - Endpoint de predição de atraso de voos (`/api/v1/predict`)
  - Endpoint de verificação de saúde (`/api/v1/health`)
  - Validação de entrada com Bean Validation
  - Tratamento de exceções com manipulador global de erros
  
- **ML Wrapper Python Flask** (Porta 5000)
  - Camada de integração com serviço ML
  - Endpoint de verificação de saúde
  - Transformação de requisições/respostas
  - Tratamento de erros e lógica de retry
  
- **Mock do Serviço ML** (Porta 8000)
  - Predições simuladas para testes
  - Atrasos e probabilidades configuráveis
  
- **Suporte Docker**
  - Dockerfile multi-stage para API Java
  - Imagem Docker Python otimizada
  - Orquestração com Docker Compose
  - Health checks para todos os serviços
  
- **Testes Automatizados**
  - 226 testes unitários Java (JUnit 5)
  - 16 testes Python (Pytest)
  - 78% de cobertura de código Python
  - Testes de integração
  
- **CI/CD com GitHub Actions**
  - Execução automática de testes em push/PR
  - Validação de build Docker
  - Artefatos de resultados de testes
  - Badges de status
  
- **Documentação**
  - README.md abrangente
  - Exemplos de uso da API
  - Diagramas de arquitetura
  - Guia de deploy com Docker
  - CONTRIBUTING.md
  - CODE_OF_CONDUCT.md
  - SECURITY.md
  - Templates de Issues e Pull Requests

#### Alterado

- Formato de resposta do Serviço ML: `probability` renomeado para `confidence` por consistência com o DTO Java

#### Corrigido

- Erro HTTP 500 em `/api/v1/predict` devido a incompatibilidade de nomes de campos
- Compatibilidade com Docker Compose v2
- Lógica de retry do health check do ML Wrapper
- Caminhos corretos de endpoints da API nos workflows

### 🏗️ Stack Técnico

- Java 17 + Spring Boot 3.2.12
- Python 3.11 + Flask 3.0
- Docker + Docker Compose
- Maven 3.9
- JUnit 5 + Pytest
- GitHub Actions

### 📊 Cobertura de Testes

- Java: 100% de taxa de sucesso (226/226)
- Python: 78% de cobertura de código (16/16 testes)
- Total: 242 testes automatizados

### 🎯 Pronto para Oracle Cloud

- Arquitetura containerizada
- Health checks implementados
- Configuração via variáveis de ambiente
- Imagens Docker prontas para produção

---

**Changelog Completo**: [https://github.com/Mateus-Redivo/FlightOnTime/commits/v1.0.0](https://github.com/Mateus-Redivo/FlightOnTime/commits/v1.0.0)
