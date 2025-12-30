from flask import Flask, request, jsonify
import random

app = Flask(__name__)

@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint"""
    return jsonify({"status": "healthy", "service": "mock-ml-service"}), 200

@app.route('/predict', methods=['POST'])
def predict():
    """
    Mock ML prediction endpoint
    Returns random predictions for testing
    """
    data = request.get_json()
    
    # Generate mock prediction (0 = ON_TIME, 1 = DELAYED)
    prediction = random.choice([0, 1])
    confidence = round(random.uniform(0.6, 0.95), 2)
    
    response = {
        "prediction": prediction,
        "confidence": confidence  # Changed from 'probability' to 'confidence'
    }
    
    print(f"Mock ML Service - Received: {data.get('flightNumber', 'N/A')}")
    print(f"Mock ML Service - Returning: {response}")
    
    return jsonify(response), 200

if __name__ == '__main__':
    print("Starting Mock ML Service on port 8000...")
    app.run(host='0.0.0.0', port=8000, debug=False)
