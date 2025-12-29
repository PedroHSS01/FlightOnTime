# Relatório de Integração do Modelo (escrito)

Resumo executivo
---------------
Integramos o arquivo de modelo treinado `Modelagem/Modelos/modelo_previsao_voos.bin` ao pipeline existente (Java API -> `ml-wrapper` -> `modelos-ml`). O bin não era um pickle; carregamos diretamente com CatBoost, ajustamos o wrapper para mapear corretamente os campos e acrescentei validações e pequenos scripts de diagnóstico para facilitar testes.

O que foi alterado (visão geral)
--------------------------------
- `Modelagem/Modelos/app.py`
  - Carregamento robusto do modelo com fallback para detectar formatos; uso principal de `catboost` para este bin.
  - Inclusão de `ModelAdapter` para fornecer uma API única (`predict` / `predict_proba`) mesmo quando o modelo não implementa `predict_proba`.
  - Montagem explícita do vetor de entrada com as colunas e tipos que o modelo espera: `sg_empresa_icao`, `sg_icao_origem`, `sg_icao_destino`, `sg_equipamento_icao`, `nr_assentos_ofertados`, `cd_tipo_linha`, `mes_partida`, `dia_semana`, `hora_partida`.
  - Extração de `mes_partida`, `dia_semana` e `hora_partida` a partir do campo `data_partida` (ISO datetime) e conversão de tipos críticos.

- `mlwrapper/app/services/ml_client.py`
  - Mapeamento do DTO Java para o payload do serviço de modelo:
    - `companyName` -> `companhia`
    - `flightOrigin` -> `origem`
    - `flightDestination` -> `destino`
    - `flightDepartureDate` -> `data_partida`
    - `flightDistance` -> `nr_assentos_ofertados` (fallback quando necessário)
  - Validação/normalização adicional para evitar erros de conversão (ex.: uppercase, parsing de data).

- `Modelagem/Modelos/requirements.txt`
  - Removidos `lightgbm` e `xgboost` para reduzir o tamanho da imagem; mantido apenas `catboost` (necessário para este bin).

- Scripts de suporte (novos)
  - `Modelagem/Modelos/check_model.py`: inspeciona o bin e imprime as features esperadas.
  - `Modelagem/Modelos/run_predict_debug.py`: reproduz uma predição localmente com as features no formato esperado.

Por que fiz essas mudanças
--------------------------
- O bin recebido não era um pickle; forçar um joblib.load causava erros. Carregar com CatBoost preserva o modelo e evita retraining/conversão.
- Ajustes no `ml-wrapper` garantem que a API Java continue inalterada; o wrapper faz a tradução necessária para o modelo.
- Remover dependências desnecessárias reduz build time e tamanho da imagem Docker, facilitando deploy.

Como testar (passo a passo)
--------------------------

- 1) Build e subir os serviços com Docker Compose:

```powershell
docker-compose build --no-cache modelos-ml ml-wrapper
docker-compose up -d modelos-ml ml-wrapper fot-api
```

- 2) Verificar logs dos serviços (se necessário):

```powershell
docker-compose logs -f modelos-ml
docker-compose logs -f ml-wrapper
```

- 3) Teste o fluxo completo (Java API -> `ml-wrapper` -> `modelos-ml`):

Quando for testar, ajuste a data para uma data futura válida. Caso contrário, o teste caira em exceção de data na API Java.

```powershell
$body = @{
    flightNumber = "AA1234"
    companyName = "AA"
    flightOrigin = "JFK"
    flightDestination = "LAX"
    flightDepartureDate = "2025-12-30T14:30:00"
    flightDistance = 3974
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/predict" `
                  -Method POST `
                  -Body $body `
                  -ContentType "application/json"
```

Exemplo de saída esperada (Java API):

```txt
prediction            : ON_TIME
probability           : 0,00517540742221168
confidence            : VERY_LOW
probabilityPercentage : 0,517540742221168
formattedProbability  : 0.52%
confidenceLevel       : VERY_LOW
highConfidence        : False
lowConfidence         : True
summary               : Flight is predicted to be ON_TIME with VERY_LOW confidence (0.52%)
```
- 4) Testes locais e de debug (sem o fluxo Java):

```powershell
# ativar venv e instalar dependências se necessário
cd Modelagem/Modelos
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python run_predict_debug.py
```

O `run_predict_debug.py` imprime a ordem das features e o valor retornado pelo `predict()` (raw margin ou probabilidade). Use isso para ajustar tipos e valores antes de testar o caminho completo.

Verificação rápida (checklist)
-----------------------------
- [ ] `modelos-ml` sobe sem erro e carrega o modelo (ver `docker-compose logs modelos-ml`).
- [ ] `ml-wrapper` recebe requisições do Java API e encaminha para `modelos-ml`.
- [ ] Resposta contém `prediction` e `probability` com valores numéricos.

Notas de produção e recomendações finais
--------------------------------------
- Execução: usar um WSGI server (ex.: gunicorn) para o `modelos-ml` em produção.
- Dependências: `catboost` é necessária; `lightgbm` e `xgboost` foram removidos para reduzir a imagem.
- Segurança: não exponha o endpoint de predição sem autenticação em ambientes públicos.

Rollback
--------
Para reverter: restaurar a versão anterior de `Modelagem/Modelos/app.py` e rebuildar a imagem de `modelos-ml`.

Arquivos alterados
-----------------
- `Modelagem/Modelos/app.py` (modificado)
- `Modelagem/Modelos/requirements.txt` (modificado)
- `mlwrapper/app/services/ml_client.py` (modificado)
- `Modelagem/Modelos/check_model.py` (adicionado)
- `Modelagem/Modelos/run_predict_debug.py` (adicionado)
---

Rodando o container de load-test
--------------------------------

Há um serviço `load-tester` no `docker-compose.yml` que executa o script `mlwrapper/scripts/load_test.py` dentro de um container Python (sem necessidade de instalar dependências localmente).

Exemplos de uso (PowerShell):

1) Subir serviços necessários e rodar o tester isoladamente:

```powershell
docker-compose up -d modelos-ml ml-wrapper fot-api
docker-compose run --rm load-tester
```

2) Rodar via profile de testes (inicia containers do profile e para quando o tester terminar):

```powershell
docker-compose --profile test up --abort-on-container-exit load-tester
```

3) Para passar argumentos distintos ao script (por exemplo alterar número de requisições):

```powershell
docker-compose run --rm load-tester python scripts/load_test.py --url http://fot-api:8080/api/v1/predict -n 200 --concurrency 10 --output results.json
```

Onde os resultados ficam
------------------------

O `load-tester` monta `./mlwrapper` em `/app`, então o arquivo de saída `results.json` será escrito em `mlwrapper/results.json` no host.

Observações
-----------
- O `load-tester` roda dentro do container e instala dependências via `pip` no container, portanto não afeta o ambiente Python local.
- Ajuste a URL para apontar para outro serviço se necessário (por exemplo `http://localhost:8080/api/v1/predict` quando não usar rede de containers).
