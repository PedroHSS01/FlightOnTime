import requests
import json
import random
from datetime import datetime, timedelta

# --- CONFIGURAÇÃO ---
URL_API = "http://localhost:5000/predict"
ARQUIVO_LOG = "log_testes_iata.txt"

# --- DADOS PARA GERAÇÃO DE TESTES (PADRÃO IATA) ---

# Empresas (Mantive conforme seus dados mostraram: AZU, TAM, GLO...)
EMPRESAS = ['AZU', 'TAM', 'GLO', 'ACN', 'PTB']

# Lista de Aeroportos (IATA - 3 Letras)
AEROPORTOS_IATA = [
    'GRU', 'CGH', 'VCP', 'BSB', 'CNF', 'GIG', 'SDU', 
    'CWB', 'POA', 'REC', 'SSA', 'FLN', 'BEL', 'GYN'
]

def gerar_data_aleatoria():
    """Gera uma data futura ou passada para simulação."""
    dias_offset = random.randint(-30, 30)
    hora_aleatoria = random.randint(0, 23)
    minuto_aleatorio = random.randint(0, 59)
    
    data = datetime.now() + timedelta(days=dias_offset)
    data = data.replace(hour=hora_aleatoria, minute=minuto_aleatorio, second=0)
    
    return data.strftime("%Y-%m-%d %H:%M:%S")

def salvar_log(entrada, saida, status_code):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    with open(ARQUIVO_LOG, "a", encoding="utf-8") as f:
        f.write(f"--- TESTE IATA REALIZADO EM: {timestamp} ---\n")
        f.write(f"STATUS HTTP: {status_code}\n")
        f.write("DADOS ENVIADOS (Input):\n")
        f.write(json.dumps(entrada, indent=4, ensure_ascii=False))
        f.write("\n\nRESPOSTA DA API (Output):\n")
        f.write(json.dumps(saida, indent=4, ensure_ascii=False))
        f.write("\n" + "="*40 + "\n\n")

def executar_teste():
    origem = random.choice(AEROPORTOS_IATA)
    destino = random.choice(AEROPORTOS_IATA)
    
    while destino == origem:
        destino = random.choice(AEROPORTOS_IATA)

    # Payload atualizado para usar as chaves que o modelo espera (IATA)
    payload = {
        "sg_empresa_icao": random.choice(EMPRESAS),
        "sg_iata_origem": origem,   # Mudou de sg_icao_origem para sg_iata_origem
        "sg_iata_destino": destino, # Mudou de sg_icao_destino para sg_iata_destino
        "dt_partida_prevista": gerar_data_aleatoria()
    }

    print(f"Enviando teste: {payload['sg_empresa_icao']} | {origem} -> {destino} ...")

    try:
        response = requests.post(URL_API, json=payload)
        
        try:
            dados_resposta = response.json()
        except:
            dados_resposta = response.text

        salvar_log(payload, dados_resposta, response.status_code)
        
        print(f"-> Status: {response.status_code}")
        if response.status_code == 200:
            print(f"-> Previsão: {dados_resposta.get('previsao', 'N/A')}")
        else:
            print(f"-> Erro: {dados_resposta}")
        print("------------------------------------------------")

    except requests.exceptions.ConnectionError:
        print("ERRO: Não foi possível conectar. Verifique se o app.py está rodando.")

if __name__ == "__main__":
    QTD_TESTES = 5

    print(f"Iniciando {QTD_TESTES} testes com padrão IATA...\n")
    for _ in range(QTD_TESTES):
        executar_teste()
    print(f"\nTestes finalizados. Verifique '{ARQUIVO_LOG}'.")
