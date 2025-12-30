from catboost import CatBoost
import pandas as pd
import traceback

m = CatBoost()
print('Loading model...')
m.load_model('modelo_previsao_voos.bin')
print('Model loaded')

# Construct input matching feature_order used in app
row = {
    'sg_empresa_icao': 'AA',
    'sg_icao_origem': 'JFK',
    'sg_icao_destino': 'LAX',
    'sg_equipamento_icao': 'A320',
    'nr_assentos_ofertados': 186,
    'cd_tipo_linha': 0,
    'mes_partida': 12,
    'dia_semana': 1,
    'hora_partida': 14
}

df = pd.DataFrame([row])
print('Input DF:')
print(df)
print('DF dtypes:')
print(df.dtypes)

try:
    preds = m.predict(df)
    print('Predictions:', preds)
except Exception as e:
    print('Exception during predict:')
    traceback.print_exc()
