# ✈️ API de Previsão de Atraso de Voos (Flight Delay Predictor)

Este projeto consiste em uma API REST desenvolvida em **Python** com **Flask**, que utiliza um modelo de Machine Learning (**Random Forest**) para prever a probabilidade de atraso de voos comerciais no Brasil.

A aplicação recebe dados do voo (companhia aérea, origem, destino, data/hora e distância) e retorna se o voo está previsto para chegar "NO HORÁRIO" ou "ATRASADO", junto com a probabilidade calculada.

---

## 📋 Pré-requisitos

* Python 3.8 ou superior
* Pip (Gerenciador de pacotes do Python)

---

## 🚀 Instalação e Configuração

Siga os passos abaixo para configurar o ambiente e rodar o projeto localmente.

### 1. Clone o repositório
```bash
git clone [https://github.com/seu-usuario/nome-do-repositorio.git](https://github.com/seu-usuario/nome-do-repositorio.git)
cd nome-do-repositorio
```
### **2. Crie e ative o Ambiente Virtual (Recomendado)**
```bash
python -m venv venv
.\venv\Scripts\activate
```
###**3. Instale as dependências**
```bash
pip install -r requirements.txt
```
(Certifique-se de que o arquivo requirements.txt contém: flask, pandas, numpy, scikit-learn, joblib, requests)

##🛠️ Como Rodar a API
Para iniciar o servidor Flask, execute o comando abaixo no terminal (com o venv ativado):
```bash
python app.py
```
Se tudo estiver correto, você verá a mensagem:

Running on http://0.0.0.0:5000

A API estará pronta para receber requisições POST no endpoint /predict.

##🤖 Como Testar (Script Automatizado)
O projeto inclui um script de teste automatizado (test_api.py) que gera cenários aleatórios de voos e envia requisições para a API.
Para rodar os testes:

1. Mantenha o app.py rodando em um terminal.

2. Abra um novo terminal, ative o venv e execute:
     
```bash
python test_api.py
```
O script irá:

1. Gerar dados aleatórios (Companhias aéreas e Aeroportos IATA).

2. Enviar as requisições para o servidor.

3. Exibir o status no terminal.

4. Salvar os detalhes no arquivo de log.

##📄 **Logs e Resultados**
Todas as requisições realizadas pelo script de teste são salvas automaticamente em um arquivo de texto para auditoria e conferência.

* **Nome do arquivo**: log_testes_iata.txt

* **Localização**: Raiz do projeto (gerado automaticamente após o primeiro teste).

**Exemplo do conteúdo do log:**

```Plaintext
--- TESTE IATA REALIZADO EM: 2025-12-30 17:45:00 ---
STATUS HTTP: 200
DADOS ENVIADOS (Input):
{
    "sg_empresa_icao": "TAM",
    "sg_iata_origem": "GRU",
    "sg_iata_destino": "SSA",
    "dt_partida_prevista": "2025-12-07 05:02:00"
}

RESPOSTA DA API (Output):
{
    "prediction": 0,
    "previsao": "NO HORÁRIO",
    "probabilidade": 0.12,
    "status": "success"
}
========================================
```
##🧠 **Sobre o Modelo e Previsões**
**Modelo Utilizado**
O sistema utiliza um classificador** Random Forest** (modelo_atraso_voos_rf_res.pkl) treinado com dados históricos de voos. O modelo analisa padrões de sazonalidade, rotas e companhias aéreas.

**Formato de Entrada**
A API espera um JSON com as seguintes chaves:

`sg_empresa_icao`: Sigla da empresa (Ex: "TAM", "AZU", "GLO").

`sg_iata_origem`: Código IATA do aeroporto de origem (Ex: "GRU", "CGH").

`sg_iata_destino`: Código IATA do aeroporto de destino (Ex: "SDU", "BSB").

`dt_partida_prevista`: Data e hora no formato "YYYY-MM-DD HH:MM:SS".

**Tratamento de Dados**
O sistema possui inteligência interna para:

Converter códigos e validar colunas obrigatórias.

Extrair features temporais (dia da semana, hora, mês) automaticamente da data informada.

Utilizar multiprocessamento para entregar a previsão rapidamente.

**Resultados Alcançados**
Nos testes realizados, a API demonstrou estabilidade retornando Status 200 para todas as combinações válidas de aeroportos e datas futuras/passadas, classificando corretamente as instâncias entre "NO HORÁRIO" e "ATRASADO" com base nas probabilidades calculadas pelo modelo.
