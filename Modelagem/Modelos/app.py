import sys
import traceback
import joblib
import pandas as pd
import numpy as np
from flask import Flask, request, jsonify

# --- 1. BLOCO DE COMPATIBILIDADE (Monkey Patch & Classe) ---
# Mantido igual, pois é necessário para o joblib.load funcionar corretamente
from sklearn.base import BaseEstimator, TransformerMixin
import sklearn.compose._column_transformer

try:
    if not hasattr(sklearn.compose._column_transformer, '_RemainderColsList'):
        class _RemainderColsList(list):
            def __getstate__(self):
                return self[:]
            def __setstate__(self, state):
                self[:] = state
        sklearn.compose._column_transformer._RemainderColsList = _RemainderColsList
except Exception:
    pass

class ExtratorDeDatas(BaseEstimator, TransformerMixin):
    def fit(self, X, y=None):
        return self
    def transform(self, X):
        X_copy = X.copy()
        if isinstance(X_copy, pd.DataFrame):
            series = X_copy.iloc[:, 0]
        else:
            series = X_copy
        dt_series = pd.to_datetime(series, errors='coerce')
        return pd.DataFrame({
            'mes': dt_series.dt.month,
            'dia_semana': dt_series.dt.dayofweek,
            'hora': dt_series.dt.hour,
            'dia_ano': dt_series.dt.day_of_year
        })

# --- 2. CONFIGURAÇÃO DO APP ---
app = Flask(__name__)
MODEL_FILE = 'modelo_atraso_voos_rf_res.pkl'
model = None

# Nova lista de colunas reduzida
COLUNAS_ESPERADAS = [
    'sg_empresa_icao', 
    'sg_iata_origem',   # Antes era sg_icao_origem
    'sg_iata_destino',  # Antes era sg_icao_destino
    'dt_partida_prevista'
]

print(f"Carregando modelo: {MODEL_FILE}...")
try:
    model = joblib.load(MODEL_FILE)
    print("Modelo carregado com SUCESSO!")
except Exception:
    print("ERRO CRÍTICO: Não foi possível carregar o modelo.")
    traceback.print_exc()

@app.route('/predict', methods=['POST'])
def predict():
    global model
    if model is None:
        return jsonify({'message': 'Modelo offline', 'status': 'error'}), 500

    try:
        data = request.get_json()
        if not data:
            return jsonify({'message': 'Nenhum dado JSON fornecido', 'status': 'error'}), 400
        
        # --- VALIDAÇÃO DOS DADOS ---
        # Verifica se todas as colunas necessárias estão presentes no JSON enviado
        input_data = {}
        colunas_faltantes = []

        for col in COLUNAS_ESPERADAS:
            if col in data:
                input_data[col] = data[col]
            else:
                colunas_faltantes.append(col)
        
        # Se houver colunas faltando, retorna erro para o usuário
        if colunas_faltantes:
            return jsonify({
                'message': f'Faltando colunas obrigatórias: {colunas_faltantes}', 
                'status': 'error'
            }), 400

        # Cria o DataFrame apenas com os dados corretos
        df_input = pd.DataFrame([input_data])
        
        print("\nDataFrame montado para previsão:")
        print(df_input)

        # Realiza a previsão
        prediction = model.predict(df_input)[0]
        
        proba = 0.0
        if hasattr(model, 'predict_proba'):
            try:
                proba = float(model.predict_proba(df_input)[0][1])
            except: pass

        return jsonify({
            'prediction': int(prediction),
            'previsao': "ATRASADO" if prediction == 1 else "NO HORÁRIO",
            'probabilidade': proba,
            'status': 'success'
        })

    except Exception as e:
        print("Erro durante o processamento:")
        traceback.print_exc()
        return jsonify({'message': str(e), 'status': 'error'}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
