import json
import os
import time
import urllib.request
import urllib.error

BASE_URL = "http://localhost:8081"
API_KEY = "reviewer-secret-key-67890"  # Configured in application.yml

def send_request(path, method="GET", payload=None):
    url = f"{BASE_URL}{path}"
    headers = {
        "X-API-KEY": API_KEY,
        "Content-Type": "application/json"
    }
    
    data = None
    if payload:
        data = json.dumps(payload).encode("utf-8")
        
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    
    try:
        with urllib.request.urlopen(req) as response:
            res_data = response.read().decode("utf-8")
            return json.loads(res_data) if res_data else {}
    except urllib.error.HTTPError as e:
        print(f"HTTP Error: {e.code} - {e.reason}")
        print(e.read().decode("utf-8"))
        raise e
    except Exception as e:
        print(f"Connection Error: {e}")
        raise e

def read_sample_file(filename):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    filepath = os.path.join(script_dir, filename)
    with open(filepath, "r", encoding="utf-8") as f:
        return f.read()

def main():
    print("=== PLAGIARISM ENGINE SEED DATA & TEST SCRIPT ===")
    
    # 1. Create a submission set
    print("\n[1] Creating a new submission set...")
    set_res = send_request("/api/submissions/sets", "POST", {"name": "Classroom Assignment 1"})
    set_id = set_res["id"]
    print(f"Created Submission Set: '{set_res['name']}' with ID: {set_id}")
    
    # 2. Upload sample files
    samples = [
        {"name": "JavaOriginal1.java", "lang": "java"},
        {"name": "JavaPlagiarized1.java", "lang": "java"},
        {"name": "JavaOriginal2.java", "lang": "java"},
        {"name": "PythonOriginal1.py", "lang": "python"},
        {"name": "PythonPlagiarized1.py", "lang": "python"},
        {"name": "CppOriginal1.cpp", "lang": "cpp"},
        {"name": "CppPlagiarized1.cpp", "lang": "cpp"},
        {"name": "CppOriginal2.cpp", "lang": "cpp"}
    ]
    
    print("\n[2] Uploading source files...")
    for sample in samples:
        filename = sample["name"]
        code = read_sample_file(filename)
        payload = {
            "name": filename,
            "language": sample["lang"],
            "sourceCode": code,
            "submissionSetId": set_id
        }
        sub_res = send_request("/api/submissions", "POST", payload)
        print(f" -> Uploaded: {filename} (ID: {sub_res['id']})")
        
    # 3. Trigger batch comparison
    print(f"\n[3] Triggering batch comparison on set: {set_id}...")
    # This endpoint returns a status immediately (asynchronously)
    trigger_res = send_request(f"/api/comparisons/sets/{set_id}?threshold=0.30", "POST")
    print(f"Server response: {trigger_res}")
    
    # 4. Wait for background processing and check reports
    print("\n[4] Waiting 5 seconds for background comparison execution...")
    time.sleep(5)
    
    print("\n[5] Fetching plagiarism report (flagged pairs)...")
    report = send_request(f"/api/comparisons/reports/sets/{set_id}?threshold=0.30")
    
    if not report:
        print("No pairs flagged above threshold.")
    else:
        print("\n================ FLAG REPORT ================")
        for pair in report:
            subA = pair["submissionA"]["name"]
            subB = pair["submissionB"]["name"]
            score = pair["similarityScore"] * 100
            w_score = pair["winnowingScore"] * 100
            c_score = pair["cosineScore"] * 100
            l_score = pair["lcsScore"] * 100
            print(f"Similarity Flag: {subA} <-> {subB}")
            print(f"  - Combined Score: {score:.1f}%")
            print(f"  - Winnowing Score: {w_score:.1f}%")
            print(f"  - Vector Cosine: {c_score:.1f}%")
            print(f"  - AST Sequence Alignment (LCS): {l_score:.1f}%")
            print("-" * 45)

if __name__ == "__main__":
    main()
