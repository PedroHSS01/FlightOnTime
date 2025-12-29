#!/usr/bin/env python3
"""
Simple load-test script for POST /predict
Usage examples:
  python load_test.py --url http://localhost:5000/predict -n 100 -c 10
  python load_test.py --url http://localhost:8080/api/v1/predict -n 50 --concurrency 5 --output results.json
"""
import argparse
import concurrent.futures
import json
import random
import string
import time
from datetime import datetime, timedelta

import requests

SAMPLE_AIRPORTS = [
    "JFK", "LAX", "SFO", "ORD", "ATL", "DFW", "MIA", "SEA", "BOS", "LAS",
]
AIRLINE_CODES = ["AA", "DL", "UA", "SW", "BA", "AF", "LH", "IB"]


def random_flight_number():
    code = random.choice(AIRLINE_CODES)
    num = random.randint(1, 9999)
    return f"{code}{num}"


def random_payload():
    flight_number = random_flight_number()
    company = flight_number[:2]
    origin = random.choice(SAMPLE_AIRPORTS)
    dest = random.choice([a for a in SAMPLE_AIRPORTS if a != origin])
    # random departure in next 7 days
    dt = datetime.utcnow() + timedelta(days=random.randint(0, 7), hours=random.randint(0, 23), minutes=random.randint(0, 59))
    departure = dt.replace(microsecond=0).isoformat()
    distance = random.randint(50, 5000)
    return {
        "flightNumber": flight_number,
        "companyName": company,
        "flightOrigin": origin,
        "flightDestination": dest,
        "flightDepartureDate": departure,
        "flightDistance": distance,
    }


def worker(session, url, payload, timeout=10):
    start = time.perf_counter()
    try:
        r = session.post(url, json=payload, timeout=timeout)
        latency = time.perf_counter() - start
        try:
            data = r.json()
        except Exception:
            data = r.text
        return {
            "status_code": r.status_code,
            "latency": latency,
            "payload": payload,
            "response": data,
        }
    except Exception as e:
        latency = time.perf_counter() - start
        return {"status_code": None, "latency": latency, "payload": payload, "response": str(e)}


def run(url, total, concurrency, delay, output):
    results = []
    session = requests.Session()
    start_all = time.perf_counter()

    with concurrent.futures.ThreadPoolExecutor(max_workers=concurrency) as ex:
        futures = []
        for i in range(total):
            payload = random_payload()
            futures.append(ex.submit(worker, session, url, payload))
            if delay and (i + 1) % concurrency == 0:
                # small sleep to throttle groups
                time.sleep(delay)

        for fut in concurrent.futures.as_completed(futures):
            res = fut.result()
            results.append(res)

    total_time = time.perf_counter() - start_all
    # summarize
    ok = sum(1 for r in results if r.get("status_code") and 200 <= r["status_code"] < 300)
    errors = len(results) - ok
    latencies = [r["latency"] for r in results if r.get("latency") is not None]
    avg_latency = sum(latencies) / len(latencies) if latencies else None

    summary = {
        "url": url,
        "requests": total,
        "concurrency": concurrency,
        "successful": ok,
        "failed": errors,
        "total_time_s": total_time,
        "avg_latency_s": avg_latency,
    }

    print("--- Load test summary ---")
    print(json.dumps(summary, indent=2))

    if output:
        try:
            with open(output, "w", encoding="utf-8") as f:
                json.dump({"summary": summary, "results": results}, f, ensure_ascii=False, indent=2)
            print(f"Results written to: {output}")
        except Exception as e:
            print("Failed to write output:", e)

    return summary


def parse_args():
    p = argparse.ArgumentParser(description="Load test POST /predict")
    p.add_argument("--url", default="http://localhost:5000/predict", help="Endpoint URL")
    p.add_argument("-n", "--requests", type=int, default=100, help="Total number of requests")
    p.add_argument("-c", "--concurrency", type=int, default=10, help="Number of concurrent workers")
    p.add_argument("--delay", type=float, default=0.0, help="Delay in seconds between groups of requests")
    p.add_argument("--output", help="Write full results to JSON file")
    return p.parse_args()


def main():
    args = parse_args()
    run(args.url, args.requests, args.concurrency, args.delay, args.output)


if __name__ == "__main__":
    main()
