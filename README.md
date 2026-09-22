# AI-Native Core Service

## 1. Project Purpose
This repository serves as the baseline foundation for the Core Service API. It is architected under the AI-Native SDLC paradigm, utilizing GitHub Copilot for spec-driven generation and gated quality verification.

### Repository layout

| Location | Purpose |
| --- | --- |
| [docs/rules/](docs/README.md) | Shared coding, API, and security rules. |
| [docs/specs/](docs/README.md) | Technical specifications and domain/API specification stubs. |
| [labs/](labs/README.md) | Lab prompts, analysis, reviews, completion reports, and verification results, grouped by lab. |
| `.github/` | Tool instructions and contribution templates. |
| `scratch/`, `tests/`, `scripts/` | Exercise code, executable tests, and runner scripts. |

The [document index](docs/README.md) and [lab index](labs/README.md) explain each file's
purpose and its placement. Lab outputs are kept separate from the shared specifications.

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

The Rules Pack contains [coding rules](docs/rules/coding-rules.md),
[API rules](docs/rules/api-rules.md), and [security rules](docs/rules/security-rules.md).
The draft follows the lab's second example: `sum(String, String)` in
[ScratchHandler.java](scratch/ScratchHandler.java).

See the [context prompt and draft contract](labs/lab-2.1-context-engineering/prompts/draft-generation.md) and the
[compliance scorecard](labs/lab-2.1-context-engineering/results/compliance-scorecard.md). As requested, Codex performed
the draft generation directly and saved the results in this repository.

Run the draft checks from the repository root with PowerShell and JDK 17+:

```powershell
.\scripts\test-lab21.ps1
```

No Maven, Spring server, or database is needed for this scratch exercise.

## 6. Lab2.2 — Context and Business Requirements Analysis

[Workspace instructions](.github/copilot-instructions.md) apply the lab's role and core rules.
The [original prompts with their inputs](labs/lab-2.2-business-analysis/prompts/executed-prompts.md) were executed directly by Codex.

- [Business analysis](labs/lab-2.2-business-analysis/analysis/work-order-business-analysis.md): entities, open questions, and UI/Data/API decomposition.
- [Code review](labs/lab-2.2-business-analysis/reviews/security-code-review.md): 14 findings on the deliberately vulnerable
  [training fixture](scratch/lab22/unsafe/WorkOrderController.java), in the requested priority order.
- [Proposed safe structure](labs/lab-2.2-business-analysis/reviews/proposed-safe-design.md): responsibilities, parameter binding, and test plan.
- [Done Criteria coverage](labs/lab-2.2-business-analysis/results/done-criteria.md): evidence for both official criteria and each step.

The vulnerable fixture is for static review only and is outside application source.
This lab does not build or deploy a Spring application.
