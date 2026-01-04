import sys
import traceback
import joblib
import pandas as pd
import numpy as np
from flask import Flask, request, jsonify

# --- 1. BLOCO DE COMPATIBILIDADE ---
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

print(f"--- INICIANDO SERVIDOR ---")
print(f"Tentando carregar modelo: {MODEL_FILE}")
try:
    model = joblib.load(MODEL_FILE)
    print("✅ Modelo carregado com SUCESSO!")
except Exception as e:
    print(f"❌ ERRO CRÍTICO AO CARREGAR MODELO: {e}")
    traceback.print_exc()

# --- 3. ENDPOINT HEALTH (Blindado contra erros 500) ---
@app.route('/health', methods=['GET'])
def health():
    try:
        # Verifica se a variável 'model' existe no escopo global e não é None
        # Usar globals().get evita NameError se a variável não tiver sido definida
        model_obj = globals().get('model')
        is_up = model_obj is not None
        
        status_data = {
            "status": "UP" if is_up else "DOWN",
            "service": "modelos-ml",
            "model_loaded": is_up
        }
        
        # Se o modelo não carregou, retornamos 503 (Service Unavailable).
        # Se carregou, 200 (OK).
        code = 200 if is_up else 503
        
        print(f"Health check: {code} - {status_data}")
        return jsonify(status_data), code

    except Exception as e:
        # Se algo muito estranho acontecer, logamos o erro e retornamos 500 com mensagem
        print(f"❌ ERRO NO HEALTH CHECK: {e}")
        traceback.print_exc()
        return jsonify({'status': 'ERROR', 'message': str(e)}), 500

# --- 4. ENDPOINT PREDICT (Com validação solicitada) ---
@app.route('/predict', methods=['POST'])
def predict():
    # Verifica modelo
    current_model = globals().get('model')
    if current_model is None:
        return jsonify({'message': 'Modelo offline - falha no carregamento', 'status': 'error'}), 503

    try:
        data_json = request.get_json()
        if not data_json:
            return jsonify({'status': 'error', 'message': 'JSON vazio.'}), 400

        # Validação do Contrato (Java Wrapper)
        expected = ['companhia', 'origem', 'destino', 'data_partida']
        missing_input = [k for k in expected if k not in data_json]
        
        if missing_input:
            return jsonify({'status': 'error', 'message': f'Campos faltando na entrada: {missing_input}'}), 400

        # Montagem dos dados para o Pickle
        dados_modelo = {}
        # Mapeia: input do Java -> colunas do modelo (IATA)
        dados_modelo['sg_empresa_icao'] = data_json.get('companhia')
        dados_modelo['sg_iata_origem'] = data_json.get('origem')   # Backend já manda IATA
        dados_modelo['sg_iata_destino'] = data_json.get('destino') # Backend já manda IATA
        dados_modelo['dt_partida_prevista'] = data_json.get('data_partida')

        df_input = pd.DataFrame([dados_modelo])
        
        print("\nDataFrame montado para previsão:")
        print(df_input)

        # Previsão
        prediction = current_model.predict(df_input)[0]
        
        proba = 0.0
        if hasattr(current_model, 'predict_proba'):
            try:
                proba = float(current_model.predict_proba(df_input)[0][1])
            except: pass

        return jsonify({
            'prediction': int(prediction),
            'previsao': "ATRASADO" if prediction == 1 else "NO HORÁRIO",
            'probabilidade': proba,
            'status': 'success'
        })

    except Exception as e:
        print("Erro durante o processamento da previsão:")
        traceback.print_exc()
        return jsonify({'message': str(e), 'status': 'error'}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
