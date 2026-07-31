# API Documentation - Semantic Code Plagiarism Detection Engine

The Plagiarism Engine exposes REST endpoints secured with custom API-Key authentication.

## Authentication Headers

All requests to protected endpoints must include the following header:
- `X-API-KEY`: The API key matching configured access roles.
  - Reviewer Key: `reviewer-secret-key-67890` (allows uploads, comparisons, and report viewing)
  - Admin Key: `admin-secret-key-12345` (allows full access, including audits and config changes)

---

## 1. Submission Endpoints

### Create Submission Set
Create a logical group for code files (e.g. classroom assignment).
- **Method**: `POST`
- **Path**: `/api/submissions/sets`
- **Auth**: `ROLE_REVIEWER`
- **Request Body**:
  ```json
  {
    "name": "Classroom Assignment 1"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "id": "a0b1c2d3-e4f5-6789-0123-456789abcdef",
    "name": "Classroom Assignment 1",
    "createdAt": "2026-07-26T00:30:00",
    "submissionCount": 0
  }
  ```

### Upload Source Code
Ingest a single code file.
- **Method**: `POST`
- **Path**: `/api/submissions`
- **Auth**: `ROLE_REVIEWER`
- **Request Body**:
  ```json
  {
    "name": "Calculator.java",
    "language": "java",
    "sourceCode": "public class Calculator { ... }",
    "submissionSetId": "a0b1c2d3-e4f5-6789-0123-456789abcdef"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "id": "f8e7d6c5-b4a3-2109-8765-43210fedcba9",
    "name": "Calculator.java",
    "language": "java",
    "fileHash": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    "createdAt": "2026-07-26T00:31:00",
    "submissionSetId": "a0b1c2d3-e4f5-6789-0123-456789abcdef"
  }
  ```

### Upload ZIP Archive
Upload a zipped folder containing multiple source files (automatically extracts supported `.java`, `.py`, and `.cpp` files).
- **Method**: `POST`
- **Path**: `/api/submissions/sets/{setId}/zip`
- **Auth**: `ROLE_REVIEWER`
- **Content-Type**: `multipart/form-data`
- **Form Data**:
  - `file`: (Binary Zip Archive)
- **Response**: `200 OK`
  ```json
  [
    {
      "id": "e5c4b3a2-9d8c-7b6a-5a4b-3c2d1e0f9e8d",
      "name": "StudentA/Calculator.java",
      "language": "java",
      "fileHash": "fc389c898c...",
      "createdAt": "2026-07-26T00:32:00",
      "submissionSetId": "a0b1c2d3-e4f5-6789-0123-456789abcdef"
    }
  ]
  ```

---

## 2. Comparison & Similarity Endpoints

### Pairwise Comparison
Trigger a comparison between two specific code files.
- **Method**: `POST`
- **Path**: `/api/comparisons/pair`
- **Auth**: `ROLE_REVIEWER`
- **Query Parameters**:
  - `submissionAId` (UUID): ID of first submission.
  - `submissionBId` (UUID): ID of second submission.
- **Response**: `200 OK`
  ```json
  {
    "id": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
    "submissionAId": "f8e7d6c5-b4a3-2109-8765-43210fedcba9",
    "submissionBId": "e5c4b3a2-9d8c-7b6a-5a4b-3c2d1e0f9e8d",
    "similarityScore": 0.8752,
    "winnowingScore": 0.9211,
    "cosineScore": 0.8105,
    "lcsScore": 0.8000,
    "createdAt": "2026-07-26T00:35:00"
  }
  ```

### Batch Set Comparison
Trigger asynchronous pair-wise comparisons across all submissions in a set.
- **Method**: `POST`
- **Path**: `/api/comparisons/sets/{setId}`
- **Auth**: `ROLE_REVIEWER`
- **Query Parameters**:
  - `threshold` (Double, default: `0.50`): Minimum score to flag in background results.
- **Response**: `202 Accepted`
  ```text
  Asynchronous batch comparison initiated for submission set a0b1c2d3-e4f5-6789-0123-456789abcdef
  ```

### Fetch Plagiarism Reports
Retrieve pairs that cross the specified similarity threshold.
- **Method**: `GET`
- **Path**: `/api/comparisons/reports` or `/api/comparisons/reports/sets/{setId}`
- **Auth**: `ROLE_REVIEWER`
- **Query Parameters**:
  - `threshold` (Double, default: `0.50`): Minimum score to filter results.
- **Response**: `200 OK`
  ```json
  [
    {
      "comparisonId": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
      "submissionA": {
        "id": "f8e7d6c5-b4a3-2109-8765-43210fedcba9",
        "name": "Calculator.java",
        "language": "java",
        "fileHash": "e3b0c442...",
        "createdAt": "2026-07-26T00:31:00",
        "submissionSetId": "a0b1c2d3-e4f5-6789-0123-456789abcdef"
      },
      "submissionB": {
        "id": "e5c4b3a2-9d8c-7b6a-5a4b-3c2d1e0f9e8d",
        "name": "StudentA/Calculator.java",
        "language": "java",
        "fileHash": "fc389c89...",
        "createdAt": "2026-07-26T00:32:00",
        "submissionSetId": "a0b1c2d3-e4f5-6789-0123-456789abcdef"
      },
      "similarityScore": 0.8752,
      "winnowingScore": 0.9211,
      "cosineScore": 0.8105,
      "lcsScore": 0.8000
    }
  ]
  ```

---

## 3. Administrative Endpoints

### View Audit Logs
Fetch system-wide operations audit logs.
- **Method**: `GET`
- **Path**: `/api/admin/audits`
- **Auth**: `ROLE_ADMIN`
- **Response**: `200 OK`
  ```json
  [
    {
      "id": "3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d",
      "action": "END_BATCH_COMPARE",
      "details": "Completed batch comparison for set a0b1c2d3-e4f5-6789-0123-456789abcdef. Ran 45 comparisons. Found 3 flagged pairs.",
      "performedBy": "Admin",
      "createdAt": "2026-07-26T00:38:00"
    }
  ]
  ```

### Update Algorithm Weights
Update the relative weights for combined plagiarism scores.
- **Method**: `POST`
- **Path**: `/api/admin/config/weights`
- **Auth**: `ROLE_ADMIN`
- **Query Parameters**:
  - `winnowing` (Double): Weight of Winnowing Fingerprinting (e.g. `0.50`)
  - `cosine` (Double): Weight of Token Cosine vector (e.g. `0.25`)
  - `lcs` (Double): Weight of AST sequence alignment (e.g. `0.25`)
- **Response**: `200 OK`
  ```text
  Weights updated successfully
  ```
