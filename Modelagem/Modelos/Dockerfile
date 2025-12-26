# Usamos Python 3.11 Slim para garantir compatibilidade com scikit-learn 1.6+ e numpy 2.0+
# (Imagens alpine podem ter problemas de compilação com numpy/pandas novos)
FROM python:3.11-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# 1. Copia apenas o requirements primeiro para aproveitar o cache do Docker
COPY requirements.txt .

# 2. Instala as dependências
RUN pip install --no-cache-dir -r requirements.txt

# 3. Copia o restante do código (app.py e o modelo .pkl)
COPY . .

# Expõe a porta 5000 (padrão definido no seu app.py)
EXPOSE 5000

# Comando para iniciar a aplicação
# Nota: Para produção real, recomenda-se usar gunicorn, mas python app.py funciona para este setup
CMD ["python", "app.py"]
