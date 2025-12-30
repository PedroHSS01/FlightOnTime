import traceback
from pathlib import Path

print('Checking model file...')
p = Path('modelo_previsao_voos.bin')
print('exists', p.exists(), 'size', p.stat().st_size if p.exists() else None)

try:
    # Try CatBoost
    from catboost import CatBoost
    m = CatBoost()
    m.load_model(str(p))
    print('CatBoost model loaded')
    try:
        print('feature_names_ attr:', getattr(m, 'feature_names_', None))
    except Exception as e:
        print('no feature_names_:', e)
    try:
        print('get_feature_names():')
        print(m.get_feature_names())
    except Exception as e:
        print('no get_feature_names:', e)
except Exception:
    print('CatBoost load failed, trying joblib...')
    try:
        import joblib
        m = joblib.load(str(p))
        print('joblib loaded, type:', type(m))
        try:
            print('attrs with feature in name:', [
                  a for a in dir(m) if 'feature' in a.lower()])
        except Exception:
            pass
    except Exception:
        print('All loaders failed:')
        traceback.print_exc()
