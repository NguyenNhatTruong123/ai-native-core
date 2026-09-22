# AI-Native Core Service

## 1. Project Purpose
This repository serves as the baseline foundation for the Core Service API. It is architected under the AI-Native SDLC paradigm, utilizing GitHub Copilot for spec-driven generation and gated quality verification.

## 2. Technology Stack & Prerequisites
- **Language / Runtime:** Python 3.11+ / Node.js 20+ / .NET 8 / Java 21 / C++20 (Select your project primary stack)
- **Framework:** REST API (FastAPI / Express / ASP.NET Core / Spring Boot)
- **Database:** PostgreSQL 16
- **Tooling:** Docker, GitHub Copilot Extension, PyTest / Jest

## 3. Getting Started & How to Run
```bash
# Step 1: Clone the repository
git clone https://github.com/your-org/ai-native-core.git
cd ai-native-core

# Step 2: Install dependencies
pip install -r requirements.txt  # or npm install / dotnet restore

# Step 3: Run local developer server
python src/main.py               # or npm start / dotnet run
```

## 4. Repository Governance
- All feature additions must originate from an approved GitHub Issue.
- AI-generated code must strictly pass unit tests and human peer review before merging.

## 5. Lab2.1 — Context Engineering

The Rules Pack contains [coding rules](docs/coding-rules.md),
[API rules](docs/api-rules.md), and [security rules](docs/security-rules.md).
The draft follows the lab's second example: `sum(String, String)` in
[ScratchHandler.java](scratch/ScratchHandler.java).

See the [context prompt and draft contract](docs/lab21-draft-prompt.md) and the
[compliance scorecard](docs/lab21-scorecard.md). As requested, Codex performed
the draft generation directly and saved the results in this repository.

Run the draft checks from the repository root with PowerShell and JDK 17+:

```powershell
.\scripts\test-lab21.ps1
```

No Maven, Spring server, or database is needed for this scratch exercise.
